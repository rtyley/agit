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

package com.madgag.agit.git.model;

import static org.eclipse.jgit.transport.RemoteConfig.getAllRemoteConfigs;

import java.net.URISyntaxException;
import java.util.List;

import com.google.inject.Inject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;


public class RDTRemote extends RepoDomainType<RemoteConfig> {
    @Inject
	public RDTRemote(Repository repository) {
		super(repository);
	}
    
	@Override
	public String name() { return "remote"; }
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
	public CharSequence conciseSummaryTitle() {
		return "Remotes";
	}
	@Override
    public String idFor(RemoteConfig e) {
		return e.getName();
	}
	
	@Override
    public CharSequence shortDescriptionOf(RemoteConfig rc) {
		return rc.getURIs().get(0).toString();
	}
}
