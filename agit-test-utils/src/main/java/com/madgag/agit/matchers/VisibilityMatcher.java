package com.madgag.agit.matchers;

import android.view.View;
import android.widget.Checkable;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class VisibilityMatcher extends TypeSafeMatcher<View> {

    private final int visibility;

    public VisibilityMatcher(int visibility) {
        this.visibility = visibility;
    }

    protected boolean matchesSafely(View view) {
        return view.getVisibility()==visibility;
    }

    public void describeTo(Description description) {
        String text;
        switch (visibility) {
            case VISIBLE:
                text="visible";
                break;
            case GONE:
                text="gone";
                break;
            case INVISIBLE:
                text="invisible";
                break;
            default:
                throw new RuntimeException("Unknown visibility value : "+visibility);
        }
        description.appendText(text);
    }
    
	public static Matcher<View> visible() {
		return new VisibilityMatcher(VISIBLE);
	}

    public static Matcher<View> gone() {
		return new VisibilityMatcher(GONE);
	}
}
