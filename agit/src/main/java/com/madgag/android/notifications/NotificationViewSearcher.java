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
