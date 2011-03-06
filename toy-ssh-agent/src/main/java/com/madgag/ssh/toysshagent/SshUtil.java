package com.madgag.ssh.toysshagent;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import net.schmizz.sshj.common.Buffer.PlainBuffer;
import net.schmizz.sshj.common.KeyType;

import org.spongycastle.jce.provider.BouncyCastleProvider;

public class SshUtil {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public byte[] sshEncode(PublicKey publicKey) {
		return new PlainBuffer().putPublicKey(publicKey).getCompactData();
	}
	
	public byte[] sign(byte[] data, PrivateKey privateKey) {
		String keyTypeName = KeyType.fromKey(privateKey).toString();
		try {
			return new PlainBuffer().putString(keyTypeName).putBytes(rawSignatureFor(data, privateKey)).getCompactData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	byte[] rawSignatureFor(byte[] data, PrivateKey privateKey)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {
		Signature signer = Signature.getInstance("SHA1withRSA");
		signer.initSign(privateKey);
		signer.update(data);
		return signer.sign();
	}

}
