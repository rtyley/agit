package com.madgag.agit.operations;

import org.junit.Test;

import static com.madgag.agit.operations.JGitAPIExceptions.exceptionWithFriendlyMessageFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JGitAPIExceptionsTest {
	@Test
	public void testExceptionWithFriendlyMessageFor() throws Exception {
		RuntimeException re = exceptionWithFriendlyMessageFor(new NullPointerException());
		assertThat(re.getMessage(), equalTo("NullPointerException"));
	}
}
