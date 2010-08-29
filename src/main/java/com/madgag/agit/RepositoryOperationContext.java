package com.madgag.agit;

import org.eclipse.jgit.lib.Repository;

import com.madgag.agit.GitOperationsService.FetchThread;

public class RepositoryOperationContext {
	
	private final Repository repository;
	private FetchThread currentOperation;
	
	public RepositoryOperationContext(Repository repository) {
		this.repository = repository;
	}
	
	public Repository getRepository() {
		return repository;
	}
	
	public void setCurrentOperation(FetchThread currentOperation) {
		this.currentOperation = currentOperation;
	}
	
	public FetchThread getCurrentOperation() {
		return currentOperation;
	}

}
