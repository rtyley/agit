package com.madgag.agit.ssh;

import static android.content.Context.BIND_AUTO_CREATE;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    
    private final Lock lock = new ReentrantLock();
    private final Condition authAgentBound = lock.newCondition(); // TODO CountDownLatch or AbstractQueuedSynchronizer varient?
	private AndroidAuthAgent authAgent;

	@Inject
	public AndroidAuthAgentProvider(Context context) {
		bindSshAgentTo(context);
	}
	
	public AndroidAuthAgent get() {
		waitForAuthAgentBind();
		return authAgent;
	}

	private void bindSshAgentTo(Context context) {
		context.bindService(new Intent("org.openintents.ssh.BIND_SSH_AGENT_SERVICE"), new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected() : Lost "+authAgent);
				authAgent=null;
			}
			
			public void onServiceConnected(ComponentName name, IBinder binder) {
				Log.i(TAG, "onServiceConnected() : binder="+binder);
				authAgent=AndroidAuthAgent.Stub.asInterface(binder);
				// showDebugInfoForAuthAgent(); Showing this info is actually a bit confusing
				signalAuthAgentBound();
			}
		}, BIND_AUTO_CREATE);
        Log.i(TAG, "made request using context "+context+" to bind to the SSH_AGENT_SERVICE");
	}

	private void waitForAuthAgentBind() {
		lock.lock();
		try {
			boolean timeElapsed=authAgentBound.await(1,SECONDS);
			Log.d(TAG, "time-out waiting for AndroidAuthAgent: "+timeElapsed+" agent="+authAgent);
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupted waiting for AndroidAuthAgent",e);
		} finally {
			lock.unlock();
		}
	}
	
	private void signalAuthAgentBound() {
		lock.lock();
		try {
			authAgentBound.signal();
		} finally {
			lock.unlock();
		}
	}
	
	private void showDebugInfoForAuthAgent() {
		Log.d(TAG, "authAgent="+authAgent);
		try {
			Log.d(TAG, "authAgent.getIdentities()="+authAgent.getIdentities());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
