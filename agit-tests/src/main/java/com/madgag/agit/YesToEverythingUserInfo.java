package com.madgag.agit;

import roboguice.config.AbstractAndroidModule;
import android.util.Log;

import com.google.inject.Module;
import com.jcraft.jsch.UserInfo;

public final class YesToEverythingUserInfo implements UserInfo {

	public static Module module() {
		return new AbstractAndroidModule() {
			@Override
			protected void configure() {
				bind(UserInfo.class).toInstance(new YesToEverythingUserInfo());
			}
		};
	}

	private String TAG = "YTEUI";

	public void showMessage(String msg) {
		Log.i(TAG, msg);
	}

	public boolean promptYesNo(String msg) {
		Log.i(TAG, msg);
		return true;
	}

	public boolean promptPassword(String arg0) {
		return false;
	}

	public boolean promptPassphrase(String arg0) {
		return false;
	}

	public String getPassword() {
		return null;
	}

	public String getPassphrase() {
		return null;
	}
}