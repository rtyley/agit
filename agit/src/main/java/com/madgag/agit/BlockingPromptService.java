package com.madgag.agit;

import com.madgag.agit.operations.OpNotification;

public interface BlockingPromptService {

	String requestStringPrompt(OpNotification opNotification);

	Boolean requestBooleanPrompt(OpNotification opNotification);

}
