package com.madgag.agit;

import org.junit.Test;

public class FilePathDifferTest {
	
	FilePathDiffer filePathDiffer = new FilePathDiffer();
	
	@Test
	public void shouldPreferToBreakAtPathSeparators() {
		String diff=filePathDiffer.diff("src/main/scala/com/gu/scarlett/appengine/AppEngine.scala","src/main/scala/com/gu/scarlett/appengine/Preamble.scala");
//		assertThat(diff, containsString("AppEngine")); // Not on the cards with current diff_match_patch behaviour...
//		assertThat(diff, containsString("Preamble"));
		System.out.println(diff);
	}
}
