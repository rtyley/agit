package com.madgag.agit.operations;


import static com.madgag.agit.GitIntents.broadcastIntentForRepoStateChange;
import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.db.ReposDataSource;

import java.io.File;

public class RepoUpdateBroadcaster {

    @Inject
    Application context;
    @Inject
    @Named("gitdir")
    File gitdir;

    @Inject
    ReposDataSource reposDataSource;

    public void broadcastUpdate() {
        reposDataSource.registerRepo(gitdir);
        context.sendBroadcast(broadcastIntentForRepoStateChange(gitdir));
    }
}
