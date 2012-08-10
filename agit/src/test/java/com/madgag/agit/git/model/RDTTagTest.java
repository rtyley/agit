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

package com.madgag.agit.git.model;

import static com.google.common.collect.Iterables.find;
import static com.madgag.agit.OracleJVMTestEnvironment.helper;
import static com.madgag.agit.git.model.RDTTag.TagSummary.SORT_BY_TIME_AND_NAME;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import com.google.common.base.Predicate;
import com.madgag.agit.git.model.RDTTag.TagSummary;

import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;

public class RDTTagTest {

    @Test
    public void shouldHaveConsistentComparatorNotBrokenByIntegerOverflow() throws Exception {
        TagSummary earlyTag = new TagSummary(new StubRef("early"), null, null, 0L);
        for (int timeShift = 0; timeShift<60;++timeShift) {
            long laterTime = 1L << timeShift;
            TagSummary lateTag = new TagSummary(new StubRef("late"), null, null, laterTime);
            assertThat("timeShift="+timeShift, SORT_BY_TIME_AND_NAME.compare(earlyTag, lateTag), lessThan(0));
        }
    }

    @Test
    public void shouldHandleTheDatelessAnnotatedTagsThatGitUsedToHave() throws Exception {
        Repository repository = helper().unpackRepo("git-repo-has-dateless-tag.depth2.zip");
        RDTTag rdtTag = new RDTTag(repository);
        List<TagSummary> listOfTagsInRepo = rdtTag.getAll();
        assertThat(listOfTagsInRepo, hasSize(1));
        TagSummary tagSummary = listOfTagsInRepo.get(0);

        assertThat(tagSummary.getTime(), equalTo(1121037394L));
        // RevTag tag = new RevWalk(repository).parseTag(ObjectId.fromString
        // ("d6602ec5194c87b0fc87103ca4d67251c76f233a"));
    }

    @Test
    public void shouldNotThrowNPEDueToUnparsedObjectDataEspeciallyForRepoWithJustOneAnnotatedTag() throws Exception {
        RDTTag rdtTag = new RDTTag(helper().unpackRepo("repo-with-just-an-annotated-tag-of-a-commit.zip"));
        List<TagSummary> listOfTagsInRepo = rdtTag.getAll();
        assertThat(listOfTagsInRepo, hasSize(1));
        TagSummary loneTag = listOfTagsInRepo.get(0);
        assertThat(rdtTag.shortDescriptionOf(loneTag).toString(), notNullValue());
    }


    @Test
    public void shouldHaveTaggedObjectFieldCorrectlySetForAnAnnotatedTag() throws Exception {
        RDTTag rdtTag = new RDTTag(helper().unpackRepo("small-repo.with-tags.zip"));
        List<TagSummary> tags = rdtTag.getAll();

        TagSummary tag = find(tags, tagNamed("annotated-tag-of-2nd-commit"));
        assertThat(tag.getTaggedObject(), instanceOf(RevCommit.class));
    }

    @Test
    public void shouldDescribeThingsProperly() throws Exception {
        RDTTag rdtTag = new RDTTag(helper().unpackRepo("small-repo.with-tags.zip"));
        List<TagSummary> tags = rdtTag.getAll();

        TagSummary tag = find(tags, tagNamed("annotated-tag-of-2nd-commit"));
        assertThat(rdtTag.shortDescriptionOf(tag).toString(), startsWith("Commit"));
    }

    private Predicate<TagSummary> tagNamed(final String tagName) {
        return new Predicate<TagSummary>() {
            public boolean apply(TagSummary tag) {
                return tag.getName().equals(tagName);
            }
        };
    }

    private class StubRef implements Ref {

        private final String name;

        public StubRef(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isSymbolic() {
            return false;
        }

        @Override
        public Ref getLeaf() {
            return null;
        }

        @Override
        public Ref getTarget() {
            return null;
        }

        @Override
        public ObjectId getObjectId() {
            return null;
        }

        @Override
        public ObjectId getPeeledObjectId() {
            return null;
        }

        @Override
        public boolean isPeeled() {
            return false;
        }

        @Override
        public Storage getStorage() {
            return null;
        }
    }
}
