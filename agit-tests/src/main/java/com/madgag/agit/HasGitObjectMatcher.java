package com.madgag.agit;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasGitObjectMatcher extends TypeSafeMatcher<Repository> {

	private final AnyObjectId objectId;

	public HasGitObjectMatcher(AnyObjectId objectId) {
		this.objectId = objectId;
	}

	@Override
	public boolean matchesSafely(Repository repository) {
		return repository.hasObject(objectId);
	}

	public void describeTo(Description description) {
		description.appendText("has git object with id ").appendValue(objectId);
	}

	@Factory
	public static <T> Matcher<Repository> hasGitObject(String objectId) {
		return new HasGitObjectMatcher(ObjectId.fromString(objectId));
	}

}
