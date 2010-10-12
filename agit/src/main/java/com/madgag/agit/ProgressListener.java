package com.madgag.agit;

public interface ProgressListener<P> {
	void publish(P... values);
}
