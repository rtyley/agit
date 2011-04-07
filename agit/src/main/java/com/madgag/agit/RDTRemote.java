package com.madgag.agit;

import static org.eclipse.jgit.transport.RemoteConfig.getAllRemoteConfigs;

import java.net.URISyntaxException;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;


public class RDTRemote extends RepoDomainType<RemoteConfig> {
    @Inject
	public RDTRemote(Repository repository) {
		super(repository);
	}
    
	@Override
	String name() { return "remote"; }
	public List<RemoteConfig> getAll() {
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
