package com.madgag.agit;

import org.eclipse.jgit.lib.Repository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static com.madgag.agit.CommitViewerActivity.commitViewerIntentCreatorFor;
import static com.madgag.agit.CommitViewerActivity.revCommitViewIntentFor;
import static com.madgag.agit.GitTestUtils.unpackRepo;
import static com.madgag.agit.GitTestUtils.unpackRepoAndGetGitDir;
import static com.madgag.agit.TagViewer.tagViewerIntentFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(InjectedTestRunner.class)
public class CommitViewerRobolectricTest {
    private static File gitdirForSmallRepo;

    @BeforeClass
    public static void setUp() throws Exception {
        gitdirForSmallRepo = unpackRepoAndGetGitDir("small-repo.with-tags.zip");
    }

    @Test
	public void shouldHaveCorrectCommitTitle() throws Exception {
        CommitViewerActivity activity = new CommitViewerActivity();
        activity.setIntent(revCommitViewIntentFor(gitdirForSmallRepo, "0d2489a0db53c5446ab3e8a93b91a18e061b25a9"));

//        activity.onCreate(null);
//        activity.onContentChanged();
//        assertThat(activity.currentCommitView.commit.getShortMessage(), equalTo("Adding my happy folder with it's tags"));

        
//        assertThat(activity.taggerIdentView.getIdent().getEmailAddress(), is("roberto.tyley@guardian.co.uk"));
//        assertThat(activity.tagMessage, visible());
//        assertThat(activity.tagMessage.getText().toString(), is("I even like the 2nd commit, I am tagging it\n"));
	}

}
