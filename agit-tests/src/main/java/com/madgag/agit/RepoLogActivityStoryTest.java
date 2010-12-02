package com.madgag.agit;


import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.agit.RepoLogActivity.repoLogIntentFor;
import static com.madgag.compress.CompressUtil.unzip;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

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
		
		Repository earlyRepo = unpackRepo("small-repo.early.zip");
		Repository laterRepo = unpackRepo("small-repo.later.zip");
		RemoteConfig remoteForSourceRepo = addRepoAsRemote(earlyRepo.getConfig(), laterRepo);
		
		setActivityIntent(repoLogIntentFor(earlyRepo));
		RepoLogActivity activity = getActivity();
		ListView listView = (ListView) activity.getListView();
		assertTrue(listView.getChildCount()==1);
		
		getInstrumentation().callActivityOnPause(activity);
		
		new Git(earlyRepo).fetch().setRemote(remoteForSourceRepo.getName()).call();
		
		getInstrumentation().callActivityOnResume(activity);
		
		assertTrue(listView.getChildCount()>1);
	}

	private RemoteConfig addRepoAsRemote(StoredConfig configToChange, Repository remoteRepo)
			throws URISyntaxException, IOException {
		RemoteConfig remoteConfig = new RemoteConfig(configToChange, "origin");
		remoteConfig.addURI(new URIish(remoteRepo.getDirectory().getAbsolutePath()));
		remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
		remoteConfig.update(configToChange);
		configToChange.save();
		return remoteConfig;
	}

	private Repository unpackRepo(String fileName) throws IOException, ArchiveException {
		AssetManager am = getInstrumentation().getContext().getAssets();
		File startRepo = newFolder();
		InputStream rawZipFileInputStream = am.open(fileName);
		unzip(rawZipFileInputStream, startRepo);
		rawZipFileInputStream.close();
		File gitdir = new File(startRepo,".git");
		return new FileRepository(gitdir);
	}
	
}
