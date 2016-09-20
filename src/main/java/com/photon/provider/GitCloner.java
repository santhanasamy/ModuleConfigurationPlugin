package com.photon.provider;

import com.intellij.openapi.project.Project;
import com.photon.data.CoreModel;
import com.photon.data.GitRepository;
import com.photon.data.SubModule;
import com.photon.utils.Constants;
import com.photon.utils.Constants.GitCommand;
import com.photon.utils.FileUtils;
import com.photon.utils.GitHelper;
import com.photon.utils.GitHelper.GitStatusListener;
import com.photon.utils.GitHelper.StreamGobbler;

import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Worker to deal with the GIT interaction in background.
 */
public class GitCloner extends SwingWorker<Boolean, String> {

    private final CoreModel mCoreModel;
    private final List<SubModule> moduleList;
    private final Project mProject;
    private final Map<String, GitRepository> mRepoMap;
    private final GitStatusListener mListener;

    private StreamGobbler outputGobbler = null;
    private StreamGobbler errorGobbler = null;

    public GitCloner(CoreModel aCoreData, Project aProject, GitStatusListener aListener) {

        mCoreModel = aCoreData;
        moduleList = new ArrayList<>(aCoreData.getModuleSet());
        mProject = aProject;

        mRepoMap = new HashMap<String, GitRepository>();
        Set<GitRepository> lRepoMap = aCoreData.getRepositorySet();
        for (GitRepository lRepo : lRepoMap) {
            mRepoMap.put(lRepo.getRepoName(), lRepo);
        }
        mListener = aListener;
        mListener.onStart();
    }

    @Override
    protected void process(List<String> chunks) {
        for (String lMsg : chunks) {
            mListener.onProgressUpdate(lMsg);
        }
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        if (null == mProject) {
            return null;
        }

        String lModPath = "";
        for (int i = 0; i < moduleList.size(); i++) {

            SubModule lMod = moduleList.get(i);
            lMod.setOriginName("origin_" + i);
            String lModuleType = lMod.getModuleType().getTypeStrValue();

            if (null != lModuleType && lModuleType.equalsIgnoreCase(Constants.Module.JAR.getTypeStrValue())) {
                continue;
            }

            String lPath = mProject.getBasePath() + File.separator;
            lModPath = lMod.getSourcePath();

            if (null == lModPath || 0 == lModPath.length()) {
                lPath = lPath + "temp";
            } else {
                lPath = lPath + lModPath;
            }
            File lFile = new File(lPath);
            if (!lFile.exists()) {
                lFile.getParentFile().mkdirs();
                boolean lStatus = lFile.mkdir();
                // System.out.println("[File Creation Status] [" + lStatus + "]");
            }
            lPath = lFile.getCanonicalPath();
            if (Constants.DEBUG) {
                System.out.println("[Execution Dir] [" + lPath + "]");
            }
            cloneByJava(lFile, lMod);
        }
        return true;
    }

    @Override
    protected void done() {
        mListener.onComplete();
        if (null != errorGobbler) {
            errorGobbler.quit();
        }
        if (null != outputGobbler) {
            outputGobbler.quit();
        }
    }

