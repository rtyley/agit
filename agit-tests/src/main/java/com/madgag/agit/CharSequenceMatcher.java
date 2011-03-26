package com.madgag.agit;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CharSequenceMatcher extends TypeSafeMatcher<CharSequence> {

	private final Matcher<String> stringMatcher;

	public CharSequenceMatcher(Matcher<String> stringMatcher) {
		this.stringMatcher = stringMatcher;
	}

	@Override
	public boolean matchesSafely(CharSequence cs) {
		return stringMatcher.matches(cs.toString());
	}

	public void describeTo(Description description) {
		description.appendText("charsequence with ").appendDescriptionOf(stringMatcher);
	}

	@Factory
	public static <T> Matcher<CharSequence> charSequence(Matcher<String> textMatcher) {
		return new CharSequenceMatcher(textMatcher);
	}

}
