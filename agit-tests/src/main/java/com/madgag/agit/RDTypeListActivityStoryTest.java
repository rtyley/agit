package com.madgag.agit;


import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.compress.CompressUtil.unzip;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
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

		assertThat(tagDomainType.getAll().size(), is(4));
		assertThat(listView.getChildCount(), is(4));
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
