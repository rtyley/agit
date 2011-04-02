package com.madgag.agit;

import static java.util.Arrays.asList;

import java.util.List;

import roboguice.application.RoboApplication;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.google.inject.Module;
import com.madgag.agit.ssh.jsch.GUIUserInfo;

public class AgitApplication extends RoboApplication {
	
	public AgitApplication() {}
	
	public AgitApplication(Context context) {
		attachBaseContext(context);
	}
	
	public AgitApplication(Instrumentation instrumentation) {
		attachBaseContext(instrumentation.getTargetContext());
	}

	protected void addApplicationModules(List<Module> modules) {
		Log.i("AA", "Adding application modules...");
        modules.addAll(asList(new AgitModule(), GUIUserInfo.module()));
    }
}
