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


import static java.lang.Math.min;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.quote;

import com.google.common.base.Predicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePathMatcher implements Predicate<FilePath> {

    private final Pattern pattern;
    private final String constraint;
    private final int[] matchingLetters;
    private final char[] constraintUC, constraintLC;
    private final int constraintLen;
    private final boolean userSpecifiedPathSegments;

    public FilePathMatcher(String constraint) {
        this.constraint = constraint;
        userSpecifiedPathSegments = constraint.contains("/");
        constraintLen = constraint.length();

        // OPTIM : Don't case-fold every filepath - VERY slow! Instead try both lower & upper versions of constraint
        constraintUC = constraint.toUpperCase().toCharArray();
        constraintLC = constraint.toLowerCase().toCharArray();
        pattern = patternFor(constraint);
        matchingLetters = new int[constraintLen];
    }

    @Override
    public boolean apply(FilePath filePath) {
        String p = filePath.getPath();
        int pathLen = p.length();
        int index = 0;
        for (int i = 0; i < constraintLen; ++i) {
            char uc = constraintUC[i], lc = constraintLC[i];
            int uci = p.indexOf(uc, index), lci = p.indexOf(lc, index); // OPTIM : indexOf is native and fast
            index = 1 + min(uci >= 0 ? uci : pathLen, lci >= 0 ? lci : pathLen);
            if (index > pathLen) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return int array which will be overwritten on the next call - consume or copy before next invocation!
     */
    public int[] match(CharSequence filePath) {
        Matcher matcher = pattern.matcher(filePath);
        if (matcher.find()) {
            for (int i = 0; i < matchingLetters.length; ++i) {
                matchingLetters[i] = matcher.start(i + 1);
            }
            return matchingLetters;
        } else {
            return null;
        }
    }

    private static Pattern patternFor(CharSequence constraint) {
        StringBuilder builder = new StringBuilder(".*");
        for (int i = 0; i < constraint.length(); ++i) {
            builder.append("(").append(quote("" + constraint.charAt(i))).append(").*?");
        }
        builder.append("$");
        return Pattern.compile(builder.toString(), CASE_INSENSITIVE);
    }


    public double score(FilePath fp) {
        double pathScore = scoreSegment(fp, 0);
        return userSpecifiedPathSegments?pathScore:(pathScore + scoreSegment(fp, fp.getPath().lastIndexOf('/') + 1));
    }

    /**
     * This method is a tweaked/optimised filepath-specific adaptation of the MIT-licensed string_score algorithm by
     * Joshaven Potter (https://github.com/joshaven/string_score), adapted from the Java version by
     * Shingo Omura (https://github.com/everpeace/string-score).
     */
    private double scoreSegment(FilePath filePath, int fromIndex) {
        String path = filePath.getPath();

        // If the path is equal to the abbreviation, perfect match.
        if (path.substring(fromIndex).equals(constraint)) {
            return 1.0d;
        }
        //if it's not a perfect match and is empty return 0
        if (constraint.isEmpty()) {
            return 0d;
        }

        int fpLen = path.length();
        double totalCharacterScore = 0d;
        double abbreviationScore = 0d;

        // Walk through abbreviation and add up scores.
        int fpPos = fromIndex;
        for (int i = 0; i < constraint.length(); i++) {
            double characterScore;

            // OPTIM : indexOf is native and fast - *much* faster than lowercasing the filepath...
            int uci = path.indexOf(constraintUC[i], fpPos), lci = path.indexOf(constraintLC[i], fpPos);
            int indexInString = min(uci >= 0 ? uci : fpLen, lci >= 0 ? lci : fpLen);

            //If no value is found
            if (indexInString == fpLen) {
                return 0d;
            } else {
                characterScore = 0.1d;
            }

            // Consecutive letter & start-of-path Bonus
            if (indexInString == fpPos) {
                // Increase the score when matching first character of the
                // remainder of the path
                characterScore += 0.6d;
            } else {
                // Acronym Bonus
                // Weighing Logic: Typing the first character of an acronym is as if you
                // preceded it with two perfect character matches.
                if (path.charAt(indexInString - 1) == '/') { // used to be ' '
                    characterScore += 0.8d;
                }
            }
            fpPos = indexInString + 1;
            totalCharacterScore += characterScore;
        }

        double segmentLen = fpLen - fromIndex;
        double abbreviationLength = constraint.length();
        abbreviationScore = totalCharacterScore / abbreviationLength;

        // Reduce penalty for longer strings.
        return ((abbreviationScore * (abbreviationLength / segmentLen)) + abbreviationScore) / 2;
    }
}
