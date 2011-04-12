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

import static org.apache.commons.io.FileUtils.deleteDirectory;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

public class RepoDeleter extends AsyncTask<Void, Void, Void> {
	
	public static final String TAG = "RepoDeleter";
	
	public static final String REPO_DELETE_COMPLETED = "git.operation.repo.delete.completed";
	
	private final File gitdir;
	private final Context context;
    private final File topFolderToDelete;

    RepoDeleter(Repository repository, Context context) {
        this.gitdir = repository.getDirectory();
        this.topFolderToDelete = repository.isBare()?repository.getDirectory():repository.getWorkTree();
		this.context = context;
    }
	
	@Override
	protected void onPreExecute () {
		// show 'deleting' dialog in RMA
	}
	
	@Override
	protected Void doInBackground(Void... args) {
    	try {
    		Log.d(TAG, "Deleting : "+topFolderToDelete);
			deleteDirectory(topFolderToDelete);
			Log.d(TAG, "Deleted : "+topFolderToDelete);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	@Override
    protected void onPostExecute(Void result) {
        // finish the RMA, which should wipe the progress bar on it as well.
		Intent deletionBroadcast = new GitIntentBuilder(REPO_DELETE_COMPLETED).gitdir(gitdir).toIntent();
		context.sendBroadcast(deletionBroadcast);
		Log.d(TAG, "Sent broadcast : "+deletionBroadcast);
    }

}
