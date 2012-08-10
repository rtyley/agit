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

package com.madgag.agit.weblaunchers;

import static com.madgag.agit.GitIntents.sourceUriFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import android.net.Uri;

import com.google.inject.Inject;
import com.madgag.agit.InjectedTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectedTestRunner.class)
public class GoogleCodeWebLaunchActivityTest {

    @Inject
    GoogleCodeWebLaunchActivity activity;

    @Test
    public void shouldSupplyCloneSourceFromGoogleCodeProjectPage() {
        String projectUrl = "https://code.google.com/p/test-for-agit/";
        assertThat(sourceUriDerivedFrom(projectUrl), is(projectUrl));
    }

    @Test
    public void shouldSupplyCloneSourceFromGoogleCodeCheckoutPage() {
        String sourceUri = sourceUriDerivedFrom("https://code.google.com/p/test-for-agit/source/checkout");
        assertThat(sourceUri, is("https://code.google.com/p/test-for-agit/"));
    }

    @Test
    public void shouldSupplyCloneSourceFromEclipseLabsCheckoutPage() {
        String sourceUri = sourceUriDerivedFrom("http://code.google.com/a/eclipselabs" +
                ".org/p/code-recommenders/source/checkout");
        assertThat(sourceUri, is("https://code.google.com/a/eclipselabs.org/p/code-recommenders/"));
    }

    private String sourceUriDerivedFrom(String url) {
        return sourceUriFrom(activity.cloneLauncherForWebBrowseIntent(Uri.parse(url)));
    }

}
