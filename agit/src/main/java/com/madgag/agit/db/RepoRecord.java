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

package com.madgag.agit.db;

import com.google.common.base.Function;

import java.io.File;

public class RepoRecord {

    public final static Function<RepoRecord, File> GITDIR_FOR_RECORD = new Function<RepoRecord, File>() {
        public File apply(RepoRecord repoRecord) {
            return repoRecord.gitdir;
        }
    };

    public long id;
    public File gitdir;

    public RepoRecord(long id, File gitdir) {
        this.id = id;
        this.gitdir = gitdir;
    }

    public String toString() {
        return "["+id+","+gitdir+"]";
    }
}
