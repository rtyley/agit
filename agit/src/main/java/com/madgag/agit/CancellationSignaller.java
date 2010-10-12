package com.madgag.agit;

public interface CancellationSignaller {
	void setCancelled();
	
	boolean isCancelled();
}
