/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit.diff;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.List;

import android.util.Log;
import org.eclipse.jgit.diff.DiffEntry;

public class FileDiff {
    private static final String TAG = "FD";

    private final LineContextDiffer lineContextDiffer;
    private final DiffEntry diffEntry;
    private List<Hunk> hunks;

    public FileDiff(LineContextDiffer lineContextDiffer,DiffEntry diffEntry) {
		this.lineContextDiffer = lineContextDiffer;
		this.diffEntry = diffEntry;
	}
	
	public List<Hunk> getHunks() {
        if (hunks==null) {
            hunks = calculateHunks();
        }
		return hunks;
	}

    private List<Hunk> calculateHunks() {
        List<Hunk> h;
        try {
            h = lineContextDiffer.format(diffEntry);
            Log.d(TAG, "Calculated "+h.size()+" hunks for "+diffEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return h;
    }

    public DiffEntry getDiffEntry() {
		return diffEntry;
	}
}
