package com.madgag.agit.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import roboguice.inject.InjectorProvider;

public class AgitSyncAdapterService extends Service {

    public static final String TAG = "ASAS";

	private static SyncAdapterImpl syncAdapter = null;

	public AgitSyncAdapterService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
        if (syncAdapter == null)
            syncAdapter = new SyncAdapterImpl(this);
        return syncAdapter.getSyncAdapterBinder();
	}

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		private final Context context;

		public SyncAdapterImpl(Context context) {
			super(context, true);
			this.context = context;
		}

		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            Context applicationContext = context.getApplicationContext();
            Log.d(TAG, "onPerformSync account="+account+" "+ applicationContext);
            InjectorProvider injectorProvider = (InjectorProvider) applicationContext;

            injectorProvider.getInjector().getInstance(SyncService.class).syncAll(syncResult);
		}
	}
}