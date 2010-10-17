package com.madgag.agit;

import static android.widget.Toast.LENGTH_LONG;
import static com.google.common.collect.Sets.newHashSet;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.common.collect.Sets;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class GitOperationsService extends Service {

	public static final String TAG = "GitIntentService";
	private Map<File,RepositoryOperationContext> map=new HashMap<File,RepositoryOperationContext>();
	
    public class GitOperationsBinder extends Binder {
    	GitOperationsService getService() {
            return GitOperationsService.this;
        }
    }
  
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new GitOperationsBinder();
	private NotificationManager notificationManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public NotificationManager getNotificationManager() {
		return notificationManager;
	}
	
    AndroidAuthAgent authAgent;
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent("com.madgag.android.ssh.BIND_SSH_AGENT_SERVICE"), new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected - losing "+authAgent);
				authAgent=null;
			}
			
			@SuppressWarnings("unchecked")
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.i(TAG, "onServiceConnected... got "+binder);
				authAgent=AndroidAuthAgent.Stub.asInterface(binder);
				Log.i(TAG, "bound "+authAgent);
				Map<String, byte[]> sendIdentities;
				try {
					sendIdentities = authAgent.getIdentities();
					Log.d(TAG, "here are identities "+sendIdentities);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, BIND_AUTO_CREATE);
        Log.i(TAG, "Asked for my SSH_AGENT_SERVICE ");
		
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	if (intent==null) {
    		return START_STICKY;
    	}
		
		String remote=Constants.DEFAULT_REMOTE_NAME;
		
		String action = intent.getAction();
		String gitdirString = intent.getStringExtra("gitdir");
		Log.i(TAG, "Got action "+action+" "+gitdirString);
		File gitdir=new File(gitdirString);
		RepositoryOperationContext repositoryOperationContext=getOrCreateRepositoryOperationContextFor(gitdir);
		if (action.equals("git.CLONE")) {
			String sourceUriString = intent.getStringExtra("source-uri");
			try {
				URIish sourceUri=new URIish(sourceUriString);
				repositoryOperationContext.enqueue(new Cloner(sourceUri, gitdir, repositoryOperationContext));
			} catch (URISyntaxException e) {
				Toast.makeText(this, "Invalid uri "+sourceUriString, LENGTH_LONG);
			}
		} else if (action.equals("git.FETCH")) {
			Log.i(TAG, "gitdir is "+gitdir.getAbsolutePath());
			Repository repository = repositoryOperationContext.getRepository();
			try {
				repositoryOperationContext.enqueue(new Fetcher(new RemoteConfig(repository.getConfig(), remote), repositoryOperationContext));
			} catch (URISyntaxException e) {
				e.printStackTrace();
				Toast.makeText(this, "Bad config "+e, LENGTH_LONG).show();
			}
		} else {
			Log.e(TAG, "Why not is");
		}
		
		
		//repositoryOperationContext.setCurrentOperation(fetchThread);

		return START_STICKY;
    }

    public Set<RepositoryOperationContext> getRepositoryOperationContextsFor(URIish remote) {
    	Set<RepositoryOperationContext> rocs = newHashSet();
    	for (RepositoryOperationContext roc : map.values()) {
    		Repository repo = roc.getRepository();
			if (repo!=null) {
				List<RemoteConfig> allRemoteConfigs;
				try {
					allRemoteConfigs = RemoteConfig.getAllRemoteConfigs(repo.getConfig());
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				for (RemoteConfig rc : allRemoteConfigs) {
					if (rc.getURIs().contains(remote)) {
						rocs.add(roc);
					}
				}
			}
    	}
    	return rocs;
    }

	public RepositoryOperationContext getOrCreateRepositoryOperationContextFor(Repository db) {
		return getOrCreateRepositoryOperationContextFor(db.getDirectory());
	}
    
    RepositoryOperationContext getOrCreateRepositoryOperationContextFor(File gitdir) {
    	if (!map.containsKey(gitdir)) {
				map.put(gitdir, new RepositoryOperationContext(fileRepoFor(gitdir),this));
			
    	}
    	return map.get(gitdir);
    }

	private FileRepository fileRepoFor(File gitdir) {
//		Log.i(TAG, "about to hand over "+this);
//		AndroidFS androidFS = new AndroidFS(this);
//		Log.i(TAG, "androidFS="+androidFS);
		try {
			FileRepository fileRepo = new FileRepository(
					new FileRepositoryBuilder()
						.setGitDir(gitdir)
						// .setFS(androidFS)
						.setup());
			return fileRepo;
		} catch (IOException e) {
			Log.i(TAG, "whoop arg "+e);
			throw new RuntimeException();
		}
	}
    
    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
//            if (bundle.containsKey("total")) {
//            	progressDialog.setMax(bundle.getInt("total"));
//            	progressDialog.setMessage(bundle.getString("title"));
//            }
//            if (bundle.containsKey("completed")) {
//            	progressDialog.setProgress(bundle.getInt("completed"));
//            }
        }
    };

}