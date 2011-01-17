package com.madgag.agit;

import static org.eclipse.jgit.lib.Repository.shortenRefName;

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
	CharSequence shortDescriptionOf(Ref e) {
		return "...";
	}

}
