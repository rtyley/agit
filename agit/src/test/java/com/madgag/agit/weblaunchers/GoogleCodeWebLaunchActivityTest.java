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
