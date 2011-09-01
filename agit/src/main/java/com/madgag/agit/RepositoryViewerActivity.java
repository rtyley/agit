/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.google.inject.Inject;
import com.madgag.agit.operation.lifecycle.CasualShortTermLifetime;
import com.madgag.agit.operations.GitAsyncTaskFactory;
import com.madgag.agit.operations.GitOperationExecutor;
import com.madgag.agit.operations.RepoDeleter;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import roboguice.inject.InjectView;

import java.io.File;

import static android.R.drawable.ic_menu_delete;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.madgag.agit.GitIntents.*;
import static com.madgag.agit.R.drawable.ic_title_fetch;
import static com.madgag.agit.R.string.*;
import static com.madgag.agit.git.Repos.niceNameFor;


public class RepositoryViewerActivity extends RepoScopedActivityBase {

    public static final String TAG = "RMA";

	public static Intent manageRepoIntent(File gitdir) {
		return new GitIntentBuilder("repo.VIEW").gitdir(gitdir).toIntent();
	}

	private final static int DELETE_ID=Menu.FIRST;
    private final int DELETION_IN_PROGRESS_DIALOG =3;
	private final int DELETION_CONFIRMATION_DIALOG =DELETION_IN_PROGRESS_DIALOG + 1;

	private DialogInterface.OnClickListener DELETION_CONFIRMATION_DIALOG_LISTENER = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialogInterface, int which) {
			if (which== BUTTON_POSITIVE) {
				doDeletion();
			}
		}
	};

    @Inject GitAsyncTaskFactory gitAsyncTaskFactory;

    @InjectView(R.id.actionbar) ActionBar actionBar;
    @InjectView(android.R.id.list) ListView listView;

    @Inject RepoSummaryAdapter summaryAdapter;
    @Inject GitOperationExecutor gitOperationExecutor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_management_activity);

        actionBar.setHomeAction(new HomeAction(this));
		actionBar.setTitle(niceNameFor(repo()));
        actionBar.addAction(new Action() {
            public void performAction(View view) {
                startService(new GitIntentBuilder("repo.SYNC").repository(repo()).toIntent());
            }
			
			public int getDrawable() {
				return ic_title_fetch;
			}
        });

        listView.setOnItemClickListener(summaryAdapter.getOnItemClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.delete_repo_menu_option).setShortcut('0', 'd').setIcon(ic_menu_delete);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case DELETE_ID:
			showDialog(DELETION_CONFIRMATION_DIALOG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private void doDeletion() {
		showDialog(DELETION_IN_PROGRESS_DIALOG);

		gitAsyncTaskFactory.createTaskFor(new RepoDeleter(repo()), new CasualShortTermLifetime()).execute();
	}

	BroadcastReceiver operationProgressBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "Got broadcast : "+action);
		}
	};
	
	BroadcastReceiver repoStateChangeBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "repoStateChangeBroadcastReceiver got broadcast : "+intent);
			if (!gitdir().exists()) {
				finish();
			}
		}
	};
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DELETION_IN_PROGRESS_DIALOG:
			ProgressDialog deletionDialog = new ProgressDialog(this);
			deletionDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			deletionDialog.setMessage("Deleting repo...");
			deletionDialog.setIndeterminate(true);
			return deletionDialog;
		case DELETION_CONFIRMATION_DIALOG:
			return new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle(repo_deletion_dialog_confirmation_title)
					.setMessage(repo_deletion_dialog_confirmation_message)
					.setPositiveButton(button_yes, DELETION_CONFIRMATION_DIALOG_LISTENER)
					.setNegativeButton(button_no, DELETION_CONFIRMATION_DIALOG_LISTENER)
					.create();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	@Override
	protected void onNewIntent(Intent newIntent) {
		Log.i(TAG, "onNewIntent called with "+newIntent+" "+gitDirFrom(newIntent));
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
		registerReceiver(operationProgressBroadcastReceiver, new IntentFilter("org.openintents.git.operation.progress.update"));

		registerReceiver(repoStateChangeBroadcastReceiver, new IntentFilter(actionWithSuffix(REPO_STATE_CHANGED_BROADCAST)));
		
		updateUI();
		
    }

	void updateUI() {
        listView.setAdapter(summaryAdapter);
//        TextView remotesSummary = (TextView) findViewById(remotes_summary);
//        remotesSummary.setText(new RDTRemote(repo()).summariseAll());
//
//        TextView branchesSummary = (TextView) findViewById(branches_summary);
//        branchesSummary.setText(new RDTBranch(repo()).summariseAll());
//
//        TextView tagsSummary = (TextView) findViewById(tags_summary);
//        tagsSummary.setText(new RDTTag(repo()).summariseAll());
	}



	@Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver(operationProgressBroadcastReceiver);
        unregisterReceiver(repoStateChangeBroadcastReceiver);
    }
	
	public static PendingIntent manageRepoPendingIntent(File gitdir,Context context) {
		Log.i(TAG, "manageRepoPendingIntent yeah - creating with "+gitdir);
		Intent intentForNotification = manageRepoIntent(gitdir);
        intentForNotification.setFlags(FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(context, gitdir.hashCode(), intentForNotification, 0);
	}


}
