package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.identityHashCode;

import java.io.File;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jcraft.jsch.JSchException;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.GitOperation;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.OpPrompt;

public class RepositoryOperationContext implements ResponseProvider {

	public static final String TAG = "RepositoryOperationContext";

	private final GitOperationsService service;
	private final File gitdir;
	public final int opCompletionNotificationId, ongoingOpNotificationId,
			promptNotificationId;
	private final PendingIntent manageGitRepo;
	private GitAsyncTask currentOperation;

	private final PromptHelper promptHelper;
	private ResponseProvider responseProvider;

	private Handler promptHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			responseProvider.accept(promptHelper);
		}
	};

	private RepositoryManagementActivity repositoryManagementActivity;

	public RepositoryOperationContext(File gitdir, GitOperationsService service, ResponseProvider responseProvider) {
		this.gitdir = gitdir.getAbsoluteFile();
		this.service = service;
		this.ongoingOpNotificationId = this.gitdir.hashCode();
		this.opCompletionNotificationId = ongoingOpNotificationId + 1;
		this.promptNotificationId = opCompletionNotificationId + 1;
		promptHelper = new PromptHelper(TAG);
		this.responseProvider = responseProvider;
		promptHelper.setHandler(promptHandler);
		manageGitRepo = manageRepoPendingIntent(gitdir, service);
	}

	
	
	public RepositoryOperationContext(File gitdir, GitOperationsService service) {
		this(gitdir, service, null);
		this.responseProvider = this;
	}



	private void showStatusBarNotificationFor(OpPrompt<?> opPrompt) {
		OpNotification opNotification = opPrompt.getOpNotification();
		Notification n = createNotificationWith(opNotification);
		n.flags |= FLAG_AUTO_CANCEL;
		service.getNotificationManager().notify(promptNotificationId, n);
	}

	void clearPromptNotificationFromStatusBar() {
		service.getNotificationManager().cancel(promptNotificationId);
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(GitOperation action) {
		GitAsyncTask gitOperation = new GitAsyncTask(this, action);
		currentOperation = gitOperation;
		gitOperation.execute();
		showOngoingNotificationFor(gitOperation);
	}

	public void notifyOngoing(Notification notification) {
		service.getNotificationManager().notify(ongoingOpNotificationId,
				notification);
	}

	public Transport transportFor(Repository repo, RemoteConfig remoteConfig) {
		Transport tn;
		try {
			Log.i(TAG, "Creating transport for repo with "
					+ identityHashCode(repo));
			tn = Transport.open(repo, remoteConfig);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		}
		if (tn instanceof SshTransport) {
			((SshTransport) tn)
					.setSshSessionFactory(new AndroidSshSessionFactory(service,
							promptHelper));
		}
		return tn;
	}

	private void showOngoingNotificationFor(GitAsyncTask gitOperation) {
		Notification ongoingNotification = gitOperation
				.getOngoingNotification();
		ongoingNotification.flags = ongoingNotification.flags
				| FLAG_ONGOING_EVENT;
		Log.i(TAG, "Starting " + gitOperation.getClass().getSimpleName()
				+ " in the foreground...");
		try {
			service.startForeground(ongoingOpNotificationId,
					ongoingNotification);
		} catch (NullPointerException e) {
			Log.i(TAG,
					"startForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117");
		}
	}

	public void notifyCompletion(Notification completedNotification) {
		try {
			service.stopForeground(true); // Actually, we only want to call this
											// if ALL threads are completed, I
											// think...
		} catch (NullPointerException e) {
			Log.i(TAG,
					"stopForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117",
					e);
		}
		NotificationManager notificationManager = service
				.getNotificationManager();
		notificationManager.cancel(ongoingOpNotificationId);
		completedNotification.flags |= FLAG_AUTO_CANCEL;
		Log.i(TAG, "notifyCompletion() " + this + " : " + completedNotification);
		notificationManager.notify(opCompletionNotificationId,
				completedNotification);
	}

	public Intent getRMAIntent() {
		return manageRepoIntent(gitdir);
	}

	public GitAsyncTask getCurrentOperation() {
		return currentOperation;
	}

	public ResponseInterface getResponseInterface() {
		return promptHelper;
	}

	public void setManagementActivity(
			RepositoryManagementActivity repositoryManagementActivity) {
		this.repositoryManagementActivity = repositoryManagementActivity;
	}

	public FetchResult fetch(Repository repository, RemoteConfig remote,
			ProgressListener<Progress> progressListener) {
		Transport transport = transportFor(repository, remote);
		try {
			return transport.fetch(new MessagingProgressMonitor(
					progressListener), null);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		} catch (TransportException e) {
			Log.e(TAG, "TransportException ", e);
			String message = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof JSchException) {
				message = "SSH: " + ((JSchException) cause).getMessage();
			}
			throw new RuntimeException(message, e);
		} finally {
			transport.close();
		}
	}

	public Notification createNotificationWith(OpNotification opNotification) {
		Notification n = new Notification(opNotification.getDrawable(),
				opNotification.getTickerText(), currentTimeMillis());
		n.setLatestEventInfo(getService(), opNotification.getEventTitle(),
				opNotification.getEventDetail(), manageGitRepo);
		Log.i(TAG, "createNotificationWith... and I am " + gitdir);
		return n;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + gitdir + "]";
	}

	public void accept(ResponseInterface responseInterface) {
		OpPrompt<?> opPrompt = responseInterface.getOpPrompt();
		if (repositoryManagementActivity != null) {
			Log.i("I could prob show this directly without status bar",	opPrompt.getOpNotification().getEventDetail());
			repositoryManagementActivity.updateUIToReflectServicePromptRequests();
		} else {
			showStatusBarNotificationFor(opPrompt);
		}
	}



	public File getGitDir() {
		return gitdir;
	}

}
