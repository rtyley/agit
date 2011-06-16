/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import com.markupartist.android.widget.ActionBar;
import com.petebevin.markdown.MarkdownProcessor;
import org.apache.commons.io.IOUtils;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.io.Reader;

import static com.madgag.agit.R.layout.about_activity;
import static com.madgag.agit.R.string.about_activity_title;

public class AboutActivity extends RoboActivity {
	private final static String TAG="AboutActivity";

	@InjectView(R.id.actionbar) ActionBar actionBar;
    @InjectView(R.id.webView) WebView webView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(about_activity);
        actionBar.setHomeAction(new HomeAction(this));
        actionBar.setTitle(about_activity_title);

        String markdown;
        try {
            markdown=IOUtils.toString(getAssets().open("CREDITS.markdown"));
        } catch (Exception e) {
            markdown="Problem loading 'About' file.";
            Log.e(TAG,markdown,e);
        }

        MarkdownProcessor m = new MarkdownProcessor();
        String html=m.markdown(markdown);
        webView.loadDataWithBaseURL(null, html,"text/html", "UTF-8", null);
    }
}