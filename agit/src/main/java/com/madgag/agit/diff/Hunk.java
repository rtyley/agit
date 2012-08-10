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

package com.madgag.agit.diff;

import android.util.Log;

import java.util.LinkedList;

import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;

/**
 * A 'hunk' is a diff chunk that includes context lines around one or more edits.
 */
public class Hunk {
    public static final String TAG = "HUNK";

    public final String before, after;
    private LinkedList<diff_match_patch.Diff> diffs;

    public Hunk(String before, String after) {
        this.before = before;
        this.after = after;
    }

    public LinkedList<diff_match_patch.Diff> diffs() {
        if (diffs == null) {
            diffs = calculateDiffs();
        }
        return diffs;
    }

    private LinkedList<diff_match_patch.Diff> calculateDiffs() {
        Log.d(TAG, "Calculating diffs");
        diff_match_patch differ = new diff_match_patch(new StandardBreakScorer());
        LinkedList<diff_match_patch.Diff> diffs = differ.diff_main(before, after);
        differ.diff_cleanupSemantic(diffs);
        return diffs;
    }
}
