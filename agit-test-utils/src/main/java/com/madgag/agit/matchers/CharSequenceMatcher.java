/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.matchers;

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
