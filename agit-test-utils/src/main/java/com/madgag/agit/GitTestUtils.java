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

import android.os.Environment;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GitTestUtils {
    public static final String RSA_USER = "rsa_user", DSA_USER = "dsa_user";

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

	public static URIish integrationGitServerURIFor(String repoPath)
			throws URISyntaxException, IOException, FileNotFoundException,
			UnknownHostException {
        return new URIish()
                .setScheme("ssh")
                .setUser(RSA_USER) // use RSA user by default - mini-git-server currently requires publickey auth
                .setHost(gitServerHostAddress())
                .setPort(29418)
                .setPath(repoPath);
	}

    public static Repository repoFor(File folder) throws IOException {
        File resolvedGitDir = resolveGitDirFor(folder);
        assertThat("gitdir "+resolvedGitDir+" exists",resolvedGitDir, notNullValue());
        return new FileRepository(resolvedGitDir);
    }

    private static File resolveGitDirFor(File folder) {
        return RepositoryCache.FileKey.resolve(folder, FS.detect());
    }
}
