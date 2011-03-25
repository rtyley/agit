package com.madgag.agit.ssh;

import static android.R.drawable.stat_sys_warning;
import android.util.Log;

import com.jcraft.jsch.UserInfo;
import com.madgag.agit.BlockingPromptService;
import com.madgag.agit.operations.OpNotification;

public class AndroidUserInfo implements UserInfo {
	private final static String TAG="AndroidUserInfo";
	private final BlockingPromptService blockingPromptService;

	public AndroidUserInfo(BlockingPromptService blockingPromptService) {
		this.blockingPromptService = blockingPromptService;
	}
	
	private String password, passphrase;
	
	public String getPassphrase() {
		return passphrase;
	}

	public String getPassword() {
		return password;
	}

	public boolean promptPassphrase(String msg) {
		Log.i(TAG, "promptPassphrase : "+msg);
		passphrase = blockingPromptService.requestStringPrompt(new OpNotification(stat_sys_warning, "Passphrase required", "Please enter your passphrase", msg));
		return passphrase!=null;
	}

	public boolean promptPassword(String msg) {
		Log.i(TAG, "promptPassword : "+msg);
		password = blockingPromptService.requestStringPrompt(new OpNotification(stat_sys_warning, "Password required", "Please enter your password", msg));
		return password!=null;
	}

	public boolean promptYesNo(String msg) {
		Log.i(TAG, "promptYesNo : "+msg);
		Boolean bool = blockingPromptService.requestBooleanPrompt(new OpNotification(stat_sys_warning, msg, "SSH", msg));
		return bool!=null?bool:false;
	}
	
	public void showMessage(String msg) {
		Log.i(TAG, "Should show this: "+msg);
	}

}
