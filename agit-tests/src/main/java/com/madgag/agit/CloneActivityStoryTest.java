package com.madgag.agit;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import com.github.calculon.CalculonStoryTest;

public class CloneActivityStoryTest extends CalculonStoryTest<Clone> {
	
	private String existingFolder = File.listRoots()[0].getAbsolutePath();

	public CloneActivityStoryTest() {
		super("com.agit",Clone.class);
	}
	
    public void testCloneButtonDisabledIfGitDirAlreadyExists() {
        assertThat(R.id.GitDirEditText).setText(existingFolder).implies(R.id.GoCloneButton).isDisabled();
    }
    
    public void testWarningShownIfGitDirAlreadyExists() {
        assertThat(R.id.GitDirEditText).setText(existingFolder).implies(R.id.GitDirWarning).isVisible();
    }
   
    public void testCloneButtonEnabledIfGitDirDoesNotAlreadyExist() {
        assertThat(R.id.GitDirEditText).setText(newFolder()).implies(R.id.GoCloneButton).isEnabled();
    }
    
    public void testWarningNotShownIfGitDirDoesNotAlreadyExist() {
        assertThat(R.id.GitDirEditText).setText(newFolder()).implies(R.id.GitDirWarning).isInvisible();
    }

	private String newFolder() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		return path.getAbsolutePath()+"/32324432";
	}

}
