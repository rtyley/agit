package com.madgag.agit.weblaunchers;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import android.content.Intent;
import android.net.Uri;

public class GitProtocolLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-git";

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        return cloneLauncherIntentFor(uri.toString());
    }
}
