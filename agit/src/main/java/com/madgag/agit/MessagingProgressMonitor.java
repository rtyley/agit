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

import com.google.inject.Inject;
import com.madgag.agit.guice.OperationScoped;
import org.eclipse.jgit.lib.ProgressMonitor;

import android.util.Log;

@OperationScoped
public class MessagingProgressMonitor implements ProgressMonitor {

	public static final String TAG = "MessagingProgressMonitor";
	
	private boolean output;

	private long taskBeganAt;

	private String msg;

	private int lastWorked;

	private int totalWork;
    
	public Progress currentProgress;

	private final ProgressListener<Progress> progressListener;
    private final CancellationSignaller cancellationSignaller;

    public Progress getCurrentProgress() {
		return currentProgress;
	}

    @Inject
	public MessagingProgressMonitor( ProgressListener<Progress> progressListener, CancellationSignaller cancellationSignaller) {
		this.progressListener = progressListener;
        this.cancellationSignaller = cancellationSignaller;
    }
	
	public boolean isCancelled() {
		return cancellationSignaller.isCancelled();
	}
	
	public void beginTask(final String title, final int total) {
		Log.d(TAG, "started "+title+" total="+total);
		
		endTask();
		msg = title;
		lastWorked = 0;
		totalWork = total;
	}

	public void endTask() {
		if (output) {
			if (totalWork != UNKNOWN)
				display(totalWork);
			System.err.println();
		}
		output = false;
		msg = null;
	}



	public void start(int arg0) {}

	public void update(int completed) {
		final int cmp = lastWorked + completed;
		//Log.d(TAG, "cmp "+cmp);
		
		if (totalWork == UNKNOWN) {
			display(cmp);
		} else {
			if ((cmp * 10 / totalWork) != (lastWorked * 10) / totalWork) {
				display(cmp);
			}
		}
		lastWorked = cmp;
		output = true;
	}

	private void display(int cmp) {
		currentProgress=new Progress(msg, totalWork, cmp);
		progressListener.publish(currentProgress);
	}

}
