package com.madgag.agit;

import static com.madgag.agit.Clone.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.github.calculon.CalculonUnitTest;
import com.github.calculon.predicate.Predicate;

public class CloneActivityUnitTest extends CalculonUnitTest<Clone> {
	
	final String appleProjectSourceUri="/example/apple";
	final String targetDir="/sdcard/tango";
	
	public CloneActivityUnitTest() {
		super(Clone.class);
	}
	
	public void testUsesDefaultGitDirLocationIfOnlySourceUriIsProvidedInIntent() {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SOURCE_URI, appleProjectSourceUri);
		
		startActivity(bundle);
		getInstrumentation().callActivityOnStart(getActivity());
		
		assertThat(R.id.CloneUrlEditText).satisfies(hasText(appleProjectSourceUri));
		assertThat(R.id.UseDefaultGitDirLocation).satisfies(isChecked(true));
	}
	
	public void testUsesSpecifiedRepoDirLocationFromIntentIfSupplied() {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SOURCE_URI, appleProjectSourceUri);
		bundle.putString(EXTRA_TARGET_DIR, targetDir);
		
		startActivity(bundle);
		getInstrumentation().callActivityOnStart(getActivity());
		
		assertThat(R.id.GitDirEditText).satisfies(hasText(targetDir));
		assertThat(R.id.UseDefaultGitDirLocation).satisfies(isChecked(false));
		assertThat(R.id.CloneUrlEditText).satisfies(hasText(appleProjectSourceUri));
	}

	
	private Predicate<View> isChecked(final boolean checked) {
		return new Predicate<View>() {
			public boolean check(View target) {
				return ((Checkable) target).isChecked()==checked;
			}
		};
	}
	
	private static Predicate<View> hasText(final String text) {
		return new Predicate<View>() {
			public boolean check(View target) {
				return ((TextView) target).getText().toString().equals(text);
			}
		};
	}
}
