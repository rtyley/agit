package com.madgag.agit;


import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.compress.CompressUtil.unzip;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import android.app.ListActivity;
import android.content.res.AssetManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.madgag.agit.RDTTag.TagSummary;

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

		List<TagSummary> summaries = tagDomainType.getAll();
		Log.i(TAG, "Should be "+summaries.size()+" elements in the list.. there are "+listView.getCount());
		assertThat(listView.getCount(), is(summaries.size()));
		for (int index=0; index<summaries.size(); ++index) {
			TagSummary summary = summaries.get(index);
			View itemView=getItemViewFrom(activity, index);
			Log.d(TAG, "summary="+summary+" view="+itemView);
			TextView itemTitleTextView = (TextView) itemView.findViewById(android.R.id.text1);
			assertThat(itemTitleTextView.getText(), is(summary.getName()));
		}
	}

	private View getItemViewFrom(final ListActivity activity, final int index) {
		final ListView listView = activity.getListView();
		activity.runOnUiThread(new Runnable() {
		    public void run() {
				listView.setSelection(index);
		    }
		});
		getInstrumentation().waitForIdleSync();
		return listView.getSelectedView();
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
