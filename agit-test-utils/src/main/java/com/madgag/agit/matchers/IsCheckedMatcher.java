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
        return checkable.isChecked() == checked;
    }

    public void describeTo(Description description) {
        description.appendText(checked ? "checked" : "unchecked");
    }

    public static Matcher<Checkable> checked() {
        return new IsCheckedMatcher(true);
    }

    public static Matcher<Checkable> unchecked() {
        return new IsCheckedMatcher(false);
    }
}
