/**
 * 
 */
package com.madgag.agit;

import static android.widget.TextView.BufferType.EDITABLE;
import android.content.Context;
import android.text.Editable;
import android.widget.TextView;

import com.madgag.agit.LineContextDiffer.Hunk;

public class HunkDiffView extends TextView {
	
	private DiffText diffText;

	public HunkDiffView(Context context, Hunk hunk) {
		super(context);
		setHunk(hunk);
	}
	
	public void setHunk(Hunk hunk) {
		diffText = new DiffText(getText());
		diffText.initWith(hunk.before, hunk.after);
	}
	
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, EDITABLE);
    }
	
	public void setProgress(float proportion) {
		diffText.setTransitionProgress(proportion);
	}
	
    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }
    
    public DiffText getDiffText() {
		return diffText;
	}

}