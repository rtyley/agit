package com.madgag.agit;

import static com.madgag.agit.MessagingProgressMonitor.GIT_OPERATION_PROGRESS_UPDATE;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.connectbot.service.PromptHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.madgag.agit.GitOperationsService.FetchThread;
import com.madgag.agit.GitOperationsService.GitOperationsBinder;
import com.madgag.agit.MessagingProgressMonitor.Progress;


public class RepositoryManagementActivity extends android.app.Activity {

	private ProgressDialog progressDialog;
	private AlertDialog stringEntryDialog,yesNoDialog;
	
	private final int PROGRESS_DIALOG=0,STRING_ENTRY_DIALOG=1,YES_NO_DIALOG=2;
	public static final String TAG = "RepositoryManagementActivity";
	private File gitdir;
	private RepositoryOperationContext repositoryOperationContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_management);
        bindService(new Intent(this,GitOperationsService.class), new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				repositoryOperationContext=null;
			}
			
			public void onServiceConnected(ComponentName name, IBinder binder) {
				repositoryOperationContext=((GitOperationsBinder) binder).getService().getOrCreateRepositoryOperationContextFor(gitdir);
				Log.i(TAG, "bound opService="+repositoryOperationContext);
			}
		}, BIND_AUTO_CREATE);
        ((Button) findViewById(R.id.FetchButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startService(new Intent("git.FETCH", Uri.fromFile(gitdir), RepositoryManagementActivity.this,GitOperationsService.class));
			}
		});
        ((Button) findViewById(R.id.DeleteButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					FileUtils.deleteDirectory(gitdir.getParentFile());
					startActivity(new Intent(RepositoryManagementActivity.this, RepositoryListActivity.class));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
        ((Button) findViewById(R.id.LogButton)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("git.log", Uri.fromFile(gitdir), RepositoryManagementActivity.this,RepoLogActivity.class));
			}
		});
    }
    
    
	BroadcastReceiver operationProgressBroadcastReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "Got broadcast : "+intent.getAction());
			if (intent.getAction().equals(GIT_OPERATION_PROGRESS_UPDATE)){
				updateOperationProgressDisplay();
			} else if (intent.getAction()=="git.user.interation.request") {
				Log.d(TAG, "I should probably do something helpful");
			}
		}
	};
	
	private void updateOperationProgressDisplay() {
		Log.d(TAG, "Updating Operation Progress display");
//		String boo = gitOperationsService.currentOperationFor(db);
//		((TextView) findViewById(R.id.GitOperationStatus)).setText(boo);
		showDialog(PROGRESS_DIALOG);
		FetchThread ft=repositoryOperationContext.getCurrentOperation();
		Progress currentProgress = ft.progressMonitor.getCurrentProgress();
		progressDialog.setProgress(currentProgress.totalCompleted);
		progressDialog.setMax(currentProgress.totalWork);
		progressDialog.setMessage(currentProgress.msg);
	}
	
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("Fetching...");
			progressDialog.setTitle("Fetch totmarto");
			progressDialog.setCancelable(true);
			return progressDialog;
		case YES_NO_DIALOG:
			new AlertDialog.Builder(this)
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
					repositoryOperationContext.getCurrentOperation().promptHelper.setResponse((CharSequence)input.getText());
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
				repositoryOperationContext.getCurrentOperation().promptHelper.setResponse(bool);
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
					repositoryOperationContext.getCurrentOperation().getCancellationSignaller().setCancelled();
				}
			});
		case YES_NO_DIALOG:
			AlertDialog alertDialog=(AlertDialog) dialog;
			alertDialog.setMessage(repositoryOperationContext.getCurrentOperation().promptHelper.promptHint);
		default:
		}
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
        gitdir=getGitDirFrom(getIntent());
		((TextView) findViewById(R.id.RepositoryFileLocation)).setText(gitdir.getAbsolutePath());
		registerReceiver(operationProgressBroadcastReceiver, new IntentFilter("git.operation.progress.update"));
		registerRecieverForServicePromptRequests();
		updateUIToReflectServicePromptRequests();
    }
    
    private void registerRecieverForServicePromptRequests() {
		// TODO Auto-generated method stub
		
	}

	private void unregisterRecieverForServicePromptRequests() {
		// TODO Auto-generated method stub
		
	}
	
	private void updateUIToReflectServicePromptRequests() {
		if (repositoryOperationContext==null) {
			//guess there's not been a running service yet...
			return;
		}
		FetchThread currentOperation = repositoryOperationContext.getCurrentOperation();
		if (currentOperation==null) {
			// wipe dialogs?
			return;
		}
		PromptHelper prompt=currentOperation.promptHelper;
		if (String.class.equals(prompt.promptRequested)) {
			showDialog(STRING_ENTRY_DIALOG);
		} else if(Boolean.class.equals(prompt.promptRequested)) {
			showDialog(YES_NO_DIALOG);
		} else {
//			hideAllPrompts();
//			view.requestFocus();
		}
	}

	@Override
    protected void onPause() {
    	super.onPause();
    	unregisterReceiver(operationProgressBroadcastReceiver);
    	unregisterRecieverForServicePromptRequests();
    }


	public static File getGitDirFrom(Intent intent) {
		File gd=new File(intent.getData().getPath());
    	Log.i(TAG, "gd is "+gd.getAbsolutePath());
    	return gd;
	}
}
