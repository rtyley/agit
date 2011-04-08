package com.madgag.agit.blockingprompt;

import com.madgag.agit.operations.OpPrompt;

public interface ResponseInterface {
	
	public OpPrompt<?> getOpPrompt();
	
	public void setResponse(Object value);
	
}
