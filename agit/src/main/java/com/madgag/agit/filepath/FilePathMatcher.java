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


import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.quote;

import com.google.common.base.Predicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePathMatcher implements Predicate<CharSequence> {

    private final Pattern pattern;
    private final CharSequence constraint;
    private final int[] matchingLetters;

    public FilePathMatcher(CharSequence constraint) {
        this.constraint = constraint;
        pattern = patternFor(constraint);
        matchingLetters = new int[constraint.length()];
    }

    @Override
    public boolean apply(CharSequence filePath) {
        String boom = filePath.toString().toLowerCase();
        int index = -1;
        for (int i = 0; i < constraint.length(); ++i) {
            index = boom.indexOf(constraint.charAt(i), index + 1);
            if (index < 0) {
                return false;
            }
        }
        return true;
        //return pattern.matcher(filePath).find();
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

}
