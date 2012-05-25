package com.madgag.agit;

import static com.madgag.agit.OracleJVMTestEnvironment.helper;
import static com.madgag.agit.TagViewer.tagViewerIntentFor;

import com.actionbarsherlock.ActionBarSherlock;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(InjectedTestRunner.class)
public class TagViewerRobolectricTest {
    private static File gitdirForSmallRepo;

    @BeforeClass
    public static void setUp() throws Exception {
        gitdirForSmallRepo = helper().unpackRepoAndGetGitDir("small-repo.with-tags.zip");
    }


    @Test
    public void shouldShowTagMessageAndIdentOnAnnotatedTag() throws Exception {
        TagViewer activity = new TagViewer();
        activity.setIntent(tagViewerIntentFor(gitdirForSmallRepo, "annotated-tag-of-2nd-commit"));
        activity.onCreate(null);
        activity.onContentChanged();
//        assertThat(activity.objectSummaryView.getObject()..getEmailAddress(), is("roberto.tyley@guardian.co.uk"));
//        assertThat(activity.tagMessage, visible());
//        assertThat(activity.tagMessage.getText().toString(), is("I even like the 2nd commit, I am tagging it\n"));
    }

    @Test
    public void shouldNotShowTagMessageOrIdentOnLightwieghtTag() throws Exception {
        TagViewer activity = new TagViewer();
        activity.setIntent(tagViewerIntentFor(gitdirForSmallRepo, "lightweight-tag-of-1st-commit"));
        activity.onCreate(null);
        activity.onContentChanged();
//        assertThat(activity.taggerIdentView, gone());
//        assertThat(activity.tagMessage, gone());
        // assertThat(activity.objectSummaryView.getObject().getId(),
        // is(ObjectId.fromString("ba1f63e4430bff267d112b1e8afc1d6294db0ccc")));
    }
}
