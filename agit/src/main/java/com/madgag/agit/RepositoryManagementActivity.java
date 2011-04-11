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
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.inject.Inject;
import com.madgag.agit.GitOperationsService.GitOperationsBinder;
import com.madgag.agit.blockingprompt.PromptHumper;
import com.madgag.agit.blockingprompt.PromptUIProvider;
import com.madgag.agit.blockingprompt.ResponseInterface;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import roboguice.inject.InjectView;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.RepoDeleter.REPO_DELETE_COMPLETED;
import static com.madgag.agit.Repos.niceNameFor;


public class RepositoryManagementActivity extends RepositoryActivity implements PromptUIProvider {

	private ProgressDialog progressDialog;
	private AlertDialog stringEntryDialog,yesNoDialog;
	
	private final static int DELETE_ID=Menu.FIRST;
	
	final int PROGRESS_DIALOG=0,STRING_ENTRY_DIALOG=1, YES_NO_DIALOG=2;
	private final int DELETION_DIALOG=3;
	public static final String TAG = "RMA";
    private ResponseInterface responseInterface;

    @InjectView(R.id.actionbar) ActionBar actionBar;
    
    @Inject
    PromptHumper promptHumper;

	
	private RepositoryOperationContext repositoryOperationContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_management);
        actionBar.setHomeLogo(R.drawable.actionbar_agit_logo);
        
        bindService(new Intent(this,GitOperationsService.class), serviceConnectionToRegisterThisAsManagementUI(), BIND_AUTO_CREATE);
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(niceNameFor(repo()));
        actionBar.addAction(new Action() {
            public void performAction(View view) {
                startService(new GitIntentBuilder("git.FETCH").repository(repo()).toIntent());
            }
			
			public int getDrawable() {
				return R.drawable.ic_title_fetch;
			}
        });
        
		rdtTypeList = (ListView) findViewById(R.id.BranchList);
		rdtTypeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2));
		rdtTypeList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				RepoDomainType<?> rdt = (RepoDomainType<?>) parent.getAdapter().getItem(position);
				startActivity(rdt.listIntent());
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.delete_repo_menu_option).setShortcut('0', 'd').setIcon(android.R.drawable.ic_menu_delete);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case DELETE_ID:
        	showDialog(DELETION_DIALOG);
			new RepoDeleter(gitdir(), RepositoryManagementActivity.this).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
	private ServiceConnection serviceConnectionToRegisterThisAsManagementUI() {
		return new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected - losing "+repositoryOperationContext);
				repositoryOperationContext=null;
			}
			
			public void onServiceConnected(ComponentName name, IBinder binder) {
				GitOperationsService service = ((GitOperationsBinder) binder).getService();
				repositoryOperationContext=service.registerManagementActivity(RepositoryManagementActivity.this);
				Log.i(TAG, "bound opService="+repositoryOperationContext);
				updateUIToReflectServicePromptRequests();
			}
		};
	}
    
    private void buttonUp(int id, OnClickListener listener) {
    	((Button) findViewById(id)).setOnClickListener(listener);
    }
    
    
	BroadcastReceiver operationProgressBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "Got broadcast : "+action);
			if (action.equals(REPO_DELETE_COMPLETED)) {
				if (intent.getData().equals(Uri.fromFile(gitdir()))) {
					finish();
				}
			}
		}
	};
	
	BroadcastReceiver deletionBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "deletionBroadcastReceiver got broadcast : "+intent);
			if (!gitdir().exists()) {
			//if (intent.getData().equals(Uri.fromFile(gitdir))) {
				finish();
			}
		}
	};
	private ListView rdtTypeList;
	private ListView tagList;
	
