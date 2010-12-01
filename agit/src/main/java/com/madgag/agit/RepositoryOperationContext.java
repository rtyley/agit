package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoPendingIntent;
import static java.lang.System.currentTimeMillis;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import android.app.Notification;
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
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class RepositoryOperationContext {
	
	public static final String TAG = "RepositoryOperationContext";
	
	private final GitOperationsService service;
	private final Repository repository;
	public final int opCompletionNotificationId,ongoingOpNotificationId,promptNotificationId;
	public final PendingIntent manageGitRepo;
	private GitAsyncTask currentOperation;

	private final PromptHelper promptHelper;
	
	private Handler promptHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			OpPrompt<?> opPrompt = promptHelper.getOpPrompt();
			if (repositoryManagementActivity!=null) {
				Log.i("I could prob show this directly without status bar", opPrompt.getOpNotification().getEventDetail());
				repositoryManagementActivity.updateUIToReflectServicePromptRequests();
			} else {
				showStatusBarNotificationFor(opPrompt);
			}
		}


	};
	


	private RepositoryManagementActivity repositoryManagementActivity;	private void showStatusBarNotificationFor(OpPrompt<?> opPrompt) {
		OpNotification opNotification = opPrompt.getOpNotification();
		Notification n = createNotificationWith(opNotification);
		n.flags |= FLAG_AUTO_CANCEL;
		service.getNotificationManager().notify(promptNotificationId, n);
	}

	public RepositoryOperationContext(Repository repository, GitOperationsService service) {
		this.repository = repository;
		this.service = service;
		this.ongoingOpNotificationId = hashCode();
		this.opCompletionNotificationId = ongoingOpNotificationId;
		this.promptNotificationId = ongoingOpNotificationId+1;
		promptHelper = new PromptHelper(TAG);
		promptHelper.setHandler(promptHandler);
		manageGitRepo = manageRepoPendingIntent(getRepository(), service);
	}
	

	
	
	
	public Repository getRepository() {
		return repository;
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(GitOperation action) {
		GitAsyncTask gitOperation = new GitAsyncTask(this, action);
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
	
	private void showOngoingNotificationFor(GitAsyncTask gitOperation) {
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
		return manageRepoIntent(getRepository().getDirectory());
	}

	public GitAsyncTask getCurrentOperation() {
		return currentOperation;
	}

	public AndroidAuthAgent getAuthAgent() {
		return service.authAgent;
	}



	public PromptHelper getPromptHelper() {
		return promptHelper;
	}



	public void setManagementActivity(RepositoryManagementActivity repositoryManagementActivity) {
		this.repositoryManagementActivity = repositoryManagementActivity;
	}



	public FetchResult fetch(RemoteConfig remote, ProgressListener<Progress> progressListener) {
		Transport transport=transportFor(remote);
		try {
			return transport.fetch(new MessagingProgressMonitor(progressListener), null);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		} catch (TransportException e) {
			Log.e(TAG, "TransportException ",e);
			String message=e.getMessage();
			Throwable cause=e.getCause();
			if (cause!=null && cause instanceof JSchException) {
				message="SSH: "+((JSchException) cause).getMessage();
			}
			throw new RuntimeException(message, e);
		} finally {
			transport.close();
		}
	}

	public Notification createNotificationWith(OpNotification opNotification) {
		Notification n=new Notification(opNotification.getDrawable(), opNotification.getTickerText(), currentTimeMillis());
		n.setLatestEventInfo(getService(), opNotification.getEventTitle(), opNotification.getEventDetail(), manageGitRepo);
		return n;
	}

}
