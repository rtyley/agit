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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Clone extends Activity {

	private final static String TAG="Clone";

	private Button button;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.GoCloneButton);
		button.setOnClickListener(goCloneButtonListener);
        TextWatcher watcher = new TextWatcher() {
			
			public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {	
				Log.i(TAG, "onTextChanged="+text);
			}
			
			public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
				Log.i(TAG, "beforeTextChanged="+text);
			}
			
			public void afterTextChanged(Editable gitDirEditText) {
				File f=new File(gitDirEditText.toString());
				boolean goodGitDir = !f.exists();
				Log.i(TAG, "goodGitDir="+goodGitDir);
				//button.setEnabled(goodGitDir);
			}
		};
		((TextView) findViewById(R.id.GitDirEditText)).addTextChangedListener(watcher);
    }
    
    protected void onStart() {
    	super.onStart();
    	Intent intent = getIntent();
    	Log.i("Cloner", "Starting with "+intent);
    	if (intent!=null && intent.getExtras()!=null) {
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
				String message = "Couldn't create "+repoDir;
				Toast.makeText(Clone.this, message, LENGTH_LONG).show();
				throw new IOException(message);
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
    };
    
}