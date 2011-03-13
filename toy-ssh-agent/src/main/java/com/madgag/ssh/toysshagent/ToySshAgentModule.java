package com.madgag.ssh.toysshagent;

import android.util.Log;
import roboguice.config.AbstractAndroidModule;

public class ToySshAgentModule extends AbstractAndroidModule {

	@Override
    protected void configure() {
    	Log.i("TSAM", "Configuring the Toy ssh-agent app...");
    }
	
}
