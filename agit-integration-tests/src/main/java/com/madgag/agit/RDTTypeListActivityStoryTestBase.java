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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class RDTTypeListActivityStoryTestBase<T extends RDTypeListActivity> extends
        ActivityInstrumentationTestCase2<T> {

    private final static String TAG = RDTTypeListActivityStoryTestBase.class.getSimpleName();

    public RDTTypeListActivityStoryTestBase(String pkg, Class<T> activityClass) {
        super(pkg, activityClass);
    }

    protected void checkCanSelectEveryItemInNonEmpty(ListView listView) {
        assertThat(listView.getCount() > 0, is(true));
        for (int index = 0; index < listView.getCount(); ++index) {
            View itemView = getItemViewBySelecting(listView, index);
            Log.d(TAG, "view=" + itemView);
        }
    }

    protected View getItemViewBySelecting(final ListView listView, final int index) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                listView.setSelection(index);
            }
        });
        getInstrumentation().waitForIdleSync();
        return listView.getSelectedView();
    }
}
