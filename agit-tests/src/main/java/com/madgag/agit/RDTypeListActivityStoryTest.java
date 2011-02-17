package com.madgag.agit;


import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.compress.CompressUtil.unzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;

public class RDTypeListActivityStoryTest extends ActivityInstrumentationTestCase2<RDTypeListActivity> {
	
	private final static String TAG = RDTypeListActivityStoryTest.class.getSimpleName();
	
	public RDTypeListActivityStoryTest() {
		super("com.agit",RDTypeListActivity.class);
	}
	
	public void testShouldShowAllTags() throws Exception {
		Repository repoWithTags = unpackRepo("small-repo.with-tags.zip");
		
		RDTTag tagDomainType= new RDTTag(repoWithTags);
		
		setActivityIntent(tagDomainType.listIntent());
		
		final RDTypeListActivity activity = getActivity();
		
		ListView listView = (ListView) activity.getListView();
		assertTrue(listView.getChildCount()==1);
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
