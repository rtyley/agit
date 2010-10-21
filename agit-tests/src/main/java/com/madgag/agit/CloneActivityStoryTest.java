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
	
    public void testCloneButtonDisabledAndWarningShownIfGitDirAlreadyExists() {
    	setUp(R.id.UseDefaultGitDirLocation).uncheck().now();
        assertThat(R.id.GitDirEditText).setText(existingFolder).implies(R.id.GoCloneButton).isDisabled();
        assertThat(R.id.GitDirWarning).isVisible();
    }
    
    public void testWarningNotShownIfGitDirDoesNotAlreadyExist() {
    	setUp(R.id.UseDefaultGitDirLocation).uncheck().now();
        assertThat(R.id.GitDirEditText).setText(newFolder()).implies(R.id.GitDirWarning).isInvisible();
    }

	private String newFolder() {
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		return path.getAbsolutePath()+"/32324432";
	}

}
