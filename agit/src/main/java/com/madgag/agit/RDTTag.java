package com.madgag.agit;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitObjects.evaluate;
import static com.madgag.agit.RDTTag.TagSummary.SORT_BY_TIME_AND_NAME;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import com.google.common.base.Function;
import com.madgag.agit.RDTTag.TagSummary;

public class RDTTag extends RepoDomainType<TagSummary> {

	public RDTTag(Repository repository) {
		super(repository);
	}

	@Override
	String name() { return "tag"; }
	
	public List<TagSummary> getAll() {
		final RevWalk revWalk = new RevWalk(repository);
		List<TagSummary> tagSummaries = newArrayList(transform(repository.getTags().values(), new Function<Ref, TagSummary>() {
			public TagSummary apply(Ref tagRef) {
				RevObject objectPointedToByRef;
				try {
					objectPointedToByRef = revWalk.parseAny(tagRef.getObjectId());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				RevTag tagObject = null;
				RevObject taggedObject;
				if (objectPointedToByRef instanceof RevTag) {
					tagObject = (RevTag) objectPointedToByRef;
					try {
						taggedObject = revWalk.parseAny(tagObject.getId());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					taggedObject = objectPointedToByRef;
				}
				
				return new TagSummary(tagRef, tagObject, taggedObject);
			}
		}));
		Collections.sort(tagSummaries, SORT_BY_TIME_AND_NAME);
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
	CharSequence conciseSummaryTitle() {
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

		public TagSummary(Ref tagRef, RevTag tagObject, RevObject taggedObject) {
			this.tagRef = tagRef;
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
			if (isLightweight()) {
				return evaluate(taggedObject, GIT_OBJECT_TIME);
			} else {
				return tagObject.getTaggerIdent().getWhen().getTime();
			}
		}

		private static GitObjectFunction<Long> GIT_OBJECT_TIME = new GitObjectFunction<Long>() {
				public Long apply(RevCommit commit) {
					return (long)commit.getCommitTime();
				}
				public Long apply(RevTree tree) {
					return 0L;
				}
				public Long apply(RevBlob blob) {
					return 0L;
				}
				public Long apply(RevTag tag) {
					return tag.getTaggerIdent().getWhen().getTime();
				}
		};
	}
	
	private static GitObjectFunction.Base<String> GIT_OBJECT_SHORT_DESCRIPTION = new GitObjectFunction.Base<String>() {
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
}
