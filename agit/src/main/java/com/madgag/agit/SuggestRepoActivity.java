package com.madgag.agit;

import static android.R.layout.simple_list_item_2;
import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static com.madgag.agit.SuggestedRepo.SUGGESTIONS;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.markupartist.android.widget.ActionBar;

import roboguice.activity.RoboListActivity;

public class SuggestRepoActivity extends RoboListActivity {
    public static final String TAG = "SRA";

    private ViewHoldingListAdapter<SuggestedRepo> adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new HomeAction(this));
        actionBar.setTitle("Some example repos...");

        adapter = new ViewHoldingListAdapter<SuggestedRepo>(SUGGESTIONS, viewInflatorFor(this, simple_list_item_2),
                new ViewHolderFactory<SuggestedRepo>() {
            public ViewHolder<SuggestedRepo> createViewHolderFor(View view) {
                return new SuggestedRepoViewHolder(view);
            }
        });
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        setResult(RESULT_OK, cloneLauncherIntentFor(adapter.getItem(position).getURI().toString()));
        finish();
    }
}
