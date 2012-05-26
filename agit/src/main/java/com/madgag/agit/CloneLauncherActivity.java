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

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static com.madgag.agit.GitIntents.EXTRA_TARGET_DIR;
import static com.madgag.agit.GitIntents.sourceUriFrom;
import static com.madgag.agit.GitOperationsService.cloneOperationIntentFor;
import static com.madgag.agit.R.string.clone_launcher_activity_title;
import static com.madgag.agit.R.string.clone_readiness_repository_folder_already_exists;
import static com.madgag.agit.R.string.ssh_agent_not_correctly_installed;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.git.TransportProtocols.niceProtocolNameFor;
import static org.eclipse.jgit.lib.Constants.DOT_GIT_EXT;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madgag.android.ClickableText;
import com.markupartist.android.widget.ActionBar;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class CloneLauncherActivity extends RoboActivity {
    private final static String TAG = "CloneLauncherActivity";

    public static Intent cloneLauncherIntentFor(String sourceUri) {
        return new GitIntentBuilder("clone.PREPARE").sourceUri(sourceUri).toIntent();
    }

    private final boolean layoutTransitionAvailable = Build.VERSION.SDK_INT >= HONEYCOMB;

    @InjectView(R.id.BareRepo)
    CheckBox bareRepoCheckbox;
    @InjectView(R.id.GoCloneButton)
    Button button;
    @InjectView(R.id.UseDefaultGitDirLocation)
    CheckBox useDefaultGitDirLocationButton;
    @InjectView(R.id.ProtocolLabel)
    TextView protocolLabel;

    @InjectView(R.id.secondary_details)
    ViewGroup secondaryDetailsGroup;

    @InjectView(R.id.CloneReadinessMessage)
    TextView cloneReadinessMessageView;
    @InjectView(R.id.GitDirEditText)
    EditText gitDirEditText;
    @InjectView(R.id.CloneUrlEditText)
    EditText cloneUrlEditText;

    @InjectView(R.id.actionbar)
    ActionBar actionBar;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clone_launcher);
        actionBar.setHomeAction(new HomeAction(this));
        actionBar.setTitle(clone_launcher_activity_title);


        button.setOnClickListener(goCloneButtonListener);
        OnCheckedChangeListener checkBoxChangeListener = new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUIWithValidation();
            }
        };
        useDefaultGitDirLocationButton.setOnCheckedChangeListener(checkBoxChangeListener);
        bareRepoCheckbox.setOnCheckedChangeListener(checkBoxChangeListener);
        TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
            }

            public void beforeTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable gitDirEditText) {
                updateUIWithValidation();
            }
        };
        cloneUrlEditText.addTextChangedListener(watcher);
        gitDirEditText.addTextChangedListener(watcher);
    }

    protected void updateUIWithValidation() {
        boolean enableClone = true;

        String cloneUriText = getCloneUriText();
        Log.d(TAG, "cloneUriText=" + cloneUriText);
        CharSequence message = null;

        boolean cloneUrlPopulated = cloneUriText.length() > 3;

        // GONE causes jerky animation with the 1st layout transition here, I think because of the initial resize
        secondaryDetailsGroup.setVisibility((cloneUrlPopulated || !layoutTransitionAvailable)?VISIBLE:INVISIBLE);

        if (!cloneUrlPopulated) {
            enableClone = false;
            message = getString(R.string.clone_readiness_needs_user_to_enter_a_url);
        }

        URIish cloneUri = null;
        String transportProtocol = null;
        try {
            cloneUri = getCloneUri();
            transportProtocol = niceProtocolNameFor(cloneUri);
        } catch (URISyntaxException e) {
            enableClone = false;
        }

        if (transportProtocol != null) {
            Log.w(TAG, "Smells like " + transportProtocol);
            protocolLabel.setText("Protocol: " + transportProtocol);
            protocolLabel.setVisibility(VISIBLE);
            if (transportProtocol.equals("SSH")) {
                boolean sshAgentAvailable = PERMISSION_GRANTED == checkCallingOrSelfPermission("org.openintents.ssh" +
                        ".permission.ACCESS_SSH_AGENT");
                Log.d(TAG, "SSH good : " + sshAgentAvailable);
                if (!sshAgentAvailable) {
                    message = getString(ssh_agent_not_correctly_installed);
                }
            }
        } else {
            Log.w(TAG, "Don't recognise protocol");
            protocolLabel.setVisibility(INVISIBLE);
        }

        gitDirEditText.setEnabled(!useDefaultGitDirLocationButton.isChecked());
        if (useDefaultGitDirLocationButton.isChecked()) {
            String requiredText = (cloneUri == null) ? "" : defaultRepoDirFor(cloneUri).getAbsolutePath();
            String currentGitDirText = gitDirEditText.getText().toString();
            if (!currentGitDirText.equals(requiredText))
                gitDirEditText.setText(requiredText);
        }

        File f = getTargetFolder();
        boolean folderAlreadyExists = f.exists();
        if (folderAlreadyExists) {
            enableClone = false;
            if (existingRepoGitDir() != null) {
                message = getString(clone_readiness_repository_folder_already_exists);
            } else {
                message = getString(R.string.clone_readiness_folder_already_exists);
            }
        }
        displayHelp(message);

        button.setEnabled(enableClone);
    }

    private void displayHelp(CharSequence message) {
        cloneReadinessMessageView.setVisibility(message == null ? INVISIBLE : VISIBLE);
        if (message != null) {
            Editable spana = new SpannableStringBuilder(message);
            ClickableText.addLinks(spana, new ClickableText.Listener() {
                public void onClick(String command, View widget) {
                    if (command.equals("specify_target_dir")) {
                        useDefaultGitDirLocationButton.setChecked(false);
                        gitDirEditText.requestFocus();
                        setCursorToEnd(gitDirEditText);
                    } else if (command.equals("view_existing_repo")) {
                        startActivity(manageRepoIntent(existingRepoGitDir()));
                    } else if (command.equals("view_ssh_instructions")) {
                        startActivity(new Intent(CloneLauncherActivity.this, AboutUsingSshActivity.class));
                    } else if (command.equals("suggest_repo")) {
                        startActivityForResult(new GitIntentBuilder("repo.SUGGEST").toIntent(), 0);
                    }
                }
            });
            cloneReadinessMessageView.setText(spana);
            cloneReadinessMessageView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private File existingRepoGitDir() {
        return RepositoryCache.FileKey.resolve(getTargetFolder(), FS.detect());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setUpUIFromIntent(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        setUpUIFromIntent(intent);
    }

    private void setUpUIFromIntent(Intent intent) {
        Log.d(TAG, "setUpUIFromIntent with " + intent);
        if (intent != null) {
            setSourceUriFrom(intent);
            setGitDirFrom(intent);
        }
    }

    private void setSourceUriFrom(Intent intent) {
        String sourceUri = sourceUriFrom(intent);
        if (sourceUri != null) {
            cloneUrlEditText.setText(sourceUri);
            setCursorToEnd(cloneUrlEditText);
            Log.d(TAG, "Set cloneUrlEditText to " + sourceUri);
        }
    }

    private static void setCursorToEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    private void setGitDirFrom(Intent intent) {
        String gitdir = intent.getStringExtra(EXTRA_TARGET_DIR);
        useDefaultGitDirLocationButton.setChecked(gitdir == null);
        if (gitdir != null) {
            gitDirEditText.setText(gitdir);
            Log.d(TAG, "Set gitdir to " + gitdir);
        }
    }

    ;

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

    public File getTargetFolder() {
        return new File(gitDirEditText.getText().toString());
    }

    OnClickListener goCloneButtonListener = new OnClickListener() {
        public void onClick(View v) {
            URIish uri;
            try {
                uri = getCloneUri();
            } catch (URISyntaxException e) {
                Toast.makeText(v.getContext(), "bad dog", 10).show();
                return;
            }
            File checkoutLocation = getTargetFolder();
            boolean bare = bareRepoCheckbox.isChecked();
            try {
                launchClone(uri, checkoutLocation, bare);
            } catch (Exception e) {
                Toast.makeText(v.getContext(), "ARRG: " + e, 10).show();
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    };

    private void launchClone(URIish uri, File repoDir, boolean bare) throws IOException, URISyntaxException {
        if (!repoDir.mkdirs()) {
            String message = "Couldn't create " + repoDir;
            Toast.makeText(CloneLauncherActivity.this, message, LENGTH_LONG).show();
            throw new IOException(message);
        }

        startService(cloneOperationIntentFor(uri, repoDir, bare));
        Toast.makeText(getApplicationContext(), R.string.clone_launcher_farewell_due_to_clone_launched, LENGTH_SHORT).show();
        finish();
    }

    private File defaultRepoDirFor(URIish uri) {
        File reposDir = new File(Environment.getExternalStorageDirectory(), "git-repos");
        try {
            String suffix = bareRepoCheckbox.isChecked() ? DOT_GIT_EXT : "";
            return new File(reposDir, uri.getHumanishName() + suffix);
        } catch (IllegalArgumentException e) {
            return new File(reposDir, "repo");
        }
    }

}