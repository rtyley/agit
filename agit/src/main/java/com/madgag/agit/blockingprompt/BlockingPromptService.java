package com.madgag.agit.blockingprompt;

import com.madgag.agit.operations.OpPrompt;

public interface BlockingPromptService {

	<T> T request(OpPrompt<T> opPrompt);
    
}
