package com.madgag.agit;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Clone extends Activity {

	private final static String TAG="Clone";

	private Button button;
	private CheckBox useDefaultGitDirLocationButton;
	private TextView warningTextView;
	private EditText gitDirEditText, cloneUrlEditText;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.GoCloneButton);
        gitDirEditText = (EditText) findViewById(R.id.GitDirEditText);
        warningTextView = (TextView) findViewById(R.id.GitDirWarning);
        cloneUrlEditText = (EditText) findViewById(R.id.CloneUrlEditText);
        useDefaultGitDirLocationButton = (CheckBox) findViewById(R.id.UseDefaultGitDirLocation);
		button.setOnClickListener(goCloneButtonListener);
		useDefaultGitDirLocationButton.setOnClickListener(new OnClickListener() {	
			public void onClick(View v) { updateUIWithValidation(); }
		});
        TextWatcher watcher = new TextWatcher() {
			public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			
			public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			
			public void afterTextChanged(Editable gitDirEditText) { updateUIWithValidation(); }
		};
		((TextView) findViewById(R.id.GitDirEditText)).addTextChangedListener(watcher);
    }
    
    protected void updateUIWithValidation() {
    	boolean enableClone=true;
    	
    	URIish cloneUri=null;
    	try {
    		cloneUri=getCloneUri();
    	} catch (URISyntaxException e) {
    		enableClone=false;
    	}
    	
    	gitDirEditText.setEnabled(!useDefaultGitDirLocationButton.isChecked());
    	if (useDefaultGitDirLocationButton.isChecked() && cloneUri!=null) {
    		gitDirEditText.setText(defaultRepoDirFor(cloneUri).getAbsolutePath());
    	}
    	
		File f=new File(gitDirEditText.toString());
		boolean goodGitDir=!f.exists();
		Log.d("Clone", "goodGitDir="+goodGitDir);
		warningTextView.setVisibility(goodGitDir?INVISIBLE:VISIBLE);
		if (!goodGitDir) {
			enableClone=false;
		}
		
		button.setEnabled(enableClone);
    }
    
    protected void onStart() {
    	super.onStart();
    	Intent intent = getIntent();
    	Log.i("Cloner", "Starting with "+intent);
    	if (intent!=null && intent.getExtras()!=null) {
    		String sourceUri= intent.getExtras().getString("source-uri");
			cloneUrlEditText.setText(sourceUri);
    	}
    };
    
    public URIish getCloneUri() throws URISyntaxException {
    	return new URIish(cloneUrlEditText.getText().toString());
    }
    
    OnClickListener goCloneButtonListener = new OnClickListener() {
        public void onClick(View v) {
    		URIish uri;
    		try {
    			uri=getCloneUri();
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
			File repoDir = defaultRepoDirFor(uri);
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
    
	private File defaultRepoDirFor(URIish uri) {
		File reposDir=new File(Environment.getExternalStorageDirectory(),"git-repos");
		String localName = uri.getHumanishName();
		File repoDir=new File(reposDir,localName);
		return repoDir;
	} 
}