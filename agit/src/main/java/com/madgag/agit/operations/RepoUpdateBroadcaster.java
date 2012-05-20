package com.madgag.agit.operations;


import static com.madgag.agit.GitIntents.broadcastIntentForRepoStateChange;
import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.File;

public class RepoUpdateBroadcaster {

    @Inject
    Application context;
    @Inject
    @Named("gitdir")
    File gitdir;

    public void broadcastUpdate() {
        context.sendBroadcast(broadcastIntentForRepoStateChange(gitdir));
    }
}
