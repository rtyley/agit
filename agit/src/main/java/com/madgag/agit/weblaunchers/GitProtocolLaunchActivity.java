package com.madgag.agit.weblaunchers;

import android.content.Intent;
import android.net.Uri;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;

public class GitProtocolLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-git";

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        return cloneLauncherIntentFor(uri.toString());
    }
}
