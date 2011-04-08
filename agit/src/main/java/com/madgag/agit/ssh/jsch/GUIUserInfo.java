package com.madgag.agit.ssh.jsch;

import static android.R.drawable.stat_sys_warning;
import static com.madgag.agit.operations.OpPrompt.prompt;

import android.util.Log;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.jcraft.jsch.UserInfo;
import com.madgag.agit.blockingprompt.BlockingPromptService;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.OpNotification;

@RepositoryScoped
public class GUIUserInfo implements UserInfo {
	private final static String TAG="GUIUI";
	
	public static Module module() {
		return new AbstractModule() {
			public void configure() {
				bind(UserInfo.class).to(GUIUserInfo.class);
			}
		};
	}
	
	private final BlockingPromptService blockingPrompt;

	@Inject
	public GUIUserInfo(BlockingPromptService blockingPrompt) {
		this.blockingPrompt = blockingPrompt;
	}
	
	private String password, passphrase;
	
	public String getPassphrase() {
		return passphrase;
	}

	public String getPassword() {
		return password;
	}

	public boolean promptPassphrase(String msg) {
		Log.d(TAG, "promptPassphrase : "+msg);
		passphrase = blockingPrompt.request(prompt(String.class, note("Passphrase required", "Please enter your passphrase", msg)));
		return passphrase!=null;
	}


    public boolean promptPassword(String msg) {
		Log.d(TAG, "promptPassword : "+msg);
		password = blockingPrompt.request(prompt(String.class, note("Password required", "Please enter your password", msg)));
        return password!=null;
	}

	public boolean promptYesNo(String msg) {
		Log.d(TAG, "promptYesNo : "+msg);
		Boolean bool = blockingPrompt.request(prompt(Boolean.class, note(msg, "SSH", msg)));
		return bool!=null?bool:false;
	}
	
	public void showMessage(String msg) {
		Log.i(TAG, "Should show this: "+msg);
	}

    private OpNotification note(String tickerText, String eventTitle, String eventDetail) {
        return new OpNotification(stat_sys_warning, tickerText, eventTitle, eventDetail);
    }

}
