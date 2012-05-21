package com.madgag.agit.weblaunchers;


import static com.madgag.agit.GitIntents.sourceUriFrom;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import roboguice.activity.RoboActivity;

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
