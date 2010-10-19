package com.madgag.agit;

import java.io.File;

import com.github.calculon.CalculonStoryTest;

public class CloneActivityStoryTest extends CalculonStoryTest<Clone> {
	
	private String existingFolder = File.listRoots()[0].getAbsolutePath();

	public CloneActivityStoryTest() {
		super("com.agit",Clone.class);
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
        assertThat(R.id.CloneUrlEditText).setText(existingFolder).implies(R.id.GitDirWarning).isInvisible();
    }
}
