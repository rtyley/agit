package com.madgag.agit.operations;

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
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.GitIndex;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefComparator;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.lib.WorkDirCheckout;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import android.util.Log;

import com.madgag.agit.GitOperation;
import com.madgag.agit.OpNotification;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;

public class Cloner implements GitOperation {

	public static final String TAG = "Cloner";

	private final URIish sourceUri;
	private final File gitdir;

	public Cloner(URIish sourceUri, File gitdir) {
		this.sourceUri = sourceUri;
		this.gitdir = gitdir;
		Log.i(TAG, "Constructed with " + sourceUri + " gitdir=" + gitdir);
	}

	public OpNotification execute(
			RepositoryOperationContext repositoryOperationContext,
			ProgressListener<Progress> progressListener) {
		File gitDirParentFolder = gitdir.getParentFile();
		Log.i(TAG,
				"Starting doInBackground... will ensure parent of gitdir exists. gitdir="
						+ gitdir);
		if (!gitDirParentFolder.exists()) {
			Log.d(TAG, "Parent folder " + gitDirParentFolder
					+ " needs to be created...");
			boolean created = gitDirParentFolder.mkdirs();
			Log.d(TAG, "mkdirs 'created' returned : " + created
					+ " and gitDirParentFolder.exists()="
					+ gitDirParentFolder.exists());
		}

		String remoteName = Constants.DEFAULT_REMOTE_NAME;

		try {
			Repository db = new FileRepository(gitdir);
			Log.d(TAG, "about to execute create() on " + db);
			db.create();
			Log.d(TAG, "Created FileRepository " + db);
			RemoteConfig remote = createRemote(remoteName,db.getConfig());
			
			Log.d(TAG, "About to save config...");
			db.getConfig().save();
			Log.d(TAG, "About to run fetch : " + db.getDirectory());
			FetchResult r = repositoryOperationContext.fetch(remote,progressListener);
			Ref branch = guessHEAD(r);
			progressListener.publish(new Progress("Performing checkout"));
			doCheckout(db, branch);
			Log.d(TAG, "Completed checkout!");
		} catch (IOException e) {
			Log.e(TAG, "An actual IO exception", e);
			throw new RuntimeException(e);
		}

		return new OpNotification(stat_sys_download_done, "Cloned "
				+ sourceUri.getHumanishName(), "Clone completed", sourceUri
				.toString());
	}

	private RemoteConfig createRemote(String remoteName, StoredConfig config) {
		RemoteConfig remote;
		try {
			remote = new RemoteConfig(config, remoteName);
		} catch (URISyntaxException e2) {
			throw new RuntimeException(e2);
		}
		// dst.getConfig().setBoolean("core", null, "bare", false);
		// dst.getConfig().save();

		remote.addURI(sourceUri);
		Log.i(TAG, "GAMMA");
		remote.addFetchRefSpec(new RefSpec().setForceUpdate(true)
				.setSourceDestination(R_HEADS + "*",
						R_REMOTES + remoteName + "/*"));
		remote.update(config);
		return remote;
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

	private void doCheckout(Repository db, final Ref branch) throws IOException {
		// if (branch == null)
		// throw die(CLIText.get().cannotChekoutNoHeadsAdvertisedByRemote);
		if (!Constants.HEAD.equals(branch.getName())) {
			RefUpdate u = db.updateRef(HEAD);
			u.disableRefLog();
			u.link(branch.getName());
		}

		final RevCommit commit = parseCommit(db,branch);
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

	private RevCommit parseCommit(Repository db, Ref branch)
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

	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public String getTickerText() {
		return "Cloning " + sourceUri;
	}

	public String getName() {
		return "Clone";
	}

	public String getDescription() {
		return "cloning " + sourceUri;
	}

	public CharSequence getUrl() {
		return sourceUri.toString();
	}

	public String getShortDescription() {
		return "Cloning";
	}
}
