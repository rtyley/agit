package com.madgag.agit;

import static android.widget.Toast.LENGTH_LONG;
import static com.madgag.agit.GitIntents.addDirectoryTo;
import static com.madgag.agit.GitIntents.directoryFrom;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import roboguice.service.RoboService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;
import com.madgag.agit.operation.lifecycle.LongRunningServiceLifetime;
import com.madgag.agit.operation.lifecycle.RepoNotifications;
import com.madgag.agit.operations.Clone;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;

public class GitOperationsService extends RoboService {

	public static final String TAG = "GitIntentService";
	
	@Inject GitAsyncTaskFactory asyncTaskFactory;
	private Map<File,RepositoryOperationContext> map=new HashMap<File,RepositoryOperationContext>();

	public static Intent cloneOperationIntentFor(URIish uri, File directory) {
		Intent intent = new Intent("git.CLONE");
		intent.putExtra("source-uri", uri.toPrivateString());
		addDirectoryTo(intent, directory);
		return intent;
	}
	
    public class GitOperationsBinder extends Binder {
    	GitOperationsService getService() {
            return GitOperationsService.this;
        }
    }
  
    private final IBinder mBinder = new GitOperationsBinder();
    
	private NotificationManager notificationManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
    	handleMethod(intent);
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return handleMethod(intent);
    }

	private int handleMethod(Intent intent) {
		Log.i(TAG, "handleMethod "+intent);
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	if (intent==null) {
    		return START_NOT_STICKY;
    	}
		
		String action = intent.getAction();
		Log.i(TAG, "Got action "+action);
		
		
		
		GitOperation operation = null;
		if (action.equals("git.CLONE")) {
			String sourceUriString = intent.getStringExtra("source-uri");
			try {
				operation = new Clone(false, new URIish(sourceUriString), directoryFrom(intent));
			} catch (URISyntaxException e) {
				Toast.makeText(this, "Invalid uri "+sourceUriString, LENGTH_LONG).show();
				return START_NOT_STICKY;
			}
		} else if (action.equals("git.FETCH")) {
			File gitdir = gitDirFrom(intent);
			
			String remoteName = DEFAULT_REMOTE_NAME;
            operation = new Fetch(gitDirFrom(intent), remoteConfigFor(gitdir, remoteName));
		} else {
			Log.e(TAG, "What is "+action);
			return START_NOT_STICKY;
		}
		
		LongRunningServiceLifetime lifecycleSupport = new LongRunningServiceLifetime(new RepoNotifications(this,operation.getGitDir()), this);
		GitAsyncTask task = asyncTaskFactory.createTaskFor(operation, lifecycleSupport);
		// repositoryOperationContext.enqueue(operation);
		task.execute();
		return START_STICKY;
	}

	private RemoteConfig remoteConfigFor(File gitdir, String remoteName) {
		try {
			Repository repository = new FileRepository(gitdir);
			return new RemoteConfig(repository.getConfig(), remoteName);
		} catch (Exception e) {
			Log.e(TAG, "Couldn't parse config", e);
			throw new RuntimeException(e);
		}
	}

	public RepositoryOperationContext registerManagementActivity(RepositoryManagementActivity repositoryManagementActivity) {
//		RepositoryOperationContext operationContext = getOrCreateRepositoryOperationContextFor(repositoryManagementActivity.gitdir());
//		operationContext.setManagementActivity(repositoryManagementActivity);
		return null;
	}
}