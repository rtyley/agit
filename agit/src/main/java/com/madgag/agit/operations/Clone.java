package com.madgag.agit.operations;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import com.madgag.android.listviews.pinnedheader.R;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import roboguice.inject.InjectResource;

import java.io.File;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static com.madgag.agit.R.string.cloned_repo;
import static com.madgag.agit.R.string.cloning;
import static com.madgag.agit.R.string.fetching;
import static com.madgag.agit.operations.JGitAPIExceptions.exceptionWithFriendlyMessageFor;
import static com.madgag.android.listviews.pinnedheader.R.string.clone;
import static com.madgag.android.listviews.pinnedheader.R.string.cloning_repo;
import static org.eclipse.jgit.api.Git.cloneRepository;
import static org.eclipse.jgit.lib.Constants.*;

public class Clone extends GitOperation {

	public static final String TAG = "Clone";
    
    private final boolean bare;
	private final URIish sourceUri;
	private final File directory;
    private String branch = HEAD;

    @Inject ProgressListener<Progress> progressListener;
    @Inject RepoUpdateBroadcaster repoUpdateBroadcaster;
	@Inject MessagingProgressMonitor messagingProgressMonitor;
	@Inject CredentialsProvider credentialsProvider;
	@Inject TransportConfigCallback transportConfigCallback;
	@InjectResource(clone) String opName;

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

		try {
			cloneRepository()
				.setBare(bare)
				.setDirectory(directory)
				.setURI(sourceUri.toPrivateString())
				.setProgressMonitor(messagingProgressMonitor)
				.setTransportConfigCallback(transportConfigCallback)
				.setCredentialsProvider(credentialsProvider)
				.call();

			Log.d(TAG, "Completed checkout!");
		} catch (JGitInternalException e) {
			throw exceptionWithFriendlyMessageFor(e);
		} finally {
			repoUpdateBroadcaster.broadcastUpdate();
		}

		return new OpNotification(stat_sys_download_done, string(cloned_repo, sourceUri.getHumanishName()),
				str_operationCompleted(),
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

	public String getName() {
		return opName;
	}

	public String getTickerText() {
		return string(cloning_repo, sourceUri.getHumanishName())+"...";
	}

	public String getActionTitle() {
		return string(cloning);
	}

	public CharSequence getUrl() {
		return sourceUri.toString();
	}

    public String toString() {
        return getClass().getSimpleName()+"["+sourceUri+"]";
    }
}
