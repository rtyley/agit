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

package com.madgag.agit;

import static com.madgag.agit.GitIntents.UNTIL_REVS;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;

import com.google.common.base.Function;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import roboguice.inject.InjectExtra;

public class LogStartProvider {

    private static final String TAG = "LSP";

    public static final Function<Ref, ObjectId> OBJECT_IDS_FOR_REFS = new Function<Ref, ObjectId>() {
        public ObjectId apply(Ref ref) {
            return ref.getObjectId();
        }
    };

    private @Inject Repository repository;
    private @InjectExtra(value=UNTIL_REVS, optional = true) ArrayList<String> untilRevs;

    public String getCurrentRef() {
        return untilRevs == null ? null : untilRevs.get(0);
    }

    public void markStartsOn(RevWalk revWalk) {
        try {
            for (String untilRev : impliedRevs()) {
                revWalk.markStart(revWalk.parseCommit(repository.resolve(untilRev)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Iterable<String> impliedRevs() throws IOException {
        return (untilRevs == null) ? repository.getRefDatabase().getRefs(R_REMOTES).keySet() : untilRevs;
    }
}
