package com.madgag.agit;

import java.util.List;

import roboguice.application.RoboApplication;

import android.content.Context;
import android.util.Log;

import com.google.inject.Module;

public class AgitApplication extends RoboApplication {
	
	public AgitApplication() {}
	
	public AgitApplication(Context context) {
		attachBaseContext(context);
	}
	
	protected void addApplicationModules(List<Module> modules) {
		Log.i("AA", "HELLO");
        modules.add(new AgitModule());
    }
}
