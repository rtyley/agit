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

import static java.lang.System.identityHashCode;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.util.FS;

import android.util.Log;

public class Repos {

    private final static String TAG = "Repos";
    
	public static Repository openRepoFor(File gitdir) {
		try {
			Repository repo = RepositoryCache.open(FileKey.lenient(gitdir, FS.DETECTED),false);
			Log.d("REPO", "Opened "+describe(repo));
			return repo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String describe(Repository repository) {
		return repository+" #"+identityHashCode(repository);
	}


	public static RemoteConfig remoteConfigFor(Repository repository, String remoteName) {
		try {
			return new RemoteConfig(repository.getConfig(), remoteName);
		} catch (Exception e) {
			Log.e(TAG, "Couldn't parse config", e);
			throw new RuntimeException(e);
		}
	}
}
