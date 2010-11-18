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
import android.widget.SeekBar.OnSeekBarChangeListener;

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

	private Editable spannableText;
	private Vibrator vibrator;

	private CharacterStyle deltaSpanStyle,fadeSpanStyle;

	private List<CharacterStyle> insertSpans,deleteSpans;

	private DeltaSpan insertSpan;

	private DeltaSpan deleteSpan;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
		setContentView(R.layout.diff_player_view);
		textView = (TextView) findViewById(R.id.DiffPlayerTextView);
		bonk("ALPHA FISH HAPPY but slightly slapdash.\nFrosting\nFrotinghello\n\nGolly\nMoo\nBoo",
				"ALPHA GOOGLE HAPPY and slapping the side of the boat.\nFrosting\nFrosting\nMoo");
		textView.setText(spannableText);
		spannableText=(Editable) textView.getText();
		
		seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
		seekBar.setMax(1000);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO - should we animate this movement?
				seekBar.setProgress(unitProgress(seekBar)<0.5?0:seekBar.getMax());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress==0 || seekBar.getMax()==progress) {
					vibrator.vibrate(18);
				}
				
				float proportion = unitProgress(seekBar);
				updateDisplayWith(proportion);
			}

			private float unitProgress(SeekBar seekBar) {
				return ((float)seekBar.getProgress())/seekBar.getMax();
			}

		});
	}
	
	

	private void updateDisplayWith(float proportion) {
		insertSpan = new DeltaSpan(true, proportion);
		deleteSpan = new DeltaSpan(false, proportion);
		replace(insertSpans, insertSpan);
		replace(deleteSpans, deleteSpan);
	}

	private void replace(List<CharacterStyle> deltaSpans, CharacterStyle spanStyle) {
		for (int i=0;i<deltaSpans.size() ; ++i) {
			CharacterStyle oldSpanStyle = deltaSpans.get(i);
			int start=spannableText.getSpanStart(oldSpanStyle ),end=spannableText.getSpanEnd(oldSpanStyle);
			spannableText.removeSpan(oldSpanStyle);
			CharacterStyle mySpanStyle = CharacterStyle.wrap(spanStyle);
			spannableText.setSpan(mySpanStyle, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
			deltaSpans.set(i, mySpanStyle);
		}
	}
	
	private void bonk(String before,String after) {
		diff_match_patch differ = new diff_match_patch(new StandardBreakScorer());
		LinkedList<Diff> diffs = differ.diff_main(before, after);
		differ.diff_cleanupSemantic(diffs);
		spannableText=new SpannableStringBuilder();
		insertSpan = new DeltaSpan(true, 0.5f);
		deleteSpan = new DeltaSpan(false, 0.5f);
		insertSpans = newArrayList();
		deleteSpans = newArrayList();
		for (Diff diff : diffs) {
			spannableText.append(diff.text);
			if (diff.operation!=Operation.EQUAL) {
				boolean insertNotDelete = diff.operation==INSERT;
				CharacterStyle deltaSpan = CharacterStyle.wrap(insertNotDelete?insertSpan:deleteSpan);
				spannableText.setSpan(deltaSpan, spannableText.length()-diff.text.length(), spannableText.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
				(insertNotDelete?insertSpans:deleteSpans).add(deltaSpan);
			}
			
		}
		
	}
	
	private CharacterStyle deltaSpan(float proportion) {
		return new DeltaSpan(true,proportion);
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