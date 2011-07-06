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

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.madgag.agit.RDTTag.TagSummary;
import com.madgag.agit.git.GitObjectFunction;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.*;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.git.GitObjects.evaluate;
import static com.madgag.agit.RDTTag.TagSummary.SORT_BY_TIME_AND_NAME;
import static java.util.Collections.sort;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

public class RDTTag extends RepoDomainType<TagSummary> {

    @Inject
	public RDTTag(Repository repository) {
		super(repository);
	}

	@Override
	public String name() { return "tag"; }
	
	public List<TagSummary> getAll() {
		final RevWalk revWalk = new RevWalk(repository);
		List<TagSummary> tagSummaries = newArrayList(transform(repository.getTags().values(), new TagSummaryFactory(revWalk)));
		sort(tagSummaries, SORT_BY_TIME_AND_NAME);
		return tagSummaries;
		
	}
	
	@Override
	String conciseSeparator() {
		return " • ";
	}

	@Override
	CharSequence conciseSummary(TagSummary tagRef) {
		return idFor(tagRef);
	}

	@Override
	String idFor(TagSummary tagSummary) {
		return shortenRefName(tagSummary.getRef().getName());
	}
	
	@Override
	public CharSequence conciseSummaryTitle() {
		return "Tags";
	}
	
	@Override
	CharSequence shortDescriptionOf(TagSummary tagSummary) {
		//ObjectId peeledObjectId = repository.peel(tagSummary.getRef()).getPeeledObjectId();
		//ObjectId taggedId = peeledObjectId==null?ref.getObjectId():peeledObjectId;
		return evaluate(tagSummary.getTaggedObject(), GIT_OBJECT_SHORT_DESCRIPTION);
	}



	public static class TagSummary {
		public final static Comparator<TagSummary> SORT_BY_TIME_AND_NAME = new Comparator<TagSummary>() {
			public int compare(TagSummary t1, TagSummary t2) {
				long timeDiff = t1.getTime() - t2.getTime();
				if (timeDiff!=0) {
					return (int) timeDiff;
				}
				return t1.name.compareTo(t2.name);
			}
		};

		private final String name;
		private final Ref tagRef;
		private final RevTag tagObject;
		private final RevObject taggedObject;
        private final long time;

		public TagSummary(Ref tagRef, RevTag tagObject, RevObject taggedObject, long time) {
			this.tagRef = tagRef;
            this.time = time;
            name = shortenRefName(tagRef.getName());
			this.tagObject = tagObject;
			this.taggedObject = taggedObject;
		}
		
		public CharSequence getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return name;
		}

		public RevObject getTaggedObject() {
			return taggedObject;
		}

		public Ref getRef() {
			return tagRef;
		}
		
		public boolean isLightweight() {
			return tagObject==null;
		}

		public long getTime() {
            return time;
        }

    }

	
	static GitObjectFunction.Base<String> GIT_OBJECT_SHORT_DESCRIPTION = new GitObjectFunction.Base<String>() {
		public String apply(RevCommit commit) {
			return "Commit: "+commit.abbreviate(4).name()+" "+commit.getShortMessage();
		}
		public String apply(RevTree tree) {
			return "Tree: "+tree.abbreviate(4).name()+" "+tree;
		}
		public String applyDefault(RevObject revObject) {
			return "...";
		}
	};

    private static class TagSummaryFactory implements Function<Ref, TagSummary> {
        private final RevWalk revWalk;

        public TagSummaryFactory(RevWalk revWalk) {
            this.revWalk = revWalk;
        }

        public GitObjectFunction<Long> gitObjectTime =  new GitObjectFunction<Long>() {
            public Long apply(RevCommit commit) {
                return (long) commit.getCommitTime();
            }
            public Long apply(RevTree tree) { return 0L; }
            public Long apply(RevBlob blob) { return 0L; }
            public Long apply(RevTag tag) {
                PersonIdent taggerIdent = tag.getTaggerIdent();
                if (taggerIdent!=null) {
                    return taggerIdent.getWhen().getTime();
                }

                try {
                    return evaluate(revWalk.parseAny(tag.getObject()), this);
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
        };

        public TagSummary apply(Ref tagRef) {
            RevObject objectPointedToByRef;
            try {
                objectPointedToByRef = revWalk.parseAny(tagRef.getObjectId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            RevTag tagObject = null;
            RevObject taggedObject;
            long time;
            if (objectPointedToByRef instanceof RevTag) {
                // annotated
                tagObject = (RevTag) objectPointedToByRef;
                try {
                    taggedObject = revWalk.parseAny(tagObject.getObject());
                    time = evaluate(tagObject, gitObjectTime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                // lightweight
                taggedObject = objectPointedToByRef;
                time = evaluate(taggedObject, gitObjectTime);
            }
            return new TagSummary(tagRef, tagObject, taggedObject, time);
        }
    }
}
