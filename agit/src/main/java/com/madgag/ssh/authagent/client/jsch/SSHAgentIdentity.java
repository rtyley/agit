package com.madgag.ssh.authagent.client.jsch;

import android.os.RemoteException;
import android.util.Log;

import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSchException;
import com.madgag.agit.AndroidAuthAgentProvider;

public class SSHAgentIdentity implements Identity {
	private final String TAG="SSHAgentIdentity";

	private final AndroidAuthAgentProvider authAgentProvider;
	private final byte[] publicKey;
	private final String name;
	
	public SSHAgentIdentity(AndroidAuthAgentProvider authAgentProvider, byte[] publicKey, String name) {
		this.authAgentProvider = authAgentProvider;
		this.publicKey = publicKey;
		this.name = name;
	}
	
	public void clear() {
	}

	public boolean decrypt() {
		return false;
	}

	public String getAlgName() {
		return "ssh-rsa";
	}

	public String getName() {
		return name;
	}

	public byte[] getPublicKeyBlob() {
		return publicKey;
	}

	public byte[] getSignature(byte[] data) {
		try {
			return authAgentProvider.getAuthAgent().sign(publicKey, data);
		} catch (RemoteException e) {
			Log.e(TAG, "sign() failed", e);
			throw new RuntimeException(e);
		}
	}

	public boolean isEncrypted() {
		return false;
	}

	public boolean setPassphrase(byte[] passphrase) throws JSchException {
		return false;
	}

}
