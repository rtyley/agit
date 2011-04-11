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

import static com.madgag.agit.Repos.openRepoFor;

import java.io.File;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import android.content.Intent;
import android.util.Log;

public class GitIntents {

	private static final String TAG="GitIntents";

    public static String
            BARE="bare",
            EXTRA_TARGET_DIR="target-dir",
            EXTRA_SOURCE_URI="source-uri";

	public static File directoryFrom(Intent intent) {
		String directory = intent.getStringExtra("directory");
		return new File(directory);
	}
	
	public static File gitDirFrom(Intent intent) {
		String gitdirString = intent.getStringExtra("gitdir");
		Log.i(TAG, "gitdirString = "+gitdirString);
		File gitdir=new File(gitdirString);
		Log.i(TAG, "gitdir for "+intent+" = "+gitdir.getAbsolutePath());
		return gitdir;
	}

	public static String branchNameFrom(Intent intent) {
		return intent.getStringExtra("branch");
	}
	
	public static String tagNameFrom(Intent intent) {
		return intent.getStringExtra("tag");
	}

	public static void addDirectoryTo(Intent intent, File directory) {
		intent.putExtra("directory", directory.getAbsolutePath());
	}	
	
	public static void addGitDirTo(Intent intent, File gitdir) {
		intent.putExtra("gitdir", gitdir.getAbsolutePath());
	}
	
	public static Repository repositoryFrom(Intent intent) {
		return openRepoFor(gitDirFrom(intent));
	}

	public static ObjectId commitIdFrom(Intent intent) {
		return ObjectId.fromString(intent.getStringExtra("commit"));
	}


}
