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

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ListAdapter;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;

public abstract class ListLoadingFragment<E> extends RoboSherlockListFragment implements LoaderCallbacks<List<E>> {

    private static final String TAG = "ListLoadingFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void refresh() {
        Log.d(getClass().getSimpleName(), "Refresh requested...");
        if (getActivity() != null)
            getLoaderManager().restartLoader(0, null, this);
    }

    public void onLoadFinished(Loader<List<E>> loader, List<E> items) {
        setList(items);
        if (shouldAnimateShowingList()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    private void setList(List<E> items) {
        @SuppressWarnings("unchecked")
        ViewHoldingListAdapter<E> listAdapter = (ViewHoldingListAdapter<E>) getListAdapter();
        if (listAdapter == null) {
            setListAdapter(adapterFor(items));
        } else {
            listAdapter.setList(items);
        }
    }

    public void setListAdapter(ListAdapter adapter) {
        if (!shouldAnimateShowingList()) {
            // anticipates and negates the animation that the default setListAdapter() implementation always does
            setListShownNoAnimation(true);
        }
        super.setListAdapter(adapter);
    }

    /**
     * List-Shown-Fade-Transition animation causes jerkyness in ViewPager swipe on ICS 4.0.4 Galaxy Nexus, so
     * don't do the animation if the fragment isn't visible. Note that FragmentPagerAdapter currently updates
     * 'userVisibleHint' but FragmentStatePagerAdapter currently does not.
     */
    private boolean shouldAnimateShowingList() {
        return isResumed() && getUserVisibleHint();
    }

    protected abstract ViewHoldingListAdapter<E> adapterFor(List<E> items);

    @Override
    public void onLoaderReset(Loader<List<E>> listLoader) {
    }
}
