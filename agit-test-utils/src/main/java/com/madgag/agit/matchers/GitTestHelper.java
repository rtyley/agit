/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.matchers;


import static com.madgag.agit.GitTestUtils.repoFor;
import static com.madgag.compress.CompressUtil.unzip;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.madgag.agit.TestEnvironment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.lib.Repository;

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
        assertThat("Stream for "+fileName, rawZipFileInputStream, notNullValue());

        File repoParentFolder = newFolder("unpacked-" + fileName);
        unzip(rawZipFileInputStream, repoParentFolder);
        rawZipFileInputStream.close();
        return repoParentFolder;
    }

    public File newFolder() {
        return newFolder("tmp");
    }

    public File newFolder(String prefix) {
        return new File(environment.tempFolder(), prefix + "-" + (++uniqueIndex));
    }
}
