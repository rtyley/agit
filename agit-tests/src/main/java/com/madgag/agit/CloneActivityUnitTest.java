package com.madgag.agit;

import java.io.File;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class CloneActivityUnitTest extends ActivityUnitTestCase<Clone> {
	private EditText cloneUrlEditText;
	private Button button;

	public CloneActivityUnitTest() {
		super(Clone.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Clone activity = getActivity();
		Log.i("CloneActivityUnitTest", "Lawks "+activity);
		cloneUrlEditText = (EditText) activity.findViewById(R.id.CloneUrlEditText);
		button = (Button) activity.findViewById(R.id.GoCloneButton);
		
	}

	@SmallTest
	public void testPreconditions() {
		assertEquals("", cloneUrlEditText.getText().toString());
	}

	@SmallTest
	public void testWarningShownIfGitDirAlreadyExists() throws Exception {
		final File existingDir=File.listRoots()[0];
		getInstrumentation().runOnMainSync(new Runnable() {
			public void run() {
				cloneUrlEditText.setText(existingDir.getAbsolutePath());
			}
		});
		getInstrumentation().waitForIdleSync();
		assertFalse(button.isEnabled());
	}
}
