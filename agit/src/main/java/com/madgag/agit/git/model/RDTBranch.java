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

package com.madgag.agit.git.model;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.madgag.agit.git.model.RDTBranch.BranchSummary;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Iterables.transform;
import static com.madgag.agit.git.Repos.COMMIT_TIME_ORDERING;
import static com.madgag.agit.util.Time.timeSinceSeconds;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

public class RDTBranch extends RepoDomainType<BranchSummary> {

    @Inject
	public RDTBranch(Repository repository) {
		super(repository);
	}

	@Override
	public String name() { return "branch"; }
	
	public List<BranchSummary> getAll() {
		RefDatabase refDatabase = repository.getRefDatabase();
		try {
			Map<String, Ref> remoteBranchRefs = refDatabase.getRefs(R_REMOTES);
			final RevWalk revWalk = new RevWalk(repository);
			
			Iterable<BranchSummary> branchSummaries = transform(remoteBranchRefs.values(), new Function<Ref, BranchSummary>() {
				public BranchSummary apply(Ref branchRef) {
					try {
						RevCommit branchHeadCommit = revWalk.parseCommit(branchRef.getObjectId());
						return new BranchSummary(branchRef, branchHeadCommit);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
				
			});

			return COMMIT_TIME_ORDERING.sortedCopy(branchSummaries);
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
	public CharSequence conciseSummaryTitle() {
		return "Branches";
	}

	@Override
    public String idFor(BranchSummary bs) {
        return bs.getShortName();
	}
	
	@Override
    public CharSequence shortDescriptionOf(BranchSummary bs) {
        return bs.getLatestCommit().getShortMessage()+" "+timeSinceSeconds(bs.getLatestCommit().getCommitTime());
	}


//    public ViewFactory<BranchSummary> getViewFactoryFor(Context context) {
//        return  new ViewFactory<BranchSummary>(viewInflatorFor(context, branch_list_item), new ViewHolderFactory<BranchSummary>() {
//            public ViewHolder<BranchSummary> createViewHolderFor(View view) {
//                return new RDTypeInstanceViewHolder(RDTBranch.this, view);
//            }
//        });
//    }
	
	public static class BranchSummary implements HasLatestCommit {

		private final Ref branchRef;
		private final RevCommit headCommit;

		public BranchSummary(Ref branchRef, RevCommit headCommit) {
			this.branchRef = branchRef;
			this.headCommit = headCommit;
		}

        public Ref getRef() {
			return branchRef;
		}

        public String getShortName() {
		    return shortenRefName(branchRef.getName());
        }

        public RevCommit getLatestCommit() {
            return headCommit;
        }
    }
}
