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

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class VisibilityMatcher extends TypeSafeMatcher<View> {

    private final int visibility;

    public VisibilityMatcher(int visibility) {
        this.visibility = visibility;
    }

    protected boolean matchesSafely(View view) {
        return view.getVisibility() == visibility;
    }

    public void describeTo(Description description) {
        String text;
        switch (visibility) {
            case VISIBLE:
                text = "visible";
                break;
            case GONE:
                text = "gone";
                break;
            case INVISIBLE:
                text = "invisible";
                break;
            default:
                throw new RuntimeException("Unknown visibility value : " + visibility);
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
