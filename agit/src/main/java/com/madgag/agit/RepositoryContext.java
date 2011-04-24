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

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.app.Activity;
import android.util.Log;
import com.madgag.agit.guice.RepositoryScope;

public class RepositoryContext
      //  implements IndexChangedListener, RefsChangedListener
{
    private @Inject
    RepositoryScope scope;
    private @Inject @Named("gitdir") File gitdir;
    private final Activity activity;
	private final String tag;

    @Inject
	public RepositoryContext(Activity activity) {
        this.activity = activity;
        //this.rsa = (RepoScopedActivityLifecycle) activity;
		this.tag = activity.getClass().getSimpleName();
	}
	
	public void onResume() {
		if (!gitdir.exists()) {
			Log.d(tag, "Finishing activity as gitdir gone : "+ gitdir);
			activity.finish();
			return;
		}
        enterScope();
        try {
            //addListeners();
		    activity.onContentChanged();

            //rsa.onRepoScopedResume();
        } finally {
            exitScope();
        }
	}



    public void onPause() {
        enterScope();
        try {
            //removeListeners();
            // rsa.onRepoScopedPause();
        } finally {
            exitScope();
        }
	}

	public void onDestroy() {
        enterScope();
        try {
            //RepositoryCache.close(repository);
            // rsa.onRepoScopedDestroy();
        } finally {
            exitScope();
        }
	}

    private void enterScope() {
        //scope.enterWithRepoGitdir(gitdir);
    }
    private void exitScope() {
        // scope.exit();
    }


//  private final List<ListenerHandle> listeners = newArrayList();
//  private final Handler handler = new Handler();
//
//    private final Runnable onContentChangeRunnable = new Runnable() {
//		public void run() { activity.onContentChanged(); }
//	};
//	private void removeListeners() {
//		Log.d(tag, "Removing listeners for " + describe(repository));
//		for (ListenerHandle handle : listeners) {
//			handle.remove();
//		}
//		listeners.clear();
//	}
	
//	private void addListeners() {
//		removeListeners();
//		Log.d(tag, "Adding listeners for "+describe(repository));
//		ListenerList listenerList = repository.getListenerList();
//		listeners.add(listenerList.addIndexChangedListener(this));
//		listeners.add(listenerList.addRefsChangedListener(this));
//	}
//
//	public void onIndexChanged(IndexChangedEvent event) {
//		handler.post(onContentChangeRunnable);
//	}
//
//	public void onRefsChanged(RefsChangedEvent event) {
//		handler.post(onContentChangeRunnable);
//	}


}
