package com.madgag.agit.sync;

import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;

import roboguice.inject.ContextScopedProvider;
import roboguice.service.RoboService;

public class AgitSyncAdapterService extends RoboService {

    @Inject
    ContextScopedProvider<SyncAdapter> syncAdapterProvider;

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapterProvider.get(this).getSyncAdapterBinder();
    }
}