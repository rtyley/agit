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
