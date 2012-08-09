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
import static com.google.common.collect.Lists.newArrayList;
import android.support.v4.util.LruCache;

import java.util.List;

public class CachingFilePathListMatcher {

    private final LruCache<CharSequence, List<CharSequence>> filePathCache;

    public CachingFilePathListMatcher(final List<CharSequence> filePaths) {
        filePathCache = new LruCache<CharSequence, List<CharSequence>>(32) {
            @Override
            protected List<CharSequence> create(CharSequence key) {
                List<CharSequence> searchSpace;
                if (key.length() > 1) {
                    searchSpace = filePathCache.get(key.subSequence(0, key.length() - 1));
                } else {
                    searchSpace = filePaths;
                }
                return newArrayList(filter(searchSpace, new FilePathMatcher(key)));
            }
        };
    }

    public List<CharSequence> get(CharSequence constraint) {
        return filePathCache.get(constraint);
    }
}
