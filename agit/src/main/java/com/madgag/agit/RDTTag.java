package com.madgag.agit;

import java.util.Collection;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

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
		return tagRef.getLeaf().getName();
	}

	@Override
	CharSequence conciseSummaryTitle() {
		return "Tags";
	}
}
