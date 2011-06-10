package com.madgag.agit.operations;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static org.eclipse.jgit.lib.Constants.*;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import static org.eclipse.jgit.lib.RepositoryCache.close;

public class Clone implements GitOperation {

	public static final String TAG = "Clone";
    public static final String GIT_REPO_INITIALISED_INTENT = "org.openintents.git.repo.initialised";

    private final boolean bare;
	private final URIish sourceUri;
	private final File directory, gitdir;
    private String branch = Constants.HEAD;

    @Inject ProgressListener<Progress> progressListener;
    @Inject GitFetchService fetchService;
    @Inject Context context;

    public Clone(boolean bare, URIish sourceUri, File directory) {
		this.bare = bare;
		this.sourceUri = sourceUri;
		this.directory = directory;
		gitdir = bare ? directory : new File(directory, DOT_GIT);

		Log.d(TAG, "Constructed with " + sourceUri + " directory=" + directory
				+ " gitdir=" + gitdir);
	}

	public OpNotification execute() {
		Log.d(TAG, "Starting execute... directory=" + directory);
		ensureFolderExists(directory.getParentFile());

		String remoteName = DEFAULT_REMOTE_NAME;

		try {
			Git.init().setBare(bare).setDirectory(directory).call();
			Repository repository = new FileRepository(gitdir);
			RemoteConfig remote = addRemote(remoteName, repository);

            context.sendBroadcast(new Intent(GIT_REPO_INITIALISED_INTENT));

			FetchResult fetchResult = fetchService.fetch(remote, null);

			if (!bare) {
				checkoutHeadFrom(fetchResult, repository);
			}

			close(repository); // do with a guice scope?
			Log.d(TAG, "Completed checkout!");
		} catch (Exception e) {
			Log.e(TAG, "An actual exception", e);
			throw new RuntimeException(e);
		}

		return new OpNotification(stat_sys_download_done, "Cloned "
				+ sourceUri.getHumanishName(), "Clone completed",
				sourceUri.toString());
	}

	private RemoteConfig addRemote(String remoteName, Repository repository)
			throws IOException {
		RemoteConfig remote = createRemote(remoteName, repository.getConfig());

		Log.d(TAG, "About to save config...");
		repository.getConfig().save();
		return remote;
	}

	private void checkoutHeadFrom(FetchResult fetchResult, Repository db)
			throws RefAlreadyExistsException, RefNotFoundException,
			InvalidRefNameException, IOException {

		Ref branch = guessHEAD(fetchResult);
		String branchName = branch.getName();
		Log.d(TAG, "Guessed head branchName=" + branchName);
		progressListener.publish(new Progress("Performing checkout of "+  shortenRefName(branchName)));


        checkout(db, fetchResult);
	}

    private void checkout(Repository repo, FetchResult result)
			throws JGitInternalException,
			MissingObjectException, IncorrectObjectTypeException, IOException {

		if (branch.startsWith(Constants.R_HEADS)) {
			final RefUpdate head = repo.updateRef(Constants.HEAD);
			head.disableRefLog();
			head.link(branch);
		}

		final Ref head = result.getAdvertisedRef(branch);
		if (head == null || head.getObjectId() == null)
			return; // throw exception?

		final RevCommit commit = parseCommit(repo, head);

		boolean detached = !head.getName().startsWith(Constants.R_HEADS);
		RefUpdate u = repo.updateRef(Constants.HEAD, detached);
		u.setNewObjectId(commit.getId());
		u.forceUpdate();

		if (!bare) {
			DirCache dc = repo.lockDirCache();
			DirCacheCheckout co = new DirCacheCheckout(repo, dc,
					commit.getTree());
			co.checkout();
		}
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

	private static void ensureFolderExists(File folder) {
		if (!folder.exists()) {
			Log.d(TAG, "Folder " + folder + " needs to be created...");
			boolean created = folder.mkdirs();
			Log.d(TAG, "mkdirs 'created' returned : " + created
					+ " and gitDirParentFolder.exists()=" + folder.exists());
		}
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

	public File getGitDir() {
		return gitdir;
	}
}
