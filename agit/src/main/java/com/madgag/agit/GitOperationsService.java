package com.madgag.agit;

import static android.widget.Toast.LENGTH_LONG;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.Repos.openRepoFor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.madgag.agit.operations.Clone;
import com.madgag.agit.operations.Fetch;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class GitOperationsService extends Service implements AndroidAuthAgentProvider {

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
		
        bindSshAgent();
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	if (intent==null) {
    		return START_NOT_STICKY;
    	}
		
		String action = intent.getAction();
		Log.i(TAG, "Got action "+action);
		
		File gitdir = GitIntents.gitDirFrom(intent);
		
		RepositoryOperationContext repositoryOperationContext=getOrCreateRepositoryOperationContextFor(gitDirFrom(intent));
		if (action.equals("git.CLONE")) {
			String sourceUriString = intent.getStringExtra("source-uri");
			try {
				URIish sourceUri=new URIish(sourceUriString);
				repositoryOperationContext.enqueue(new Clone(sourceUri, gitdir));
			} catch (URISyntaxException e) {
				Toast.makeText(this, "Invalid uri "+sourceUriString, LENGTH_LONG);
			}
		} else if (action.equals("git.FETCH")) {
			String remote=Constants.DEFAULT_REMOTE_NAME;
			try {
				repositoryOperationContext.enqueue(new Fetch(openRepoFor(gitdir), remote));
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Toast.makeText(this, "Bad config "+e, LENGTH_LONG).show();
			}
		} else {
			Log.e(TAG, "Why not is");
		}

		return START_STICKY;
	}


    private AndroidAuthAgent authAgent;

	public AndroidAuthAgent getAuthAgent() {
		return authAgent;
	}
	
	private void bindSshAgent() {
		bindService(new Intent("org.openintents.ssh.BIND_SSH_AGENT_SERVICE"), new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected - losing "+authAgent);
				authAgent=null;
			}
			
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.i(TAG, "onServiceConnected... got "+binder);
				authAgent=AndroidAuthAgent.Stub.asInterface(binder);
				Log.i(TAG, "bound "+authAgent);
				try {
					Log.d(TAG, "here are identities "+authAgent.getIdentities());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, BIND_AUTO_CREATE);
        Log.i(TAG, "Asked for my SSH_AGENT_SERVICE ");
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