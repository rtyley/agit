package com.madgag.agit.weblaunchers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;


public class GitHubWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-github";

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        final String[] pathParts = uri.getPath().split("/");
        String path = uri.getPath();
        if (pathParts.length >= 2) {
            path = pathParts[0] + "/" + pathParts[1];
        }
        return cloneLauncherIntentFor("git://github.com/"+ path +".git");
    }
}
