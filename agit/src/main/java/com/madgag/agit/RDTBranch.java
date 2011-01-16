package com.madgag.agit;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;

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
		return branchRef.getName();
	}

	@Override
	String conciseSeparator() {
		return " • ";
	}

	@Override
	CharSequence conciseSummaryTitle() {
		return "Branches";
	}
}
