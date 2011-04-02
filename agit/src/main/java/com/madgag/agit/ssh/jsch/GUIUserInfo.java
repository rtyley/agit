package com.madgag.agit.ssh.jsch;

import static android.R.drawable.stat_sys_warning;
import static com.google.inject.name.Names.named;

import java.io.File;

import android.util.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.jcraft.jsch.UserInfo;
import com.madgag.agit.BlockingPromptService;
import com.madgag.agit.RepoOpScope;
import com.madgag.agit.RepoOpScoped;
import com.madgag.agit.operations.OpNotification;

@RepoOpScoped
public class GUIUserInfo implements UserInfo {
	private final static String TAG="GUIUI";
	
	public static Module module() {
		return new AbstractModule() {
			public void configure() {
				bind(UserInfo.class).to(GUIUserInfo.class);
			}
		};
	}
	
	private final BlockingPromptService blockingPromptService;

	@Inject
	public GUIUserInfo(BlockingPromptService blockingPromptService) {
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
