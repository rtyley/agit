/*
 * Copyright (c) 2011 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import static android.R.layout.two_line_list_item;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.madgag.agit.GitIntents.*;
import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import android.text.*;
import android.text.method.LinkMovementMethod;
import android.view.animation.*;
import android.widget.*;
import com.madgag.android.listviews.ViewHoldingListAdapter;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import com.markupartist.android.widget.ActionBar;
import org.eclipse.jgit.transport.URIish;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class CloneLauncherActivity extends RoboActivity {
	private final static String TAG="CloneLauncherActivity";

    public static Intent cloneLauncherIntentFor(String sourceUri) {
        return new GitIntentBuilder("com.madgag.git.clone.prepare").sourceUri(sourceUri).toIntent();
	}

    @InjectView(R.id.BareRepo) CheckBox bareRepoCheckbox;
    @InjectView(R.id.GoCloneButton) Button button;
	@InjectView(R.id.UseDefaultGitDirLocation) CheckBox useDefaultGitDirLocationButton;
	@InjectView(R.id.CloneReadinessMessage) TextView cloneReadinessMessageView;
	@InjectView(R.id.GitDirEditText) EditText gitDirEditText;
    @InjectView(R.id.CloneUrlEditText) EditText cloneUrlEditText;
    
	@InjectView(R.id.actionbar) ActionBar actionBar;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clone_launcher);
        actionBar.setHomeLogo(R.drawable.actionbar_agit_logo);


		button.setOnClickListener(goCloneButtonListener);
        OnCheckedChangeListener checkBoxChangeListener = new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUIWithValidation();
            }
        };
        useDefaultGitDirLocationButton.setOnCheckedChangeListener( checkBoxChangeListener);
        bareRepoCheckbox.setOnCheckedChangeListener( checkBoxChangeListener);
        TextWatcher watcher = new TextWatcher() {
			public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			
			public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {}
			
			public void afterTextChanged(Editable gitDirEditText) { updateUIWithValidation(); }
		};
		cloneUrlEditText.addTextChangedListener(watcher);
		gitDirEditText.addTextChangedListener(watcher);
    }

    protected void updateUIWithValidation() {
    	boolean enableClone=true;

        String cloneUriText = getCloneUriText();
        Log.d(TAG, "cloneUriText="+cloneUriText);
        CharSequence message=null;
        if (cloneUriText.length()<=3) {
            message = getString(R.string.clone_readiness_needs_user_to_enter_a_url);
        }
    	URIish cloneUri=null;
    	try {
    		cloneUri=getCloneUri();
    	} catch (URISyntaxException e) {
    		enableClone=false;
    	}
    	
    	gitDirEditText.setEnabled(!useDefaultGitDirLocationButton.isChecked());
    	if (useDefaultGitDirLocationButton.isChecked() && cloneUri!=null) {
    		String currentGitDirText=gitDirEditText.getText().toString();
    		String requiredText = defaultRepoDirFor(cloneUri).getAbsolutePath();
    		if (!currentGitDirText.equals(requiredText))
    			gitDirEditText.setText(requiredText);
    	}
    	
		File f=new File(gitDirEditText.getText().toString());
		boolean goodGitDir=!f.exists();
		if (!goodGitDir) {
			enableClone=false;
            message=getString(R.string.clone_readiness_requires_fresh_checkout_folder);
		}
        cloneReadinessMessageView.setVisibility(message==null?INVISIBLE:VISIBLE);
        if (message!=null) {
            Editable spana=new SpannableStringBuilder(message);
            ClickableText.addLinks(spana, new ClickableText.Listener() {
                public void onClick(String command, View widget) {
                    if (command.equals("specify_target_dir")) {
                        useDefaultGitDirLocationButton.setChecked(false);
                        gitDirEditText.requestFocus();
                        setCursorToEnd(gitDirEditText);
                    } else if (command.equals("suggest_repo")) {
                        startActivity(new Intent("com.madgag.git.repo.suggest"));
                    }
                }
            });
            cloneReadinessMessageView.setText(spana);
            cloneReadinessMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        }


		
		button.setEnabled(enableClone);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	Intent intent = getIntent();
    	Log.d(TAG, "Starting with da "+intent);
    	if (intent!=null) {
    		setSourceUriFrom(intent);
    		setGitDirFrom(intent);
    	}
    }

	private void setSourceUriFrom(Intent intent) {
		String sourceUri= intent.getStringExtra(EXTRA_SOURCE_URI);
		if (sourceUri != null) {
			cloneUrlEditText.setText(sourceUri);
            setCursorToEnd(cloneUrlEditText);
			Log.d(TAG, "Set cloneUrlEditText to "+sourceUri);
		}
	}

    private static void setCursorToEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

	private void setGitDirFrom(Intent intent) {
		String gitdir= intent.getStringExtra(EXTRA_TARGET_DIR);
		useDefaultGitDirLocationButton.setChecked(gitdir==null);
		if (gitdir!=null) { 
			gitDirEditText.setText(gitdir);
			Log.d(TAG, "Set gitdir to "+gitdir);
		}
	};
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.d(TAG, "onResume called");
    	updateUIWithValidation();
    }
    
    public URIish getCloneUri() throws URISyntaxException {
    	return new URIish(getCloneUriText());
    }

    private String getCloneUriText() {
        return cloneUrlEditText.getText().toString();
    }

    public File getCheckoutLocation() {
    	return new File(gitDirEditText.getText().toString());
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
    		File checkoutLocation=getCheckoutLocation();
            boolean bare=bareRepoCheckbox.isChecked();
    		try {
				launchClone(uri, checkoutLocation, bare);
			} catch (Exception e) {
				Toast.makeText(v.getContext(), "ARRG: "+e, 10).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }



    };

    private void launchClone(URIish uri, File repoDir, boolean bare) throws IOException, URISyntaxException {
        if (!repoDir.mkdirs()) {
            String message = "Couldn't create "+repoDir;
            Toast.makeText(CloneLauncherActivity.this, message, LENGTH_LONG).show();
            throw new IOException(message);
        }

        startService(cloneOperationIntentFor(uri, repoDir, bare));
        Toast.makeText(getApplicationContext(), R.string.clone_launcher_farewell_due_to_clone_launched, LENGTH_SHORT).show();
        finish();
    }
    
	private File defaultRepoDirFor(URIish uri) {
		File reposDir=new File(Environment.getExternalStorageDirectory(),"git-repos");
		try {
            String suffix = bareRepoCheckbox.isChecked()?DOT_GIT_EXT:"";
			return new File(reposDir, uri.getHumanishName()+suffix);
		} catch (IllegalArgumentException e) {
			return new File(reposDir, "repo");
		}
	}

}