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

import static java.lang.Math.max;
import static java.lang.Math.round;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class DeltaSpan extends MetricAffectingSpan {

	private final static int insertColour = 0x00c2ffc2,	deleteColour = 0x00FFE6E6;

	private final float magnitude;
	private final int alpha;
	private final boolean insertNotDelete;

	public DeltaSpan(boolean insertNotDelete, float progress) {
		this.insertNotDelete = insertNotDelete;
		this.magnitude = max(0.01f,(insertNotDelete ? progress : (1 - progress))); // TODO max is HACK!

		alpha = round(magnitude * 0xff);
	}

	public int describeContents() {
		return 0;
	}

	@Override
	public void updateDrawState(TextPaint textPaint) {
		textPaint.setTextSize(textPaint.getTextSize() * magnitude);
		textPaint.setAlpha(alpha);
		textPaint.bgColor = (insertNotDelete ? insertColour : deleteColour)	+ (alpha << 24);
	}

	@Override
	public void updateMeasureState(TextPaint ds) {
		ds.setTextSize(ds.getTextSize() * magnitude);
	}
}
