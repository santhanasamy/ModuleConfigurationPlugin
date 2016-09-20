package com.photon.data;

/**
 * Created by santhanasamy_a on 8/26/2016.
 */
public class GitRepository {

    private String userName;
    private String password;
    private String repoName;
    private String baseURI;
    private String branch;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {

        return "[" + repoName + ":" + baseURI + ":" + branch + "]";
    }
}
