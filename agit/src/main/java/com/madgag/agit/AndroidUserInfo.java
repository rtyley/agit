package com.madgag.agit;

import static android.R.drawable.stat_sys_warning;

import org.connectbot.service.PromptHelper;

import android.util.Log;

import com.jcraft.jsch.UserInfo;
import com.madgag.agit.operations.OpNotification;

public class AndroidUserInfo implements UserInfo {
	private final static String TAG="AndroidUserInfo";
	private final PromptHelper uiThreadConduit;
	private final RepositoryOperationContext repositoryOperationContext;

	public AndroidUserInfo(RepositoryOperationContext repositoryOperationContext, PromptHelper uiThreadConduit) {
		this.repositoryOperationContext = repositoryOperationContext;
		this.uiThreadConduit = uiThreadConduit;
	}
	
	private String password;
	private String passphrase;
	
	public String getPassphrase() {
		return passphrase;
	}

	public String getPassword() {
		return password;
	}

	public boolean promptPassphrase(String msg) {
		Log.i(TAG, "promptPassphrase : "+msg);
		//notifyUserNeedsPrompt(msg);
		passphrase = uiThreadConduit.requestStringPrompt(new OpNotification(stat_sys_warning, "Passphrase required", "Please enter your passphrase", msg));
		return passphrase!=null;
	}

	public boolean promptPassword(String msg) {
		Log.i(TAG, "promptPassword : "+msg);
		//notifyUserNeedsPrompt(msg);
		password = uiThreadConduit.requestStringPrompt(new OpNotification(stat_sys_warning, "Password required", "Please enter your password", msg));
		return password!=null;
	}

	public boolean promptYesNo(String msg) {
		Log.i(TAG, "promptYesNo : "+msg);
		
		//notifyUserNeedsPrompt(msg);
		
		Boolean bool = uiThreadConduit.requestBooleanPrompt(new OpNotification(stat_sys_warning, msg, "SSH", msg));
		return bool!=null?bool:false;
	}

//	private void notifyUserNeedsPrompt(String msg) {
//		Notification n = new Notification(android.R.drawable.stat_sys_warning,"Need to know something", currentTimeMillis());
//		n.setLatestEventInfo(repositoryOperationContext.getService(), "SSH thing", msg, repositoryOperationContext.manageGitRepo);
//		repositoryOperationContext.notifyCompletion(n);
//	}
	
	public void showMessage(String msg) {
		Log.i(TAG, "Should show this: "+msg);
	}

}
