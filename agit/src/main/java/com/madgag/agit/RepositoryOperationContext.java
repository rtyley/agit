package com.madgag.agit;

import static android.app.Notification.FLAG_AUTO_CANCEL;
import static android.app.Notification.FLAG_ONGOING_EVENT;
import static com.madgag.agit.RepositoryManagementActivity.manageGitRepo;

import org.eclipse.jgit.lib.Repository;

import com.madgag.ssh.android.authagent.AndroidAuthAgent;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.util.Log;

public class RepositoryOperationContext {
	
	public static final String TAG = "RepositoryOperationContext";
	
	private final GitOperationsService service;
	private final Repository repository;
	public final int fetchCompletionId,fetchOngoingId;
	public final PendingIntent manageGitRepo;
	private GitOperation currentOperation;
	
	//private FetchThread currentOperation;
	
	public RepositoryOperationContext(Repository repository, GitOperationsService service) {
		this.repository = repository;
		this.service = service;
		this.fetchOngoingId = hashCode();
		this.fetchCompletionId = fetchOngoingId;
		manageGitRepo = manageGitRepo(getRepository(), service);
	}
	
	
	
	public Repository getRepository() {
		return repository;
	}

	public Service getService() {
		return service;
	}

	// grandiose name
	public void enqueue(GitOperation gitOperation) {
		currentOperation=gitOperation;
		gitOperation.execute();
		showOngoingNotificationFor(gitOperation);
	}

	public void notifyOngoing(Notification notification) {
		service.getNotificationManager().notify(fetchOngoingId, notification);
	}
	
	
	private void showOngoingNotificationFor(GitOperation gitOperation) {
    	Notification ongoingNotification=gitOperation.createOngoingNotification();
    	ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
    	Log.i(TAG, "Starting "+gitOperation.getClass().getSimpleName()+" in the foreground...");
    	try {
    		service.startForeground(fetchOngoingId, ongoingNotification);
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
		service.getNotificationManager().notify(fetchCompletionId, completedNotification);
	}



	public PendingIntent getRMAPendingIntent() {
		return manageGitRepo;
	}




	public GitOperation getCurrentOperation() {
		return currentOperation;
	}



	public AndroidAuthAgent getAuthAgent() {
		// TODO Auto-generated method stub
		return service.authAgent;
	}
	
//	public void setCurrentOperation(FetchThread currentOperation) {
//		this.currentOperation = currentOperation;
//	}
//	
//	public FetchThread getCurrentOperation() {
//		return currentOperation;
//	}

}
