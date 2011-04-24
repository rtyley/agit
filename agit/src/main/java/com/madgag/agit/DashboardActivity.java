package com.madgag.agit;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.io.File;

import static android.graphics.PixelFormat.RGBA_8888;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;

public class DashboardActivity extends RoboActivity {
    private static final String TAG = "DA";

    @InjectView(android.R.id.list) ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        setupRepoList();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setFormat(RGBA_8888);
    }

    private void setupRepoList() {
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        Cursor mCursor = managedQuery(GitInfoProvider.CONTENT_URI, null, null, null, null);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_1,
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {"gitdir"},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String gitdir=((TextView)view.findViewById(android.R.id.text1)).getText().toString();
                startActivity(manageRepoIntent(new File(gitdir)));
            }
        });
    }

    // used by dashboard.xml
    public void startCloneLaunchActivity(View v) {
        startActivity(new Intent(this, CloneLauncherActivity.class));
    }
}
