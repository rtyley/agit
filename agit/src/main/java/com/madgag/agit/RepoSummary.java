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

    private final RevCommit latestCommit;

    @Inject
    public RepoSummary(Repository repo) {
        this.repo = repo;
        List<RDTBranch.BranchSummary> branchSummaries = new RDTBranch(repo).getAll();
        latestCommit = branchSummaries.isEmpty() ? null : branchSummaries.get(0).getLatestCommit();
    }

    public boolean hasCommits() {
        return latestCommit != null;
    }

    public RevCommit getLatestCommit() {
        return latestCommit;
    }

    public Repository getRepo() {
        return repo;
    }
}
