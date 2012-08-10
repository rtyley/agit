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

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;
import android.util.Log;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.annotation.Nullable;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class LogStartProvider {

    private static final String TAG = "LSP";
    public static final Function<Ref, ObjectId> OBJECT_IDS_FOR_REFS = new Function<Ref, ObjectId>() {
        public ObjectId apply(Ref ref) {
            return ref.getObjectId();
        }
    };

    @Inject
    Repository repository;
    @Inject
    @Named("branch")
    @Nullable
    Ref branch;



    public Iterable<ObjectId> get() {
        Iterable<Ref> refs = getRefs();
        Log.d(TAG, "Using refs " + refs);
        return transform(refs, OBJECT_IDS_FOR_REFS);
    }

    public Ref getCurrentRef() {
        return branch;
    }

    private Iterable<Ref> getRefs() {
        if (branch != null) {
            return asList(branch);
        }

        try {
            return repository.getRefDatabase().getRefs(R_REMOTES).values();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
