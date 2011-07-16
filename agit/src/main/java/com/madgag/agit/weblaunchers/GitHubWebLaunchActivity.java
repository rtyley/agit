package com.madgag.agit.weblaunchers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;


public class GitHubWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-github";

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        return cloneLauncherIntentFor("git://github.com"+ uri.getPath() +".git");
    }


}
