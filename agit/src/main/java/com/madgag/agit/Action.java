package com.madgag.agit;

public interface Action {
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpResult execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener);
}
