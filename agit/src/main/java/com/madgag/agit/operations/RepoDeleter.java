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

package com.madgag.agit.operations;

import android.util.Log;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Repository;
import roboguice.inject.InjectResource;

import java.io.File;
import java.io.IOException;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static com.madgag.agit.R.string.delete_repo;
import static com.madgag.agit.git.Repos.topDirectoryFor;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class RepoDeleter extends GitOperation {
	
	public static final String TAG = "RepoDeleter";

    @Inject RepoUpdateBroadcaster repoUpdateBroadcaster;
	@InjectResource(delete_repo) String opName;

    private final File topFolderToDelete;

    public RepoDeleter(Repository repository) {
        super(repository.getDirectory());
        this.topFolderToDelete = topDirectoryFor(repository);
    }

    public OpNotification execute() {
    	try {
    		Log.d(TAG, "Deleting : "+topFolderToDelete);
			deleteDirectory(topFolderToDelete);
			Log.d(TAG, "Deleted : "+topFolderToDelete);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        repoUpdateBroadcaster.broadcastUpdate();
		return new OpNotification(stat_sys_download_done, "Deleted", "Delete completed", gitdir.getAbsolutePath());
    }


    public int getOngoingIcon() {
		return stat_sys_download;
	}

	@Override
    public String getTickerText() {
		return "Deleting " + gitdir;
	}

	public String getName() {
		return opName;
	}

	public String getDescription() {
		return "deleting " + gitdir;
	}

    @Override
	public CharSequence getUrl() {
		return "";
	}

	public String getActionTitle() {
		return "Deleting Repo";
	}

    public String toString() {
        return getClass().getSimpleName()+"["+gitdir+"]";
    }
}
