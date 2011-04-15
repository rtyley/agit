/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import static com.madgag.agit.GitIntents.EXTRA_SOURCE_URI;
import static com.madgag.agit.GitIntents.EXTRA_TARGET_DIR;
import static com.madgag.agit.R.id.CloneUrlEditText;
import static com.madgag.agit.R.id.GitDirEditText;
import static com.madgag.agit.R.id.UseDefaultGitDirLocation;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.MatcherAssert.assertThat;

import android.os.Bundle;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.github.calculon.CalculonUnitTest;
import com.github.calculon.predicate.Predicate;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import roboguice.test.RoboActivityUnitTestCase;

public class CloneActivityUnitTest extends RoboActivityUnitTestCase<CloneLauncherActivity> {
	
	final String appleProjectSourceUri="/example/apple";
	final String targetDir="/sdcard/tango";
	
	public CloneActivityUnitTest() {
		super(CloneLauncherActivity.class);
	}
	
	public void testUsesDefaultGitDirLocationIfOnlySourceUriIsProvidedInIntent() {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SOURCE_URI, appleProjectSourceUri);
		startActivity(new GitIntentBuilder("").add(EXTRA_SOURCE_URI, appleProjectSourceUri).toIntent(), null, null);
		getInstrumentation().callActivityOnStart(getActivity());

        assertThat(textView(CloneUrlEditText), hasText(appleProjectSourceUri));
        assertThat(checkable(UseDefaultGitDirLocation), isChecked(true));
	}

    private Checkable checkable(int checkableId) {
        return (Checkable) view(checkableId);
    }

    private TextView textView(int textViewId) {
        return (TextView) view(textViewId);
    }

    private View view(int viewId) {
        return getActivity().findViewById(viewId);
    }

    public void testUsesSpecifiedRepoDirLocationFromIntentIfSupplied() {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SOURCE_URI, appleProjectSourceUri);
		bundle.putString(EXTRA_TARGET_DIR, targetDir);
		
		startActivity(new GitIntentBuilder("").add(EXTRA_SOURCE_URI, appleProjectSourceUri)
                .add(EXTRA_TARGET_DIR, targetDir)
                .toIntent(), null, null);
		getInstrumentation().callActivityOnStart(getActivity());
		
		assertThat(textView(GitDirEditText),hasText(targetDir));
		assertThat(checkable(UseDefaultGitDirLocation),isChecked(false));
		assertThat(textView(CloneUrlEditText),hasText(appleProjectSourceUri));
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
