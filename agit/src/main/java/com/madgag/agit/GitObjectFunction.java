package com.madgag.agit;

import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;

import com.google.common.base.Function;

public interface GitObjectFunction<T> {
	
	T apply(RevCommit commit);
	T apply(RevTree tree);
	T apply(RevBlob blob);
	T apply(RevTag tag);
	
	public static abstract class Base<T> implements GitObjectFunction<T> {

		public T apply(RevCommit commit) { return applyDefault(commit); }

		public T apply(RevTree tree) { return applyDefault(tree); }

		public T apply(RevBlob blob) { return applyDefault(blob); }

		public T apply(RevTag tag) { return applyDefault(tag); }
		
		public abstract T applyDefault(RevObject revObject);
		
	}
}
