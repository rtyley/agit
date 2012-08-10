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

import static android.net.Uri.parse;
import static com.madgag.agit.GitIntents.sourceUriFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import android.content.Intent;

import com.google.inject.Inject;
import com.madgag.agit.InjectedTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(InjectedTestRunner.class)
public class GitHubWebLaunchActivityTest {

    @Inject
    GitHubWebLaunchActivity activity;

    @Test
    public void shouldSupplyCloneSourceForRegularGithubProjectPage() {
        Intent cloneIntent = activity.cloneLauncherForWebBrowseIntent(parse("https://github.com/JodaOrg/joda-time"));
        assertThat(sourceUriFrom(cloneIntent), is("git://github.com/JodaOrg/joda-time.git"));
    }

    @Test
    public void shouldSupplyOnlyUserepoOwnerAndNameForCloneUrl() {
        Intent cloneIntent = activity.cloneLauncherForWebBrowseIntent(parse("https://github" +
                ".com/eddieringle/hubroid/issues/66"));
        assertThat(sourceUriFrom(cloneIntent), is("git://github.com/eddieringle/hubroid.git"));
    }
}
