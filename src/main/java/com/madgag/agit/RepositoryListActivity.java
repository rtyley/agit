package com.madgag.agit;

import static android.content.Intent.ACTION_VIEW;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class RepositoryListActivity extends ListActivity {
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
            startActivity(new Intent(this, Clone.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String gitdir=((TextView)v.findViewById(android.R.id.text1)).getText().toString();
        Uri gitdirUri = Uri.fromFile(new File(gitdir));
        startActivity(new Intent(ACTION_VIEW, gitdirUri, this, RepositoryManagementActivity.class));
//        String action = getIntent().getAction();
//        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
//            // The caller is waiting for us to return a note selected by
//            // the user.  The have clicked on one, so return it now.
//            setResult(RESULT_OK, new Intent().setData(uri));
//        } else {
//            // Launch activity to view/edit the currently selected item
//            startActivity(new Intent(Intent.ACTION_EDIT, uri));
//        }
    }

}
