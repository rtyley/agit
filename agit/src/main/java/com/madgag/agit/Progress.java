/**
 * 
 */
package com.madgag.agit;

import static org.eclipse.jgit.lib.ProgressMonitor.UNKNOWN;

public class Progress {
	public final String msg;
	public final int totalWork,totalCompleted;
	
	public Progress(String msg) {
		this.msg = msg;
		this.totalWork = 0;
		this.totalCompleted = 0;
	}
	
	public Progress(String msg, int totalWork, int totalCompleted) {
		this.msg = msg;
		this.totalWork = totalWork;
		this.totalCompleted = totalCompleted;
	}
	
	public boolean isIndeterminate() {
		return totalWork==UNKNOWN;
	}
}