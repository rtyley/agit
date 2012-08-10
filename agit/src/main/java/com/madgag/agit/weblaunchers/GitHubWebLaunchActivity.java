/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

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
