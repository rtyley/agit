package com.madgag.agit.weblaunchers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;


public class GitoriousWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-Gitorious";

    Intent cloneLauncherForWebBrowseIntent(Intent intent) {
        String path = intent.getData().getPath();
        // git://gitorious.org/yr-api/mainline.git
        String sourceUri = "git://gitorious.org"+ path +".git";
        Log.d(TAG, "original intent data path=" + path + " sourceUri=" + sourceUri);
        return cloneLauncherIntentFor(sourceUri);
    }
}
