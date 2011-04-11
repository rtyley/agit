package com.madgag.agit.weblaunchers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;


public class GitHubWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-github";

    Intent cloneLauncherForWebBrowseIntent(Intent intent) {
        String path = intent.getData().getPath();
        String sourceUri = "git://github.com"+ path +".git";
        Log.d(TAG, "original intent data path=" + path + " sourceUri=" + sourceUri);
        return cloneLauncherIntentFor(sourceUri);
    }


}
