package com.madgag.android.notifications;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import static java.lang.System.currentTimeMillis;
import android.R;
import android.app.Notification;
import android.content.Context;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.common.base.Predicate;
import com.madgag.agit.operations.OpNotification;

public class ProgressNotification {

    public final Notification notification;
    public final TextViewIds textViewIds;

    public ProgressNotification(Notification notification, TextViewIds textViewIds) {
        this.notification = notification;
        this.textViewIds = textViewIds;
    }

    public static class TextViewIds {
        public final int title;
        public final int text;
        public final int info;

        public TextViewIds(int title, int text, int info) {
            this.title = title;
            this.text = text;
            this.info = info;
        }
    }

    public static class Builder {
        public static final String INFO_MARKER_TEXT = "MARKER:INFO";

        private final Context context;
        private final StatusBarNotificationStyles notificationStyles;

        public Builder(Context context) {
            this.context = context;
            this.notificationStyles = new StatusBarNotificationStyles(context);
        }

        public ProgressNotification generateRawProgressNotification(OpNotification opn,int backupLayout, TextViewIds backupLayoutViewIds) {
            return (SDK_INT >= ICE_CREAM_SANDWICH)?
                    generateUsingICS(opn):generatePreICS(opn, backupLayout, backupLayoutViewIds);
        }


        private ProgressNotification generateUsingICS(OpNotification opn) {
            Notification n;
            n = new Notification.Builder(context)
                    .setSmallIcon(opn.getDrawable())
                    .setContentTitle(opn.getEventTitle())
                    .setContentText(opn.getEventDetail())
                    .setProgress(1, 0, true)
                    .setContentInfo(INFO_MARKER_TEXT)
                    .getNotification();
            ContentInfoTextViewSearchPredicate searchPredicate = new ContentInfoTextViewSearchPredicate();
            new NotificationViewSearcher(n, context, searchPredicate).search();
            TextViewIds viewIds = new TextViewIds(R.id.title, 0, searchPredicate.getContentInfoTextViewId());
            ProgressNotification progressNotification = new ProgressNotification(n, viewIds);
            n.contentView.setTextViewText(viewIds.info, "");
            return progressNotification;
        }

        private ProgressNotification generatePreICS(OpNotification opn, int layout, TextViewIds viewIds) {
            Notification n = new Notification(opn.getDrawable(), opn.getTickerText(), currentTimeMillis());

            n.contentView = new RemoteViews(context.getApplicationContext().getPackageName(), layout);

            notificationStyles.getTitleAppearance().setOn(n.contentView, viewIds.title);
            StatusBarNotificationStyles.TextAppearance textAppearance = notificationStyles.getTextAppearance();
            textAppearance.setOn(n.contentView, viewIds.text);
            textAppearance.setOn(n.contentView, viewIds.info);

            n.contentView.setTextViewText(viewIds.title, opn.getEventTitle());
            n.contentView.setTextViewText(viewIds.text, opn.getEventDetail());
            n.contentView.setProgressBar(android.R.id.progress, 1, 0, true);
            return new ProgressNotification(n, viewIds);
        }

        private static class ContentInfoTextViewSearchPredicate implements Predicate<View> {
            private int contentInfoTextViewId;

            public boolean apply(View view) {
                if (view instanceof TextView) {
                    String text = ((TextView) view).getText().toString();
                    if (text.equals(INFO_MARKER_TEXT)) {
                        contentInfoTextViewId = view.getId();
                        return true;
                    }
                }
                return false;
            }

            public int getContentInfoTextViewId() {
                return contentInfoTextViewId;
            }
        }
    }

}
