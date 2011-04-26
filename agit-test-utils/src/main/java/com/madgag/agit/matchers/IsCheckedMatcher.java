package com.madgag.agit.matchers;

import android.widget.Checkable;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsCheckedMatcher extends TypeSafeMatcher<Checkable> {

    private final boolean checked;

    public IsCheckedMatcher(boolean checked) {

        this.checked = checked;
    }

    protected boolean matchesSafely(Checkable checkable) {
        return checkable.isChecked()==checked;
    }

    public void describeTo(Description description) {
        description.appendText(checked?"checked":"unchecked");
    }
    
	public static Matcher<Checkable> checked() {
		return new IsCheckedMatcher(true);
	}

    public static Matcher<Checkable> unchecked() {
		return new IsCheckedMatcher(false);
	}
}
