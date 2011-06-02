package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.agit.RDTRemote;
import com.madgag.agit.RepositoryManagementActivity;

import java.util.List;

public class LatestCommitView extends LinearLayout {
    @Inject
    public LatestCommitView(Context context, LayoutInflater layoutInflater) {
        super(context);
        layoutInflater.inflate(R.layout.latest_commit_view, this);
    }
}
