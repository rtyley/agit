/**
 * 
 */
package com.madgag.agit;

public class Progress {
	final String msg;
	final int totalWork,totalCompleted;
	public Progress(String msg, int totalWork, int totalCompleted) {
		this.msg = msg;
		this.totalWork = totalWork;
		this.totalCompleted = totalCompleted;
	}
}