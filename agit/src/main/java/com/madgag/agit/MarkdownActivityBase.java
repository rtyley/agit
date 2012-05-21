package com.madgag.agit;

import static com.madgag.agit.R.layout.about_activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.markupartist.android.widget.ActionBar;
import com.petebevin.markdown.MarkdownProcessor;

import org.apache.commons.io.IOUtils;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public abstract class MarkdownActivityBase extends RoboActivity {

    private final static String TAG = "MA";

    @InjectView(R.id.actionbar)
    ActionBar actionBar;
    @InjectView(R.id.webView)
    WebView webView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(about_activity);
        actionBar.setHomeAction(new HomeAction(this));
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
