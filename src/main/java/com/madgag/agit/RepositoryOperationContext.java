package com.madgag.agit;

import org.eclipse.jgit.lib.Repository;

public class RepositoryOperationContext {
	
	private final Repository repository;
	public final int fetchCompletionId,fetchOngoingId;
	
	//private FetchThread currentOperation;
	
	public RepositoryOperationContext(Repository repository) {
		this.repository = repository;
		this.fetchOngoingId = hashCode();
		this.fetchCompletionId = fetchOngoingId;
	}
	
	
	
	public Repository getRepository() {
		return repository;
	}
	
//	public void setCurrentOperation(FetchThread currentOperation) {
//		this.currentOperation = currentOperation;
//	}
//	
//	public FetchThread getCurrentOperation() {
//		return currentOperation;
//	}

}
