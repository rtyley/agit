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

package com.madgag.agit.filepath;


import com.google.common.base.Function;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.util.RawParseUtils;

public class FilePath {

    public static Function<FilePath, String> PATH = new Function<FilePath, String>() {
        public String apply(FilePath fp) {
            return fp.getPath();
        }
    };

    public static Function<String, FilePath> TO_FILEPATH = new Function<String, FilePath>() {
        public FilePath apply(String input) {
            return new FilePath(input);
        }
    };

    private byte[] rawPath;

    private String path;

    public FilePath(byte[] rawPath) {
        this.rawPath = rawPath;
    }

    public FilePath(String path) {
        this.path = path;
    }

    public synchronized String getPath() {
        if (path == null) {
            path = RawParseUtils.decode(Constants.CHARSET, rawPath);
            rawPath = null; // release memory
        }
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilePath filePath = (FilePath) o;

        return getPath().equals(filePath.getPath());
    }
}
