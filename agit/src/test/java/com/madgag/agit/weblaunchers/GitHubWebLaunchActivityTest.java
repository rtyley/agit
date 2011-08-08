package com.madgag.agit.weblaunchers;

import android.content.Intent;
import com.google.inject.Inject;
import com.madgag.agit.InjectedTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.net.Uri.parse;
import static com.madgag.agit.GitIntents.sourceUriFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(InjectedTestRunner.class)
public class GitHubWebLaunchActivityTest {

    @Inject GitHubWebLaunchActivity activity;

    @Test
    public void shouldSupplyCloneSourceForRegularGithubProjectPage() {
        Intent cloneIntent = activity.cloneLauncherForWebBrowseIntent(parse("https://github.com/JodaOrg/joda-time"));
        assertThat(sourceUriFrom(cloneIntent), is("git://github.com/JodaOrg/joda-time.git"));
    }

    @Test
    public void shouldSupplyOnlyUserepoOwnerAndNameForCloneUrl() {
        Intent cloneIntent = activity.cloneLauncherForWebBrowseIntent(parse("https://github.com/eddieringle/hubroid/issues/66"));
        assertThat(sourceUriFrom(cloneIntent), is("git://github.com/eddieringle/hubroid.git"));
    }
}
