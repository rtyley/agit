package com.madgag.agit;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;
import static org.eclipse.jgit.lib.Constants.OBJ_COMMIT;
import static org.eclipse.jgit.lib.Constants.OBJ_TAG;
import static org.eclipse.jgit.lib.Constants.OBJ_TREE;

import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;

public class GitObjects {
	public static <T> T evaluate(RevObject revObject, GitObjectFunction<T> f) {
		switch (revObject.getType()) {
			case OBJ_COMMIT:
				return f.apply((RevCommit) revObject);
			case OBJ_TREE:
				return f.apply((RevTree) revObject);
			case OBJ_BLOB:
				return f.apply((RevBlob) revObject);
			case OBJ_TAG:
				return f.apply((RevTag) revObject);
			default:
				throw new IllegalArgumentException("Git object type '"+revObject.getType()+"' unknown");
		}
	}
}
