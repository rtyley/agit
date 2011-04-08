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

import static com.madgag.agit.GitTestUtils.gitServerHostAddress;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.eclipse.jgit.transport.URIish;

import android.os.Environment;

public class GitTestUtils {

	public static String gitServerHostAddress() throws IOException,
			FileNotFoundException, UnknownHostException {
		File bang = new File(Environment.getExternalStorageDirectory(),
				"agit-integration-test.properties");
		Properties properties = new Properties();
		if (bang.exists()) {
			properties.load(new FileReader(bang));
		}
		String hostAddress = properties.getProperty("gitserver.host.address",
				"10.0.2.2");
		InetAddress address = InetAddress.getByName(hostAddress);
		assertThat("Test gitserver host " + hostAddress + " is reachable",
				address.isReachable(1000), is(true));
		return hostAddress;
	}

	private static long unique_number = currentTimeMillis();

	public static File newFolder() {
		File path = new File(Environment.getExternalStorageDirectory(),
				"agit-test-repos");
		return new File(path, "" + (unique_number++));
	}

	public static URIish integrationGitServerURIFor(String repoPath)
			throws URISyntaxException, IOException, FileNotFoundException,
			UnknownHostException {
		return new URIish("ssh://" + gitServerHostAddress() + ":29418/"
				+ repoPath);
	}
}
