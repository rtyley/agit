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