    public void cloneByJava(File aExeRootPath, SubModule aModule) {


        if (Constants.INFO) {
            System.out.println("Cloning[Module][" + aModule.getModuleName() + "]");
        }
        try {

            //if (!aIsPathChanged) {
            // 1.
            boolean lHasGit = FileUtils.hasGit(aExeRootPath);
            if (!lHasGit) {
                initializeGit(aExeRootPath, aModule);
            }

            // 2.
            addRemoteRepo(aExeRootPath, aModule);
            //}

            // 3.
            filterGivenModule(aExeRootPath, aModule);

            // 4.
            //if (!aIsPathChanged) {
            publish("Enabling sparse checkout(Depth=1)");
            runCommand(aExeRootPath, GitCommand.GIT_SPARSE_CONFIG_ARRAY);
            //}

            // 5.
            //if (!aIsPathChanged) {
            publish("Git Pull with [Depth-1]");
            String[] lCommand = GitCommand.configure(GitCommand.GIT_PULL_ARRAY, aModule.getOriginName(), 2);
            GitRepository lRepo = mRepoMap.get(aModule.getRepoName());
            lCommand = GitCommand.configure(lCommand, lRepo.getBranch(), 3);
            runCommand(aExeRootPath, lCommand);
            //}

            // 6.
            publish("Checking out development branch");
            lCommand = GitCommand.configure(GitCommand.GIT_CHECKOUT_ARRAY, lRepo.getBranch(), 2);
            runCommand(aExeRootPath, lCommand);

            //7.
            publish("Init Completed for Module [" + aModule.getModuleName() + "]");
        } catch (InterruptedException e) {
            if (Constants.ERROR) {
                System.out.println("[Error-Running command] [" + e.getMessage() + "]");
            }
            //e.printStackTrace();
        } catch (IOException e) {
            if (Constants.ERROR) {
                System.out.println("[Error-Running command] [" + e.getMessage() + "]");
            }
            //e.printStackTrace();
        } catch (Exception e) {
            if (Constants.ERROR) {
                System.out.println("[Error-Running command] [" + e.getMessage() + "]");
            }
        }
    }

    private void initializeGit(File aExeRootPath, SubModule aModule) throws IOException, InterruptedException {
        publish("Initializing Git Repo");
        runCommand(aExeRootPath, GitCommand.GIT_INIT_ARRAY);
    }

    private void addRemoteRepo(File aExeRootPath, SubModule aModule) throws IOException, InterruptedException {
        publish("Adding remote repository");
        String[] lCommand = GitCommand.configure(GitCommand.GIT_REMOTE_ADD_ARRAY,
                mRepoMap.get(aModule.getRepoName()).getBaseURI(), -1);

        lCommand = GitCommand.configure(lCommand, aModule.getOriginName(), 3);
        runCommand(aExeRootPath, lCommand);
    }

    private void filterGivenModule(File aExeRootPath, SubModule aModule) throws IOException {
        publish("Preparing for sparse checkout");
        String lRoot = aExeRootPath.getAbsolutePath() + File.separator + ".git";
        File lSPFile = new File(lRoot + File.separator + "info" + File.separator + "sparse-checkout");

        if (Constants.DEBUG) {
            //System.out.println("[Sp-File path] [" + lSPFile.getAbsolutePath() + "]");
        }

        if (!lSPFile.exists()) {
            lSPFile.createNewFile();
        }
        PrintWriter lWriter = new PrintWriter(new BufferedWriter(new FileWriter(lSPFile, true)));
        //FileWriter lWriter = new FileWriter(lSPFile);
        lWriter.write(aModule.getModuleName());
        lWriter.println();
        lWriter.flush();
        lWriter.close();
    }


    public void runCommand(File directory, String... command) throws IOException, InterruptedException {

        if (Constants.INFO) {
            System.out.println("[Running-Command][" + Arrays.toString(command) + "]");
        }
        long lTime = System.currentTimeMillis();
        ProcessBuilder pb = new ProcessBuilder().command(command).directory(directory);
        Process lProcess = null;
        try {
            lProcess = pb.start();
            outputGobbler = new StreamGobbler("OUTPUT");
            outputGobbler.start();
            outputGobbler.setStream(lProcess.getInputStream());

            errorGobbler = new StreamGobbler("ERROR");
            errorGobbler.start();
            errorGobbler.setStream(lProcess.getErrorStream());

            int exit = lProcess.waitFor();
            outputGobbler.join();
            errorGobbler.join();
            if (exit != 0) {
                throw new AssertionError(String.format("RunCommand returned %d", exit));
            }
        } finally {
            GitHelper.safeClear(lProcess);
        }
        lTime = System.currentTimeMillis() - lTime;
        if (Constants.INFO) {
            System.out.println("[Command execution finished][Time- Taken][" + (lTime / 6000) + "Sec]");
        }
    }

}