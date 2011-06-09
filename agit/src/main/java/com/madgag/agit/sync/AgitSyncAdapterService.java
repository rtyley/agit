package com.madgag.agit.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.Repos;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitOperationExecutor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import roboguice.inject.InjectorProvider;

import java.io.File;
import java.io.IOException;

import static com.madgag.agit.Repos.knownRepos;
import static com.madgag.agit.Repos.remoteConfigFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;
import static org.eclipse.jgit.lib.RepositoryCache.close;

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
            GitOperationExecutor operationExecutor= injectorProvider.getInjector().getInstance(GitOperationExecutor.class);

            for (final File gitdir : knownRepos()) {
                Repository repository = null;
                try {
                    repository = Repos.openRepoFor(gitdir);
                    RemoteConfig remoteConfig = remoteConfigFor(repository, DEFAULT_REMOTE_NAME);
                    Fetch fetch = new Fetch(repository, remoteConfig);
                    operationExecutor.call(fetch, new ProgressListener<Progress>() {
                        public void publish(Progress... values) {
                            Log.d(TAG, gitdir+" "+asList(values));
                        }
                    });
                    syncResult.stats.numUpdates++;
                } catch (RuntimeException e) {
                    Log.w(TAG,"Problem with "+gitdir,e);
                } finally {
                    if (repository!=null)
                        close(repository);
                }
            }
		}
	}
}