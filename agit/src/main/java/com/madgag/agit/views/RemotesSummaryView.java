package com.madgag.agit.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTRemote;

public class RemotesSummaryView extends LinearLayout {
    private final TextView remoteSummaryTextView;
    private final RDTRemote repoRemotes;
    private static final String TAG = "RSV";

    @Inject
    public RemotesSummaryView(Context context, LayoutInflater layoutInflater, RDTRemote repoRemotes) {
        super(context);
        this.repoRemotes = repoRemotes;
        layoutInflater.inflate(R.layout.remotes_summary_view, this);
        remoteSummaryTextView = (TextView) findViewById(R.id.remote_summary_thing);
        Log.d(TAG,"remoteSummaryTextView : "+remoteSummaryTextView);
        Log.d(TAG,"getChildCount() : "+getChildCount());

        updateStuff();
    }

    private void updateStuff() {
        CharSequence text = repoRemotes.summariseAll();
        Log.d(TAG,"Remote summary : "+text);
        remoteSummaryTextView.setText(text);
    }

}
