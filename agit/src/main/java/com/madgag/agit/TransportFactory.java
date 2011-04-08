/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import static java.lang.System.identityHashCode;

import com.madgag.agit.guice.RepositoryScoped;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;

@RepositoryScoped
public class TransportFactory {
	
	private final static String TAG = "TF";
	private final Repository repo;
	private final Provider<SshSessionFactory> sshSessionFactoryProvider;
	
	@Inject
	public TransportFactory(Repository repo, Provider<SshSessionFactory> sshSessionFactoryProvider) {
		this.repo = repo;
		this.sshSessionFactoryProvider = sshSessionFactoryProvider;
	}
	
	public Transport transportFor(RemoteConfig remoteConfig) {
		Transport tn;
		try {
			Log.i(TAG , "Creating transport for repo with " + identityHashCode(repo));
			tn = Transport.open(repo, remoteConfig);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (tn instanceof SshTransport) {
			((SshTransport) tn).setSshSessionFactory(sshSessionFactoryProvider.get());
		}
		return tn;
	}
}
