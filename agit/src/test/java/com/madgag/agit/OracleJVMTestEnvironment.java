package com.madgag.agit;

import com.madgag.agit.matchers.GitTestHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;

public class OracleJVMTestEnvironment implements TestEnvironment {

	public static GitTestHelper helper() {
		return new GitTestHelper(new OracleJVMTestEnvironment());
	}

	public InputStream streamFor(String fileName) {
		return OracleJVMTestEnvironment.class.getResourceAsStream("/" + fileName);
	}

	public File tempFolder() {
		return FileUtils.getTempDirectory();
	}
}
