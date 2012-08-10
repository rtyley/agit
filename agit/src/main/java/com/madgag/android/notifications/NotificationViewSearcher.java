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

package com.madgag.android.notifications;

import android.app.Notification;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.base.Predicate;

public class NotificationViewSearcher {

    private final Notification notification;
    private final Context context;
    private final Predicate<View> searchPredicate;

    public NotificationViewSearcher(Notification notification, Context context, Predicate<View> searchPredicate) {
        this.notification = notification;
        this.context = context;
        this.searchPredicate = searchPredicate;
    }

    public void search() {
        LinearLayout viewParent = new LinearLayout(context);
        ViewGroup eventView = (ViewGroup) notification.contentView.apply(context, viewParent);
        search(eventView);
        viewParent.removeAllViews();
    }

    private boolean search(ViewGroup group) {
        int childCount = group.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            View view = group.getChildAt(i);
            boolean finishSearch = searchPredicate.apply(view);

            if (finishSearch)
                return true;

            if (view instanceof ViewGroup)
                search((ViewGroup) view);
        }
        return false;
    }
}
