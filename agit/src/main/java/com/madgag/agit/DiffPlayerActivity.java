package com.madgag.agit;

import android.app.Activity;
import android.os.Bundle;

/*
 *  When a user taps at the opposite end, the change in display from BEFORE to AFTER should not be instantaneous - it should be 
 *  rapidly animated.
 *  
 *  However, when the user is holding down the 'thumb', the display should update instantly (?) to reflect the exact value they are pointing to.
 *  Regarding 'instantly' - depending on the frame rate we can achieve, it might be better to show a very rapid, smooth animation to the correct point
 *  
 */
public class DiffPlayerActivity extends Activity {
	private final static String TAG = "DiffPlayerActivity";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setContentView(R.layout.diff_player_view);
//		TextView textView = (TextView) findViewById(R.id.DiffPlayerTextView);
//		Editable spannableText=(Editable) textView.getText();
//		DiffText diffText = new DiffText(spannableText);
//
//		diffText.initWith("ALPHA FISH HAPPY but slightly slapdash.\nFrosting\nFronghello\nFros ting\nGolly\nMoo\nBoo\nGandalf said hi",
//				"ALPHA GOOGLE HAPPY and slapping the side of the boat.\nFrosting\nFrosting\nFrosting\nMoo\nGandalf says hi");
//		
//		SeekBar seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
//		seekBar.setMax(1000);
//		DiffSeekBarChangeListener foo = new DiffSeekBarChangeListener((Vibrator) getSystemService(VIBRATOR_SERVICE));
//		foo.add(diffText);
//		seekBar.setOnSeekBarChangeListener(foo);
//		seekBar.setProgress(1000);
	}

}