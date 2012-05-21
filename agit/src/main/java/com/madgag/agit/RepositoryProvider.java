package com.madgag.agit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.madgag.agit.git.Repos;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

public class RepositoryProvider implements Provider<Repository> {
    @Inject
    @Named("gitdir")
    File gitdir;

    public Repository get() {
        return Repos.openRepoFor(gitdir);
    }
}