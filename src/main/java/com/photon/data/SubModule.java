package com.photon.data;

import com.photon.utils.Constants;

/**
 * Created by santhanasamy_a on 8/11/2016.
 */
public class SubModule {


    public String moduleName = null;
    private String[] mSupportedModuleType = null;
    private String mvnInfo = null;
    private String sourcePath = null;
    private Constants.Module mSelectedModule = null;
    private String repoName = null;
    private String originName;

    public SubModule(String aModuleName) {
        moduleName = aModuleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String[] getSupportedModuleType() {
        return mSupportedModuleType;
    }

    public void setSupportedModuleType(String[] mSupportedModuleType) {
        this.mSupportedModuleType = mSupportedModuleType;
    }

    public String getMvnInfo() {
        return mvnInfo;
    }

    public void setMvnInfo(String mvnInfo) {
        this.mvnInfo = mvnInfo;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String aSourcePath) {
        this.sourcePath = aSourcePath;
    }

    public Constants.Module getModuleType() {
        return mSelectedModule;
    }

    public void setModuleType(String aModuleType) {
        this.mSelectedModule = Constants.Module.getTypeFromStr(aModuleType);
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoName() {
        return repoName;
    }

    @Override
    public String toString() {

        return "[" + moduleName + " : " + repoName + " : " + mvnInfo + " : "
                + sourcePath + " : " + mSelectedModule + " : " + mSupportedModuleType + "]";
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }
}
