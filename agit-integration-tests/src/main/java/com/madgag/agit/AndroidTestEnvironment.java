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
import android.app.Instrumentation;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.madgag.agit.matchers.GitTestHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class AndroidTestEnvironment implements TestEnvironment {

    private final Instrumentation instrumentation;

    public AndroidTestEnvironment(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public InputStream streamFor(String fileName) {
        try {
            Context context = instrumentation.getContext();
            Log.i("ATE", asList(context.getAssets().list("")).toString());
            return context.getAssets().open(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File tempFolder() {
        return new File(Environment.getExternalStorageDirectory(), "agit-integration-tests-tmp");
    }

    public static GitTestHelper helper(Instrumentation instrumentation) {
        return new GitTestHelper(new AndroidTestEnvironment(instrumentation));
    }
}
