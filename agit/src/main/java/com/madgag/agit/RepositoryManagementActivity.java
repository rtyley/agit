package com.madgag.agit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.madgag.agit.BranchViewer.branchViewerIntentFor;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.GitIntents.repositoryFrom;
import static com.madgag.agit.MessagingProgressMonitor.GIT_OPERATION_PROGRESS_UPDATE;
import static com.madgag.agit.RepoDeleter.REPO_DELETE_COMPLETED;
import static com.madgag.agit.RepoLogActivity.repoLogIntentFor;
import static com.madgag.agit.TagViewer.tagViewerIntentFor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.events.IndexChangedEvent;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.storage.file.FileRepository;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.madgag.agit.GitOperationsService.GitOperationsBinder;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.OpPrompt;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;


public class RepositoryManagementActivity extends RepositoryActivity {

	private ProgressDialog progressDialog;
	private AlertDialog stringEntryDialog,yesNoDialog;
	
	private final static int DELETE_ID=Menu.FIRST;
	
	final int PROGRESS_DIALOG=0,STRING_ENTRY_DIALOG=1, YES_NO_DIALOG=2;
	private final int DELETION_DIALOG=3;
	public static final String TAG = "RepositoryManagementActivity";
	@Override String TAG() { return TAG; }
	
	private RepositoryOperationContext repositoryOperationContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_management);
        
        bindService(new Intent(this,GitOperationsService.class), serviceConnectionToRegisterThisAsManagementUI(), BIND_AUTO_CREATE);
        
        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle(repo().getWorkTree().getName());
        actionBar.addAction(new Action() {
			public void performAction() {
				startService(new GitIntentBuilder("git.FETCH").repository(repo()).toIntent());
			}
			
			public int getDrawable() {
				return R.drawable.ic_title_fetch;
			}
		});
        
		branchList = (ListView) findViewById(R.id.BranchList);
		branchList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2));
		branchList.setOnItemClickListener(new OnItemClickListener(){
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
			if (action.equals(GIT_OPERATION_PROGRESS_UPDATE)){
				// updateOperationProgressDisplay();
			} else if (action.equals("git.user.interation.request")) {
				Log.d(TAG, "I should probably do something helpful");
			} else if (action.equals(REPO_DELETE_COMPLETED)) {
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
	private ListView branchList;
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
				repositoryOperationContext.clearPromptNotificationFromStatusBar();
				repositoryOperationContext.getPromptHelper().setResponse(bool);
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
			String msg = repositoryOperationContext.getPromptHelper().getOpPrompt().getOpNotification().getEventDetail();
			Log.i(TAG, "Going to yes/no "+msg);
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
		registerRecieverForServicePromptRequests();
		
		//repositoryOperationContext.getCurrentOperation().getPromptHelper().;
		updateUI();
		updateUIToReflectServicePromptRequests();
    }

	void updateUI() {
		branchList.setAdapter(new RDTypesListAdapter(this, repo()));
	}
    
    private void registerRecieverForServicePromptRequests() {
    	if (repositoryOperationContext!=null) {
			repositoryOperationContext.setManagementActivity(this);
		}
	}

	private void unregisterRecieverForServicePromptRequests() {
		if (repositoryOperationContext!=null) {
			repositoryOperationContext.setManagementActivity(null);
		}
	}
	
	void updateUIToReflectServicePromptRequests() {
		Log.i(TAG, "repositoryOperationContext="+repositoryOperationContext);
		if (repositoryOperationContext==null) {
			//guess there's not been a running service yet...
			return;
		}
		GitAsyncTask currentOperation = repositoryOperationContext.getCurrentOperation();
		Log.i(TAG, "updateUIToReflectServicePromptRequests currentOperation="+currentOperation);
		if (currentOperation==null) {
			// wipe dialogs?
			return;
		}

		PromptHelper prompt=repositoryOperationContext.getPromptHelper();
		OpPrompt<?> opPrompt = prompt.getOpPrompt();
		if (opPrompt!=null) {
			Class<?> requiredResponseType = opPrompt.getRequiredResponseType();
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
	
	public static PendingIntent manageRepoPendingIntent(Repository repository, Context context) {
		return manageRepoPendingIntent(repository.getDirectory(), context);
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
	
	public Repository getRepository() {
		return repo();
	}
}
