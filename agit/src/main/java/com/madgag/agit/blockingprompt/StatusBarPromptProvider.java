package com.madgag.agit.blockingprompt;

import com.google.inject.Inject;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operation.lifecycle.RepoNotifications;

@RepositoryScoped
public class StatusBarPromptProvider implements PromptUIProvider {
    @Inject RepoNotifications repoNotifications;

    public void acceptPrompt(ResponseInterface responseInterface) {
        repoNotifications.notifyPromptWith(responseInterface.getOpPrompt().getOpNotification());
    }

    public void clearPrompt() {
        repoNotifications.clearPromptNotification();
    }
}
