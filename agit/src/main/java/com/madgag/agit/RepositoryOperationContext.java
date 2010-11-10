package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class RepositoryOperationContext {
	
	public static final String TAG = "RepositoryOperationContext";
	
	private final GitOperationsService service;
	private final Repository repository;
	public final int opCompletionNotificationId,ongoingOpNotificationId;
	public final PendingIntent manageGitRepo;
	private GitOperation currentOperation;

	private final PromptHelper promptHelper;
	
	public RepositoryOperationContext(Repository repository, GitOperationsService service) {
		this.repository = repository;
		this.service = service;
		this.ongoingOpNotificationId = hashCode();
		this.opCompletionNotificationId = ongoingOpNotificationId;
		promptHelper = new PromptHelper(TAG);
		manageGitRepo = manageRepoPendingIntent(getRepository(), service);
	}
	
	
	
	public Repository getRepository() {
		return repository;
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(Action action) {
		GitOperation gitOperation = new GitOperation(this, action);
		currentOperation=gitOperation;
		gitOperation.execute();
		showOngoingNotificationFor(gitOperation);
	}

	public void notifyOngoing(Notification notification) {
		service.getNotificationManager().notify(ongoingOpNotificationId, notification);
	}
	
	public Transport transportFor(RemoteConfig remoteConfig) {
    	Transport tn;
		try {
			tn = Transport.open(getRepository(), remoteConfig);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		}
    	if (tn instanceof SshTransport) {
			((SshTransport) tn).setSshSessionFactory(new AndroidSshSessionFactory(this, promptHelper));
		}
    	return tn;
    }
	
	private void showOngoingNotificationFor(GitOperation gitOperation) {
    	Notification ongoingNotification=gitOperation.getOngoingNotification();
    	ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
    	Log.i(TAG, "Starting "+gitOperation.getClass().getSimpleName()+" in the foreground...");
    	try {
    		service.startForeground(ongoingOpNotificationId, ongoingNotification);
    	} catch (NullPointerException e) {
    		Log.i(TAG, "startForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117");
    	}
	}

	public void notifyCompletion(Notification completedNotification) {
		try {
			service.stopForeground(true); // Actually, we only want to call this if ALL threads are completed, I think...
		} catch (NullPointerException e) {
    		Log.i(TAG, "stopForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117",e);
    	}
		completedNotification.flags |= FLAG_AUTO_CANCEL;
		service.getNotificationManager().notify(opCompletionNotificationId, completedNotification);
	}

	public PendingIntent getRMAPendingIntent() {
		return manageGitRepo;
	}
	
	public Intent getRMAIntent() {
		return manageRepoIntent(getRepository().getDirectory(), service);
	}

	public GitOperation getCurrentOperation() {
		return currentOperation;
	}

	public AndroidAuthAgent getAuthAgent() {
		return service.authAgent;
	}



	public PromptHelper getPromptHelper() {
		return promptHelper;
	}

}
