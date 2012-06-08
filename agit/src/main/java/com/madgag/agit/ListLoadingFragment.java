package com.madgag.agit;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;

public abstract class ListLoadingFragment<E> extends RoboSherlockListFragment implements LoaderCallbacks<List<E>> {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListShown(false);
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

        if (isResumed())
            setListShown(true);
        else
            setListShownNoAnimation(true);
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

    protected abstract ViewHoldingListAdapter<E> adapterFor(List<E> items);

    @Override
    public void onLoaderReset(Loader<List<E>> listLoader) {
    }
}
