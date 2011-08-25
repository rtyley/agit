package com.madgag.agit.matchers;


import com.madgag.agit.TestEnvironment;
import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.madgag.agit.GitTestUtils.repoFor;
import static com.madgag.compress.CompressUtil.unzip;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GitTestHelper {
	private final TestEnvironment environment;
	private static long uniqueIndex = currentTimeMillis();

	public GitTestHelper(TestEnvironment environment) {
		this.environment = environment;
	}

	public Repository unpackRepo(String fileName) throws IOException, ArchiveException {
        return repoFor(unpackRepoAndGetGitDir(fileName));
    }

    public File unpackRepoAndGetGitDir(String fileName) throws IOException, ArchiveException {
        InputStream rawZipFileInputStream = environment.streamFor(fileName);
		assertThat(rawZipFileInputStream, notNullValue());

		File repoParentFolder = newFolder("unpacked-"+fileName);
		unzip(rawZipFileInputStream, repoParentFolder);
        rawZipFileInputStream.close();
        return repoParentFolder;
    }

	public File newFolder() {
		return newFolder("tmp");
	}

	public File newFolder(String prefix) {
		return new File(environment.tempFolder(),prefix+"-"+(++uniqueIndex));
	}
}
