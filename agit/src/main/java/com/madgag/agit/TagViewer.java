package com.madgag.agit;

import static com.madgag.agit.GitIntents.tagNameFrom;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import com.madgag.android.lazydrawables.BitmapFileStore;
import com.madgag.android.lazydrawables.ImageProcessor;
import com.madgag.android.lazydrawables.ImageResourceDownloader;
import com.madgag.android.lazydrawables.ImageResourceStore;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.lazydrawables.ScaledBitmapDrawableGenerator;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;
import com.markupartist.android.widget.ActionBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TagViewer extends RepositoryActivity {

    public static Intent tagViewerIntentFor(Repository repository, String tagName) {
		return new GitIntentBuilder("git.tag.VIEW").repository(repository).tag(tagName).toIntent();
	}

	private static final String TAG = "TV";
	
	@Override String TAG() { return TAG; }

	private final static int DELETE_ID=Menu.FIRST;
	
	private ActionBar actionBar;
	private PersonIdentView taggerIdentView;
	
	private RevTag revTag;

	private Ref tagRef;

	private ImageSession<String, Bitmap> avatarSession;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_view);
		
		ImageProcessor<Bitmap> imageProcessor = new ScaledBitmapDrawableGenerator(34, getResources());
		ImageResourceDownloader<String, Bitmap> downloader = new GravatarBitmapDownloader();
		File file = new File(Environment.getExternalStorageDirectory(),"gravagroovy");
		ImageResourceStore<String, Bitmap> imageResourceStore = new BitmapFileStore<String>(file);
		avatarSession=new ImageSession<String, Bitmap>(imageProcessor, downloader, imageResourceStore, getResources().getDrawable(R.drawable.loading_34_centred));
		
		actionBar = (ActionBar) findViewById(R.id.actionbar);
		taggerIdentView = (PersonIdentView) findViewById(R.id.tv_tag_tagger_ident);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_ID, 0, R.string.delete_tag_menu_option).setShortcut('0', 'd');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.i(TAG, "onOptionsItemSelected "+item);
        switch (item.getItemId()) {
        case DELETE_ID:
			try {
				RefUpdate update = repo().updateRef(tagRef.getName());
				update.setForceUpdate(true);
				// update.setNewObjectId(head);
				// update.setForceUpdate(force || remote);
				Result result = update.delete();
				Toast.makeText(this, "Tag deletion : "+result.name(), Toast.LENGTH_SHORT).show();
				finish();
			} catch (IOException e) {
				Log.e(TAG, "Couldn't delete "+revTag.getName(), e);
				throw new RuntimeException(e);
			}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onContentChanged() {
    	Log.d(TAG, "updateUI called");
    	tagRef = repo().getTags().get(tagNameFrom(getIntent()));	
    	if (taggerIdentView==null) {
    		return;
    	}
    	
		if (tagRef==null) {
			actionBar.setTitle("unknown tag");
		} else {
			ObjectId tagId = tagRef.getObjectId();
			try {
				revTag = new RevWalk(repo()).parseTag(tagId);
				actionBar.setTitle(revTag.getTagName());
				taggerIdentView.setIdent(avatarSession, "Tagger", revTag.getTaggerIdent());
			} catch (IOException e) {
				Log.e(TAG, "Couldn't get parse tag", e);
				Toast.makeText(this, "Couldn't get tag "+tagId, Toast.LENGTH_LONG).show();
			}
		}
    }
}
