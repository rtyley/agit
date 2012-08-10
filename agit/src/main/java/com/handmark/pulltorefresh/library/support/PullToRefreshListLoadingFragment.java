package com.handmark.pulltorefresh.library.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.madgag.agit.ListLoadingFragment;

/**
 * Based off https://gist.github.com/2702232 by Chris Banes for his Android-PullToRefresh library.
 *
 * See also https://github.com/chrisbanes/Android-PullToRefresh/issues/3
 */
public abstract class PullToRefreshListLoadingFragment<E> extends ListLoadingFragment<E> {

    private PullToRefreshListView mPullToRefreshListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        ListView lv = (ListView) layout.findViewById(android.R.id.list);
        ViewGroup parent = (ViewGroup) lv.getParent();

        // Iterate through parent's children until we find the ListView
        for (int i = 0, z = parent.getChildCount(); i < z; i++) {
            View child = parent.getChildAt(i);

            if (child == lv) {
                // Remove the ListView first
                parent.removeViewAt(i);

                // Now create ListView, and add it in it's place...
                mPullToRefreshListView = new PullToRefreshListView(getActivity());
                parent.addView(mPullToRefreshListView, i, lv.getLayoutParams());
                break;
            }
        }

        return layout;
    }

    protected PullToRefreshListView getPullToRefreshListView() {
        return mPullToRefreshListView;
    }
}