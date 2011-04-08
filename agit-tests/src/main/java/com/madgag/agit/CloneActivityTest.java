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

package com.madgag.agit;

import static android.content.Context.BIND_AUTO_CREATE;

import java.util.Set;

import org.eclipse.jgit.transport.URIish;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.madgag.agit.GitOperationsService.GitOperationsBinder;

public class CloneActivityTest extends ActivityInstrumentationTestCase2<CloneLauncherActivity> {
	private EditText cloneUrlEditText;
	private Button button;
	private GitOperationsService gitOperationsService;
	private final String smallProjectUri = "git://github.com/agittest/small-project.git";

	public CloneActivityTest() {
		super(CloneLauncherActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final CloneLauncherActivity activity = getActivity();
		activity.bindService(new Intent(activity, GitOperationsService.class),
				new ServiceConnection() {
					public void onServiceDisconnected(ComponentName cn) {
						gitOperationsService=null;
					}

					public void onServiceConnected(ComponentName cn, IBinder binder) {
						gitOperationsService = ((GitOperationsBinder) binder).getService();
					}
				}, BIND_AUTO_CREATE);
		cloneUrlEditText = (EditText) activity
				.findViewById(R.id.CloneUrlEditText);
		button = (Button) activity.findViewById(R.id.GoCloneButton);
		
		while (gitOperationsService==null) {
			try {
				Log.i("CAt", "Sleepin... oh yeah");
				Thread.sleep(500L);
			} catch (InterruptedException e) {}
		}
	}

	@SmallTest
	public void testPreconditions() {
		assertEquals("", cloneUrlEditText.getText().toString());
	}

	@SmallTest
	public void testReadOnlyCloneOfPublicSmallProject() throws Exception {
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				cloneUrlEditText.setText(smallProjectUri);
				button.performClick();
			}
		});
		getInstrumentation().waitForIdleSync();
		try {
			Thread.sleep(2000L); // TODO Ah, blaaaaa!
		} catch (InterruptedException e) {}
		//Set<RepositoryOperationContext> rocs = gitOperationsService.getRepositoryOperationContextsFor(new URIish(smallProjectUri));
//		assertTrue("Well, I should be seeing 1 dude "+rocs, rocs.size()==1);
//		RepositoryOperationContext roc = rocs.iterator().next();
//		Notification notification = roc.getCurrentOperation().get();
//		Log.i("CAT", "found... "+notification.tickerText);
//		assertTrue(roc.getRepository().hasObject(ObjectId.fromString("9e0b5e42b3e1c59bc83b55142a8c50dfae36b144")));
		// assertTrue(getInstrumentation().checkMonitorHit(monitor, 1));
	}
}
