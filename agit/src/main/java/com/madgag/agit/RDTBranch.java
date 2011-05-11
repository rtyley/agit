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

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.RDTBranch.BranchSummary.SORT_BY_COMMIT_TIME;
import static com.madgag.agit.Time.timeSinceSeconds;
import static java.util.Collections.sort;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.google.common.base.Function;
import com.madgag.agit.RDTBranch.BranchSummary;

public class RDTBranch extends RepoDomainType<BranchSummary> {

    @Inject
	public RDTBranch(Repository repository) {
		super(repository);
	}

	@Override
	String name() { return "branch"; }
	
	public List<BranchSummary> getAll() {
		RefDatabase refDatabase = repository.getRefDatabase();
		try {
			Map<String, Ref> remoteBranchRefs = refDatabase.getRefs(R_REMOTES);
			final RevWalk revWalk = new RevWalk(repository);
			
			List<BranchSummary> branchSummaries = newArrayList(transform(remoteBranchRefs.values(), new Function<Ref, BranchSummary>() {
				public BranchSummary apply(Ref branchRef) {
					try {
						RevCommit branchHeadCommit = revWalk.parseCommit(branchRef.getObjectId());
						return new BranchSummary(branchRef, branchHeadCommit);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				
			}));
			
			sort(branchSummaries, SORT_BY_COMMIT_TIME);
			return branchSummaries;
		} catch (IOException e) { throw new RuntimeException(e); }
	}
	
	@Override
	CharSequence conciseSummary(BranchSummary bs) {
		return idFor(bs);
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
	String idFor(BranchSummary bs) {
		return shortenRefName(bs.getRef().getName());
	}
	
	@Override
	CharSequence shortDescriptionOf(BranchSummary bs) {
		return bs.getHeadCommit().getShortMessage()+" "+timeSinceSeconds(bs.getHeadCommit().getCommitTime());
	}
	
	public static class BranchSummary {
		public final static Comparator<BranchSummary> SORT_BY_COMMIT_TIME = new Comparator<BranchSummary>() {
			public int compare(BranchSummary b1, BranchSummary b2) {
				return b2.getHeadCommit().getCommitTime()-b1.getHeadCommit().getCommitTime();
			}
		};

		private final Ref branchRef;
		private final RevCommit headCommit;

		public BranchSummary(Ref branchRef, RevCommit headCommit) {
			this.branchRef = branchRef;
			this.headCommit = headCommit;
		}

		public RevCommit getHeadCommit() {
			return headCommit;
		}

		public Ref getRef() {
			return branchRef;
		}
		
	}
}
