package com.madgag.agit;

import java.util.Map;
import java.util.Map.Entry;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshConfigSessionFactory;
import org.eclipse.jgit.util.FS;

import android.os.RemoteException;
import android.util.Log;

import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;
import com.madgag.ssh.authagent.client.jsch.SSHAgentIdentity;

public class AndroidSshSessionFactory extends SshConfigSessionFactory {

	private static final String TAG = "ASSF";
	private final AndroidAuthAgentProvider androidAuthAgentProvider;
	private final PromptHelper promptHelper;
	
	public AndroidSshSessionFactory(AndroidAuthAgentProvider androidAuthAgentProvider, PromptHelper promptHelper) {
		this.androidAuthAgentProvider = androidAuthAgentProvider;
		this.promptHelper = promptHelper;
	}
	
	@Override
	protected void configure(Host host, Session session) {
		session.setUserInfo(new AndroidUserInfo(promptHelper));
	}

	@Override
	protected JSch createDefaultJSch(FS fs) throws JSchException {
		final JSch jsch = new JSch();
		// knownHosts(jsch, fs);
		
		addSshAgentTo(jsch);
		return jsch;
	}

	private void addSshAgentTo(final JSch jsch) throws JSchException {
		AndroidAuthAgent authAgent=androidAuthAgentProvider.getAuthAgent();
		if (authAgent==null) {
			Log.w(TAG, "NO SSH-AGENT AVAILABLE");
		} else {
			updateJschWithAvailableIdentities(jsch, authAgent);
		}
	}

	private void updateJschWithAvailableIdentities(final JSch jsch,
			AndroidAuthAgent authAgent) throws JSchException {
		Map<String, byte[]> identities;
		try {
			identities = authAgent.getIdentities();
		} catch (RemoteException e) {
			throw new JSchException("Couldn't get identities from Auth Agent "+authAgent, e);
		}
		for (Entry<String,byte[]> i : identities.entrySet()) {			
			byte[] publicKey = i.getValue();
			String name = i.getKey();
			Identity identity = new SSHAgentIdentity(androidAuthAgentProvider, publicKey, name);
			jsch.addIdentity(identity , null);
		}
	}
}
