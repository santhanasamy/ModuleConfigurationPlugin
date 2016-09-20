package com.photon.data;

import java.util.Set;

/**
 * Created by santhanasamy_a on 9/2/2016.
 */
public class CoreModel {

    private Set<GitRepository> repositorySet;

    private Set<SubModule> moduleSet;

    public Set<SubModule> getModuleSet() {
        return moduleSet;
    }

    public void setModuleSet(Set<SubModule> moduleSet) {
        this.moduleSet = moduleSet;
    }

    public Set<GitRepository> getRepositorySet() {
        return repositorySet;
    }

    public void setRepositorySet(Set<GitRepository> repositorySet) {
        this.repositorySet = repositorySet;
    }
}
