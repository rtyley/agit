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
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.common.collect.Lists;

/*
 *  When a user taps at the opposite end, the change in display from BEFORE to AFTER should not be instantaneous - it should be animated.
 *  
 *  However, when the user is holding down the 'thumb', the display should update instantly to reflect the exact value they are pointing to.
 */
public class DiffPlayerActivity extends Activity {
	private final static String TAG = "DiffPlayerActivity";

	private SeekBar seekBar;
	private TextView textView;
	private DiffPanel diffPanel;

	private SpannableStringBuilder spannableText;
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
		diffPanel = (DiffPanel) findViewById(R.id.DiffPlayerDiffPanel);
		textView = (TextView) findViewById(R.id.DiffPlayerTextView);
		bonk("ALPHA FISH HAPPY but slightly slapdash.\nFrosting\nFroting","ALPHA GOOGLE HAPPY and slapping the side of the boat.\nFrosting\nFrosting");
		textView.setText(spannableText);
		
		seekBar = (SeekBar) findViewById(R.id.DiffPlayerSeekBar);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO - should we animate this movement?
				seekBar.setProgress(unitProgress(seekBar)<0.5?0:seekBar.getMax());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (progress==0 || seekBar.getMax()==progress) {
					vibrator.vibrate(15);
				}
				
				float proportion = unitProgress(seekBar);
				Log.i("RAH", "proportion="+proportion);
				insertSpan = new DeltaSpan(true, proportion);
				deleteSpan = new DeltaSpan(false, proportion);
				replace(insertSpans, insertSpan);
				replace(deleteSpans, deleteSpan);

				diffPanel.setProgress(seekBar.getProgress());
				diffPanel.invalidate();
				
			}

			private void replace(List<CharacterStyle> deltaSpans, CharacterStyle spanStyle) {
				Log.i("RAH", "deltaSpans.size()="+deltaSpans.size());
				for (int i=0;i<deltaSpans.size() ; ++i) {
					CharacterStyle oldSpanStyle = deltaSpans.get(i);
					int start=spannableText.getSpanStart(oldSpanStyle ),end=spannableText.getSpanEnd(oldSpanStyle);
					Log.i("RAH", "oldSpanStyle "+start+"-"+end);
					spannableText.removeSpan(oldSpanStyle);
					CharacterStyle mySpanStyle = CharacterStyle.wrap(spanStyle);
					spannableText.setSpan(mySpanStyle, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
					deltaSpans.set(i, mySpanStyle);
				}
			}

			private float unitProgress(SeekBar seekBar) {
				return ((float)seekBar.getProgress())/seekBar.getMax();
			}

		});
	}
	
	private void bonk(String before,String after) {
		diff_match_patch differ = new diff_match_patch(new StandardBreakScorer());
		LinkedList<Diff> diffs = differ.diff_main(before, after);
		differ.diff_cleanupSemantic(diffs);
		spannableText=new SpannableStringBuilder();
		insertSpan = new DeltaSpan(true, 0);
		deleteSpan = new DeltaSpan(false, 0);
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