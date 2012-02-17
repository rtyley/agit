/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.sync;

import android.os.Build;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import roboguice.test.RoboUnitTestCase;

import java.lang.reflect.Method;

import static android.os.Build.VERSION_CODES.FROYO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class PeriodicSyncTest extends AndroidTestCase {

	private static final String TAG = "PST";

    @SmallTest
	public void testPeriodicSyncMethodAvailable() throws Exception {
        Method m = AccountAuthenticatorService.methodContentResolver_addPeriodicSync;
        boolean expectPeriodicSyncAvailable = Build.VERSION.SDK_INT >= FROYO;
        Log.d(TAG,"Expect Periodic-Sync available : "+expectPeriodicSyncAvailable+" method="+m);
        if (expectPeriodicSyncAvailable) {
            assertThat("Required period sync method", m, notNullValue());
            assertThat(m.getName(), is("addPeriodicSync"));
        } else {
            assertThat(m, nullValue());
        }

	}
}
