package com.madgag.agit.weblaunchers;

import android.net.Uri;
import com.google.inject.Inject;
import com.madgag.agit.InjectedTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.GitIntents.sourceUriFrom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(InjectedTestRunner.class)
public class GoogleCodeWebLaunchActivityTest {

    @Inject GoogleCodeWebLaunchActivity activity;

    @Test
    public void shouldSupplyCloneSourceFromGoogleCodeProjectPage() {
        String projectUrl = "https://code.google.com/p/test-for-agit/";
        assertThat(sourceUriDerivedFrom(projectUrl), is(projectUrl));
    }

    @Test
    public void shouldSupplyCloneSourceFromGoogleCodeProjectCheckoutPage() {
        String sourceUri = sourceUriDerivedFrom("https://code.google.com/p/test-for-agit/source/checkout");
        assertThat(sourceUri, is("https://code.google.com/p/test-for-agit/"));
    }

    private String sourceUriDerivedFrom(String url) {
        return sourceUriFrom(activity.cloneLauncherForWebBrowseIntent(Uri.parse(url)));
    }

}
