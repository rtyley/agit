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
import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

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

	private SeekBar seekBar;
	private TextView textView;

	private DiffText diffText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.diff_player_view);
		textView = (TextView) findViewById(R.id.DiffPlayerTextView);
		Editable spannableText=(Editable) textView.getText();
		diffText = new DiffText(spannableText);

		diffText.initWith("ALPHA FISH HAPPY but slightly slapdash.\nFrosting\nFronghello\nFros ting\nGolly\nMoo\nBoo\nGandalf said hi",
				"ALPHA GOOGLE HAPPY and slapping the side of the boat.\nFrosting\nFrosting\nFrosting\nMoo\nGandalf says hi");
		
		seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
		seekBar.setMax(1000);
		DiffSeekBarChangeListener foo = new DiffSeekBarChangeListener((Vibrator) getSystemService(VIBRATOR_SERVICE));
		foo.add(diffText);
		seekBar.setOnSeekBarChangeListener(foo);
		seekBar.setProgress(1000);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "Starting up!");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume called");
	}

}