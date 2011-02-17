package com.madgag.agit;

import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;

public interface GitObjectFunction<T> {
	
	T apply(RevCommit commit);
	T apply(RevTree tree);
	T apply(RevBlob blob);
	T apply(RevTag tag);
}
