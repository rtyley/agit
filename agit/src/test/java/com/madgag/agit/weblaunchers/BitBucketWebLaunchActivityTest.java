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
