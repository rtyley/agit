package com.madgag.agit;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import java.io.File;

import static android.R.layout.two_line_list_item;
import static android.graphics.PixelFormat.RGBA_8888;
import static com.madgag.agit.Repos.knownRepos;
import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;

public class DashboardActivity extends RoboActivity {
    private static final String TAG = "DA";

    @InjectView(android.R.id.list) ListView listView;
    ViewHoldingListAdapter<File> listAdapter;

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

        listAdapter = new ViewHoldingListAdapter<File>(knownRepos(), viewInflatorFor(this, two_line_list_item), new ViewHolderFactory<File>() {
            public ViewHolder<File> createViewHolderFor(View view) {
                return new RepositoryViewHolder(view);
            }
        });

        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(manageRepoIntent(listAdapter.getItem(position)));
            }
        });
    }

    protected void onResume() {
        super.onResume();
        listAdapter.setList(knownRepos());
    }

    // used by dashboard.xml
    public void startCloneLaunchActivity(View v) {
        startActivity(new Intent(this, CloneLauncherActivity.class));
    }
}
