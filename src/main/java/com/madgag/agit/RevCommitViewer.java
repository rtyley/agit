package com.madgag.agit;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
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
				files = DiffEntry.scan(tw);
				Log.i("RCCV",files.toString());
				
				
		        mAdapter = new MyExpandableListAdapter(this);
		        setListAdapter(mAdapter);
		        
//				ListView listView=(ListView) findViewById(R.id.commit_view_diffs_list);
//				listView.setAdapter(new ArrayAdapter<DiffEntry>(this, android.R.layout.simple_list_item_1, files));
//				
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    	LayoutInflater mInflater;
    	
    	public MyExpandableListAdapter(Context context) {
    		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	}
    	
		public Object getChild(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getChildId(int arg0, int arg1) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			// TODO Auto-generated method stub
			return null;
		}

		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getGroup(int index) {
			return files.get(index);
		}

		public int getGroupCount() {
			return files.size();
		}

		public long getGroupId(int index) {
			return files.get(index).hashCode(); //Pretty lame
		}

	    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
	            ViewGroup parent) {
	        View v;
	        if (convertView == null) {
	            v = newGroupView(isExpanded, parent);
	        } else {
	            v = convertView;
	        }
	        DiffEntry diffEntry = files.get(groupPosition);
			int changeTypeIcon=R.drawable.diff_changetype_modify;
			String filename=diffEntry.getNewPath();
			switch (diffEntry.getChangeType()) {
				case ADD: changeTypeIcon=R.drawable.diff_changetype_add; break;
				case DELETE: changeTypeIcon=R.drawable.diff_changetype_delete; filename=diffEntry.getOldPath(); break;
				case MODIFY: changeTypeIcon=R.drawable.diff_changetype_modify; break;
				case RENAME: changeTypeIcon=R.drawable.diff_changetype_rename; break;
				case COPY: changeTypeIcon=R.drawable.diff_changetype_add; break;
			}
			((ImageView) v.findViewById(R.id.commit_file_diff_type)).setImageResource(changeTypeIcon);
			((TextView) v.findViewById(R.id.commit_file_textview)).setText(filename);
			
	        return v;
	    }
	    
		private View newGroupView(boolean isExpanded, ViewGroup parent) {
	        return mInflater.inflate(isExpanded?R.layout.commit_group_view:R.layout.commit_group_view, parent, false);
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
