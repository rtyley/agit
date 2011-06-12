package com.madgag.agit;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

import static android.graphics.PixelFormat.RGBA_8888;
import static com.madgag.agit.GitIntents.REPO_STATE_CHANGED_BROADCAST;
import static com.madgag.agit.GitIntents.actionWithSuffix;
import static com.madgag.agit.R.layout.dashboard_repo_list_header;
import static com.madgag.agit.R.layout.repo_list_item;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.sync.AccountAuthenticatorService.addAccount;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;

public class DashboardActivity extends RoboActivity {
    private static final String TAG = "DA";

    private final static int MENU_ABOUT_ID= Menu.FIRST;
    
    @InjectView(android.R.id.list) ListView listView;
    ViewHoldingListAdapter<RepoSummary> listAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        setupRepoList();
        
        try {
            addAccount(this);
        } catch (Exception e) {
            Log.w(TAG, "Unable to add account for syncing",e);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setFormat(RGBA_8888);
    }

    BroadcastReceiver repoStateChangeReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "repoStateChangeReceiver got broadcast : " + intent);
			updateRepoList();
		}
	};

    private void setupRepoList() {

        listAdapter = new ViewHoldingListAdapter<RepoSummary>(RepoSummary.getAllReposOrderChronologically(), viewInflatorFor(this, repo_list_item), new ViewHolderFactory<RepoSummary>() {
            public ViewHolder<RepoSummary> createViewHolderFor(View view) {
                return new RepositoryViewHolder(view);
            }
        });

        listView.addHeaderView(repoListHeader(), null, false);
        listView.setAdapter(listAdapter);
        listView.setHeaderDividersEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(manageRepoIntent(((RepoSummary) listView.getAdapter().getItem(position)).getRepo().getDirectory()));
            }
        });
    }

    private TextView repoListHeader() {
        TextView repoListHeader = (TextView) LayoutInflater.from(this).inflate(dashboard_repo_list_header, null);
        repoListHeader.setCompoundDrawables(null, null, null, listView.getDivider());
        return repoListHeader;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ABOUT_ID, 0, R.string.about_app_menu_option).setShortcut('0', 'a');
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ABOUT_ID:
        	startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void onResume() {
        super.onResume();
        registerReceiver(repoStateChangeReceiver, new IntentFilter(actionWithSuffix(REPO_STATE_CHANGED_BROADCAST)));
        updateRepoList();
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(repoStateChangeReceiver);
    }

    private void updateRepoList() {
        listAdapter.setList(RepoSummary.getAllReposOrderChronologically());
    }

    // used by dashboard.xml
    public void startCloneLaunchActivity(View v) {
        startActivity(new Intent(this, CloneLauncherActivity.class));
    }
}
