package com.madgag.agit;

import static android.app.Notification.*;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.GitIndex;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefComparator;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.lib.WorkDirCheckout;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

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
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	if (intent==null || intent.getData()==null) {
    		return START_STICKY;
    	}
    	Uri data = intent.getData();
		File gitdir=new File(data.getPath());
    	Log.i(TAG, "gitdir is "+gitdir.getAbsolutePath());
    	
		RepositoryOperationContext repositoryOperationContext=getOrCreateRepositoryOperationContextFor(gitdir);
		
		String remote=Constants.DEFAULT_REMOTE_NAME;
		FetchThread fetchThread = new FetchThread(repositoryOperationContext.getRepository(), remote, handler);
		repositoryOperationContext.setCurrentOperation(fetchThread);
        fetchThread.start();

		return START_STICKY;
    }

	private Notification floomTrumpter(int notificationId, File gitdir, String remote) {
        int icon = R.drawable.diff_changetype_add;
        CharSequence tickerText = "Hello";
        long when = currentTimeMillis();
        Log.i("GOS", "Sent notification");
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = notification.flags | FLAG_ONGOING_EVENT;
		notification.setLatestEventInfo(this, "Fetching "+remote, "Like a horse", manageGitRepo(gitdir));
		notification.contentView=fetchProgressNotificationRemoteView();
		startForeground(notificationId, notification);
		return notification;
	}

	private PendingIntent manageGitRepo(File gitdir) {
		Intent intentForNotification = new Intent(ACTION_VIEW, Uri.fromFile(gitdir), this,RepositoryManagementActivity.class);
        intentForNotification.setFlags(FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(this, 0, intentForNotification, 0);
	};
    
    private RemoteViews fetchProgressNotificationRemoteView() {
		RemoteViews remoteView=new RemoteViews(getApplicationContext().getPackageName(), R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress, 512, 128, false);
		return remoteView;
    }
    
	public RepositoryOperationContext getOrCreateRepositoryOperationContextFor(Repository db) {
		return getOrCreateRepositoryOperationContextFor(db.getDirectory());
	}
    
    RepositoryOperationContext getOrCreateRepositoryOperationContextFor(File gitdir) {
    	if (!map.containsKey(gitdir)) {
    		try {
				map.put(gitdir, new RepositoryOperationContext(new FileRepository(gitdir)));
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

   
    class FetchThread extends Thread {
        
		private final Repository db;
		private final String remote;
		final MessagingProgressMonitor progressMonitor;
		public final PromptHelper promptHelper;
		public Notification notification;
		private final int notificationId;
		private NotificationManager notificationManager;
       
        FetchThread(Repository db, String remote, Handler h) {
            this.db = db;
			this.remote = remote;
			this.promptHelper=new PromptHelper(db);
			notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
			notificationId = hashCode();
			notification = floomTrumpter(notificationId, db.getDirectory(), remote);
			progressMonitor = new MessagingProgressMonitor(GitOperationsService.this, notificationId, notification, notificationManager);
        }
        
        CancellationSignaller getCancellationSignaller() {
        	return progressMonitor;
        }
       
        public void run() {
            try {
				final FetchResult r = runFetch();
				Log.i(TAG, "Finished fetch "+r);
				final Ref branch = guessHEAD(r);
				doCheckout(branch);
				Log.i(TAG, "Completed checkout, thread done");
				//notificationManager.cancel(notificationId); // It seems 'On-going' notifications can't be converted to ordinary ones.
				stopForeground(true);// Actually, we only want to call this if ALL threads are completed, I think...
				
				Notification completedNotification=new Notification(R.drawable.diff_changetype_modify, "Fetch complete", currentTimeMillis());
				completedNotification.setLatestEventInfo(GitOperationsService.this, "Fetched "+remote, "UTTERLY", manageGitRepo(db.getDirectory()));
				completedNotification.flags |= FLAG_AUTO_CANCEL;
				notificationManager.notify(notificationId+1, completedNotification);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        }
        

    	private Ref guessHEAD(final FetchResult result) {
    		final Ref idHEAD = result.getAdvertisedRef(Constants.HEAD);
    		final List<Ref> availableRefs = new ArrayList<Ref>();
    		Ref head = null;
    		for (final Ref r : result.getAdvertisedRefs()) {
    			final String n = r.getName();
    			if (!n.startsWith(Constants.R_HEADS))
    				continue;
    			availableRefs.add(r);
    			if (idHEAD == null || head != null)
    				continue;
    			if (r.getObjectId().equals(idHEAD.getObjectId()))
    				head = r;
    		}
    		Collections.sort(availableRefs, RefComparator.INSTANCE);
    		if (idHEAD != null && head == null)
    			head = idHEAD;
    		return head;
    	}
    	
    	private void doCheckout(final Ref branch) throws IOException {
//    		if (branch == null)
//    			throw die(CLIText.get().cannotChekoutNoHeadsAdvertisedByRemote);
    		if (!Constants.HEAD.equals(branch.getName())) {
    			RefUpdate u = db.updateRef(Constants.HEAD);
    			u.disableRefLog();
    			u.link(branch.getName());
    		}

    		final RevCommit commit = parseCommit(branch);
    		final RefUpdate u = db.updateRef(Constants.HEAD);
    		u.setNewObjectId(commit);
    		u.forceUpdate();

    		final GitIndex index = new GitIndex(db);
    		final Tree tree = db.mapTree(commit.getTree());
    		final WorkDirCheckout co;

    		co = new WorkDirCheckout(db, db.getWorkTree(), index, tree);
    		co.checkout();
    		index.write();
    	}
    	
    	private RevCommit parseCommit(final Ref branch)
    			throws MissingObjectException, IncorrectObjectTypeException,
    			IOException {
    		final RevWalk rw = new RevWalk(db);
    		final RevCommit commit;
    		try {
    			commit = rw.parseCommit(branch.getObjectId());
    		} finally {
    			rw.release();
    		}
    		return commit;
    	}
    	
    	
        
		private FetchResult runFetch() throws NotSupportedException, URISyntaxException, TransportException {
			SshSessionFactory.setInstance(new AndroidSshSessionFactory(promptHelper));
			final Transport tn = Transport.open(db, remote);
			final FetchResult r;
			try {
				r = tn.fetch(progressMonitor, null);
			} finally {
				tn.close();
			}
			// showFetchResult(tn, r);
			return r;
		}
    }




}