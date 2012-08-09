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
import com.google.common.collect.Ordering;

public class ScoredPath {

    public static Function<ScoredPath, FilePath> PATH = new Function<ScoredPath, FilePath>() {
        public FilePath apply(ScoredPath input) {
            return input.filePath;
        }
    };

    public static Function<FilePath, ScoredPath> scoreFor(final String constraint) {
        final FilePathMatcher filePathMatcher = new FilePathMatcher(constraint);
        return new Function<FilePath, ScoredPath>() {
            public ScoredPath apply(FilePath s) {
                return new ScoredPath(s, filePathMatcher.score(s));
            }
        };
    }

    public final static Ordering<ScoredPath> ORDERING = new Ordering<ScoredPath>() {
        public int compare(ScoredPath left, ScoredPath right) {
            return Double.compare(right.score, left.score);
        }
    };

    public final FilePath filePath;
    public final double score;

    public ScoredPath(FilePath filePath, double score) {
        this.filePath = filePath;
        this.score = score;
    }
}
