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
import android.graphics.Canvas;
import android.text.Editable;
import android.util.Log;
import android.widget.TextView;

public class HunkDiffView extends TextView {

    private static final String TAG = "HDV";

    private final DiffText diffText;
    private final DiffStateProvider diffStateProvider;
    private float state;

    public HunkDiffView(Context context, Hunk hunk, DiffStateProvider diffStateProvider) {
        super(context);
        this.diffStateProvider = diffStateProvider;
        setTypeface(MONOSPACE);
        diffText = new DiffText(getText());
        float requiredDiffState = diffStateProvider.getDiffState();
        diffText.initWith(hunk.diffs(), requiredDiffState);
        state = requiredDiffState;
    }

    public void onDraw(Canvas c) {
        Log.d(TAG, "asked to draw hunk");
        updateDiffTextStateIfRequired();
        super.onDraw(c);
    }

    private void updateDiffTextStateIfRequired() {
        float requiredDiffState = diffStateProvider.getDiffState();
        if (state != requiredDiffState) {
            updateDiffTextStateTo(requiredDiffState);
        }
    }

    private void updateDiffTextStateTo(float requiredDiffState) {
        diffText.setTransitionProgress(requiredDiffState);
        Log.d(TAG, "updated text state from " + state + " to " + requiredDiffState);
        state = requiredDiffState;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, EDITABLE);
    }

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }


}