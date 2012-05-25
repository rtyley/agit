package com.madgag.agit;

import static android.R.layout.simple_list_item_2;
import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static com.madgag.agit.SuggestedRepo.SUGGESTIONS;
import static com.madgag.android.ActionBarUtil.homewardsWith;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;

public class SuggestRepoActivity extends RoboSherlockListActivity {
    public static final String TAG = "SRA";

    private ViewHoldingListAdapter<SuggestedRepo> adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Some example repos...");

        adapter = new ViewHoldingListAdapter<SuggestedRepo>(SUGGESTIONS, viewInflatorFor(this, simple_list_item_2),
                new ViewHolderFactory<SuggestedRepo>() {
            public ViewHolder<SuggestedRepo> createViewHolderFor(View view) {
                return new SuggestedRepoViewHolder(view);
            }
        });
        setListAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return homewardsWith(this, new Intent(this, CloneLauncherActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        setResult(RESULT_OK, cloneLauncherIntentFor(adapter.getItem(position).getURI().toString()));
        finish();
    }
}
