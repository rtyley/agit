/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.transform;
import static com.madgag.agit.git.Repos.COMMIT_TIME_ORDERING;
import static com.madgag.agit.git.Repos.reposInDefaultRepoDir;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.madgag.agit.git.model.HasLatestCommit;
import com.madgag.agit.git.model.RDTBranch;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

public class RepoSummary implements HasLatestCommit {

    public final static Function<File, RepoSummary> REPO_SUMMARY_FOR_GITDIR = new Function<File, RepoSummary>() {
        public RepoSummary apply(File gitdir) {
            try {
                Repository repo = new FileRepository(gitdir);

                return new RepoSummary(repo);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    };

    private static final Predicate<RepoSummary> NON_NULL_REPO = new Predicate<RepoSummary>() {
        public boolean apply(RepoSummary repo) {
            return repo != null;
        }
    };

    public static List<RepoSummary> sortReposByLatestCommit(List<RepoSummary> repoSummaries) {
        return COMMIT_TIME_ORDERING.sortedCopy(filter(repoSummaries, NON_NULL_REPO));
    }


    private final Repository repo;

    public final RDTBranch.BranchSummary mostlyRecentlyUpdatedBranch;

    @Inject
    public RepoSummary(Repository repo) {
        this.repo = repo;
        List<RDTBranch.BranchSummary> branchSummaries = new RDTBranch(repo).getAll();
        mostlyRecentlyUpdatedBranch = branchSummaries.isEmpty() ? null : branchSummaries.get(0);
    }

    public boolean hasCommits() {
        return mostlyRecentlyUpdatedBranch != null;
    }

    public RevCommit getLatestCommit() {
        return hasCommits()?mostlyRecentlyUpdatedBranch.getLatestCommit():null;
    }

    public Repository getRepo() {
        return repo;
    }
}
