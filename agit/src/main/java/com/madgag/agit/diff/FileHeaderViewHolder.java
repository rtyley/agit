package com.madgag.agit.diff;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.madgag.android.listviews.ViewHolder;
import org.eclipse.jgit.diff.DiffEntry;

import static com.madgag.agit.R.drawable.*;
import static com.madgag.agit.R.id.commit_file_diff_type;
import static com.madgag.agit.R.id.commit_file_textview;

public class FileHeaderViewHolder implements ViewHolder<FileDiff> {

    private static final FilePathDiffer filePathDiffer = new FilePathDiffer();

    private final ImageView changeTypeImageView;
    private final TextView filePathTextView;
    private final boolean expanded;

    public FileHeaderViewHolder(View v, boolean isExpanded) {
        expanded = isExpanded;
        changeTypeImageView = (ImageView) v.findViewById(commit_file_diff_type);
        filePathTextView = (TextView) v.findViewById(commit_file_textview);
    }

    public void updateViewFor(FileDiff fileDiff) {
        DiffEntry diffEntry = fileDiff.getDiffEntry();
        int changeTypeIcon = diff_changetype_modify;
        String filename = diffEntry.getNewPath();
        switch (diffEntry.getChangeType()) {
            case ADD:
                changeTypeIcon = diff_changetype_add;
                break;
            case DELETE:
                changeTypeIcon = diff_changetype_delete;
                filename = diffEntry.getOldPath();
                break;
            case MODIFY:
                changeTypeIcon = diff_changetype_modify;
                break;
            case RENAME:
                changeTypeIcon = diff_changetype_rename;
                filename = filePathDiffer.diff(diffEntry.getOldPath(), diffEntry.getNewPath());
                break;
            case COPY:
                changeTypeIcon = diff_changetype_add;
                break;
        }

        filePathTextView.setText(filename);
        changeTypeImageView.setImageResource(changeTypeIcon);
    }

    public boolean isExpanded() {
        return expanded;
    }
}
