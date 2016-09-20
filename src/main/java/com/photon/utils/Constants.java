package com.photon.utils;

import com.photon.data.GitRepository;

import java.util.regex.Pattern;

/**
 *
 */
public class Constants {

    public static final boolean DEBUG = true;
    public static final boolean INFO = true;
    public static final boolean ERROR = true;

    public static final String COLUMN_MODULE_NAME_TXT = "Module Name";
    public static final String COLUMN_MODULE_TYPE_TXT = "Type";
    public static final String COLUMN_MODULE_ARTIFACT = "Mvn-Info";
    public static final String COLUMN_MODULE_TYPE_VER = "Path";

    public static final String DOT_GIT_FILE = ".git";
    public static final String BUILD_DOT_GRADLE_FILE = "build.gradle";
    public static final String SETTINGS_FILE = "settings.ptn";
    public static final String GRADLE_DOT_PROPERTY_FILE = "gradle.properties";

    public static final String TOKEN_INCLUDE = "include";
    public static final String TOKEN_MVN = "mvn";
    public static final String TOKEN_TYPE = "type";
    public static final String TOKEN_PATH = "path";
    public static final String TOKEN_REPO = "repo";

    public static final String[] CONFIG_TABLE_COLUMN_NAMES = new String[]{COLUMN_MODULE_NAME_TXT,
            COLUMN_MODULE_TYPE_TXT, COLUMN_MODULE_ARTIFACT, COLUMN_MODULE_TYPE_VER};

    public static final String[] MODULE_TYPE = new String[]{Module.SOURCE.getTypeStrValue(), Module.JAR.getTypeStrValue()};

    public static final String MODULE_CONFIG_TOOL_WINDOW_ID = "Module Configuration";

    public static final String WAG_BASE_REPO_URI = "@wagwiki.walgreens.com/stash/scm/mand/";

    public static String DYNAMIC_VAR_PATTERN_STR = "\\$\\{[A-Za-z0-9_]*\\}";

    public static Pattern DYNAMIC_VAR_PATTERN = Pattern.compile(DYNAMIC_VAR_PATTERN_STR);

    public enum Module {

        SOURCE("Source"), JAR("Jar"), UNKNOWN("Unknown");

        String mType;

        Module(String aType) {
            mType = aType;
        }

        public String getTypeStrValue() {
            return mType;
        }

        public static Module getTypeFromStr(String aModuleStr) {

            for (Module lModule : Module.values()) {
                if (lModule.getTypeStrValue().equalsIgnoreCase(aModuleStr)) {
                    return lModule;
                }
            }
            return UNKNOWN;
        }
    }

    public static final class GitCommand {

        //git init
        public static final String[] GIT_INIT_ARRAY = new String[]{"git", "init"};

        //git remote add -f origin URI-TO_BE_ADDED
        public static final String[] GIT_REMOTE_F_ADD_ARRAY = new String[]{"git",
                "remote", "add", "-f", "origin", "URI"};

        //git remote add origin URI-TO_BE_ADDED
        public static final String[] GIT_REMOTE_ADD_ARRAY = new String[]{"git",
                "remote", "add"};

        //git config core.sparseCheckout true
        public static final String[] GIT_SPARSE_CONFIG_ARRAY = new String[]{"git",
                "config", "core.sparseCheckout", "true"};

        //Command to add module.
        public static final String[] GIT_MODULE_CONFIG_ARRAY = new String[]{"echo", "MODULE_NAME", ">>.git/info/sparse-checkout"};

        //git pull origin development --depth=1
        public static final String[] GIT_PULL_ARRAY = new String[]{"git", "pull", "--depth=1"};

        //git checkout development
        public static final String[] GIT_CHECKOUT_ARRAY = new String[]{"git", "checkout"};

        public static String[] configure(String[] aInputArray, String aInput, int aInsertPosition) {

            if (null == aInputArray) {
                aInputArray = new String[1];
            }

            String[] lResStr = new String[aInputArray.length + 1];

            for (int j = 0, k = 0; (k < lResStr.length && j < aInputArray.length); k++) {
                if (k == aInsertPosition) {
                    lResStr[k] = aInput;
                } else {
                    lResStr[k] = aInputArray[j];
                    j++;
                }
            }
            if (-1 == aInsertPosition) {
                lResStr[aInputArray.length] = aInput;
            }
            return lResStr;
        }
    }

    public static GitRepository getTestRepo() {
        GitRepository lRepo = new GitRepository();
        lRepo.setUserName("santhanasamy");
        lRepo.setPassword("samyrepoA1!");
        lRepo.setRepoName("gitexpriment_1");
        lRepo.setBaseURI("@bitbucket.org/santhanasamy/");
        return lRepo;
    }
}
