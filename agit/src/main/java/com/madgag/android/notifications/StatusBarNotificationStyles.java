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


import static android.R.color.black;
import static android.content.Context.WINDOW_SERVICE;
import android.app.Notification;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

public class StatusBarNotificationStyles {

    private final static String TEXT_SEARCH_PREFIX = "MARKER:";
    private final static String TEXT_SEARCH_TEXT = TEXT_SEARCH_PREFIX+"Text";
    private final static String TEXT_SEARCH_TITLE = TEXT_SEARCH_PREFIX+"Title";

    private Context context;
    private TextAppearance textAppearance, titleAppearance;
    private DisplayMetrics displayMetrics;

    public StatusBarNotificationStyles(Context context) {
        this.context = context;
        displayMetrics = getDisplayMetrics();
        discoverStyle();
    }

    private void discoverStyle() {
        try {
            Notification n = new Notification();
            n.setLatestEventInfo(context, TEXT_SEARCH_TITLE, TEXT_SEARCH_TEXT, null);

            new NotificationViewSearcher(n, context, new Predicate<View>() {
                public boolean apply(View view) {
                    if (view instanceof TextView) {
                        TextView tv = (TextView) view;
                        if (tv.getText().toString().startsWith(TEXT_SEARCH_PREFIX)) {
                            int textColour = tv.getTextColors().getDefaultColor();
                            TextAppearance appearance = new TextAppearance(textColour, tv.getTextSize()/displayMetrics.scaledDensity);

                            if (TEXT_SEARCH_TEXT.equals(tv.getText().toString())) {
                                textAppearance = appearance;
                            } else {
                                titleAppearance = appearance;
                            }
                            return !needToSearch();
                        }
                    }
                    return false;
                }
            }).search();

        } catch (Exception e) {
            if (titleAppearance == null)
                titleAppearance = new TextAppearance(black, 12);

            if (textAppearance == null)
                textAppearance = new TextAppearance(black, 11);
        }
    }

    public TextAppearance getTextAppearance() {
        return textAppearance;
    }

    public TextAppearance getTitleAppearance() {
        return titleAppearance;
    }

    private boolean needToSearch() {
        return textAppearance == null || titleAppearance == null;
    }

    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static class TextAppearance {
        public final int colour;
        public final float size;

        public TextAppearance(int colour, float size) {
            this.colour = colour;
            this.size = size;
        }

        public void setOn(RemoteViews remoteViews, int viewId) {
            remoteViews.setTextColor(viewId, colour);
            remoteViews.setFloat(viewId, "setTextSize", size);
        }
    }
}
