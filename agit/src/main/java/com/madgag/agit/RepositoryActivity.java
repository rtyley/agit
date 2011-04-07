package com.madgag.agit;

import android.app.Activity;
import android.content.Intent;
import com.google.inject.Inject;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.eclipse.jgit.lib.Repository;
import roboguice.activity.RoboActivity;
import android.os.Bundle;
import roboguice.inject.InjectorProvider;

import java.io.File;

import static com.madgag.agit.GitIntents.gitDirFrom;

public class RepositoryActivity extends RoboActivity {

    private @Inject @Named("gitdir") File gitdir;
    private @Inject RepositoryContext rc;
    private @Inject Repository repository;
    protected @Inject RepositoryScope repositoryScope;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        RepositoryScope repositoryScope = enterRepositoryScopeFor(this,getIntent());
		try {
            super.onCreate(savedInstanceState);
        } finally {
            repositoryScope.exit();
        }
	}

    static RepositoryScope enterRepositoryScopeFor(InjectorProvider injectorProvider, Intent intent) {
        RepositoryScope repositoryScope = injectorProvider.getInjector().getInstance(RepositoryScope.class);
        repositoryScope.enterWithRepoGitdir(gitDirFrom(intent));
        return repositoryScope;
    }

    @Override
	protected void onResume() {
		super.onResume();
		rc.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rc.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rc.onDestroy();
	}

    protected Repository repo() {
        return repository;
    }

    protected File gitdir() {
        return gitdir;
    }


    public void onRepoScopedResume() {
    }

    public void onRepoScopedPause() {
    }

    public void onRepoScopedDestroy() {}
}
