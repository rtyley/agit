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

import com.madgag.diff.Update;
import com.madgag.diff.UpdatesFromDiffConverter;

import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.SemanticBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class FilePathDiffer {

    UpdatesFromDiffConverter converter = new UpdatesFromDiffConverter();

    /*
      * Try converting EQUAL, INSERT, DELETE to EQUAL, UPDATE ? Update has old, new - grow update area to Preferred
      * boundaries?
      */
    private final diff_match_patch differ = new diff_match_patch(
            new SemanticBreakScorer() {
                public int scoreBreakOver(String one, String two) {
                    if (one.length() == 0 || two.length() == 0) {
                        // Edges are the best.
                        return 5;
                    }
                    int score = 0;
                    // One point for non-alphanumeric.
                    char endOne = one.charAt(one.length() - 1);
                    char startTwo = two.charAt(0);
                    if (Character.isLowerCase(endOne) && Character.isUpperCase(startTwo)) {
                        score++;
                    }
                    if (endOne == '/' || startTwo == '/') {
                        score++;
                    }
//					if (endOne== && Character.isUpperCase(startTwo)) {
//				      score++;
//					}
                    return score;
                }
            });

    public String diff(String oldPath, String newPath) {
        LinkedList<Diff> diffs = differ.diff_main(oldPath, newPath);
        differ.diff_cleanupSemantic(diffs);
        List<Update> updates = converter.convert(diffs);
        StringBuilder sb = new StringBuilder();
        for (Update u : updates) {
            sb.append(u);
        }
        return sb.toString();
    }
}
