package com.madgag.agit;

import java.io.File;

import com.google.inject.internal.Nullable;
import roboguice.inject.InjectExtra;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;


public class RepoGitDirProvider implements Provider<File> {

	@InjectExtra(value="gitdir", optional=true) String gitdirStringFromContext;
	@Inject(optional=true) @Nullable
    @Named(value="gitdir-from-operation") File gitdirFromOperation;
	
	public File get() {
		if (gitdirFromOperation!=null) {
			return gitdirFromOperation;
		}
		return new File(gitdirStringFromContext);
	}

}
