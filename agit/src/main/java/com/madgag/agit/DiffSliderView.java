package com.madgag.agit;

import static android.content.Context.VIBRATOR_SERVICE;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class DiffSliderView extends RelativeLayout {

	public DiffSliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.diff_seekbar_view, this);
		
		SeekBar seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
		DiffSeekBarChangeListener foo = new DiffSeekBarChangeListener((Vibrator) context.getSystemService(VIBRATOR_SERVICE));
		seekBar.setOnSeekBarChangeListener(foo);
		seekBar.setProgress(seekBar.getMax());
	}	
}
