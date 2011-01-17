package com.madgag.agit;

import static org.eclipse.jgit.transport.RemoteConfig.getAllRemoteConfigs;

import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;

public class RDTRemote extends RepoDomainType<RemoteConfig> {

	public RDTRemote(Repository repository) {
		super(repository);
	}
	@Override
	String name() { return "remote"; }
	public Collection<RemoteConfig> getAll() {
		try {
			return getAllRemoteConfigs(repository.getConfig());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	CharSequence conciseSummary(RemoteConfig rc) {
		return rc.getName()+": "+rc.getURIs().get(0);
	}

	@Override
	String conciseSeparator() {
		return "\n";
	}

	@Override
	CharSequence conciseSummaryTitle() {
		return "Remotes";
	}
	@Override
	String idFor(RemoteConfig e) {
		return e.getName();
	}
	
	@Override
	CharSequence shortDescriptionOf(RemoteConfig rc) {
		return rc.getURIs().get(0).toString();
	}
}
