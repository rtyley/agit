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

package com.madgag.agit.git;

import org.eclipse.jgit.revwalk.RevBlob;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;

public interface GitObjectFunction<T> {

    T apply(RevCommit commit);

    T apply(RevTree tree);

    T apply(RevBlob blob);

    T apply(RevTag tag);

    public static abstract class Base<T> implements GitObjectFunction<T> {

        public T apply(RevCommit commit) {
            return applyDefault(commit);
        }

        public T apply(RevTree tree) {
            return applyDefault(tree);
        }

        public T apply(RevBlob blob) {
            return applyDefault(blob);
        }

        public T apply(RevTag tag) {
            return applyDefault(tag);
        }

        public abstract T applyDefault(RevObject revObject);

    }
}
