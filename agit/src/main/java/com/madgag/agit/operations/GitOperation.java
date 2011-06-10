package com.madgag.agit.operations;

import java.io.File;

import com.madgag.agit.Progress;
import com.madgag.agit.ProgressListener;

public interface GitOperation {
	
	String getTickerText();
	
	int getOngoingIcon(); 
	
	OpNotification execute();

	String getName();

	String getShortDescription();
	
	String getDescription();

	CharSequence getUrl();

	File getGitDir();
}
