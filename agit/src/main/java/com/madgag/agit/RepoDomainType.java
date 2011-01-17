package com.madgag.agit;

import java.util.Collection;

import org.eclipse.jgit.lib.Repository;

import android.content.Intent;

public abstract class RepoDomainType<E> { 
	
	protected final Repository repository;

	public RepoDomainType(Repository repository) {
		this.repository = repository;
	}
	
	abstract String name();
	
	abstract Collection<E> getAll();
	
	abstract CharSequence conciseSummaryTitle();
	
	abstract CharSequence conciseSummary(E e);
	
	abstract CharSequence shortDescriptionOf(E e);
	
	
	public CharSequence summarise(Collection<E> list) {
		StringBuilder sb = new StringBuilder();
		for (E e : list) {
			if (sb.length()>0) {
				sb.append(conciseSeparator());
			}
			sb.append(conciseSummary(e));
		}
		return sb.toString();
	}

	abstract String conciseSeparator();

	public CharSequence summariseAll() {
		return summarise(getAll());
	}

	public Intent listIntent() {
		return action("git."+name()+".LIST").toIntent();
	}

	
	public Intent viewIntentFor(E e) {
		return action("git."+name()+".VIEW").add(name(),idFor(e)).toIntent();
	}
	
	abstract String idFor(E e);

	private GitIntentBuilder action(String action) {
		return new GitIntentBuilder(action).repository(repository);
	}
	
}
