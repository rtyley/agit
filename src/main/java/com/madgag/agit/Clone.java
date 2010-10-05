package com.madgag.agit;

import static android.widget.Toast.LENGTH_LONG;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.URIish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Clone extends Activity {

	private final static String TAG="Clone";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((Button) findViewById(R.id.GoCloneButton)).setOnClickListener(goCloneButtonListener);
    }
    
    protected void onStart() {
    	super.onStart();
    	Intent intent = getIntent();
    	Log.i("Cloner", "Starting with "+intent);
    	if (intent!=null) {
    		String sourceUri= intent.getExtras().getString("source-uri");
    		((EditText) findViewById(R.id.CloneUrlEditText)).setText(sourceUri);
    	}
    };
    
    OnClickListener goCloneButtonListener = new OnClickListener() {
        public void onClick(View v) {
            //finish();
        	String sourceUri = ((EditText) findViewById(R.id.CloneUrlEditText)).getText().toString();
        	Uri u=Uri.parse(sourceUri);
        	Log.i(TAG, "scheme="+u.getScheme());
    		URIish uri;
			try {
				uri = new URIish(sourceUri);
			} catch (URISyntaxException e) {
				Toast.makeText(v.getContext(), "bad dog", 10).show();
				return;
			}
    		try {
				wham(uri);
			} catch (Exception e) {
				Toast.makeText(v.getContext(), "ARRG: "+e, 10).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

		private void wham(URIish uri) throws IOException, URISyntaxException {
			File reposDir=new File(Environment.getExternalStorageDirectory(),"git-repos");
			String localName = uri.getHumanishName();
			File repoDir=new File(reposDir,localName);
			if (!repoDir.mkdirs()) {
				Toast.makeText(Clone.this, "Couldn't create "+repoDir, LENGTH_LONG).show();
				throw new IOException();
			}
    		File gitdir = new File(repoDir, Constants.DOT_GIT);
    		Intent intent = new Intent("git.CLONE");
    		intent
    			.putExtra("source-uri", uri.toPrivateString())
    			.putExtra("gitdir", gitdir.getAbsolutePath());
    		Log.i(TAG, "Doin "+intent+" "+intent.getStringExtra("gitdir"));
			startService(intent);
    		
    		// cloneStuff(uri, gitdir);
		}


		
//		private FetchResult runFetch() throws NotSupportedException, URISyntaxException, TransportException {
//			final Transport tn = Transport.open(db, remoteName);
//			final FetchResult r;
//			try {
//				r = tn.fetch(new TextProgressMonitor(), null);
//			} finally {
//				tn.close();
//			}
//			// showFetchResult(tn, r);
//			return r;
//		}
    };
    
}