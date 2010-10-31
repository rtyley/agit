package com.madgag.agit;

import static android.R.drawable.stat_notify_error;
import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static org.eclipse.jgit.lib.Constants.HEAD;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
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
import org.eclipse.jgit.transport.URIish;

import android.app.Notification;
import android.util.Log;
import android.widget.RemoteViews;

import com.jcraft.jsch.JSchException;

public class Cloner extends GitOperation {
	
	public static final String TAG = "Cloner";
	
	private final URIish sourceUri;
	private final File gitdir;

	private MessagingProgressMonitor progressMonitor;
	
	private Repository db;


	Cloner(URIish sourceUri, File gitdir, RepositoryOperationContext operationContext) {
		super(operationContext);
        this.sourceUri = sourceUri;
		this.gitdir = gitdir;
		Log.i(TAG, "Constructed with "+sourceUri+" gitdir="+gitdir);
		progressMonitor = new MessagingProgressMonitor(this);
    }
	
	
	Notification createOngoingNotification() {
		Notification n = createNotificationWith(stat_sys_download,"Clonin","Cloning "+sourceUri, "Like a horse");
		n.contentView = cloneProgressNotificationView();
		return n;
	}

	private RemoteViews cloneProgressNotificationView() {
		RemoteViews v=remoteViewWithLayout(R.layout.fetch_progress);
		v.setTextViewText(R.id.status_text, "Cloning: "+sourceUri);
		v.setProgressBar(R.id.status_progress,1,0,true);
		return v;
	}

	@Override
	protected Notification doInBackground(Void... arg0) {
		File gitDirParentFolder = gitdir.getParentFile();
		Log.i(TAG, "Starting doInBackground... will ensure parent of gitdir exists. gitdir="+gitdir);
		if (!gitDirParentFolder.exists()) {
			Log.d(TAG, "Parent folder "+gitDirParentFolder+" needs to be created...");
			boolean created=gitDirParentFolder.mkdirs();
			Log.d(TAG, "mkdirs 'created' returned : "+created+" and gitDirParentFolder.exists()="+gitDirParentFolder.exists());
		}
		
		
		String remoteName = Constants.DEFAULT_REMOTE_NAME;

		try {
    		db = new FileRepository(gitdir);
    		Log.i(TAG, "about to execute create() on "+db);
    		db.create();
    		Log.i(TAG, "Created FileRepository "+db);
    		RemoteConfig rc;
    		try {
    			rc = new RemoteConfig(db.getConfig(), remoteName);
    		} catch (URISyntaxException e2) {
    			throw new RuntimeException(e2);
    		}
    		//dst.getConfig().setBoolean("core", null, "bare", false);
    		//dst.getConfig().save();
    		
    		rc.addURI(sourceUri);
    		Log.i(TAG, "GAMMA");
    		rc.addFetchRefSpec(new RefSpec().setForceUpdate(true)
    				.setSourceDestination(R_HEADS + "*", R_REMOTES + remoteName + "/*"));
    		rc.update(db.getConfig());
    		Log.i(TAG, "About to save config...");
    		db.getConfig().save();
    		Log.i(TAG, "About to run fetch : "+db.getDirectory());
			final FetchResult r = runFetch(rc);
			Log.i(TAG, "Finished fetch "+r);
			final Ref branch = guessHEAD(r);
			publishProgress(new Progress("Performing checkout"));
			doCheckout(branch);
			Log.i(TAG, "Completed checkout, thread done");
			//notificationManager.cancel(notificationId); // It seems 'On-going' notifications can't be converted to ordinary ones.
			return createCompletionNotification();
		} catch (TransportException e1) {
			Log.e(TAG, "TransportException ",e1);
			String message=e1.getMessage();
			Throwable cause=e1.getCause();
			if (cause!=null && cause instanceof JSchException) {
				message=((JSchException) cause).getMessage();
			}
			return createNotificationWith(
	    			stat_notify_error,
	    			"Clone failed",
	    			message,
	    			sourceUri.toString());
		} catch (IOException e) {
			Log.e(TAG, "IOException ",e);
			return createNotificationWith(
	    			stat_notify_error,
	    			"Clone failed",
	    			e.getMessage(),
	    			sourceUri.toString());
		}
    }
	
	@Override
	Notification createCompletionNotification() {
    	return createNotificationWith(
    			stat_sys_download_done,
    			"Cloned "+sourceUri.getHumanishName(),
    			"Clone completed",
    			sourceUri.toString());
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
			RefUpdate u = db.updateRef(HEAD);
			u.disableRefLog();
			u.link(branch.getName());
		}

		final RevCommit commit = parseCommit(branch);
		final RefUpdate u = db.updateRef(HEAD);
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

}
