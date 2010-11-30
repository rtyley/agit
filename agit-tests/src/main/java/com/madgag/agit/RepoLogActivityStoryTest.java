package com.madgag.agit;


import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.agit.RepoLogActivity.repoLogIntentFor;
import static com.madgag.compress.CompressUtil.unzip;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;

public class RepoLogActivityStoryTest extends ActivityInstrumentationTestCase2<RepoLogActivity> {
	
	private final static String TAG = RepoLogActivityStoryTest.class.getSimpleName();
	
	public RepoLogActivityStoryTest() {
		super("com.agit",RepoLogActivity.class);
	}
	
	public void testShouldUpdateListWithResultsOfAFetchDammit() throws Exception {
		File startRepo = newFolder();
		File gitdir = new File(startRepo,".git");
		setActivityIntent(repoLogIntentFor(gitdir));
		AssetManager am = getInstrumentation().getContext().getAssets();
		
		InputStream rawZipFileInputStream = am.open("small-project.early.zip");
		unzip(rawZipFileInputStream, startRepo);
		Log.i(TAG, "Unpacked test. gitdir = " +gitdir+" contains = "+asList(gitdir.list()));
		
		
		RepoLogActivity activity = getActivity();
		ListView listView = (ListView) activity.getListView();
		assertTrue(listView.getChildCount()==1);
		
		assertTrue(listView.getChildCount()>1);
	}
	
}
