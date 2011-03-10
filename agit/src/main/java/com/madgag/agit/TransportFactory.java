package com.madgag.agit;

import static java.lang.System.identityHashCode;

import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import com.google.inject.Inject;

import android.util.Log;

public class TransportFactory {
	
	private final AndroidSshSessionFactoryFactory sessionFactorySquare;
	private String TAG = "TF";

	@Inject
	public TransportFactory(AndroidSshSessionFactoryFactory androidSshSessionFactoryFactory) {
		this.sessionFactorySquare = androidSshSessionFactoryFactory;
	}
	
	public Transport transportFor(RepositoryOperationContext repositoryOperationContext, Repository repo, RemoteConfig remoteConfig) {
		Transport tn;
		try {
			Log.i(TAG , "Creating transport for repo with " + identityHashCode(repo));
			tn = Transport.open(repo, remoteConfig);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		}
		if (tn instanceof SshTransport) {
			SshSessionFactory sshSessionFactory = sessionFactorySquare.createSshSessionFactoryFor(repositoryOperationContext);
			((SshTransport) tn).setSshSessionFactory(sshSessionFactory);
		}
		return tn;
	}
}
