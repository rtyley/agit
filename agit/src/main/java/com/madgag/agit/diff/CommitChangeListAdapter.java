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

import static com.madgag.agit.R.layout.file_change_header_expanded_view;
import static com.madgag.agit.R.layout.file_change_header_view;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.madgag.agit.diff.DiffSliderView.OnStateUpdateListener;

import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitChangeListAdapter extends BaseExpandableListAdapter implements OnStateUpdateListener,
        DiffStateProvider {

    private static final String TAG = "CommitChangeListAdapter";

    private LayoutInflater mInflater;
    private final DiffSliderView diffSlider;
    private final ExpandableListView expandableList;
    private final Context context;
    private final Repository repository;
    private final RevCommit afterCommit, beforeCommit;
    private final List<FileDiff> fileDiffs;

    private float state = 0.5f;

    public CommitChangeListAdapter(Repository repository, RevCommit afterCommit, RevCommit beforeCommit,
                                   DiffSliderView diffSlider, ExpandableListView expandableList, Context context) {
        Log.d(TAG, "before : "+ beforeCommit);
        Log.d(TAG, "after  : "+ afterCommit);
        this.repository = repository;
        this.afterCommit = afterCommit;
        this.beforeCommit = beforeCommit;
        this.diffSlider = diffSlider;
        this.expandableList = expandableList;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        diffSlider.setStateUpdateListener(this);
        try {
            fileDiffs = new CommitDiffer().calculateCommitDiffs(repository, beforeCommit, afterCommit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object getChild(int groupPosition, int childPosition) {
        return fileDiffs.get(groupPosition).getHunks().get(childPosition);
    }

    public long getChildId(int arg0, int arg1) {
        return 0;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Hunk hunk = fileDiffs.get(groupPosition).getHunks().get(childPosition);
        return new HunkDiffView(context, hunk, this);
    }

    public int getChildrenCount(int groupPosition) {
        return fileDiffs.get(groupPosition).getHunks().size();
    }

    public Object getGroup(int index) {
        return fileDiffs.get(index);
    }

    public int getGroupCount() {
        return fileDiffs.size();
    }

    public long getGroupId(int index) {
        return fileDiffs.get(index).hashCode(); // Pretty lame
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newGroupView(isExpanded, parent);
        } else {
            v = convertView;
        }

        FileHeaderViewHolder viewHolder = (FileHeaderViewHolder) v.getTag();
        if (viewHolder != null && viewHolder.isExpanded() != isExpanded) {
            v = newGroupView(isExpanded, parent);
            viewHolder = null;
        }

        if (viewHolder == null) {
            v.setTag(viewHolder = new FileHeaderViewHolder(v, isExpanded));
        }
        viewHolder.updateViewFor(fileDiffs.get(groupPosition));

        return v;
    }

    private View newGroupView(boolean isExpanded, ViewGroup parent) {
        return mInflater.inflate(isExpanded ? file_change_header_expanded_view : file_change_header_view, parent,
                false);
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return false;
    }

    public void onStateChanged(DiffSliderView diffSliderView, float state) {
        setDiffState(state);
    }

    private void setDiffState(float state) {
        this.state = state;
        expandableList.invalidate();
        expandableList.requestLayout();
        Log.d(TAG, "list invalidated state=" + state);
    }

    public float getDiffState() {
        return state;
    }
}