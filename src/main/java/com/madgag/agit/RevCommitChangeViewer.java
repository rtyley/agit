package com.madgag.agit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RevCommitChangeViewer extends Activity {
    private File gitdir;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.rev_commit_view);
        
        Intent intent = getIntent();
		gitdir=RepositoryManagementActivity.getGitDirFrom(intent);
        
        try {
			Repository repository=new FileRepository(gitdir);
			String revisionId = intent.getStringExtra("commit");
			Log.i("RCCV",revisionId);
			RevWalk revWalk = new RevWalk(repository);
			RevCommit commit = revWalk.parseCommit(ObjectId.fromString(revisionId));
			Log.i("RCCV",commit.getFullMessage());
	
			Log.i("RCCV","Parent count "+commit.getParentCount());
			if (commit.getParentCount() == 1) {
				final TreeWalk tw = new TreeWalk(repository);
				tw.setRecursive(true);
				tw.reset();
				RevCommit commitParent = revWalk.parseCommit(commit.getParent(0));
				RevTree commitParentTree = revWalk.parseTree(commitParent.getTree());
				tw.addTree(commitParentTree);
				tw.addTree(revWalk.parseTree(commit.getTree()));
				tw.setFilter(TreeFilter.ANY_DIFF);
				List<DiffEntry> files = DiffEntry.scan(tw);
				Log.i("RCCV",files.toString());
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
