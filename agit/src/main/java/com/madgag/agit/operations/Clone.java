package com.madgag.agit.operations;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import com.madgag.agit.git.Repos;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefComparator;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static org.eclipse.jgit.api.Git.cloneRepository;
import static org.eclipse.jgit.lib.Constants.*;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import static org.eclipse.jgit.lib.RepositoryCache.close;

public class Clone extends GitOperation {

	public static final String TAG = "Clone";
    
    private final boolean bare;
	private final URIish sourceUri;
	private final File directory;
    private String branch = HEAD;

    @Inject ProgressListener<Progress> progressListener;
    @Inject Context context;
    @Inject RepoUpdateBroadcaster repoUpdateBroadcaster;
	@Inject MessagingProgressMonitor messagingProgressMonitor;
	@Inject CredentialsProvider credentialsProvider;
	@Inject TransportConfigCallback transportConfigCallback;

    public Clone(boolean bare, URIish sourceUri, File directory) {
        super(bare ? directory : new File(directory, DOT_GIT));
		this.bare = bare;
		this.sourceUri = sourceUri;
		this.directory = directory;

		Log.d(TAG, "Constructed with " + sourceUri + " directory=" + directory
				+ " gitdir=" + gitdir);
	}

	public OpNotification execute() throws Exception {
		Log.d(TAG, "Starting execute... directory=" + directory);
		ensureFolderExists(directory.getParentFile());

		cloneRepository()
				.setBare(bare)
				.setDirectory(directory)
				.setURI(sourceUri.toPrivateString())
				.setCredentialsProvider(credentialsProvider)
				.setProgressMonitor(messagingProgressMonitor)
				.call();

        repoUpdateBroadcaster.broadcastUpdate();

        Log.d(TAG, "Completed checkout!");

		return new OpNotification(stat_sys_download_done, "Cloned "
				+ sourceUri.getHumanishName(), "Clone completed",
				sourceUri.toString());
	}

	private static void ensureFolderExists(File folder) {
		if (!folder.exists()) {
			Log.d(TAG, "Folder " + folder + " needs to be created...");
			boolean created = folder.mkdirs();
			Log.d(TAG, "mkdirs 'created' returned : " + created
					+ " and gitDirParentFolder.exists()=" + folder.exists());
		}
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

    public String toString() {
        return getClass().getSimpleName()+"["+sourceUri+"]";
    }
}
