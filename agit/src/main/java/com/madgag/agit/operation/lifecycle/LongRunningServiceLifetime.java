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

package com.madgag.agit.operation.lifecycle;

import static android.app.Notification.FLAG_ONGOING_EVENT;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.madgag.agit.R;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.Progress;
import com.madgag.android.notifications.NotificationViewSearcher;
import com.madgag.android.notifications.ProgressNotification;

// Stateful? Relates to a specific operation?
public class LongRunningServiceLifetime implements OperationLifecycleSupport {

    public final static String TAG = "LRSL";

    private final RepoNotifications repoNotifications;
    private final Service service;

    private Notification ongoingNotification;
    private StatusBarProgressView statusBarProgressView;

    public LongRunningServiceLifetime(RepoNotifications repoNotifications, Service service) {
        this.repoNotifications = repoNotifications;
        this.service = service;
    }

    public void startedWith(OpNotification startNotification) {
        ProgressNotification.TextViewIds backupLayoutViewIds = new ProgressNotification.TextViewIds(android.R.id.title, R.id.operation_long_url, R.id.status_text);
        ProgressNotification pn = new ProgressNotification.Builder(service)
                .generateRawProgressNotification(startNotification, R.layout.operation_progress, backupLayoutViewIds);

        ongoingNotification = pn.notification;
        ongoingNotification.contentIntent = repoNotifications.manageGitRepo;
        ongoingNotification.flags = ongoingNotification.flags | FLAG_ONGOING_EVENT;
        statusBarProgressView = new StatusBarProgressView(ongoingNotification.contentView, pn.textViewIds.info);
        foregroundServiceWith(ongoingNotification); //definitely the job of this class, right?!
    }

    public void publish(Progress p) {
        Log.i(TAG, "Publishing " + p);
        statusBarProgressView.publish(p);
        repoNotifications.notifyOngoing(ongoingNotification);
    }

    public void error(OpNotification errorNotification) {
    }

    public void success(OpNotification successNotification) {
    }

    public void completed(OpNotification completionNotification) {
        removeServiceFromForeground();
        repoNotifications.cancelOngoingNotification();
        repoNotifications.notifyCompletionWith(completionNotification);
    }

    private void foregroundServiceWith(Notification ongoingNotification) {
        Log.i(TAG, "Starting " + ongoingNotification + " in the foreground...");
        try {
            service.startForeground(repoNotifications.getOngoingNotificationId(), ongoingNotification);
        } catch (NullPointerException e) {
            Log.d(TAG, "startForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117");
        }
    }

    private void removeServiceFromForeground() {
        try {
            // Actually, we only want to call this if ALL threads are completed
            service.stopForeground(true);
        } catch (NullPointerException e) {
            Log.d(TAG, "stopForeground NPE - see http://code.google.com/p/android/issues/detail?id=12117", e);
        }
    }
}
