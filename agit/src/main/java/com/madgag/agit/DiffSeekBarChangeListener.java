package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import android.os.Vibrator;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public final class DiffSeekBarChangeListener implements	OnSeekBarChangeListener {
	private final Vibrator vibrator;
	private final List<DiffText> diffViews=newArrayList();

	public DiffSeekBarChangeListener(Vibrator vibrator) {
		this.vibrator = vibrator;
	}

	public void add(DiffText diffText) {
		diffViews.add(diffText);
	}
	
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO - should we animate this movement?
		seekBar.setProgress(unitProgress(seekBar)<0.5?0:seekBar.getMax());
	}

	public void onStartTrackingTouch(SeekBar seekBar) {}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (progress==0 || seekBar.getMax()==progress) {
			vibrator.vibrate(17);
		}
		
		float unitProgress = unitProgress(seekBar);
		for (DiffText diffView : diffViews) {
			diffView.setTransitionProgress(unitProgress);
		}
	}

	private float unitProgress(SeekBar seekBar) {
		return ((float)seekBar.getProgress())/seekBar.getMax();
	}
}