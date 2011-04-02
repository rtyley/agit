package com.madgag.agit;

import java.io.File;

import org.eclipse.jgit.lib.Repository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

@RepoOpScoped
public class RepositoryProvider implements Provider<Repository> {
	@Inject @Named("file") File gitdir;
	
	public Repository get() {
		return Repos.openRepoFor(gitdir);
	}
}