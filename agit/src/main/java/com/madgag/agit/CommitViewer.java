package com.madgag.agit;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.markupartist.android.widget.ActionBar;

public class CommitViewer extends TabActivity {
	private static final String TAG = "CommitViewer";
	
	private final static int TAG_ID=Menu.FIRST;
	
    public static Intent revCommitViewIntentFor(File gitdir, RevCommit commit) {
		return new GitIntentBuilder("git.view.COMMIT").gitdir(gitdir).commit(commit).toIntent();
	}

	private RepositoryContext rc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rc = new RepositoryContext(this, TAG);
		setContentView(R.layout.commit_view);
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		
		try {
			
			ObjectId revisionId = GitIntents.commitIdFrom(getIntent()); // intent.getStringExtra("commit");
			Log.i("RCCV", revisionId.getName());
			RevWalk revWalk = new RevWalk(rc.repo());
			commit = revWalk.parseCommit(revisionId);
			
			actionBar.setTitle(commit.name());
			Log.i("RCCV", commit.getFullMessage());
			
			
			Resources res = getResources(); // Resource object to get Drawables
			TabHost tabHost = getTabHost();  // The activity TabHost
		    TabHost.TabSpec spec;  // Resusable TabSpec for each tab

		    // Initialize a TabSpec for each tab and add it to the TabHost
		    spec = tabHost.newTabSpec("commit_details")
		    	.setIndicator(newTabIndicator(tabHost, "Commit"))
		    	.setContent(R.id.content);
		    tabHost.addTab(spec);
		    
		    for (RevCommit parentCommit : commit.getParents()) {
		    	parentCommit = revWalk.parseCommit(parentCommit);
		    	spec = tabHost.newTabSpec(parentCommit.getName());
		    	String text = "Δ"+parentCommit.abbreviate(4).name();
		    	spec.setIndicator(newTabIndicator(tabHost, text))
		    		.setContent(R.id.content);
			    tabHost.addTab(spec);
		    }
		    

			Log.i("RCCV", "Parent count " + commit.getParentCount());
			if (commit.getParentCount() == 1) {
				//setThoseListThings(revWalk);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TextView newTabIndicator(TabHost tabHost, String text) {
		TextView v=(TextView) getLayoutInflater().inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
		v.setText(text);
		return v;
	}
	
	private CommitChangeListAdapter mAdapter;
	final int CREATE_TAG_DIALOG=0;

	private RevCommit commit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, TAG_ID, 0, R.string.tag_commit_menu_option).setShortcut('0', 't');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case TAG_ID:
        	showDialog(CREATE_TAG_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case CREATE_TAG_DIALOG:
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.create_tag_dialog, null);
            return new AlertDialog.Builder(this)
//                .setIcon(R.drawable.alert_dialog_icon)
//                .setTitle(R.string.alert_dialog_text_entry)
                .setView(textEntryView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	 String tagName=((TextView) textEntryView.findViewById(R.id.tag_name_edit)).getText().toString();
                    	 try {
							new Git(rc.repo()).tag().setName(tagName).setObjectId(commit).call();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
                    }
                })
                .create();
        }
        return null;
    }
	


	@Override
	protected void onResume() {
		super.onResume();
		rc.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rc.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rc.onDestroy();
	}
}
