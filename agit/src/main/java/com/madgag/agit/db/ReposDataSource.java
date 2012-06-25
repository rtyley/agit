package com.madgag.agit.db;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.inject.Inject;
import com.madgag.agit.git.Repos;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class ReposDataSource {

    private static final String TAG = "ReposDataSource";

    private static final FS FILESYSTEM = FS.detect();

    private SQLiteDatabase database;

    @Inject
    public ReposDataSource(Context context) {
        database = new DatabaseHelper(context).getWritableDatabase();
    }


    public void registerReposInStandardDir() {
        for (File gitdir : Repos.reposInDefaultRepoDir()) {
            registerRepo(gitdir);
        }
    }

    public List<RepoRecord> getAllRepos() {
        registerReposInStandardDir();

        List<RepoRecord> allRepos = newArrayList();
        Set<Long> missingReposIds = newHashSet();

        Cursor cursor = database.query(DatabaseHelper.TABLE_REPOS,
                new String[] { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_GITDIR }, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RepoRecord repoRecord = cursorToRepo(cursor);
            Log.d(TAG, "Found " + repoRecord);
            File currentGitDir = RepositoryCache.FileKey.resolve(repoRecord.gitdir, FILESYSTEM);
            if (currentGitDir == null) {
                missingReposIds.add(repoRecord.id);
            } else {
                allRepos.add(repoRecord);
            }

            cursor.moveToNext();
        }
        cursor.close();

        Log.d(TAG, "Found " + allRepos.size() + " repos, " + missingReposIds.size() + " missing repos");
        for (Long repoId : missingReposIds) {
            Log.d(TAG, "Deleting missing repo...");
            database.delete(DatabaseHelper.TABLE_REPOS, DatabaseHelper.COLUMN_ID + " = " + repoId, null);
        }

        return allRepos;
    }

    private RepoRecord cursorToRepo(Cursor cursor) {
        return new RepoRecord(cursor.getLong(0), new File(cursor.getString(1)));
    }

    public void registerRepo(File gitdir) {
        database.replace(DatabaseHelper.TABLE_REPOS, null, contentValuesFor(gitdir));
    }

    private ContentValues contentValuesFor(File gitdir) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_GITDIR, gitdir.getAbsolutePath());
        return values;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "agit.db";
        private static final int DATABASE_VERSION = 1;

        public static final String TABLE_REPOS = "repo";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_GITDIR = "gitdir";


        private static final String DATABASE_CREATE = "create table " + TABLE_REPOS +
                "(" + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_GITDIR + " text unique not null);";

        @Inject
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
