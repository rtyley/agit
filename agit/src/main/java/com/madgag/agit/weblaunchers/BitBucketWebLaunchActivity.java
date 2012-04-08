package com.madgag.agit.weblaunchers;

import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static java.util.regex.Pattern.compile;


public class BitBucketWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-bitbucket";

    private static final Pattern projectPathPattern = compile("^/([^/]+/[^/]+)");

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        Matcher matcher = projectPathPattern.matcher(uri.getPath());
        matcher.find();
        return cloneLauncherIntentFor("git@bitbucket.org:"+ matcher.group(1) +".git");
    }
}
