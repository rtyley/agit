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

import android.content.Context;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.create;
import static android.view.Gravity.CENTER;
import static com.madgag.agit.R.id.*;
import static com.madgag.agit.R.layout.diff_seekbar_view;

public class DiffSliderView extends LinearLayout {

    private String TAG="DSV";

    public static interface OnStateUpdateListener {
		void onStateChanged (DiffSliderView diffSliderView, float state);
	}
	
	private OnStateUpdateListener stateUpdateListener;
	private final TextView beforeTextView,afterTextView;
    private final Typeface defaultTypeface, boldTypeFace;
	public DiffSliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(CENTER);
		LayoutInflater.from(context).inflate(diff_seekbar_view, this);

        beforeTextView = (TextView) findViewById(beforeText);
        afterTextView = (TextView) findViewById(afterText);
        defaultTypeface = beforeTextView.getTypeface();
        boldTypeFace = create(defaultTypeface, BOLD);

		SeekBar seekBar = (SeekBar) findViewById(DiffPlayerSeekBar);
		DiffSeekBarChangeListener foo = new DiffSeekBarChangeListener((Vibrator) context.getSystemService(VIBRATOR_SERVICE));
		seekBar.setOnSeekBarChangeListener(foo);
		seekBar.setProgress(seekBar.getMax());
	}

	public void setStateUpdateListener(OnStateUpdateListener stateUpdateListener) {
		this.stateUpdateListener=stateUpdateListener;
	}
	

	class DiffSeekBarChangeListener implements OnSeekBarChangeListener {
		private final Vibrator vibrator;
		
		public DiffSeekBarChangeListener(Vibrator vibrator) {
			this.vibrator = vibrator;
		}
		
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO - should we animate this movement?
			seekBar.setProgress(unitProgress(seekBar)<0.5?0:seekBar.getMax());
		}

		public void onStartTrackingTouch(SeekBar seekBar) {}

		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            boolean before=progress==0, after=seekBar.getMax()==progress;
			if (before || after) {
				vibrator.vibrate(17);
			}
            // sadly makes the damn seekbar wiggle
//            beforeTextView.setTypeface(before?boldTypeFace:defaultTypeface);
//            afterTextView.setTypeface(after?boldTypeFace:defaultTypeface);
	        float unitProgress = unitProgress(seekBar);

			notifyTheOthers(unitProgress);
			
		}


		private float unitProgress(SeekBar seekBar) {
			return ((float)seekBar.getProgress())/seekBar.getMax();
		}
	}

	private void notifyTheOthers(float unitProgress) {
        Log.d(TAG, "notifyTheOthers stateUpdateListener="+stateUpdateListener);
		if (stateUpdateListener!=null) {
			stateUpdateListener.onStateChanged(this, unitProgress);
		}
	}
}
