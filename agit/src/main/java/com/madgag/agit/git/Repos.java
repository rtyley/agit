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

package com.madgag.agit.git;

import android.util.Log;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.madgag.agit.git.model.HasLatestCommit;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.WindowCache;
import org.eclipse.jgit.storage.file.WindowCacheConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.identityHashCode;
import static org.eclipse.jgit.lib.Constants.*;
import static org.eclipse.jgit.storage.file.WindowCacheConfig.MB;

public class Repos {

    private final static String TAG = "Repos";

    static {
        // Set memory config appropriate to Android devices
        WindowCacheConfig cfg = new WindowCacheConfig();
        cfg.setPackedGitLimit(2 * MB);
        cfg.setDeltaBaseCacheLimit(2 * MB);
        cfg.setStreamFileThreshold(1 * MB);
        WindowCache.reconfigure(cfg);
    }

	public static List<File> knownRepos() {
		File reposDir = new File(getExternalStorageDirectory(),"git-repos");
		if (!reposDir.exists() && !reposDir.mkdirs()) {
			throw new IllegalStateException("Can't find or create the default it repos dir : "+reposDir);
		}
        List<File> repos = newArrayList();
		for (File repoDir : reposDir.listFiles()) {
            File gitdir = RepositoryCache.FileKey.resolve(repoDir, FS.detect());
			if (gitdir!=null) {
				repos.add(gitdir);
			}
		}
        Log.d(TAG, "Found "+repos.size()+" repos in "+reposDir);
		return repos;
	}

	public static Repository openRepoFor(File gitdir) {
		try {
			Repository repo = RepositoryCache.open(FileKey.lenient(gitdir, FS.DETECTED),false);
			Log.d("REPO", "Opened "+describe(repo));
			return repo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public static String niceNameFor(File gitdir) {
        return niceNameFromNameDirectory(gitdir.getName().equals(DOT_GIT)?gitdir.getParentFile():gitdir);
    }

    public static String niceNameFor(Repository repo) {
        return niceNameFromNameDirectory(topDirectoryFor(repo));
    }

    public static File topDirectoryFor(Repository repo) {
        return repo.isBare()? repo.getDirectory(): repo.getWorkTree();
    }

    private static String niceNameFromNameDirectory(File directoryWithName) {
        String name = directoryWithName.getName();
        if (name.endsWith(DOT_GIT_EXT)) {
            name=name.substring(0, name.length()-DOT_GIT_EXT.length());
        }
        return name;
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

	public static RemoteConfig addRemoteTo(Repository repository, String remoteName, URIish sourceUri)
			throws IOException {
        StoredConfig config = repository.getConfig();
        RemoteConfig remote;
        try {
            remote = new RemoteConfig(config, remoteName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        remote.addURI(sourceUri);
        remote.addFetchRefSpec(new RefSpec().setForceUpdate(true)
                .setSourceDestination(R_HEADS + "*",
                        R_REMOTES + remoteName + "/*"));
        remote.update(config);

		Log.d(TAG, "About to save config...");
		repository.getConfig().save();
		return remote;
	}

    public final static Function<RevCommit, Integer> COMMIT_TIME = new Function<RevCommit, Integer>() {
        public Integer apply(RevCommit commit) {
            return commit==null?0:commit.getCommitTime();
        }
    };

    public final static Function<HasLatestCommit, RevCommit> LATEST_COMMIT = new Function<HasLatestCommit, RevCommit>() {
        public RevCommit apply(HasLatestCommit branch) {
            return branch==null?null:branch.getLatestCommit();
        }
    };

    public final static Ordering<HasLatestCommit> COMMIT_TIME_ORDERING =
            Ordering.natural().reverse().onResultOf(COMMIT_TIME).onResultOf(LATEST_COMMIT);
}
