package com.madgag.agit;

import com.google.common.base.Predicate;
import com.madgag.agit.git.model.RDTTag.TagSummary;
import com.madgag.agit.git.model.RDTTag;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Iterables.find;
import static com.madgag.agit.GitTestUtils.unpackRepo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RDTTagTest {

    @Test
	public void shouldHandleTheDatelessAnnotatedTagsThatGitUsedToHave() throws Exception {
        Repository repository = unpackRepo("git-repo-has-dateless-tag.depth2.zip");
        RDTTag rdtTag = new RDTTag(repository);
		List<TagSummary> listOfTagsInRepo = rdtTag.getAll();
		assertThat(listOfTagsInRepo, hasSize(1));
		TagSummary tagSummary = listOfTagsInRepo.get(0);

        assertThat(tagSummary.getTime(), equalTo(1121037394L));
        // RevTag tag = new RevWalk(repository).parseTag(ObjectId.fromString("d6602ec5194c87b0fc87103ca4d67251c76f233a"));
	}

	@Test
	public void shouldNotThrowNPEDueToUnparsedObjectDataEspeciallyForRepoWithJustOneAnnotatedTag() throws Exception {
		RDTTag rdtTag = new RDTTag(unpackRepo("repo-with-just-an-annotated-tag-of-a-commit.zip"));
		List<TagSummary> listOfTagsInRepo = rdtTag.getAll();
		assertThat(listOfTagsInRepo, hasSize(1));
		TagSummary loneTag = listOfTagsInRepo.get(0);
		assertThat(rdtTag.shortDescriptionOf(loneTag).toString(), notNullValue());
	}


	@Test
	public void shouldHaveTaggedObjectFieldCorrectlySetForAnAnnotatedTag() throws Exception {
		RDTTag rdtTag = new RDTTag(unpackRepo("small-repo.with-tags.zip"));
		List<TagSummary> tags = rdtTag.getAll();

		TagSummary tag = find(tags, tagNamed("annotated-tag-of-2nd-commit"));
		assertThat(tag.getTaggedObject(), instanceOf(RevCommit.class));
	}

	@Test
	public void shouldDescribeThingsProperly() throws Exception {
		RDTTag rdtTag = new RDTTag(unpackRepo("small-repo.with-tags.zip"));
		List<TagSummary> tags = rdtTag.getAll();
		
		TagSummary tag = find(tags, tagNamed("annotated-tag-of-2nd-commit"));
		assertThat(rdtTag.shortDescriptionOf(tag).toString(), startsWith("Commit"));
	}

	private Predicate<TagSummary> tagNamed(final String tagName) {
		return new Predicate<TagSummary>() {
			public boolean apply(TagSummary tag) { return tag.getName().equals(tagName); }
		};
	}

}
