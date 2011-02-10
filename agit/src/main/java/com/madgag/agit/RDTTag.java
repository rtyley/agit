package com.madgag.agit;

import static org.eclipse.jgit.lib.Constants.typeString;
import static org.eclipse.jgit.lib.Repository.shortenRefName;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import android.util.Log;
import android.widget.Toast;

public class RDTTag extends RepoDomainType<Ref> {

	public RDTTag(Repository repository) {
		super(repository);
	}

	@Override
	String name() { return "tag"; }
	
	public Collection<Ref> getAll() {
		return repository.getTags().values();
	}
	
	@Override
	String conciseSeparator() {
		return " • ";
	}

	@Override
	CharSequence conciseSummary(Ref tagRef) {
		return idFor(tagRef);
	}

	@Override
	String idFor(Ref e) {
		return shortenRefName(e.getName());
	}
	
	@Override
	CharSequence conciseSummaryTitle() {
		return "Tags";
	}
	
	@Override
	CharSequence shortDescriptionOf(Ref ref) {
		ObjectId peeledObjectId = repository.peel(ref).getPeeledObjectId();
		ObjectId taggedId = peeledObjectId==null?ref.getObjectId():peeledObjectId;
		
		try {
			RevObject taggedObject = new RevWalk(repository).parseAny(taggedId);
			switch (taggedObject.getType()) {
				case Constants.OBJ_COMMIT:
					RevCommit revCommit = (RevCommit) taggedObject;
					return "Commit: "+revCommit.abbreviate(4).name()+" "+revCommit.getShortMessage();
				case Constants.OBJ_TREE:
					RevTree revTree = (RevTree) taggedObject;
					return "Tree: "+revTree.abbreviate(4).name()+" "+revTree;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "...";
	}

}
