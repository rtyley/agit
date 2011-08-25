package com.madgag.agit.weblaunchers;

import android.content.Intent;
import android.net.Uri;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;


public class GitoriousWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-Gitorious";

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        // git://gitorious.org/yr-api/mainline.git
        return cloneLauncherIntentFor("git://gitorious.org"+ uri.getPath() +".git");
    }
}
