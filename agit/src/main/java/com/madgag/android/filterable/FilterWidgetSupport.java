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

package com.madgag.android.filterable;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;

import com.actionbarsherlock.view.MenuItem;
import com.madgag.agit.views.TagsSummaryView;
import com.madgag.android.filterable.searchview.HoneycombStrategy;
import com.madgag.android.filterable.searchview.LegacyStrategy;
import com.madgag.android.filterable.searchview.SearchViewStrategy;

public class FilterWidgetSupport {

    private static final String TAG = "FilterWidgetSupport";

    private final SearchViewStrategy searchViewStrategy;
    private final MenuItem searchMenuItem;


    public FilterWidgetSupport(MenuItem searchMenuItem, final Filterable filterable) {
        this.searchMenuItem = searchMenuItem;
        searchViewStrategy = (Build.VERSION.SDK_INT >= HONEYCOMB) ? new HoneycombStrategy() : new LegacyStrategy();
        searchViewStrategy.setup(searchMenuItem, new SearchViewStrategy.OnFilterTextListener() {
            public void onFilterTextChange(String newText) {
                Filter filter = filterable.getFilter();
                if (filter != null)
                    filter.filter(newText);
            }

            public void onFilterTextSubmit(String query) {
                Log.d(TAG, "onFilterTextSubmit : '"+query+"'");
                clearSearchViewOnSubmit();
            }
        });
    }

    public void onSearchRequested() {
        searchMenuItem.expandActionView();
    }

    /**
     * When the search is "committed" by the user, then hide the keyboard so the user can more easily browse the list
     * of results.
     */
    protected void clearSearchViewOnSubmit() {
        View searchView = searchMenuItem.getActionView();
        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(INPUT_METHOD_SERVICE);
        Log.d(TAG, "clearSearchViewOnSubmit : "+searchView+" "+imm);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
        searchView.clearFocus();
    }

    public void setQueryHint(CharSequence hint) {
        searchViewStrategy.setQueryHint(searchMenuItem.getActionView(), hint);
    }
}
