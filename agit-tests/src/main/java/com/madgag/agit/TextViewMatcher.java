package com.madgag.agit;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import android.widget.TextView;

public class TextViewMatcher extends TypeSafeMatcher<TextView> {

	private final Matcher<String> textMatcher;

	public TextViewMatcher(Matcher<String> textMatcher) {
		this.textMatcher = textMatcher;
	}

	@Override
	public boolean matchesSafely(TextView textView) {
		return textMatcher.matches(textView.getText().toString());
	}

	public void describeTo(Description description) {
		description.appendText("not a number");
	}

	@Factory
	public static <T> Matcher<TextView> textView(Matcher<String> textMatcher) {
		return new TextViewMatcher(textMatcher);
	}

}
