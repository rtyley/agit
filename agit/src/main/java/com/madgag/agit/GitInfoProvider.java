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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

public class GitInfoProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.madgag.agit.gitinfoprovider/repos");

    public static final String TAG = "RepoInfoProvider";

    private File reposDir;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        reposDir = new File(Environment.getExternalStorageDirectory(), "git-repos");
        if (!reposDir.exists() && !reposDir.mkdirs()) {
            Log.e(TAG, "Could not create default repos dir : " + reposDir.getAbsolutePath());
            return false;
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        MatrixCursor matrixCursor = new MatrixCursor(new String[] { "_id", "gitdir" });
        for (File repoDir : reposDir.listFiles()) {
            File gitdir = RepositoryCache.FileKey.resolve(repoDir, FS.detect());
            if (gitdir != null) {
                matrixCursor.newRow().add(gitdir.hashCode()).add(gitdir);
            }
        }
        return matrixCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
