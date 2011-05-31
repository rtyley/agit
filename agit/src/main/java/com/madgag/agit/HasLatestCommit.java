package com.madgag.agit;

import org.eclipse.jgit.revwalk.RevCommit;

public interface HasLatestCommit {

    RevCommit getLatestCommit();

}
