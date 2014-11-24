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

import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Lists.reverse;
import static com.google.common.collect.Lists.transform;
import static com.madgag.agit.filepath.ScoredPath.scoreFor;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ScoredPathTest {

    @Test
    public void shouldPreferShorterMatches() {
        assertThatListOrderIsUnchangedForConstraint(
                "rgc",
                "rgc",
                "src/bang/rgc",
                "src/bang/rgc.java",
                "src/rabcdegabccoo.java");
    }

//    @Test
//    public void shouldPositivelyRewardCapitalisedMatches() {
//        assertThatListOrderIsUnchangedForConstraint(
//                "rgc",
//                "rgc",
//                "src/bang/rgc",
//                "src/bang/rgc.java",
//                "src/ReallyGoodClass.java",
//                "src/ReallyGoodClassName.java",
//                "src/ReallyGoodToClass.java",
//                "src/ReadyGrace.java",
//                "src/rabcdegabccoo.java");
//    }

//    @Test
//    public void shouldPreferFilesWithNameMatchingConstraint() {
//        assertThatListOrderIsUnchangedForConstraint("apk",
//                "src/main/java/com/jayway/maven/plugins/android/configuration/Apk.java",
//                "src/main/java/com/jayway/maven/plugins/android/phase09package/ApkMojo.java",
//                "src/main/java/com/jayway/maven/plugins/android/phase09package/ApkBuilder.java",
//                "src/test/java/com/jayway/maven/plugins/android/phase09package/ApkMojoTest.java",
//                "src/main/java/com/jayway/maven/plugins/android/AndroidNdk.java");
//    }


    private void assertThatListOrderIsUnchangedForConstraint(String constraint, String... correctlyOrderedPaths) {
        List<String> correctList = asList(correctlyOrderedPaths);
        List<FilePath> correctlyOrderedFilePaths = transform(correctList, FilePath.TO_FILEPATH);

        List<FilePath> reversedList = reverse(correctlyOrderedFilePaths);

        List<ScoredPath> scoredPaths = ScoredPath.ORDERING.sortedCopy(transform(reversedList, scoreFor(constraint)));

        assertThat(transform(scoredPaths, compose(FilePath.PATH, ScoredPath.PATH)), equalTo(correctList));
    }
}
