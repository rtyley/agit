package com.madgag.agit;

import java.io.File;

import com.madgag.agit.git.Repos;
import org.eclipse.jgit.lib.Repository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class RepositoryProvider implements Provider<Repository> {
	@Inject @Named("gitdir") File gitdir;
	
	public Repository get() {
		return Repos.openRepoFor(gitdir);
	}
}