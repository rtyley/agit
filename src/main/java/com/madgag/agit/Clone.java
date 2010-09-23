package com.madgag.agit;

import static android.content.Intent.ACTION_VIEW;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
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
    	if (intent!=null && intent.getData()!=null) {
    		((EditText) findViewById(R.id.CloneUrlEditText)).setText(intent.getData().toString());
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
				Toast.makeText(Clone.this, "Couldn't create "+repoDir, 10).show();
				throw new IOException();
			}
    		File gitdir = new File(repoDir, Constants.DOT_GIT);
    		FileRepository dst = new FileRepository(gitdir);
    		dst.create();
    		dst.getConfig().setBoolean("core", null, "bare", false);
    		dst.getConfig().save();
    		Repository db = dst;
    		
    		String remoteName = Constants.DEFAULT_REMOTE_NAME;
    		
    		final RemoteConfig rc = new RemoteConfig(dst.getConfig(), remoteName);
    		rc.addURI(uri);
    		rc.addFetchRefSpec(new RefSpec().setForceUpdate(true)
    				.setSourceDestination(Constants.R_HEADS + "*",
    						Constants.R_REMOTES + remoteName + "/*"));
    		rc.update(dst.getConfig());
    		dst.getConfig().save();
    		Uri gitdirUri = Uri.fromFile(gitdir);
			startService(new Intent("git.FETCH", gitdirUri, Clone.this,GitOperationsService.class));
			//startActivity(new Intent(ACTION_VIEW, gitdirUri, Clone.this,RepositoryManagementActivity.class));
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