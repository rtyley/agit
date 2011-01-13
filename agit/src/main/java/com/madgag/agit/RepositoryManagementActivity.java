package com.madgag.agit;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.madgag.agit.BranchViewer.branchViewerIntentFor;
import static com.madgag.agit.GitIntents.addGitDirTo;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.agit.GitIntents.repositoryFrom;
import static com.madgag.agit.MessagingProgressMonitor.GIT_OPERATION_PROGRESS_UPDATE;
import static com.madgag.agit.RepoDeleter.REPO_DELETE_COMPLETED;
import static com.madgag.agit.RepoLogActivity.repoLogIntentFor;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
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

import com.madgag.agit.GitOperationsService.GitOperationsBinder;
import com.madgag.agit.operations.GitAsyncTask;
import com.madgag.agit.operations.OpPrompt;


public class RepositoryManagementActivity extends android.app.Activity {

	private ProgressDialog progressDialog;
	private AlertDialog stringEntryDialog,yesNoDialog;
	
	final int PROGRESS_DIALOG=0,STRING_ENTRY_DIALOG=1, YES_NO_DIALOG=2;
	private final int DELETION_DIALOG=3;
	public static final String TAG = "RepositoryManagementActivity";
	
    private Repository repository;
	
	private RepositoryOperationContext repositoryOperationContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_management);
        repository = repositoryFrom(getIntent());
        
        bindService(new Intent(this,GitOperationsService.class), serviceConnectionToRegisterThisAsManagementUI(), BIND_AUTO_CREATE);
        buttonUp(R.id.FetchButton, clickToFetch());
        buttonUp(R.id.DeleteButton,clickToDelete());
        buttonUp(R.id.LogButton,clickToShowLog());
        
		branchList = (ListView) findViewById(R.id.BranchList);
		branchList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1));
		branchList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String branchName = (String) parent.getAdapter().getItem(position);
				try {
					Ref branch = repository.getRef(branchName);
					RepositoryManagementActivity.this.startActivity(branchViewerIntentFor(repository.getDirectory(), branch));
				} catch (IOException e) {}
			}
		});
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
				updateBranches();
				updateUIToReflectServicePromptRequests();
			}
		};
	}

	private OnClickListener clickToFetch() {
		return new OnClickListener() {
			public void onClick(View v) {
				startService(new GitIntentBuilder("git.FETCH").repository(repository).toIntent());
			}
		};
	}

	private OnClickListener clickToDelete() {
		return new OnClickListener() {
			public void onClick(View v) {
				showDialog(DELETION_DIALOG);
				new RepoDeleter(repository.getDirectory(), RepositoryManagementActivity.this).execute();
			}
		};
	}

	private OnClickListener clickToShowLog() {
		return new OnClickListener() {
			public void onClick(View v) { startActivity(repoLogIntentFor(repository.getDirectory())); }			
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
				if (intent.getData().equals(Uri.fromFile(repository.getDirectory()))) {
					finish();
				}
			}
		}
	};
	
	BroadcastReceiver deletionBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "deletionBroadcastReceiver got broadcast : "+intent);
			if (!repository.getDirectory().exists()) {
			//if (intent.getData().equals(Uri.fromFile(gitdir))) {
				finish();
			}
		}
	};
	private ListView branchList;
	
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
    	repository = repositoryFrom(getIntent());
        Log.i(TAG, "onResume called with gitdir="+repository.getDirectory());
		((TextView) findViewById(R.id.RepositoryFileLocation)).setText(repository.getDirectory().getAbsolutePath());
		registerReceiver(operationProgressBroadcastReceiver, new IntentFilter("git.operation.progress.update"));
		
		registerReceiver(deletionBroadcastReceiver, new IntentFilter(REPO_DELETE_COMPLETED));
		registerRecieverForServicePromptRequests();
		
		//repositoryOperationContext.getCurrentOperation().getPromptHelper().;
		updateBranches();
		updateUIToReflectServicePromptRequests();
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

	private void updateBranches() {
		if (repositoryOperationContext==null) {
			return;
		}
		
		RefDatabase refDatabase = repository.getRefDatabase();
		ArrayAdapter<String> adapter = (ArrayAdapter<String>) branchList.getAdapter();
		adapter.clear();
		try {
			Map<String, Ref> remoteRefs = refDatabase.getRefs(Constants.R_REMOTES);
			for (Ref ref : remoteRefs.values()) {
				adapter.add(ref.getName());
			}
		} catch (IOException e) { throw new RuntimeException(e); }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RepositoryCache.close(repository);
	}

	public Repository getRepository() {
		return repository;
	}
}
