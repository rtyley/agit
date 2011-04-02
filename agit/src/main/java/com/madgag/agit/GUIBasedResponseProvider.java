package com.madgag.agit;

import com.google.inject.Inject;
import com.madgag.agit.operation.lifecycle.RepoNotifications;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.OpPrompt;

public class GUIBasedResponseProvider implements ResponseProvider {

	private final RepoNotifications repoNotifications;
	
	@Inject
	public GUIBasedResponseProvider(RepoNotifications repoNotifications) {
		this.repoNotifications = repoNotifications;
	}
	
	public void accept(ResponseInterface responseInterface) {
		OpNotification opNotification = ((OpPrompt<?>) responseInterface.getOpPrompt()).getOpNotification();
//		if (repositoryManagementActivity != null) {
//			Log.i("I could prob show this directly without status bar",	opNotification.getEventDetail());
//			repositoryManagementActivity.updateUIToReflectServicePromptRequests();
//		} else {
			repoNotifications.notifyPromptWith(opNotification);
//		}
	}

}
