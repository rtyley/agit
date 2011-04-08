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

import static com.madgag.agit.RepositoryManagementActivity.manageRepoIntent;

import java.io.File;

import roboguice.activity.RoboListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class RepositoryListActivity extends RoboListActivity {
	public static final String TAG = "RepositoryListActivity";
	private final static int CLONE_ID=Menu.FIRST;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Query for all people contacts using the Contacts.People convenience class.
        // Put a managed wrapper around the retrieved cursor so we don't have to worry about
        // requerying or closing it as the activity changes state.
        Cursor mCursor = managedQuery(GitInfoProvider.CONTENT_URI, null, null, null, null);

        // Now create a new list adapter bound to the cursor.
        // SimpleListAdapter is designed for binding to a Cursor.
        ListAdapter adapter = new SimpleCursorAdapter(
                this, // Context.
                android.R.layout.simple_list_item_1,
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {"gitdir"},           // Array of cursor columns to bind to.
                new int[] {android.R.id.text1});  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        setListAdapter(adapter);
    }
    
//    @Override
//    protected void onResume() {
//    	super.onResume();
//    	getListAdapter().
//    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, CLONE_ID, 0, R.string.clone_menu_option).setShortcut('0', 'c');

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case CLONE_ID:
            startActivity(new Intent(this, CloneLauncherActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String gitdir=((TextView)v.findViewById(android.R.id.text1)).getText().toString();
        startActivity(manageRepoIntent(new File(gitdir)));
    }

}
