package com.madgag.agit;

import com.google.inject.Inject;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.madgag.agit.GitTestUtils.unpackRepo;
import static com.madgag.agit.TagViewer.tagViewerIntentFor;
import static com.madgag.agit.matchers.VisibilityMatcher.gone;
import static com.madgag.agit.matchers.VisibilityMatcher.visible;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(InjectedTestRunner.class)
public class TagViewerRobolectricTest {
    private static Repository smallRepoWithTags;

    @BeforeClass
    public static void setUp() throws Exception {
        smallRepoWithTags = unpackRepo("small-repo.with-tags.zip");
    }

    @Test
	public void shouldShowTagMessageAndIdentOnAnnotatedTag() throws Exception {
        TagViewer activity = new TagViewer();
        activity.setIntent(tagViewerIntentFor(smallRepoWithTags, "annotated-tag-of-2nd-commit"));
        activity.onCreate(null);
        activity.onContentChanged();
//        assertThat(activity.taggerIdentView.getIdent().getEmailAddress(), is("roberto.tyley@guardian.co.uk"));
//        assertThat(activity.tagMessage, visible());
//        assertThat(activity.tagMessage.getText().toString(), is("I even like the 2nd commit, I am tagging it\n"));
	}

    @Test
	public void shouldNotShowTagMessageOrIdentOnLightwieghtTag() throws Exception {
        TagViewer activity = new TagViewer();
        activity.setIntent(tagViewerIntentFor(smallRepoWithTags, "lightweight-tag-of-1st-commit"));
        activity.onCreate(null);
        activity.onContentChanged();
//        assertThat(activity.taggerIdentView, gone());
//        assertThat(activity.tagMessage, gone());
        // assertThat(activity.objectSummaryView.getObject().getId(), is(ObjectId.fromString("ba1f63e4430bff267d112b1e8afc1d6294db0ccc")));
	}
}
