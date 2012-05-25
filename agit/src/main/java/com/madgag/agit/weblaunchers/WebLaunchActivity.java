package com.madgag.agit.weblaunchers;


import static com.madgag.agit.GitIntents.sourceUriFrom;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public abstract class WebLaunchActivity extends RoboSherlockActivity {

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
