package com.madgag.agit;

import static java.util.Arrays.asList;

import java.util.List;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.google.inject.Module;

public class AgitTestApplication extends AgitApplication {
	
	private static final String TAG = "AgitTestApplication";
	
	public AgitTestApplication(Instrumentation instrumentation) {
		super(instrumentation);
		Log.i(TAG,"GETTING CALLED with instrumentation...");
	}
	
	public AgitTestApplication(Context context) {
		super(context);
		Log.i(TAG,"REALLY GETTING CALLED!!");
	}
	
	protected void addApplicationModules(List<Module> modules) {
		modules.addAll(asList(new AgitModule(), YesToEverythingUserInfo.module()));
	}
}