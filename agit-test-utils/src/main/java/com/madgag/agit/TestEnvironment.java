package com.madgag.agit;

import java.io.File;
import java.io.InputStream;

public interface TestEnvironment {
    InputStream streamFor(String s);

    File tempFolder();
}
