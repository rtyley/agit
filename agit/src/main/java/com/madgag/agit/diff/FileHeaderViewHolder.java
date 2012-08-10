/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.diff;

import static com.madgag.agit.R.drawable.diff_changetype_add;
import static com.madgag.agit.R.drawable.diff_changetype_delete;
import static com.madgag.agit.R.drawable.diff_changetype_modify;
import static com.madgag.agit.R.drawable.diff_changetype_rename;
import static com.madgag.agit.R.id.commit_file_diff_type;
import static com.madgag.agit.R.id.commit_file_textview;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.madgag.android.listviews.ViewHolder;

import org.eclipse.jgit.diff.DiffEntry;

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
