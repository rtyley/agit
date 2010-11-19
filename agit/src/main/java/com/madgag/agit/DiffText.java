package com.madgag.agit;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.google.common.collect.Lists.newArrayList;
import static name.fraser.neil.plaintext.diff_match_patch.Operation.INSERT;

import java.util.LinkedList;
import java.util.List;

import name.fraser.neil.plaintext.StandardBreakScorer;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import android.text.Editable;
import android.text.style.CharacterStyle;

public class DiffText {
	
	private List<CharacterStyle> insertSpans,deleteSpans;
	
	private final Editable spannableText;

	public DiffText(Editable spannableText) {
		this.spannableText = spannableText;
	}

	public void setTransitionProgress(float proportion) {
		updateDisplayWith(proportion);
	}
	
	

	void updateDisplayWith(float proportion) {
		DeltaSpan insertSpan = new DeltaSpan(true, proportion);
		DeltaSpan deleteSpan = new DeltaSpan(false, proportion);
		replace(insertSpans, insertSpan);
		replace(deleteSpans, deleteSpan);
	}

	private void replace(List<CharacterStyle> deltaSpans, CharacterStyle spanStyle) {
		for (int i=0;i<deltaSpans.size() ; ++i) {
			CharacterStyle oldSpanStyle = deltaSpans.get(i);
			int start=spannableText.getSpanStart(oldSpanStyle ),end=spannableText.getSpanEnd(oldSpanStyle);
			spannableText.removeSpan(oldSpanStyle);
			CharacterStyle mySpanStyle = CharacterStyle.wrap(spanStyle);
			spannableText.setSpan(mySpanStyle, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
			deltaSpans.set(i, mySpanStyle);
		}
	}
	
	public void initWith(String before,String after) {
		diff_match_patch differ = new diff_match_patch(new StandardBreakScorer());
		LinkedList<Diff> diffs = differ.diff_main(before, after);
		differ.diff_cleanupSemantic(diffs);
		DeltaSpan insertSpan = new DeltaSpan(true, 0.5f);
		DeltaSpan deleteSpan = new DeltaSpan(false, 0.5f);
		insertSpans = newArrayList();
		deleteSpans = newArrayList();
		spannableText.clear();
		for (Diff diff : diffs) {
			spannableText.append(diff.text);
			if (diff.operation!=Operation.EQUAL) {
				boolean insertNotDelete = diff.operation==INSERT;
				CharacterStyle deltaSpan = CharacterStyle.wrap(insertNotDelete?insertSpan:deleteSpan);
				spannableText.setSpan(deltaSpan, spannableText.length()-diff.text.length(), spannableText.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
				(insertNotDelete?insertSpans:deleteSpans).add(deltaSpan);
			}
			
		}
		
	}
	
	private CharacterStyle deltaSpan(float proportion) {
		return new DeltaSpan(true,proportion);
	}

}
