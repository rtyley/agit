package com.madgag.agit;

import static java.lang.System.identityHashCode;

import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

@RepoOpScoped
public class TransportFactory {
	
	private final static String TAG = "TF";
	private final Repository repo;
	private final Provider<SshSessionFactory> sshSessionFactoryProvider;
	
	@Inject
	public TransportFactory( Repository repo, Provider<SshSessionFactory> sshSessionFactoryProvider) {
		this.repo = repo;
		this.sshSessionFactoryProvider = sshSessionFactoryProvider;
	}
	
	public Transport transportFor(RemoteConfig remoteConfig) {
		Transport tn;
		try {
			Log.i(TAG , "Creating transport for repo with " + identityHashCode(repo));
			tn = Transport.open(repo, remoteConfig);
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		}
		if (tn instanceof SshTransport) {
			((SshTransport) tn).setSshSessionFactory(sshSessionFactoryProvider.get());
		}
		return tn;
	}
}
