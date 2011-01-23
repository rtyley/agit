package com.madgag.agit;

import java.io.File;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;

import android.content.Intent;

public class GitIntentBuilder {

	private final Intent intent;

	public GitIntentBuilder(String action) {
		intent = new Intent(action);
	}
	
	public GitIntentBuilder gitdir(File gitdir) {
		return add("gitdir", gitdir.getAbsolutePath());
	}
	
	public GitIntentBuilder branch(Ref branch) {
		return add("branch", branch.getName());
	}
	
	public GitIntentBuilder remote(RemoteConfig remoteConfig) {
		return add("remote",remoteConfig.getName());
	}
	
	public GitIntentBuilder tag(String tagName) {
		return add("tag", tagName);
	}
	
	public Intent toIntent() {
		return intent;
	}

	GitIntentBuilder add(String fieldName, String value) {
		intent.putExtra(fieldName, value);
		return this;
	}

	public GitIntentBuilder repository(Repository repository) {
		return gitdir(repository.getDirectory());
	}

	public GitIntentBuilder commit(RevCommit revCommit) {
		return commit(revCommit.name());
	}

	public GitIntentBuilder commit(String commitId) {
		return add("commit", commitId);
	}

}
