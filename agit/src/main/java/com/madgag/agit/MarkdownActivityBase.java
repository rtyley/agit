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

package com.madgag.agit;

import static com.madgag.agit.R.layout.about_activity;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.petebevin.markdown.MarkdownProcessor;

import org.apache.commons.io.IOUtils;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public abstract class MarkdownActivityBase extends RoboSherlockActivity {

    private final static String TAG = "MA";

    @InjectView(R.id.webView)
    WebView webView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());
        setContentView(about_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        configureActionBar(actionBar);

        MarkdownProcessor m = new MarkdownProcessor();
        String html = m.markdown(loadMarkdown());
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        // webView.getSettings().setBuiltInZoomControls(true); // Doesn't work great
    }

    private String loadMarkdown() {
        String markdown;
        String fileName = markdownFile();
        try {
            markdown = IOUtils.toString(getAssets().open(fileName));
        } catch (Exception e) {
            markdown = "Problem loading '" + fileName + "'.";
            Log.e(TAG, markdown, e);
        }
        return markdown;
    }

    abstract protected String markdownFile();

    abstract protected void configureActionBar(ActionBar actionBar);
}
