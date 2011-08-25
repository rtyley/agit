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

import com.github.calculon.CalculonStoryTest;

import java.io.File;

public class CloneLauncherActivityStoryTest extends CalculonStoryTest<CloneLauncherActivity> {
	
	private String existingFolder = File.listRoots()[0].getAbsolutePath();

	public CloneLauncherActivityStoryTest() {
		super("com.madgag.agit",CloneLauncherActivity.class);
	}
	
	public void testDefaultsToUseDefaultGitDirLocation() {
		assertThat(R.id.UseDefaultGitDirLocation).isChecked();
	}
	
    public void testCloneButtonDisabledAndWarningShownIfGitDirAlreadyExists() {
    	setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
        assertThat(R.id.GitDirEditText).setText(existingFolder).implies(R.id.GoCloneButton).isDisabled();
        assertThat(R.id.CloneReadinessMessage).isVisible();
    }
    
    public void testDoesNotCrashDueToStackOverflow() {
    	setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
    	setUp(R.id.CloneUrlEditText).setText("/example/project").now();
        assertThat(R.id.UseDefaultGitDirLocation).setChecked(true).implies(R.id.UseDefaultGitDirLocation).isEnabled();
    }
    
    
//    public void testCheckoutToCustomLocationWorks() {
//        File customLocation = tempFolder();
//        setUp(R.id.CloneUrlEditText).setText("git://github.com/agittest/small-project.git").now();
//        setUp(R.id.UseDefaultGitDirLocation).setChecked(false).now();
//        setUp(R.id.GitDirEditText).setText(customLocation.getAbsolutePath()).now();
//        
//        setUp(R.id.GoCloneButton).click().now();
//    }

}
