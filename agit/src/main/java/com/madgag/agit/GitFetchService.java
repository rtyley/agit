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

import com.madgag.agit.git.TransportFactory;
import com.madgag.agit.guice.OperationScoped;
import com.madgag.agit.operations.*;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.transport.*;

import android.util.Log;

import com.google.inject.Inject;
import com.jcraft.jsch.JSchException;

import java.util.Collection;

@OperationScoped
public class GitFetchService {
	
	private static String TAG = "GFS";
	
	private final TransportFactory transportFactory;
    private final ProgressListener<Progress> progressListener;
    @Inject RepoUpdateBroadcaster repoUpdateBroadcaster;
    @Inject CancellationSignaller cancellationSignaller;
	
	@Inject
	public GitFetchService(TransportFactory transportFactory, ProgressListener<Progress> progressListener) {
		this.transportFactory = transportFactory;
        this.progressListener = progressListener;
    }

	public FetchResult fetch(RemoteConfig remote, Collection<RefSpec> toFetch) {
		Log.d(TAG, "About to run fetch : " + remote.getName()+" "+remote.getURIs());
		
		Transport transport = transportFactory.transportFor(remote);
		try {
			FetchResult fetchResult = transport.fetch(new MessagingProgressMonitor(progressListener, cancellationSignaller), toFetch);
			Log.d(TAG, "Fetch complete with : " + fetchResult);
            for (TrackingRefUpdate update : fetchResult.getTrackingRefUpdates()) {
                Log.d(TAG, "TrackingRefUpdate : " + update.getLocalName()+" old="+update.getOldObjectId()+" new="+update.getNewObjectId());
            }
            repoUpdateBroadcaster.broadcastUpdate();
			return fetchResult;
		} catch (NotSupportedException e) {
			throw new RuntimeException(e);
		} catch (TransportException e) {
			Log.e(TAG, "TransportException ", e);
			String message = e.getMessage();
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof JSchException) {
				message = "SSH: " + ((JSchException) cause).getMessage();
			}
			throw new RuntimeException(message, e);
		} finally {
			Log.d(TAG, "Closing transport " + transport);
			transport.close();
		}
	}
}
