package com.madgag.agit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.matchers.CharSequenceMatcher;
import org.hamcrest.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.GitIntents.EXTRA_SOURCE_URI;
import static com.madgag.agit.GitIntents.EXTRA_TARGET_DIR;
import static com.madgag.agit.R.id.CloneUrlEditText;
import static com.madgag.agit.R.id.GitDirEditText;
import static com.madgag.agit.R.id.UseDefaultGitDirLocation;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static com.xtremelabs.robolectric.matchers.TextViewHasTextMatcher.hasText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(InjectedTestRunner.class)
public class CloneLauncherActivityRobolectricTest {

    @Inject CloneLauncherActivity activity;

	final String appleProjectSourceUri="/example/apple";
	final String targetDir="/sdcard/tango";

	@Test
	public void shouldBeCool() {
		activity.onCreate(null);
        Intent intent = new GitIntentBuilder("").add(EXTRA_SOURCE_URI, appleProjectSourceUri).toIntent();
        activity.setIntent(intent);
        activity.onStart();

        assertThat(textView(CloneUrlEditText).getText().toString(), is(appleProjectSourceUri));
        assertThat(checkable(UseDefaultGitDirLocation), isChecked(true));
	}

    @Test
    public void shouldUseSpecifiedRepoDirLocationFromIntentIfSupplied() {
		activity.onCreate(null);
        Intent intent = new GitIntentBuilder("").add(EXTRA_SOURCE_URI, appleProjectSourceUri)
                .add(EXTRA_TARGET_DIR, targetDir)
                .toIntent();
        activity.setIntent(intent);
        activity.onStart();

		assertThat(textView(GitDirEditText), hasText(targetDir));
		assertThat(checkable(UseDefaultGitDirLocation), isChecked(false));
		assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
	}

    @Test
    public void shouldUpdateCheckoutFolderNameToReflectBareRepo() {
		activity.onCreate(null);
        Intent intent = new GitIntentBuilder("").add(EXTRA_SOURCE_URI, appleProjectSourceUri).toIntent();
        activity.setIntent(intent);
        activity.onStart();

		assertThat(checkable(UseDefaultGitDirLocation), isChecked(true));

        Checkable bareRepoCheckbox = checkable(R.id.BareRepo);
        TextView textView = textView(GitDirEditText);

        bareRepoCheckbox.setChecked(true);
        assertThat(textView.getText(), charSequence(endsWith(".git")));
        
        bareRepoCheckbox.setChecked(false);
        assertThat(textView.getText(), not(charSequence(endsWith(".git"))));
    }


    private Checkable checkable(int checkableId) {
        return (Checkable) view(checkableId);
    }

    private TextView textView(int textViewId) {
        return (TextView) view(textViewId);
    }

    private View view(int viewId) {
        return activity.findViewById(viewId);
    }

	private Matcher<Checkable> isChecked(final boolean checked) {
		return new TypeSafeMatcher<Checkable>() {
            protected boolean matchesSafely(Checkable checkable) {
                return checkable.isChecked()==checked;
            }

            public void describeTo(Description description) {
                description.appendText(checked?"checked":"unchecked");
            }
        };
	}

}