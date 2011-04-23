package com.madgag.agit;

import android.content.Intent;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import com.google.inject.Inject;
import org.hamcrest.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.R.id.*;
import static com.madgag.agit.matchers.CharSequenceMatcher.charSequence;
import static com.xtremelabs.robolectric.matchers.TextViewHasTextMatcher.hasText;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(InjectedTestRunner.class)
public class CloneLauncherActivityRobolectricTest {

    @Inject CloneLauncherActivity activity;

	final String appleProjectSourceUri="/example/apple";
	final String targetDir="/sdcard/tango";


    Checkable bareRepoCheckbox, defaultLocationCheckBox;
    TextView directoryEditText;
    GitIntentBuilder clone;

    @Before
    public void setUp() {
		activity.onCreate(null);
        clone = new GitIntentBuilder("");
        bareRepoCheckbox = checkable(R.id.BareRepo);
        defaultLocationCheckBox = checkable(UseDefaultGitDirLocation);
        directoryEditText = textView(GitDirEditText);
    }

	@Test
	public void shouldUseSpecifiedRepoUrlFromIntentIfSupplied() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).toIntent());

        assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
        assertThat(defaultLocationCheckBox, isChecked(true));
	}

    @Test
    public void shouldUseSpecifiedRepoDirLocationFromIntentIfSupplied() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).targetDir(targetDir).toIntent());

		assertThat(textView(GitDirEditText), hasText(targetDir));
		assertThat(defaultLocationCheckBox, isChecked(false));
		assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
	}

    @Test
    public void shouldShowHelpfulMessageIfSourceUriTextBoxIsBlank() {
        startAndResumeActivityWith(clone.toIntent());

        assertThat(textOfView(CloneReadinessMessage).toString(), containsString("Enter a url"));
	}

    @Test
    public void shouldUpdateCheckoutFolderNameToReflectBareRepo() {
        startActivityWith(clone.sourceUri(appleProjectSourceUri).toIntent());

        bareRepoCheckbox.setChecked(true);
        assertThat(textOfView(GitDirEditText), charSequence(endsWith(".git")));
        
        bareRepoCheckbox.setChecked(false);
        assertThat(textOfView(GitDirEditText), not(charSequence(endsWith(".git"))));
    }

    private void startAndResumeActivityWith(Intent intent) {
        startActivityWith(intent);
        activity.onResume();
    }

    private void startActivityWith(Intent intent) {
        activity.setIntent(intent);
        activity.onStart();
    }

    private Checkable checkable(int checkableId) {
        return (Checkable) view(checkableId);
    }

    private CharSequence textOfView(int textViewId) {
        return textView(textViewId).getText();
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