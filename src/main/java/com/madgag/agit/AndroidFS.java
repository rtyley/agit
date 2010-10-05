package com.madgag.agit;

import static android.content.Context.MODE_WORLD_READABLE;
import static android.content.Context.MODE_WORLD_WRITEABLE;

import java.io.File;

import org.eclipse.jgit.util.FS;

import android.content.Context;
import android.util.Log;

public class AndroidFS extends FS {
	
	public static final String TAG="AndroidFS";
	
	private final Context context;

	public AndroidFS(Context context) {
		Log.i(TAG, "constructor context="+context);
		this.context = context;
	}
	
	public boolean supportsExecute() {
		return false;
	}

	public boolean canExecute(final File f) {
		return false;
	}

	public boolean setExecute(final File f, final boolean canExec) {
		return false;
	}

	@Override
	public boolean retryFailedLockFileCommit() {
		return false;
	}
	
	protected File userHomeImpl() {
		Log.i(TAG, "context="+context);
		File homeDir = context.getDir("ssh-home", MODE_WORLD_READABLE & MODE_WORLD_WRITEABLE);
		Log.i(TAG, "homeDir="+homeDir);
		return homeDir;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"["+context+"]";
	}
}
