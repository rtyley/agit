package com.madgag.agit;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.widget.Toast.LENGTH_LONG;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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
				new Cloner(sourceUri, gitdir, repositoryOperationContext).execute();
			} catch (URISyntaxException e) {
				Toast.makeText(this, "Invalid uri "+sourceUriString, LENGTH_LONG);
			}
		} else if (action.equals("git.FETCH")) {
			Log.i(TAG, "gitdir is "+gitdir.getAbsolutePath());
			Repository repository = repositoryOperationContext.getRepository();
			try {
				new Fetcher(new RemoteConfig(repository.getConfig(), remote), repositoryOperationContext).execute();
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

    
	public RepositoryOperationContext getOrCreateRepositoryOperationContextFor(Repository db) {
		return getOrCreateRepositoryOperationContextFor(db.getDirectory());
	}
    
    RepositoryOperationContext getOrCreateRepositoryOperationContextFor(File gitdir) {
    	if (!map.containsKey(gitdir)) {
    		try {
				map.put(gitdir, new RepositoryOperationContext(new FileRepository(gitdir),this));
			} catch (IOException e) {
				throw new RuntimeException();
			}
    	}
    	return map.get(gitdir);
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