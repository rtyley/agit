package com.madgag.agit.weblaunchers;


import android.app.Activity;
import android.content.Intent;

public abstract class WebLaunchActivity extends Activity {

    @Override
    protected void onStart() {
    	super.onStart();
        startActivity(cloneLauncherForWebBrowseIntent(getIntent()));
        finish();
    }

    abstract Intent cloneLauncherForWebBrowseIntent(Intent intent);
}
