package com.madgag.agit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.google.inject.Inject;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboListActivity;
import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.InjectView;

import java.util.List;

import static android.R.layout.simple_list_item_2;
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
        setContentView(R.layout.list_activity_layout);
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new HomeAction(this));
		actionBar.setTitle("Some example repos...");
        // Bind to our new adapter.
        List<SuggestedRepo> suggestedRepos = asList(
                new SuggestedRepo("Connectbot", "git://github.com/kruton/connectbot.git"),
                new SuggestedRepo("Redis", "git://github.com/antirez/redis.git"),
                new SuggestedRepo("JQuery", "git://github.com/jquery/jquery.git"),
                new SuggestedRepo("sshj","git://github.com/shikhar/sshj.git"),
                new SuggestedRepo("Scalatra", "git://github.com/scalatra/scalatra.git"),
                new SuggestedRepo("JGit", "git://egit.eclipse.org/jgit.git"),
                new SuggestedRepo("MongoDB", "git://github.com/mongodb/mongo.git")
        );
        adapter = new ViewHoldingListAdapter<SuggestedRepo>(suggestedRepos, viewInflatorFor(this, simple_list_item_2), new ViewHolderFactory<SuggestedRepo>() {
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
