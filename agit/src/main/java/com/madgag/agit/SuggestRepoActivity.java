package com.madgag.agit;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import roboguice.activity.RoboListActivity;

import java.util.List;

import static android.R.layout.two_line_list_item;
import static com.madgag.agit.CloneLauncherActivity.cloneLauncherIntentFor;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import static java.util.Arrays.asList;

public class SuggestRepoActivity extends RoboListActivity {
    public static final String TAG = "SRA";
    private ViewHoldingListAdapter<SuggestedRepo> adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind to our new adapter.
        List<SuggestedRepo> voo = asList(
        new SuggestedRepo("JQuery", "git://github.com/jquery/jquery.git"),
        new SuggestedRepo("Scalatra", "git://github.com/scalatra/scalatra.git"),
        new SuggestedRepo("JGit", "git://egit.eclipse.org/jgit.git")
        );
        adapter = new ViewHoldingListAdapter<SuggestedRepo>(voo, viewInflatorFor(this, two_line_list_item), new ViewHolderFactory<SuggestedRepo>() {
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
