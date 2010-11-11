package com.madgag.agit;

public interface GitOperation {
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpResult execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener);

	String getName();

	String getDescription();
}
