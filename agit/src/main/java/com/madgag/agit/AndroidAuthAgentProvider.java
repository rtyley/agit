package com.madgag.agit;

import static android.content.Context.BIND_AUTO_CREATE;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class AndroidAuthAgentProvider implements Provider<AndroidAuthAgent> {

    protected static final String TAG = "AAAP";
    
	private AndroidAuthAgent authAgent;

	@Inject
	public AndroidAuthAgentProvider(Context context) {
		bindSshAgentTo(context);
	}
	
	public AndroidAuthAgent get() {
		return authAgent;
	}

	private void bindSshAgentTo(Context context) {
		context.bindService(new Intent("org.openintents.ssh.BIND_SSH_AGENT_SERVICE"), new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected - losing "+authAgent);
				authAgent=null;
			}
			
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.i(TAG, "onServiceConnected... got "+binder);
				authAgent=AndroidAuthAgent.Stub.asInterface(binder);
				Log.i(TAG, "bound "+authAgent);
				try {
					Log.d(TAG, "here are identities "+authAgent.getIdentities());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}, BIND_AUTO_CREATE);
        Log.i(TAG, "made context request to bind to the SSH_AGENT_SERVICE");
	}
}
