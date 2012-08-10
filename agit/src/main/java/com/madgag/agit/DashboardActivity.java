/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;

import static android.text.Html.fromHtml;
import static android.widget.Toast.LENGTH_LONG;
import static com.madgag.agit.R.string.can_not_open_non_git_folder;
import static com.madgag.agit.R.string.install_file_manager;
import static com.madgag.agit.R.string.open_git_repository;
import static com.madgag.agit.RepositoryViewerActivity.manageRepoIntent;
import static com.madgag.agit.sync.AccountAuthenticatorService.addAccount;
import static com.madgag.android.ActionBarUtil.fixImageTilingOn;
import static com.madgag.android.HtmlStyleUtil.boldCode;
import static com.madgag.android.IntentUtil.isIntentAvailable;
import static com.madgag.android.jgit.HarmonyFixInflater.checkHarmoniousRepose;
import static org.eclipse.jgit.lib.RepositoryCache.FileKey.resolve;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.madgag.android.IntentUtil;
import com.madgag.android.util.store.InstallAppDialogFragment;

import java.io.File;

import org.eclipse.jgit.util.FS;

public class DashboardActivity extends RoboSherlockFragmentActivity {

    private static final String TAG = "DashboardActivity";
    public static final String PICK_DIRECTORY_INTENT = "org.openintents.action.PICK_DIRECTORY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fixImageTilingOn(getSupportActionBar());
        Log.i(TAG, "Inflater zero-byte inflation (HARMONY-6637/Android #11755) fixed: " + checkHarmoniousRepose());
        try {
            addAccount(this);
        } catch (Exception e) {
            Log.w(TAG, "Unable to add account for syncing", e);
        }
        setContentView(R.layout.dashboard_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clone:
                startActivity(new Intent(this, CloneLauncherActivity.class));
                return true;
            case R.id.open_repo:
                if (isIntentAvailable(this, PICK_DIRECTORY_INTENT)) {
                    Intent intent = new Intent(PICK_DIRECTORY_INTENT);
                    intent.putExtra("org.openintents.extra.TITLE", "Select Git repository...");
                    startActivityForResult(intent, 0);
                } else {
                    askUserToInstallFileManager();
                }

                return true;
            case R.id.about_app:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void askUserToInstallFileManager() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        InstallAppDialogFragment.newInstance(R.drawable.icon, open_git_repository, install_file_manager,
                "org.openintents.filemanager").show(ft, "dialog");
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            File repoDir = getFile(data.getData());
            File gitdir = resolve(repoDir, FS.detect());
            if (gitdir == null) {
                Spanned messageHtml = fromHtml(getString(can_not_open_non_git_folder, boldCode(repoDir
                        .getAbsolutePath())));
                Toast.makeText(this, messageHtml, LENGTH_LONG).show();
            } else {
                startActivity(manageRepoIntent(gitdir));
            }
        }
    }


    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }
        return null;
    }

}
