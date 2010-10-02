package com.madgag.agit;

import static com.madgag.agit.RepositoryManagementActivity.manageGitRepo;

import org.eclipse.jgit.lib.Repository;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

public class RepositoryOperationContext {
	
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



	public void notifyCompletion(Notification completedNotification) {
		service.getNotificationManager().notify(fetchCompletionId, completedNotification);
	}

	public void notifyOngoing(Notification notification) {
		service.getNotificationManager().notify(fetchOngoingId, notification);
	}

	public PendingIntent getRMAPendingIntent() {
		
		return manageGitRepo;
	}

	// grandiose name
	public void enqueue(GitOperation gitOperation) {
		currentOperation=gitOperation;
		gitOperation.execute();
	}

	public GitOperation getCurrentOperation() {
		return currentOperation;
	}
	
//	public void setCurrentOperation(FetchThread currentOperation) {
//		this.currentOperation = currentOperation;
//	}
//	
//	public FetchThread getCurrentOperation() {
//		return currentOperation;
//	}

}
