package com.madgag.agit;

import static org.eclipse.jgit.lib.Repository.shortenRefName;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class RDTBranch extends RepoDomainType<Ref> {

	public RDTBranch(Repository repository) {
		super(repository);
	}

	@Override
	String name() { return "branch"; }
	
	public Collection<Ref> getAll() {
		RefDatabase refDatabase = repository.getRefDatabase();
		try {
			Map<String, Ref> remoteBranchRefs = refDatabase.getRefs(Constants.R_REMOTES);
			return remoteBranchRefs.values();
		} catch (IOException e) { throw new RuntimeException(e); }
	}
	
	@Override
	CharSequence conciseSummary(Ref branchRef) {
		return idFor(branchRef);
	}

	@Override
	String conciseSeparator() {
		return " • ";
	}

	@Override
	CharSequence conciseSummaryTitle() {
		return "Branches";
	}

	@Override
	String idFor(Ref branchRef) {
		return shortenRefName(branchRef.getName());
	}
	
	@Override
	CharSequence shortDescriptionOf(Ref branchRef) {
		RevWalk revWalk = new RevWalk(repository);
		try {
			RevCommit branchHeadCommit = revWalk.parseCommit(branchRef.getObjectId());
			return branchHeadCommit.getShortMessage()+" "+Time.timeSinceSeconds(branchHeadCommit.getCommitTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
