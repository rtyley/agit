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

/**
 * 
 */
package com.madgag.agit.diff;

import static android.graphics.Typeface.MONOSPACE;
import static android.widget.TextView.BufferType.EDITABLE;
import android.content.Context;
import android.text.Editable;
import android.widget.TextView;

public class HunkDiffView extends TextView {
	
	private DiffText diffText;

	public HunkDiffView(Context context, Hunk hunk) {
		super(context);
		setTypeface(MONOSPACE);
		setHunk(hunk);
	}
	
	public void setHunk(Hunk hunk) {
		diffText = new DiffText(getText());
		diffText.initWith(hunk.diffs());
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