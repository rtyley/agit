package com.madgag.agit;

import java.util.LinkedList;
import java.util.List;

import com.madgag.diff.Update;
import com.madgag.diff.UpdatesFromDiffConverter;

import name.fraser.neil.plaintext.SemanticBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class FilePathDiffer {
	
	UpdatesFromDiffConverter converter=new UpdatesFromDiffConverter();
	
	/*
	 * Try converting EQUAL, INSERT, DELETE to EQUAL, UPDATE ? Update has old, new - grow update area to Preferred boundaries?
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
					if (endOne=='/' || startTwo=='/') {
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
		StringBuilder sb =new StringBuilder();
		for (Update u : updates) {
			sb.append(u);
		}
		return sb.toString();
	}
}
