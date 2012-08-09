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


import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.filepath.ScoredPath.scoreFor;
import android.support.v4.util.LruCache;

import com.google.common.collect.Lists;

import java.util.List;

public class CachingFilePathListMatcher {

    private static final String TAG = "CachingFilePathListMatcher";
    private final LruCache<String, List<FilePath>> filteredPathsCache, sortedPathsCache;

    public CachingFilePathListMatcher(final List<FilePath> filePaths) {
        filteredPathsCache = new LruCache<String, List<FilePath>>(32) {
            @Override
            protected List<FilePath> create(String key) {
                List<FilePath> searchSpace;
                if (key.length() > 1) {
                    searchSpace = filteredPathsCache.get(key.substring(0, key.length() - 1));
                } else {
                    searchSpace = filePaths;
                }

                return newArrayList(filter(searchSpace, new FilePathMatcher(key)));
            }
        };
        sortedPathsCache = new LruCache<String, List<FilePath>>(16) {
            @Override
            protected List<FilePath> create(String key) {
                Iterable<FilePath> filteredPaths = filteredPathsCache.get(key);

                return Lists.transform(ScoredPath.ORDERING.sortedCopy(transform(filteredPaths,
                        scoreFor(key))), ScoredPath.PATH);
            }
        };
    }

    public List<FilePath> get(String constraint) {
        return sortedPathsCache.get(constraint);
    }
}
