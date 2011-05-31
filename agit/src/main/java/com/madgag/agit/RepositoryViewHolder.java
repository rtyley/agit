package com.madgag.agit;

import android.view.View;
import android.widget.TextView;
import com.madgag.agit.views.TextUtil;
import com.madgag.android.listviews.ViewHolder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.madgag.agit.R.id.*;
import static com.madgag.agit.Repos.niceNameFor;
import static com.madgag.agit.Time.timeSinceSeconds;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;

public class RepositoryViewHolder implements ViewHolder<RepoSummary> {
    private final TextView title,detail, commitTime;

    public RepositoryViewHolder(View v) {
        title = (TextView) v.findViewById(repo_name);
        detail = (TextView) v.findViewById(commit_subject);
        commitTime = (TextView) v.findViewById(commit_time);
    }

    public void updateViewFor(RepoSummary repoSummary) {
        title.setText(niceNameFor(repoSummary.getRepo()));
        detail.setText(repoSummary.getRepo().getDirectory().getAbsolutePath());
        String commitTimeText="...";
        RevCommit latestCommit = repoSummary.getLatestCommit();
        if (latestCommit!=null) {
            commitTimeText=Time.timeSinceSeconds(latestCommit.getCommitTime());
        }
        commitTime.setText(commitTimeText+ ITALIC_CLIPPING_BUFFER);


    }
}
