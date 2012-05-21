package com.madgag.agit.sync;

import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;
import com.google.inject.Provider;

import roboguice.service.RoboService;

public class AgitSyncAdapterService extends RoboService {

    @Inject
    Provider<SyncAdapter> syncAdapterProvider;

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapterProvider.get().getSyncAdapterBinder();
    }
}