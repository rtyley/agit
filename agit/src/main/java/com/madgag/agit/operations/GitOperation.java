package com.madgag.agit.operations;

import org.eclipse.jgit.lib.Repository;

import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;
import com.madgag.agit.RepositoryOperationContext;

public interface GitOperation {
	
	Repository getRepository();
	
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpNotification execute(RepositoryOperationContext repositoryOperationContext, ProgressListener<Progress> progressListener);

	String getName();

	String getShortDescription();
	
	String getDescription();

	CharSequence getUrl();
}
