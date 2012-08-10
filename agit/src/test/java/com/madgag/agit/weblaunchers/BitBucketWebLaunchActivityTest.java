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
public class BitBucketWebLaunchActivityTest {

    @Inject
    BitBucketWebLaunchActivity activity;

    @Test
    public void shouldSupplyCloneSourceFromBitBucketProjectPage() {
        String projectUrl = "https://bitbucket.org/grubix/git";
        assertThat(sourceUriDerivedFrom(projectUrl), is("https://bitbucket.org/grubix/git.git"));
    }

    @Test
    public void shouldSupplyCloneSourceFromBitBucketCheckoutPage() {
        String sourceUri = sourceUriDerivedFrom("https://bitbucket.org/grubix/git/src");
        assertThat(sourceUri, is("https://bitbucket.org/grubix/git.git"));
    }

    private String sourceUriDerivedFrom(String url) {
        return sourceUriFrom(activity.cloneLauncherForWebBrowseIntent(Uri.parse(url)));
    }

}
