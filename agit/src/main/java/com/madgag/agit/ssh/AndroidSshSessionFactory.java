package com.madgag.agit.ssh;

import android.os.RemoteException;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;
import com.madgag.ssh.authagent.client.jsch.SSHAgentIdentity;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

import java.util.Map;
import java.util.Map.Entry;

public class AndroidSshSessionFactory extends JschConfigSessionFactory {

	private static final String TAG = "ASSF";
	
	private final Provider<AndroidAuthAgent> androidAuthAgentProvider;
	private final UserInfo userInfo;
	// private final BlockingPromptService blockingPromptService;
	
	@Inject
	public AndroidSshSessionFactory(Provider<AndroidAuthAgent> androidAuthAgentProvider, UserInfo userInfo) {
		this.androidAuthAgentProvider = androidAuthAgentProvider;
		this.userInfo = userInfo;
	}
	
	@Override
	protected void configure(OpenSshConfig.Host host, Session session) {
		session.setUserInfo(userInfo);
	}

	@Override
	protected JSch createDefaultJSch(FS fs) throws JSchException {
		final JSch jsch = new JSch();
        // jsch.setHostKeyRepository();
		// knownHosts(jsch, fs);
		addSshAgentTo(jsch);
		return jsch;
	}

	private void addSshAgentTo(final JSch jsch) throws JSchException {
		AndroidAuthAgent authAgent=androidAuthAgentProvider.get();
		Log.w(TAG, "authAgent="+authAgent);
		if (authAgent==null) {
			Log.w(TAG, "NO SSH-AGENT AVAILABLE");
		} else {
			updateJschWithAvailableIdentities(jsch, authAgent);
		}
	}

	@SuppressWarnings("unchecked")
	private void updateJschWithAvailableIdentities(final JSch jsch,	AndroidAuthAgent authAgent) throws JSchException {
		Map<String, byte[]> identities;
		try {
			identities = authAgent.getIdentities();
		} catch (RemoteException e) {
			throw new JSchException("Couldn't get identities from Auth Agent "+authAgent, e);
		}
		updateJschWith(jsch, identities);
	}

	private void updateJschWith(final JSch jsch, Map<String, byte[]> identities) throws JSchException {
		for (Entry<String,byte[]> i : identities.entrySet()) {			
			byte[] publicKey = i.getValue();
			String name = i.getKey();
			jsch.addIdentity(new SSHAgentIdentity(androidAuthAgentProvider, publicKey, name) , null);
		}
	}




}
