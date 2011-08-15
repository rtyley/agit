package com.madgag.agit.weblaunchers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static java.util.regex.Pattern.compile;


public class GitHubWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-github";

    private static final Pattern projectPathPattern = compile("^/[^/]+/[^/]+");

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        Matcher matcher = projectPathPattern.matcher(uri.getPath());
        matcher.find();
        return cloneLauncherIntentFor("git://github.com"+ matcher.group() +".git");
    }
}
