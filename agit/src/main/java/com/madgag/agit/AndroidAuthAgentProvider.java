package com.madgag.agit;

import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public interface AndroidAuthAgentProvider {

	AndroidAuthAgent getAuthAgent();

}
