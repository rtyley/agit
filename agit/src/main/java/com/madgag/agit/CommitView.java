package com.madgag.agit;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.filter.CommitterRevFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.madgag.agit.CommitNavigationView.CommitSelectedListener;
import com.madgag.android.lazydrawables.BitmapFileStore;
import com.madgag.android.lazydrawables.ImageProcessor;
import com.madgag.android.lazydrawables.ImageResourceDownloader;
import com.madgag.android.lazydrawables.ImageResourceStore;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.lazydrawables.ScaledBitmapDrawableGenerator;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;
import com.markupartist.android.widget.ActionBar;

public class CommitView extends LinearLayout {
	
	private static final String TAG = "CommitView";

	private final LayoutInflater layoutInflater;
	private final TabHost tabHost;
	private final ActionBar actionBar;
	private final TabWidget tabWidget;
	
	private CommitNavigationView commitNavigationView;
	
	private Repository repository;
	private PlotWalk revWalk;
	
	private PlotCommit<PlotLane> commit;
	private Map<String, RevCommit> commitParents, commitChildren;

	private CommitSelectedListener commitSelectedListener;

	private ImageSession<String, Bitmap> is;

	public CommitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		ImageProcessor<Bitmap> imageProcessor = new ScaledBitmapDrawableGenerator(34, getResources());
		ImageResourceDownloader<String, Bitmap> downloader = new GravatarBitmapDownloader();
		File file = new File(Environment.getExternalStorageDirectory(),"gravagroovy");
		ImageResourceStore<String, Bitmap> imageResourceStore = new BitmapFileStore<String>(file);
		is=new ImageSession<String, Bitmap>(imageProcessor, downloader, imageResourceStore, getResources().getDrawable(R.drawable.loading_34_centred));
		
		
		layoutInflater = LayoutInflater.from(context);
		
		layoutInflater.inflate(R.layout.commit_view, this);
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		tabHost.setup();
	}
	
	public void setRepositoryContext(Repository repository, PlotWalk revWalk) {
		this.repository = repository;
		this.revWalk = revWalk;
	}
	
	public void setCommit(final PlotCommit<PlotLane> c) throws MissingObjectException, IncorrectObjectTypeException, IOException {
			//this.commit = (PlotCommit<PlotLane>) revWalk.parseCommit(c);
			this.commit = c;
			Log.d(TAG, "setCommit : "+commit);
			Log.d(TAG, "actionBar : "+actionBar);
			actionBar.setTitle(commit.name().substring(0, 4)+" "+commit.getShortMessage());
			
			tabHost.clearAllTabs();
					
		    tabHost.addTab(detailTabSpec());
		    
		    showCommitDetailsFor(commit);
		    
		    commitParents = newHashMapWithExpectedSize(commit.getParentCount());
		    TabContentFactory contentFactory = new TabContentFactory() {
				public View createTabContent(String tag) {
					RevCommit parentCommit = commitParents.get(tag);
					View v = layoutInflater.inflate(R.layout.rev_commit_view, tabWidget, false);
					DiffSliderView diffSlider = (DiffSliderView) v.findViewById(R.id.RevCommitDiffSlider);
					ExpandableListView expandableList = (ExpandableListView) v.findViewById(android.R.id.list);
					expandableList.setAdapter(new CommitChangeListAdapter(repository, commit, parentCommit, diffSlider, expandableList, getContext()));
					return v;
				}
			};
			
			
		    for (RevCommit parentCommit : commit.getParents()) {
		    	parentCommit = revWalk.parseCommit(parentCommit);
		    	String parentId = parentCommit.getName();
				commitParents.put(parentId, parentCommit);
		    	TabSpec spec = tabHost.newTabSpec(parentId);
		    	String text = "Δ "+parentId.substring(0, 4);
		    	
				spec.setIndicator(newTabIndicator(tabHost, text)).setContent(contentFactory);
			    tabHost.addTab(spec);
		    }
		    
		    commitNavigationView.setCommit(commit);
		    
	}

	private void showCommitDetailsFor(final PlotCommit<PlotLane> commit) {
		commitNavigationView = (CommitNavigationView) findViewById(R.id.commit_navigation);
		Log.d("CV", "Got commitNavigationView="+commitNavigationView+" commitSelectedListener="+commitSelectedListener);
		commitNavigationView.setCommitSelectedListener(commitSelectedListener);
		
		text(R.id.commit_id_text,commit.getName());
		PersonIdent author = commit.getCommitterIdent();

		ViewGroup vg = (ViewGroup) findViewById(R.id.commit_people_group);
		addPerson("Author",commit.getAuthorIdent(), vg);
		addPerson("Committer",commit.getCommitterIdent(), vg);
		
//		ViewGroup vg = (ViewGroup) findViewById(R.id.commit_refs_group);
//		for (int i=0; i<commit.getRefCount(); ++i) {
//			TextView tv = new TextView(getContext());
//			tv.setText(commit.getRef(i).getName());
//			vg.addView(tv);
//		}
		text(R.id.commit_message_text,commit.getFullMessage());
	}

	private void addPerson(String title, PersonIdent commiter, ViewGroup vg) {
		PersonIdentView personIdentView = new PersonIdentView(getContext(), null);
		personIdentView.setIdent(is, title, commiter);
		vg.addView(personIdentView);
	}

	private TabHost.TabSpec detailTabSpec() {
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec("commit_details")
			.setIndicator(newTabIndicator(tabHost, "Commit"))
			.setContent(new TabContentFactory() {
				public View createTabContent(String tag) {
					return layoutInflater.inflate(R.layout.commit_detail_view, tabHost.getTabWidget(), false);
				}
			});
		return spec;
	}
	
	public void setCommitSelectedListener(CommitSelectedListener commitSelectedListener) {
		this.commitSelectedListener = commitSelectedListener;
	}

	private void text(int textViewId, String text) {
		 TextView textView = (TextView) findViewById(textViewId);
		 textView.setText(text);
	}

	private TextView newTabIndicator(TabHost tabHost, String text) {
		TextView v=(TextView) layoutInflater.inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
		v.setText(text);
		return v;
	}
	
}
