package com.madgag.agit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Commit;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.GitIndex;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefComparator;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.lib.WorkDirCheckout;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

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
    };
    
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
       
        FetchThread(Repository db, String remote, Handler h) {
            this.db = db;
			this.remote = remote;
			this.promptHelper=new PromptHelper(db);
			progressMonitor = new MessagingProgressMonitor(GitOperationsService.this);
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

    		final Commit commit = db.mapCommit(branch.getObjectId());
    		final RefUpdate u = db.updateRef(Constants.HEAD);
    		u.setNewObjectId(commit.getCommitId());
    		u.forceUpdate();

    		final GitIndex index = new GitIndex(db);
    		final Tree tree = commit.getTree();
    		final WorkDirCheckout co;

    		co = new WorkDirCheckout(db, db.getWorkTree(), index, tree);
    		co.checkout();
    		index.write();
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