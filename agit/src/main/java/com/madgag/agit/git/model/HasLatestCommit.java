package com.madgag.agit.git.model;

import org.eclipse.jgit.revwalk.RevCommit;

public interface HasLatestCommit {

    RevCommit getLatestCommit();

}
