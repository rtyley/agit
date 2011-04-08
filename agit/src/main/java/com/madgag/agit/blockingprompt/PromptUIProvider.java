package com.madgag.agit.blockingprompt;

import com.madgag.agit.blockingprompt.ResponseInterface;

public interface PromptUIProvider {

	public void acceptPrompt(ResponseInterface responseInterface);

    public void clearPrompt();

}
