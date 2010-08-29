package com.madgag.agit;

import org.connectbot.service.PromptHelper;

import android.util.Log;

import com.jcraft.jsch.UserInfo;

public class AndroidUserInfo implements UserInfo {
	
	private final PromptHelper uiThreadConduit;

	public AndroidUserInfo(PromptHelper uiThreadConduit) {
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
		passphrase = uiThreadConduit.requestStringPrompt(null, msg);
		return passphrase!=null;
	}

	public boolean promptPassword(String msg) {
		password = uiThreadConduit.requestStringPrompt(null, msg);
		return password!=null;
	}

	public boolean promptYesNo(String msg) {
		Boolean bool = uiThreadConduit.requestBooleanPrompt(msg, null);
		return bool!=null?bool:false;
		//return true;
	}
	
	public void showMessage(String msg) {
		Log.i("UserInfo", "Should show this: "+msg);
	}

}
