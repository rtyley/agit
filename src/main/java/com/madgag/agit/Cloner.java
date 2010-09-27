package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.madgag.agit.RepositoryManagementActivity.manageGitRepo;
import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.URIish;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class Cloner extends AsyncTask<Void, Progress, Void> implements ProgressListener<Progress> {
	
	public static final String TAG = "Cloner";
	
	private final URIish sourceUri;
	private final File gitdir;
	private final Service service;
	private final Context context;
	private Repository db;

	private final RepositoryOperationContext operationContext;
	private MessagingProgressMonitor progressMonitor;

	private NotificationManager notificationManager;

	private Notification notification;

	Cloner(URIish sourceUri, File gitdir, Service service, RepositoryOperationContext operationContext) {
        this.sourceUri = sourceUri;
		this.gitdir = gitdir;
		this.service = service;
		this.context = service;
		this.operationContext = operationContext;
		progressMonitor = new MessagingProgressMonitor(this);
		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		notification = ongoingCloneNotification(sourceUri);
    }
	
	private Notification ongoingCloneNotification(URIish sourceUri) {
		Notification n = new Notification(R.drawable.diff_changetype_rename, "Clonin", currentTimeMillis());
		n.flags = n.flags | FLAG_ONGOING_EVENT;
		n.setLatestEventInfo(context, "Cloning "+sourceUri, "Like a horse", manageGitRepo(gitdir,context));
		n.contentView=fetchProgressNotificationRemoteView();
		return n;
	}
	

    private RemoteViews fetchProgressNotificationRemoteView() {
		RemoteViews remoteView=new RemoteViews(context.getApplicationContext().getPackageName(), R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress, 512, 128, true);
		return remoteView;
    }
	
    @Override
    protected void onPreExecute() {
    	service.startForeground(operationContext.fetchOngoingId, notification);
    }
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
    		FileRepository dst = new FileRepository(gitdir);
    		dst.create();
    		//dst.getConfig().setBoolean("core", null, "bare", false);
    		//dst.getConfig().save();
    		db = dst;
    		
    		String remoteName = Constants.DEFAULT_REMOTE_NAME;
    		
    		final RemoteConfig rc = new RemoteConfig(dst.getConfig(), remoteName);
    		rc.addURI(sourceUri);
    		rc.addFetchRefSpec(new RefSpec().setForceUpdate(true)
    				.setSourceDestination(Constants.R_HEADS + "*",
    						Constants.R_REMOTES + remoteName + "/*"));
    		rc.update(dst.getConfig());
    		dst.getConfig().save();
			final FetchResult r = runFetch();
			Log.i(TAG, "Finished fetch "+r);
			final Ref branch = guessHEAD(r);
			doCheckout(branch);
			Log.i(TAG, "Completed checkout, thread done");
			//notificationManager.cancel(notificationId); // It seems 'On-going' notifications can't be converted to ordinary ones.
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
    }
	
	@Override
	protected void onPostExecute(Void v) {
		service.stopForeground(true); // Actually, we only want to call this if ALL threads are completed, I think...
		notifyCloneComplete();
	}

	private void notifyCloneComplete() {
		// The user is not interested in old fetch Notifications if we've done a new one
		Notification completedNotification=new Notification(R.drawable.diff_changetype_modify, "Clone complete", currentTimeMillis());
		completedNotification.setLatestEventInfo(context, "Clone "+sourceUri, "Completed", manageGitRepo(gitdir,context));
		completedNotification.flags |= FLAG_AUTO_CANCEL;
		notificationManager.notify(operationContext.fetchCompletionId, completedNotification);
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
//		if (branch == null)
//			throw die(CLIText.get().cannotChekoutNoHeadsAdvertisedByRemote);
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
		PromptHelper promptHelper=null;
		String remoteName = Constants.DEFAULT_REMOTE_NAME;
		SshSessionFactory.setInstance(new AndroidSshSessionFactory(promptHelper));
		final Transport tn = Transport.open(db, remoteName);
		final FetchResult r;
		try {
			r = tn.fetch(progressMonitor, null);
		} finally {
			tn.close();
		}
		// showFetchResult(tn, r);
		Log.i(TAG, "Finished fetch "+r);
		return r;
	}
	
	@Override
	protected void onProgressUpdate(Progress... values) {
		Progress p=values[values.length-1];
		Log.i(TAG, "Got prog "+p);
	}

	public void publish(Progress... values) {
		publishProgress(values);
	}
}
