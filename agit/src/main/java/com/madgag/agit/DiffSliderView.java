package com.madgag.agit;

import static android.content.Context.VIBRATOR_SERVICE;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DiffSliderView extends RelativeLayout {

	public static interface OnStateUpdateListener {
		void onStateChanged (DiffSliderView diffSliderView, float state);
	}
	
	private OnStateUpdateListener stateUpdateListener;
	
	public DiffSliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.diff_seekbar_view, this);
		
		SeekBar seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
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
			if (progress==0 || seekBar.getMax()==progress) {
				vibrator.vibrate(17);
			}
			
			float unitProgress = unitProgress(seekBar);

			notifyTheOthers(unitProgress);
			
		}


		private float unitProgress(SeekBar seekBar) {
			return ((float)seekBar.getProgress())/seekBar.getMax();
		}
	}

	private void notifyTheOthers(float unitProgress) {
		if (stateUpdateListener!=null) {
			stateUpdateListener.onStateChanged(this, unitProgress);
		}
	}
}
