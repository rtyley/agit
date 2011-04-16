package com.madgag.agit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.TextView;
import com.google.inject.Inject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.GitIntents.EXTRA_SOURCE_URI;
import static com.madgag.agit.GitIntents.EXTRA_TARGET_DIR;
import static com.madgag.agit.R.id.CloneUrlEditText;
import static com.madgag.agit.R.id.GitDirEditText;
import static com.madgag.agit.R.id.UseDefaultGitDirLocation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
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

    private Checkable checkable(int checkableId) {
        return (Checkable) view(checkableId);
    }

    private TextView textView(int textViewId) {
        return (TextView) view(textViewId);
    }

    private View view(int viewId) {
        return activity.findViewById(viewId);
    }

    public void testUsesSpecifiedRepoDirLocationFromIntentIfSupplied() {
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

	private static Matcher<TextView> hasText(final String text) {
		return new TypeSafeMatcher<TextView>() {
            @Override
            protected boolean matchesSafely(TextView textView) {
                return textView.getText().equals(text);
            }

            public void describeTo(Description description) {
                description.appendText("has text ").appendValue(text);
            }
        };
	}
}
