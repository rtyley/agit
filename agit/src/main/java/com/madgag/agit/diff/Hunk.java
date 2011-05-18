package com.madgag.agit.diff;

import android.util.Log;
import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;

import java.util.LinkedList;

/**
 * A 'hunk' is a diff chunk that includes context lines around one or more edits.
 */
public class Hunk {
    public static final String TAG = "HUNK";
    
    public final String before, after;
    private LinkedList<diff_match_patch.Diff> diffs;

    public Hunk(String before, String after) {
        this.before=before;this.after=after;
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
