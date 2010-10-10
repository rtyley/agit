package com.madgag.ssh.authagent.client.jsch;

import android.os.RemoteException;
import android.util.Log;

import com.jcraft.jsch.Identity;
import com.jcraft.jsch.JSchException;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class SSHAgentIdentity implements Identity {
	private final String TAG="SSHAgentIdentity";

	private final AndroidAuthAgent authAgent;
	private final byte[] publicKey;
	private final String name;
	
	public SSHAgentIdentity(AndroidAuthAgent authAgent, byte[] publicKey, String name) {
		this.authAgent = authAgent;
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
			return authAgent.sign(publicKey, data);
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
