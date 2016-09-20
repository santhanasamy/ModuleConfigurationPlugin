package com.photon.utils;

import com.photon.data.GitRepository;
import com.walgreens.gradle.wag.GIT;
import com.walgreens.gradle.wag.Module;
import com.walgreens.gradle.wag.Repo;
import com.walgreens.gradle.wag.UpdateTask;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.*;
import java.util.ArrayList;

/**
 * Helper class to work with GIT.
 */
public class GitHelper {

    public interface GitStatusListener {
        void onProgressUpdate(String aMsg);

        void onStart();

        void onComplete();
    }

    public static void cloneByGroovy() {
        GIT lGit = new GIT();
        lGit.setUrl("https://santhanasamy@bitbucket.org/santhanasamy/gitexpriment.git");
        lGit.setUsername("a.santhanasamy@gmail.com");
        lGit.setPassword("samyrepoA1!");

        Repo lRepo = new Repo(null);
        lRepo.setRepoDir("./");
        lRepo.setGit(lGit);

        ArrayList<Module> lModuleList = new ArrayList<com.walgreens.gradle.wag.Module>();

        com.walgreens.gradle.wag.Module lMod = new com.walgreens.gradle.wag.Module();
        lMod.setMvn("com.walgreens.framework:core:2.0");
        lMod.setName("");
        lMod.setIsDependentOnSource(true);
        lModuleList.add(lMod);

        lRepo.setModule(lModuleList);

        org.gradle.api.Project project = ProjectBuilder.builder().build();
        UpdateTask task = (UpdateTask) project.task("update");
        task.cloneFromGit(lRepo);
    }


    public static String buildURIFromRepo(GitRepository aRepo) {
        //String URI = "https://santhanasamy:samyrepoA1!@bitbucket.org/santhanasamy/gitexpriment.git";
        //String URI = "http://sgunasp7:Brave1991A@wagwiki.walgreens.com/stash/scm/mand/wagflagshipa.git";
        //return "http://" + aRepo.getUserName() + ":" + aRepo.getPassword() + aRepo.getBaseURI() + aRepo.getRepoName() + ".git";
        return aRepo.getBaseURI();
    }

    public static void safeClear(Process lProcess) {
        if (null == lProcess) {
            return;
        }
        close(lProcess.getErrorStream());
        close(lProcess.getOutputStream());
        close(lProcess.getInputStream());
        lProcess.destroy();
    }

    private static void close(InputStream anInput) {
        try {
            if (anInput != null) {
                anInput.close();
            }
        } catch (IOException anExc) {
            anExc.printStackTrace();
        }
    }

    private static void close(OutputStream anOutput) {
        try {
            if (anOutput != null) {
                anOutput.close();
            }
        } catch (IOException anExc) {
            anExc.printStackTrace();
        }
    }

    public static class StreamGobbler extends Thread {

        private InputStream mIStream;
        private String type;
        private boolean mIsAlive = true;

        public StreamGobbler(String type) {
            this.type = type;
        }

        @Override
        public void run() {

            while (mIsAlive) {
                InputStreamReader isr = null;
                BufferedReader br = null;
                if (null == mIStream) {
                    break;
                }
                try {
                    isr = new InputStreamReader(mIStream);
                    br = new BufferedReader(isr);

                    //System.out.println("Stream-Gobbler[ISR, BR, MSTR][" + isr + "," + br + "," + mIStream + "]");
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (Constants.INFO) {
                            System.out.println(type + "> " + line);
                        }
                        if (!mIsAlive) {
                            throw new InterruptedIOException("Force finish");
                        }
                    }
                } catch (IOException ioe) {
                    if (Constants.ERROR) {
                        System.out.println("[Stream-Gobbler Error][" + ioe.getMessage());
                    }
                    //ioe.printStackTrace();
                } finally {
                    if (null != br) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            if (Constants.ERROR) {
                                System.out.println("[Stream-Gobbler [BR]-> Error][" + e.getMessage());
                            }
                        }
                    }
                    if (null != isr) {
                        try {
                            isr.close();
                        } catch (IOException e) {
                            //e.printStackTrace();
                            if (Constants.ERROR) {
                                System.out.println("[Stream-Gobbler [ISR]-> Error][" + e.getMessage());
                            }
                        }
                    }
                    mIStream = null;
                }
            }
        }

        public void setStream(InputStream stream) {
            // System.out.println("Stream -----> " + stream.toString());
            GitHelper.close(mIStream);
            mIStream = stream;
        }

        public void quit() {
            mIsAlive = false;
        }
    }

}

