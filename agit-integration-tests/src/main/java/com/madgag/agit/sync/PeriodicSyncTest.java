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
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import com.madgag.agit.AgitTestApplication;
import com.madgag.agit.operation.lifecycle.OperationLifecycleSupport;
import com.madgag.agit.operations.*;
import com.madgag.agit.sync.AccountAuthenticatorService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;
import roboguice.test.RoboUnitTestCase;
import roboguice.util.RoboLooperThread;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import static android.os.Build.VERSION_CODES.FROYO;
import static com.madgag.agit.GitTestUtils.*;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static com.madgag.agit.matchers.HasGitObjectMatcher.hasGitObject;
import static com.madgag.hamcrest.FileExistenceMatcher.exists;
import static com.madgag.hamcrest.FileLengthMatcher.ofLength;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class PeriodicSyncTest extends RoboUnitTestCase<AgitTestApplication> {

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
