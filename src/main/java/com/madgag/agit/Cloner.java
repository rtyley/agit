package com.madgag.agit;

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
import android.util.Log;
import android.widget.RemoteViews;

public class Cloner extends GitOperation {
	
	public static final String TAG = "Cloner";
	
	private final URIish sourceUri;

	private MessagingProgressMonitor progressMonitor;
	
	private Repository db;

	private final File gitdir;

	Cloner(URIish sourceUri, File gitdir, RepositoryOperationContext operationContext) {
		super(operationContext);
        this.sourceUri = sourceUri;
		this.gitdir = gitdir;
		progressMonitor = new MessagingProgressMonitor(this);
    }
	
	@Override
	Notification createOngoingNotification() {
		Notification n = new Notification(android.R.drawable.stat_sys_download,"Clonin", currentTimeMillis());
		n.setLatestEventInfo(repositoryOperationContext.getService(), "Cloning "+sourceUri, "Like a horse", repositoryOperationContext.manageGitRepo);
		
		n.contentView=fetchProgressNotificationRemoteView();
		n.contentView.setTextViewText(R.id.status_text, "Cloning: "+sourceUri);
		return n;
	}
	
    private RemoteViews fetchProgressNotificationRemoteView() {
		RemoteViews remoteView=new RemoteViews(repositoryOperationContext.getService().getApplicationContext().getPackageName(), R.layout.fetch_progress);
		remoteView.setProgressBar(R.id.status_progress,1,0,true);
		return remoteView;
    }
	
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
    		db = new FileRepository(gitdir);
    		db.create();
    		//dst.getConfig().setBoolean("core", null, "bare", false);
    		//dst.getConfig().save();
    		
    		String remoteName = Constants.DEFAULT_REMOTE_NAME;
    		
    		final RemoteConfig rc = new RemoteConfig(db.getConfig(), remoteName);
    		rc.addURI(sourceUri);
    		rc.addFetchRefSpec(new RefSpec().setForceUpdate(true)
    				.setSourceDestination(Constants.R_HEADS + "*",
    						Constants.R_REMOTES + remoteName + "/*"));
    		rc.update(db.getConfig());
    		db.getConfig().save();
			final FetchResult r = runFetch();
			Log.i(TAG, "Finished fetch "+r);
			final Ref branch = guessHEAD(r);
			publishProgress(new Progress("Performing checkout"));
			doCheckout(branch);
			Log.i(TAG, "Completed checkout, thread done");
			//notificationManager.cancel(notificationId); // It seems 'On-going' notifications can't be converted to ordinary ones.
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
    }
	
	@Override
	Notification createCompletionNotification() {
		Notification completedNotification=new Notification(android.R.drawable.stat_sys_download_done, "Cloned "+sourceUri.getHumanishName(), currentTimeMillis());
		completedNotification.setLatestEventInfo(repositoryOperationContext.getService(), "Clone completed", sourceUri.toString(), repositoryOperationContext.manageGitRepo);
		return completedNotification;
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
		SshSessionFactory.setInstance(new AndroidSshSessionFactory(repositoryOperationContext, promptHelper));
		String remoteName = Constants.DEFAULT_REMOTE_NAME;
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
	


}
