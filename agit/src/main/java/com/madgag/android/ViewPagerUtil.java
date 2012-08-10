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

package com.madgag.android;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.madgag.android.filterable.OnSearchRequestedListener;

public class ViewPagerUtil {

    private static final String TAG = "ViewPagerUtil";

    public static Fragment currentFragmentFor(ViewPager viewPager) {
        return (Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
    }

    public static Fragment onSearchRequestedForCurrentFragment(ViewPager viewPager) {
        Fragment fragment = currentFragmentFor(viewPager);
        Log.d(TAG, "want to invoke onSearchRequested for current fragment " + fragment);
        if (fragment instanceof OnSearchRequestedListener) {
            ((OnSearchRequestedListener) fragment).onSearchRequested();
        }
        return fragment;
    }
}
