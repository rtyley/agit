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

package com.madgag.agit;


import static java.util.Arrays.asList;

import java.util.List;

public class SuggestedRepo {
    private final String name;
    private final String uri;

    public SuggestedRepo(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public static List<SuggestedRepo> SUGGESTIONS = asList(
            new SuggestedRepo("ConnectBot", "https://code.google.com/p/connectbot/"), // 3.4M
            new SuggestedRepo("Scalatra", "git://github.com/scalatra/scalatra.git"), //1.6M - not that fast?
            // new SuggestedRepo("Android Music", "git://android.git.kernel.org/platform/packages/apps/Music.git"),
            // //2.0M
            new SuggestedRepo("SBT Android Plugin", "git://github.com/jberkel/android-plugin.git"), // 248K
            new SuggestedRepo("Maven Android Plugin", "git://github.com/jayway/maven-android-plugin.git"), // 3.9M
            new SuggestedRepo("sshj", "git://github.com/shikhar/sshj.git"), // 2.5M
            // new SuggestedRepo("Redis", "git://github.com/antirez/redis.git"), // 3.3M - slow due to deltas
            // new SuggestedRepo("GWT ORM", "git://android.git.kernel.org/tools/gwtorm.git"), //2.9M - currently down
            new SuggestedRepo("JGit", "git://git.eclipse.org/gitroot/jgit/jgit.git"), // 5.0M
            new SuggestedRepo("git.js", "git://github.com/danlucraft/git.js.git")
    );

    public String toString() {
        return name + " " + uri;
    }
}
