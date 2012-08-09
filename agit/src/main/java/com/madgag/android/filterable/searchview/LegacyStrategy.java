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


import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;

public class LegacyStrategy implements SearchViewStrategy {

    @Override
    public void setup(MenuItem searchMenuItem, final OnFilterTextListener onFilterTextListener) {
        final TextView actionView = (TextView) searchMenuItem.getActionView();
        actionView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onFilterTextListener.onFilterTextChange(s.toString());
            }
        });

        actionView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onFilterTextListener.onFilterTextSubmit(v.getText().toString());
                return true;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                InputMethodManager imm = inputMethodManager();
                if (imm != null) {
                    imm.hideSoftInputFromWindow(actionView.getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                actionView.post(new Runnable() {
                    @Override
                    public void run() {
                        actionView.requestFocus();
                        inputMethodManager().showSoftInput(actionView, SHOW_IMPLICIT);
                    }
                });
                return true;
            }

            private InputMethodManager inputMethodManager() {
                return (InputMethodManager) actionView.getContext().getSystemService(INPUT_METHOD_SERVICE);
            }
        });
    }

    @Override
    public void setQueryHint(View actionView, CharSequence hint) {
        ((TextView) actionView).setHint(hint);
    }
}
