package com.madgag.agit;

import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;

import com.github.calculon.CalculonUnitTest;
import com.github.calculon.predicate.Predicate;

public class CloneActivityUnitTest extends CalculonUnitTest<Clone> {
	
	public CloneActivityUnitTest() {
		super(Clone.class);
	}
	
	public void testUsesDefaultGitDirLocationIfOnlySourceUriIsProvidedInIntent() {
		Bundle bundle = new Bundle();
		final String cloneSourceUri="/example/apple";
		bundle.putString("source-uri", cloneSourceUri);
		
		startActivity(bundle);
		getInstrumentation().callActivityOnStart(getActivity());
		
		assertThat(R.id.CloneUrlEditText).satisfies(new Predicate<View>() {
			public boolean check(View target) {
				return ((EditText) target).getText().toString().equals(cloneSourceUri);
			}
		});
		
		assertThat(R.id.UseDefaultGitDirLocation).satisfies(new Predicate<View>() {
			public boolean check(View target) {
				return ((Checkable) target).isChecked()==true;
			}
		});
	}
	
	public void testUsesSpecifiedGitDirLocationFromIntentIfSupplied() {
		Bundle bundle = new Bundle();
		final String cloneSourceUri="/example/apple";
		final String gitdir="/sdcard/tango";
		bundle.putString("source-uri", cloneSourceUri);
		bundle.putString("gitdir", gitdir);
		
		startActivity(bundle);
		getInstrumentation().callActivityOnStart(getActivity());
		
		assertThat(R.id.CloneUrlEditText).satisfies(new Predicate<View>() {
			public boolean check(View target) {
				return ((EditText) target).getText().toString().equals(cloneSourceUri);
			}
		});
		
		assertThat(R.id.UseDefaultGitDirLocation).satisfies(new Predicate<View>() {
			public boolean check(View target) {
				return ((Checkable) target).isChecked()==false;
			}
		});
		
		assertThat(R.id.GitDirEditText).satisfies(new Predicate<View>() {
			public boolean check(View target) {
				return ((EditText) target).getText().toString().equals(gitdir);
			}
		});
	}
}
