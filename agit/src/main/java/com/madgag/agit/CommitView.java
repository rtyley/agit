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

package com.madgag.agit;

import static com.madgag.agit.R.styleable.CommitView_viewPagerId;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.viewpagerindicator.TabPageIndicator;

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitView extends LinearLayout {

    private static final String TAG = "CommitView";

    private final LayoutInflater layoutInflater;

    private Repository repository;
    private PlotWalk revWalk;

    PlotCommit<PlotLane> commit;

    ViewPager pager;

    TabPageIndicator tabPageIndicator;

    public CommitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(context);

        layoutInflater.inflate(R.layout.commit_view, this);

        pager = (ViewPager) findViewById(R.id.pager);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CommitView);
        int viewPagerId = array.getResourceId(CommitView_viewPagerId, R.id.pager);
        array.recycle();
        Log.d(TAG, "viewPagerId="+viewPagerId);
        pager.setId(viewPagerId);

        tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
    }

    public void setRepositoryContext(Repository repository, PlotWalk revWalk) {
        this.repository = repository;
        this.revWalk = revWalk;
    }

    public void setCommit(final PlotCommit<PlotLane> c) throws IOException {
        this.commit = c;
        CommitViewerActivity commitViewerActivity = (CommitViewerActivity) getContext();
        pager.setAdapter(new CommitPagerAdapter(commitViewerActivity.getSupportFragmentManager(), repository, c));
        tabPageIndicator.setViewPager(pager);
        tabPageIndicator.notifyDataSetChanged();

        Log.d(TAG, "setCommit : " + commit);
    }

    public static class CommitPagerAdapter extends FragmentStatePagerAdapter {

        private final Repository repository;
        private final PlotCommit<PlotLane> commit;

        public CommitPagerAdapter(FragmentManager fm, Repository repository, PlotCommit<PlotLane> c) {
            super(fm);
            this.repository = repository;
            this.commit = c;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CommitDetailsFragment.newInstance(repository.getDirectory(), commit.name());
                case 1:
                    return FileListFragment.newInstance(repository.getDirectory(), commit.name());
                default:
                    return CommitDiffFragment.newInstance(repository.getDirectory(),
                            parentCommit(position).name(), commit.name());
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Commit";
                case 1:
                    return "Files";
                default:
                    return  "Δ " + parentCommit(position).name().substring(0, 4);
            }
        }

        private RevCommit parentCommit(int position) {
            return commit.getParents()[position - 2];
        }

        @Override
        public int getCount() {
            return 2 + commit.getParents().length;
        }
    }

}
