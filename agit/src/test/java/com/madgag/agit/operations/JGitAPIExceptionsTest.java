package com.madgag.agit.operations;

import static com.madgag.agit.operations.JGitAPIExceptions.exceptionWithFriendlyMessageFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class JGitAPIExceptionsTest {
    @Test
    public void testExceptionWithFriendlyMessageFor() throws Exception {
        RuntimeException re = exceptionWithFriendlyMessageFor(new NullPointerException());
        assertThat(re.getMessage(), equalTo("NullPointerException"));
    }
}