//	private void updateOperationProgressDisplay() {
//		Log.d(TAG, "Updating Operation Progress display");
////		String boo = gitOperationsService.currentOperationFor(db);
////		((TextView) findViewById(R.id.GitOperationStatus)).setText(boo);
//		showDialog(PROGRESS_DIALOG);
//		FetchThread ft=repositoryOperationContext.getCurrentOperation();
//		Progress currentProgress = ft.progressMonitor.getCurrentProgress();
//		progressDialog.setProgress(currentProgress.totalCompleted);
//		progressDialog.setMax(currentProgress.totalWork);
//		progressDialog.setMessage(currentProgress.msg);
//	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Fetching...");
			progressDialog.setTitle("Fetch totmarto");
			progressDialog.setCancelable(true);
			return progressDialog;
		case DELETION_DIALOG:
			ProgressDialog deletionDialog = new ProgressDialog(this);
			deletionDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			deletionDialog.setMessage("Deleting repo...");
			deletionDialog.setIndeterminate(true);
			return deletionDialog;
		case YES_NO_DIALOG:
			return new AlertDialog.Builder(this)
				.setMessage("...")
				.setPositiveButton("Yes", sendDialogResponseOf(true))
				.setNegativeButton("No", sendDialogResponseOf(false))
				.create();
		case STRING_ENTRY_DIALOG:
			AlertDialog.Builder stringDialogBuilder=new AlertDialog.Builder(this);
			final EditText input = new EditText(this);  
			stringDialogBuilder.setView(input);
			stringDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//repositoryOperationContext.getCurrentOperation().promptHelper.setResponse((CharSequence)input.getText());
				}
			});

			return stringDialogBuilder.create();
		default:
			return null;
		}
	}

	private android.content.DialogInterface.OnClickListener sendDialogResponseOf(final boolean bool) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                responseInterface.setResponse(bool);
			}
		};
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case PROGRESS_DIALOG:
			ProgressDialog progressDialog = (ProgressDialog) dialog;
			progressDialog.setMessage("Ghostbusters...");
			progressDialog.setProgress(0);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					// repositoryOperationContext.getCurrentOperation().getCancellationSignaller().setCancelled();
				}
			});
		case YES_NO_DIALOG:
			AlertDialog alertDialog=(AlertDialog) dialog;

			String msg = responseInterface.getOpPrompt().getOpNotification().getEventDetail();
			Log.d(TAG, "Going to yes/no " + msg);
			alertDialog.setMessage(msg);
		default:
		}
	}
	
	@Override
	protected void onNewIntent(Intent newIntent) {
		Log.i(TAG, "onNewIntent called with "+newIntent+" "+gitDirFrom(newIntent));
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
		((TextView) findViewById(R.id.RepositoryFileLocation)).setText(repo().getDirectory().getAbsolutePath());
        registerReceiver(operationProgressBroadcastReceiver, new IntentFilter("git.operation.progress.update"));

		registerReceiver(deletionBroadcastReceiver, new IntentFilter(REPO_DELETE_COMPLETED));
		registerReceiverForServicePromptRequests();
		
		//repositoryOperationContext.getCurrentOperation().getPromptHelper().;
		updateUI();
		updateUIToReflectServicePromptRequests();
    }

	void updateUI() {
		rdtTypeList.setAdapter(new RDTypesListAdapter(this, repo()));
	}
    
    private void registerReceiverForServicePromptRequests() {
        Log.d(TAG, "Registering as prompt UI provider with "+promptHumper);
    	promptHumper.setActivityUIProvider(this);
	}

	private void unregisterRecieverForServicePromptRequests() {
		promptHumper.clearActivityUIProvider();
	}
	
	void updateUIToReflectServicePromptRequests() {
		if (responseInterface!=null && responseInterface.getOpPrompt()!=null) {
			Class<?> requiredResponseType = responseInterface.getOpPrompt().getRequiredResponseType();
			if (String.class.equals(requiredResponseType)) {
				showDialog(STRING_ENTRY_DIALOG);
			} else if(Boolean.class.equals(requiredResponseType)) {
				showDialog(YES_NO_DIALOG);
			} else {
	//			hideAllPrompts();
	//			view.requestFocus();
			}
		}
	}

	@Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver(operationProgressBroadcastReceiver);
    	unregisterRecieverForServicePromptRequests();
    }
	
	public static PendingIntent manageRepoPendingIntent(File gitdir,Context context) {
		Log.i(TAG, "manageRepoPendingIntent yeah - creating with "+gitdir);
		Intent intentForNotification = manageRepoIntent(gitdir);
        intentForNotification.setFlags(FLAG_ACTIVITY_NEW_TASK);
		return PendingIntent.getActivity(context, gitdir.hashCode(), intentForNotification, 0);
	}

	public static Intent manageRepoIntent(File gitdir) {
		return new GitIntentBuilder("git.repo.MANAGE").gitdir(gitdir).toIntent();
	}

    public void acceptPrompt(ResponseInterface responseInterface) {
        this.responseInterface = responseInterface;
        updateUIToReflectServicePromptRequests();
    }

    public void clearPrompt() {
        // TODO clear any actual prompt that's going on...
        this.responseInterface = null;
    }
}
