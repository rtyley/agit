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

package com.madgag.android.filterable.searchview;


import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.actionbarsherlock.view.MenuItem;

public class HoneycombStrategy implements SearchViewStrategy {

    private static final String TAG = "SVS.Honeycomb";

    @Override
    public void setup(MenuItem searchMenuItem, final OnFilterTextListener onFilterTextListener) {
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newQueryText) {
                Log.d(TAG, "onQueryTextChange : "+newQueryText);
                onFilterTextListener.onFilterTextChange(newQueryText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit : "+query);
                onFilterTextListener.onFilterTextSubmit(query);
                return true;
            }
        });
    }

    @Override
    public void setQueryHint(View actionView, CharSequence hint) {
        ((SearchView) actionView).setQueryHint(hint);
    }
}
