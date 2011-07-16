package com.madgag.agit.weblaunchers;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.madgag.agit.GitIntents;
import roboguice.activity.RoboActivity;

import static com.madgag.agit.GitIntents.sourceUriFrom;

public abstract class WebLaunchActivity extends RoboActivity {

    private static final String TAG = "WL";

    @Override
    protected void onStart() {
    	super.onStart();
        Uri webUrl = getIntent().getData();
        Intent cloneIntent = cloneLauncherForWebBrowseIntent(webUrl);

        Log.d(TAG, "original web url=" + webUrl + " derived sourceUri=" + sourceUriFrom(cloneIntent));
        startActivity(cloneIntent);
        finish();
    }

    abstract Intent cloneLauncherForWebBrowseIntent(Uri uri);
}
