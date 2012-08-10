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


import static com.madgag.agit.GitIntents.sourceUriFrom;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public abstract class WebLaunchActivity extends RoboSherlockActivity {

    private static final String TAG = "WL";

    @Override
    protected void onStart() {
        super.onStart();
        Uri webUrl = getIntent().getData();
        Intent cloneIntent = cloneLauncherForWebBrowseIntent(webUrl);

        Log.d(TAG, "original web url=" + webUrl + " derived sourceUri=" + sourceUriFrom(cloneIntent));
        startActivity(cloneIntent);
        finish();
    }

    abstract Intent cloneLauncherForWebBrowseIntent(Uri uri);
}
