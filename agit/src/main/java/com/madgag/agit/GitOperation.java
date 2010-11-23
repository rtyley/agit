package com.madgag.agit;

public interface GitOperation {
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpNotification execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener);

	String getName();

	String getShortDescription();
	
	String getDescription();

	CharSequence getUrl();
}
