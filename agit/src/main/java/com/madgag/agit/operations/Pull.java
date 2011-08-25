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

import android.content.Context;
import com.google.inject.Inject;
import com.madgag.agit.GitFetchService;
import com.madgag.agit.R;
import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.FetchResult;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import static android.R.drawable.stat_sys_download;
import static android.R.drawable.stat_sys_download_done;
import static com.madgag.agit.R.string.*;
import static com.madgag.agit.git.Repos.remoteConfigFor;
import static org.eclipse.jgit.api.RebaseCommand.Operation.BEGIN;
import static org.eclipse.jgit.lib.ConfigConstants.*;
import static org.eclipse.jgit.lib.ConfigConstants.CONFIG_KEY_REBASE;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.RepositoryState.SAFE;

public class Pull extends GitOperation {

	public static final String TAG = "Pull";
    private final Repository repo;
    //private final RemoteConfig remote;

	@Inject GitFetchService fetchService;
    @Inject Context context;
    private static final String DOT = ".";

    public Pull(Repository repository) {
        super(repository.getDirectory());
        this.repo = repository;
    }

	public int getOngoingIcon() {
		return stat_sys_download;
	}

	public String getTickerText() {
		return "Pulling!";
	}

    
    public RuntimeException exceptionMessage(int resId, java.lang.Object... formatArgs) {
        return new RuntimeException(context.getString(resId, formatArgs));
    }

	public OpNotification execute() {
//		Log.d(TAG, "start execute() : repository=" + repository + " remote=" + remote.getName());
//		FetchResult r = fetchService.fetch(remote, toFetch);
//		return new OpNotification(stat_sys_download_done,"Fetch complete", "Fetched "+remote.getName(), fetchUrl());


        // monitor.beginTask(JGitText.get().pullTaskName, 2);

		String branchName;
		try {
			String fullBranch = repo.getFullBranch();
			if (!fullBranch.startsWith(R_HEADS)) {
                throw exceptionMessage(can_not_pull_on_a_repo_with_detached_head, repo.getRepositoryState());
			}
			branchName = fullBranch.substring(R_HEADS.length());
		} catch (IOException e) {
			throw new JGitInternalException(
					JGitText.get().exceptionCaughtDuringExecutionOfPullCommand,
					e);
		}

		if (!repo.getRepositoryState().equals(SAFE))
            throw exceptionMessage(can_not_pull_on_a_repo_with_state, repo.getRepositoryState());

        Config repoConfig = repo.getConfig();
        
        // get the configured remote for the currently checked out branch
        // stored in configuration key branch.<branch name>.remote
		String remote = repoConfig.getString(CONFIG_BRANCH_SECTION, branchName, CONFIG_KEY_REMOTE);
		if (remote == null)
			// fall back to default remote
			remote = Constants.DEFAULT_REMOTE_NAME;

		// get the name of the branch in the remote repository
		// stored in configuration key branch.<branch name>.merge
		String remoteBranchName = getConfigOrDie(repoConfig, CONFIG_BRANCH_SECTION, branchName, CONFIG_KEY_MERGE);

		final boolean isRemote = !remote.equals(".");
		String remoteUri;
		FetchResult fetchRes;
		if (isRemote) {
            remoteUri = getConfigOrDie(repoConfig, CONFIG_REMOTE_SECTION, remote, CONFIG_KEY_URL);

            checkCancellation();

            fetchRes = fetchService.fetch(remoteConfigFor(repo, remote), null);
		} else {
			// we can skip the fetch altogether
			remoteUri = "local repository";
			fetchRes = null;
		}

		// monitor.update(1);

        checkCancellation();

        AnyObjectId commitToMerge = commitToMergeFor(fetchRes, remoteBranchName, isRemote);


        PullResult result;
		if (branchUsesPullRebase(branchName, repoConfig)) {
			RebaseCommand rebase = Git.wrap(repo).rebase();
			try {
				RebaseResult rebaseRes = rebase.setUpstream(commitToMerge)
						// .setProgressMonitor(monitor)
                        .setOperation(BEGIN).call();
			} catch (NoHeadException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (RefNotFoundException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (JGitInternalException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (GitAPIException e) {
				throw new JGitInternalException(e.getMessage(), e);
			}
		} else {
			MergeCommand merge = Git.wrap(repo).merge();
			String name = "branch \'"
					+ Repository.shortenRefName(remoteBranchName) + "\' of "
					+ remoteUri;
			merge.include(name, commitToMerge);
			MergeResult mergeRes;
			try {
				mergeRes = merge.call();
				// monitor.update(1);
			} catch (NoHeadException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (ConcurrentRefUpdateException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (CheckoutConflictException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (InvalidMergeHeadsException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (WrongRepositoryStateException e) {
				throw new JGitInternalException(e.getMessage(), e);
			} catch (NoMessageException e) {
				throw new JGitInternalException(e.getMessage(), e);
			}
		}
        return new OpNotification(stat_sys_download_done,"Pull complete", "Pulled "+remote);
    }

    private AnyObjectId commitToMergeFor(FetchResult fetchRes, String remoteBranchName, boolean remote) {
        // we check the updates to see which of the updated branches
        // corresponds
        // to the remote branch name
        AnyObjectId commitToMerge;
        if (remote) {
            Ref r = null;
            if (fetchRes != null) {
                r = fetchRes.getAdvertisedRef(remoteBranchName);
                if (r == null)
                    r = fetchRes.getAdvertisedRef(R_HEADS + remoteBranchName);
            }
            if (r == null)
                throw new JGitInternalException(MessageFormat.format(JGitText
                        .get().couldNotGetAdvertisedRef, remoteBranchName));
            else
                commitToMerge = r.getObjectId();
        } else {
            try {
                commitToMerge = repo.resolve(remoteBranchName);
                if (commitToMerge == null)
                    throw exceptionMessage(R.string.ref_not_resolved, remoteBranchName);
            } catch (IOException e) {
                throw new JGitInternalException(
                        JGitText.get().exceptionCaughtDuringExecutionOfPullCommand,
                        e);
            }
        }
        return commitToMerge;
    }

    private void checkCancellation() {
        if (isCancelled())
            throw exceptionMessage(pull_cancelled);
    }

    private String getConfigOrDie(Config repoConfig, String section, String subsection, String name) {
        String val = repoConfig.getString(section, subsection, name);
        if (val == null) {
            throw exceptionMessage(missing_configuration_for_key, section, subsection, name);
        }
        return val;
    }

    private boolean branchUsesPullRebase(String branchName, Config repoConfig) {
        return repoConfig.getBoolean(CONFIG_BRANCH_SECTION, branchName, CONFIG_KEY_REBASE, false);
    }

    public String getName() {
		return "Pull";
	}

	public String getDescription() {
		return "Pulling!";
	}

	public CharSequence getUrl() {
		return "Arrgg";
	}

	public String getShortDescription() {
		return "Pulling";
	}

	public File getGitDir() {
		return repo.getDirectory();
	}
}
