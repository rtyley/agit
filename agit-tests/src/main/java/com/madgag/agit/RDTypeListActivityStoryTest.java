package com.madgag.agit;


import static com.madgag.agit.GitOperationsServiceTest.newFolder;
import static com.madgag.agit.RDTypeListActivity.listIntent;
import static com.madgag.agit.CharSequenceMatcher.charSequence;
import static com.madgag.compress.CompressUtil.unzip;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

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
		
		setActivityIntent(listIntent(repoWithTags, "tag"));
		
		final RDTypeListActivity activity = getActivity();
		
		ListView listView = activity.getListView();

		checkCanSelectEveryItemInNonEmpty(listView);

		RDTTag tagDomainType= new RDTTag(repoWithTags);
		List<TagSummary> summaries = tagDomainType.getAll();
		Log.i(TAG, "Should be "+summaries.size()+" elements in the list.. there are "+listView.getCount());
		assertThat(listView.getCount(), is(summaries.size()));
		for (int index=0; index<summaries.size(); ++index) {
			TagSummary summary = summaries.get(index);
			View itemView=getItemViewBySelecting(listView, index);
			Log.d(TAG, "summary="+summary+" view="+itemView);
			TextView itemTitleTextView = (TextView) itemView.findViewById(android.R.id.text1);
			assertThat(itemTitleTextView.getText(), is(summary.getName()));
			
			if (summary.getName().equals("annotated-tag-of-2nd-commit")) {
				CharSequence dt = ((TextView) itemView.findViewById(android.R.id.text2)).getText();
				Log.i(TAG, "Looking... "+ dt);
				assertThat(dt, charSequence(startsWith("Commit")));
				assertThat(dt, charSequence(containsString("Adding my happy folder with it's tags")));
			}
		}
	}

	private void checkCanSelectEveryItemInNonEmpty(ListView listView) {
		assertThat(listView.getCount()>0, is(true));
		for (int index=0; index<listView.getCount(); ++index) {
			View itemView=getItemViewBySelecting(listView, index);
			Log.d(TAG, "view="+itemView);
		}
	}

	private View getItemViewBySelecting(final ListView listView, final int index) {
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
				listView.setSelection(index);
		    }
		});
		getInstrumentation().waitForIdleSync();
		return listView.getSelectedView();
	}

	private Repository unpackRepo(String fileName) throws IOException, ArchiveException {
		AssetManager am = getInstrumentation().getContext().getAssets();
		File repoParentFolder = newFolder();
		InputStream rawZipFileInputStream = am.open(fileName);
		return unzipRepoFromStreamToFolder(rawZipFileInputStream, repoParentFolder);
	}

	private Repository unzipRepoFromStreamToFolder(
			InputStream rawZipFileInputStream, File destinationFolder)
			throws IOException, ArchiveException {
		unzip(rawZipFileInputStream, destinationFolder);
		rawZipFileInputStream.close();
		return new FileRepository(new File(destinationFolder,".git"));
	}
	
}
