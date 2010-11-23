package com.madgag.agit.operations;

import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;

public interface GitOperation {
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpNotification execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener);

	String getName();

	String getShortDescription();
	
	String getDescription();

	CharSequence getUrl();
}
