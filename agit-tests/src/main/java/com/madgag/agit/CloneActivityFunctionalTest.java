package com.madgag.agit;

import java.io.File;

import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.github.calculon.CalculonStoryTest;

public class CloneActivityFunctionalTest extends CalculonStoryTest<Clone> {
	private EditText cloneUrlEditText;
	private Button button;
	
	private String existingFolder = File.listRoots()[0].getAbsolutePath();

	public CloneActivityFunctionalTest() {
		super("com.agit",Clone.class);
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

	
    public void testCloneButtonDisabledIfGitDirAlreadyExists() {
        assertThat(R.id.CloneUrlEditText).setText(existingFolder).implies(R.id.GoCloneButton).isDisabled();
    }
    
    public void testWarningShownIfGitDirAlreadyExists() {
        assertThat(R.id.CloneUrlEditText).setText(existingFolder).implies(R.id.GitDirWarning).isVisible();
    }
    
    public void testCloneButtonEnabledIfGitDirDoesNotAlreadyExist() {
        assertThat(R.id.CloneUrlEditText).setText(existingFolder).implies(R.id.GoCloneButton).isEnabled();
    }
    
    public void testWarningNotShownIfGitDirDoesNotAlreadyExist() {
        assertThat(R.id.CloneUrlEditText).setText(existingFolder).implies(R.id.GitDirWarning).isVisible();
    }
}
