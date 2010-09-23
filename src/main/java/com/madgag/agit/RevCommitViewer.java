package com.madgag.agit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.FollowFilter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RevCommitViewer extends ExpandableListActivity {
	
	
	private File gitdir;
	private MyExpandableListAdapter mAdapter;
	private List<DiffEntry> files;
	private Repository repository;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.rev_commit_view);

		Intent intent = getIntent();
		gitdir = RepositoryManagementActivity.getGitDirFrom(intent);

		try {
			
			repository = new FileRepository(gitdir);
			String revisionId = intent.getStringExtra("commit");
			Log.i("RCCV", revisionId);
			RevWalk revWalk = new RevWalk(repository);
			RevCommit commit = revWalk.parseCommit(ObjectId
					.fromString(revisionId));
			Log.i("RCCV", commit.getFullMessage());

			Log.i("RCCV", "Parent count " + commit.getParentCount());
			if (commit.getParentCount() == 1) {
				final TreeWalk tw = new TreeWalk(repository);
				tw.setRecursive(true);
				tw.reset();
				RevCommit commitParent = revWalk.parseCommit(commit
						.getParent(0));
				RevTree commitParentTree = revWalk.parseTree(commitParent
						.getTree());
				tw.addTree(commitParentTree);
				RevTree commitTree = revWalk.parseTree(commit.getTree());
				tw.addTree(commitTree);
				TreeFilter pathFilter = TreeFilter.ANY_DIFF;
				tw.setFilter(pathFilter);
				files = DiffEntry.scan(tw);
				Log.i("RCCV", files.toString());

				boolean detectRenames=true;
//				if (pathFilter instanceof FollowFilter && isAdd(files)) {
					// The file we are following was added here, find where it
					// came from so we can properly show the rename or copy,
					// then continue digging backwards.
					//
					
//					tw.reset();
//					tw.addTree(commitParentTree);
//					tw.addTree(commitTree);
//					tw.setFilter(pathFilter);
//					files = updateFollowFilter(detectRenames(DiffEntry.scan(tw)));
//
//				} else 
					if (detectRenames)
					files = detectRenames(files);

				mAdapter = new MyExpandableListAdapter(this);
				setListAdapter(mAdapter);

				// ListView listView=(ListView)
				// findViewById(R.id.commit_view_diffs_list);
				// listView.setAdapter(new ArrayAdapter<DiffEntry>(this,
				// android.R.layout.simple_list_item_1, files));
				//				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<DiffEntry> detectRenames(List<DiffEntry> files)
			throws IOException {
		RenameDetector rd = new RenameDetector(repository);
//		if (renameLimit != null)
//			rd.setRenameLimit(renameLimit.intValue());
		rd.addAll(files);
		return rd.compute();
	}

//	private boolean isAdd(List<DiffEntry> files) {
//		String oldPath = ((FollowFilter) pathFilter).getPath();
//		for (DiffEntry ent : files) {
//			if (ent.getChangeType() == ChangeType.ADD
//					&& ent.getNewPath().equals(oldPath))
//				return true;
//		}
//		return false;
//	}
//
//	private List<DiffEntry> updateFollowFilter(List<DiffEntry> files) {
//		String oldPath = ((FollowFilter) pathFilter).getPath();
//		for (DiffEntry ent : files) {
//			if (isRename(ent) && ent.getNewPath().equals(oldPath)) {
//				pathFilter = FollowFilter.create(ent.getOldPath());
//				return Collections.singletonList(ent);
//			}
//		}
//		return Collections.emptyList();
//	}

	private static boolean isRename(DiffEntry ent) {
		return ent.getChangeType() == ChangeType.RENAME
				|| ent.getChangeType() == ChangeType.COPY;
	}

	public class MyExpandableListAdapter extends BaseExpandableListAdapter {

		LayoutInflater mInflater;

		public MyExpandableListAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View v = mInflater.inflate(R.layout.diff_view, parent, false);
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			try {
				DiffFormatter diffFormatter = new DiffFormatter(bas);
				diffFormatter.setRepository(repository);
				diffFormatter.format(files.get(groupPosition));
				String rubbish = new String(bas.toByteArray(), "utf-8");
				((TextView) v.findViewById(R.id.diff_hunk_textview))
						.setText(rubbish);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return v;
		}

		public int getChildrenCount(int groupPosition) {
			return 1;
		}

		public Object getGroup(int index) {
			return files.get(index);
		}

		public int getGroupCount() {
			return files.size();
		}

		public long getGroupId(int index) {
			return files.get(index).hashCode(); // Pretty lame
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = newGroupView(isExpanded, parent);
			} else {
				v = convertView;
			}
			DiffEntry diffEntry = files.get(groupPosition);
			int changeTypeIcon = R.drawable.diff_changetype_modify;
			String filename = diffEntry.getNewPath();
			switch (diffEntry.getChangeType()) {
			case ADD:
				changeTypeIcon = R.drawable.diff_changetype_add;
				break;
			case DELETE:
				changeTypeIcon = R.drawable.diff_changetype_delete;
				filename = diffEntry.getOldPath();
				break;
			case MODIFY:
				changeTypeIcon = R.drawable.diff_changetype_modify;
				break;
			case RENAME:
				changeTypeIcon = R.drawable.diff_changetype_rename;
				filename = nameChange(diffEntry);
				break;
			case COPY:
				changeTypeIcon = R.drawable.diff_changetype_add;
				break;
			}
			((ImageView) v.findViewById(R.id.commit_file_diff_type))
					.setImageResource(changeTypeIcon);
			((TextView) v.findViewById(R.id.commit_file_textview))
					.setText(filename);

			return v;
		}

		private String nameChange(DiffEntry diffEntry) {
			return new FilePathDiffer().diff(diffEntry.getOldPath(), diffEntry.getNewPath());
		}

		private View newGroupView(boolean isExpanded, ViewGroup parent) {
			return mInflater.inflate(isExpanded ? R.layout.commit_group_view
					: R.layout.commit_group_view, parent, false);
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isChildSelectable(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
