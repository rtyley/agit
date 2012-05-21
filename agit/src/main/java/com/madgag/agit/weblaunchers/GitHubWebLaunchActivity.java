package com.madgag.agit.weblaunchers;

import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static java.util.regex.Pattern.compile;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GitHubWebLaunchActivity extends WebLaunchActivity {

    private static final String TAG = "WL-github";

    private static final Pattern projectPathPattern = compile("^/[^/]+/[^/]+");

    Intent cloneLauncherForWebBrowseIntent(Uri uri) {
        Matcher matcher = projectPathPattern.matcher(uri.getPath());
        matcher.find();
        return cloneLauncherIntentFor("git://github.com" + matcher.group() + ".git");
    }
}
