package com.madgag.agit;

import com.madgag.agit.operation.lifecycle.RepoNotifications;

public class StatusBarPromptProvider implements PromptUIProvider {
    RepoNotifications repoNotifications;

    public void acceptPrompt(ResponseInterface responseInterface) {
        repoNotifications.notifyPromptWith(responseInterface.getOpPrompt().getOpNotification());
    }

    public void clearPrompt() {
        repoNotifications.clearPromptNotification();
    }
}
