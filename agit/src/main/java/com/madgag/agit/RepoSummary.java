package com.madgag.agit;

import com.google.common.base.Function;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.transform;
import static com.madgag.agit.Repos.COMMIT_TIME_ORDERING;
import static com.madgag.agit.Repos.knownRepos;

public class RepoSummary implements HasLatestCommit {

    private final static Function<File,RepoSummary> REPO_SUMMARY_FOR_GITDIR = new Function<File, RepoSummary>() {
        public RepoSummary apply(File gitdir) {
            try {
                Repository repo = new FileRepository(gitdir);
                List<RDTBranch.BranchSummary> branchSummaries = new RDTBranch(repo).getAll();
                RevCommit latestCommit = branchSummaries.isEmpty()?null: branchSummaries.get(0).getLatestCommit();
                return new RepoSummary(repo, latestCommit);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    };

//    	public final static Function<BranchSummary, RevCommit> LATEST_COMMIT_FOR_BRANCH = new Function<BranchSummary, RevCommit>() {
//            public RevCommit apply(BranchSummary branch) {
//                return branch.getHeadCommit();
//            }
//        };
//
//        public final static Ordering<RepoSummary> REPO_LATEST_COMMIT_ORDERING =
//            Ordering.from(Repos.SORT_COMMIT_BY_COMMIT_TIME).onResultOf(HEAD_COMMIT_FOR_BRANCH);

    public static List<RepoSummary> getAllReposOrderChronologically() {
        return COMMIT_TIME_ORDERING.sortedCopy(transform(knownRepos(), REPO_SUMMARY_FOR_GITDIR));
    }

    private final Repository repo;

    private final RevCommit latestCommit;

    public RepoSummary(Repository repo, RevCommit latestCommit) {
        this.repo = repo;
        this.latestCommit = latestCommit;
    }

    public RevCommit getLatestCommit() {
        return latestCommit;
    }

    public Repository getRepo() {
        return repo;
    }
}
