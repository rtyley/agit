package com.madgag.agit;

import static com.madgag.agit.GitOperationsServiceTest.newFolder;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

import com.github.calculon.CalculonStoryTest;

public class CloneActivityStoryTest extends CalculonStoryTest<Clone> {
	
	private String existingFolder = File.listRoots()[0].getAbsolutePath();

	public CloneActivityStoryTest() {
		super("com.agit",Clone.class);
	}
	
	public void testDefaultsToUseDefaultGitDirLocation() {
		assertThat(R.id.UseDefaultGitDirLocation).isChecked();
	}
	
    public void testCloneButtonDisabledAndWarningShownIfGitDirAlreadyExists() {
    	setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
        assertThat(R.id.GitDirEditText).setText(existingFolder).implies(R.id.GoCloneButton).isDisabled();
        assertThat(R.id.GitDirWarning).isVisible();
    }
    
    public void testWarningNotShownIfGitDirDoesNotAlreadyExist() {
    	setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
        assertThat(R.id.GitDirEditText).setText(newFolder().getAbsolutePath()).implies(R.id.GitDirWarning).isInvisible();
    }
    
    public void testDoesNotCrashDueToStackOverflow() {
    	setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
    	setUp(R.id.CloneUrlEditText).setText("/example/project").now();
        assertThat(R.id.UseDefaultGitDirLocation).setChecked(true).implies(R.id.UseDefaultGitDirLocation).isEnabled();
    }
    
    
//    public void testCheckoutToCustomLocationWorks() {
//        File customLocation = newFolder();
//        setUp(R.id.CloneUrlEditText).setText("git://github.com/agittest/small-project.git").now();
//        setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
//        setUp(R.id.GitDirEditText).setText(customLocation.getAbsolutePath()).now();
//        
//        setUp(R.id.GoCloneButton).click().now();
//    }

}
