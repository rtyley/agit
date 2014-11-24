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

package com.madgag.agit.filepath;


import static java.lang.Character.toUpperCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class FilePathMatcherTest {

    @Test
    public void shouldMatchEndOfCandidateString() {
        assertThat(match("xml", "pom.xml"), is(true));
    }

    @Test
    public void shouldNotMatchDifferingEndOfCandidateString() {
        assertThat(match("xml", "pom.xmo"), is(false));
    }

    @Test
    public void shouldMatch() {
        assertThat(match("attrib", "Documentation/.gitattributes"), is(true));
        assertThat(match("att", "Documentation/.gitattributes"), is(true));
        assertThat(match("docgit", "Documentation/.gitattributes"), is(true));
        assertThat(match("agitt", "agitb"), is(false));
    }

    private boolean match(String constraint, String path) {
        return new FilePathMatcher(constraint).apply(new FilePath(path));
    }

    private double score(String constraint, String path) {
        return new FilePathMatcher(constraint).score(new FilePath(path));
    }

    @Test
    public void shouldMatchAGoodChunk() {
        assertThat(capitaliseMatches("Documentation/.gitattributes", "attrib"), equalTo("documentation/.gitATTRIButes"));
        assertThat(capitaliseMatches("src/main/java/com/jayway/maven/plugins/android/configuration/Apk.java", "apk"),
                             equalTo("src/main/java/com/jayway/maven/plugins/android/configuration/APK.java"));
    }

    private String capitaliseMatches(String path, String constraint) {
        StringBuilder sb = new StringBuilder(path.toLowerCase());
        for (int index : new FilePathMatcher(constraint).match(path)) {
            sb.setCharAt(index, toUpperCase(path.charAt(index)));
        }
        return sb.toString();
    }

}
