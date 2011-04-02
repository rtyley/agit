package com.madgag.agit;

import static android.widget.Toast.LENGTH_LONG;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.Repos.openRepoFor;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;

import roboguice.service.RoboService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.madgag.agit.operations.Clone;
import com.madgag.agit.operations.Fetch;
import com.madgag.agit.operations.GitOperation;

public class GitOperationsService extends RoboService {

	public static final String TAG = "GitIntentService";
	private Map<File,RepositoryOperationContext> map=new HashMap<File,RepositoryOperationContext>();

	public static Intent cloneOperationIntentFor(URIish uri, File gitdir) {
		Intent intent = new Intent("git.CLONE");
		intent.putExtra("source-uri", uri.toPrivateString());
		addGitDirTo(intent, gitdir);
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
    
    public NotificationManager getNotificationManager() {
		return notificationManager;
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
		Log.i(TAG, "onStartCommand "+intent);
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	if (intent==null) {
    		return START_NOT_STICKY;
    	}
		
		String action = intent.getAction();
		Log.i(TAG, "Got action "+action);
		
		File gitdir = GitIntents.gitDirFrom(intent);
		
		RepositoryOperationContext repositoryOperationContext=getOrCreateRepositoryOperationContextFor(gitDirFrom(intent));
		GitOperation operation = null;
		if (action.equals("git.CLONE")) {
			String sourceUriString = intent.getStringExtra("source-uri");
			try {
				operation = new Clone(false, new URIish(sourceUriString), gitdir);
			} catch (URISyntaxException e) {
				Toast.makeText(this, "Invalid uri "+sourceUriString, LENGTH_LONG).show();
				return START_NOT_STICKY;
			}
		} else if (action.equals("git.FETCH")) {
			//operation = new Fetch(openRepoFor(gitdir), DEFAULT_REMOTE_NAME);
		} else {
			Log.e(TAG, "What is "+action);
			return START_NOT_STICKY;
		}
		repositoryOperationContext.enqueue(operation);
		
		return START_STICKY;
	}



	public RepositoryOperationContext getOrCreateRepositoryOperationContextFor(File gitdir) {
    	if (!map.containsKey(gitdir)) {
			map.put(gitdir, new RepositoryOperationContext(gitdir,this));
    	}
    	return map.get(gitdir);
    }

	public RepositoryOperationContext registerManagementActivity(RepositoryManagementActivity repositoryManagementActivity) {
		RepositoryOperationContext operationContext = getOrCreateRepositoryOperationContextFor(repositoryManagementActivity.gitdir());
		operationContext.setManagementActivity(repositoryManagementActivity);
		return operationContext;
	}
}