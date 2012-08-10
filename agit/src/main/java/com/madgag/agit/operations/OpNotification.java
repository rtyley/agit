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

package com.madgag.agit.operations;

import static android.R.drawable.stat_sys_warning;

public class OpNotification {
    private final int icon;
    private final CharSequence tickerText, eventTitle, eventDetail;
    private final boolean successful;

    public OpNotification(int icon, CharSequence tickerText, CharSequence eventDetail) {
        this(icon, tickerText, tickerText, eventDetail, true);
    }

    public OpNotification(int icon, CharSequence tickerText, CharSequence eventTitle, CharSequence eventDetail) {
        this(icon, tickerText, eventTitle, eventDetail, true);
    }

    public OpNotification(int icon, CharSequence tickerText, CharSequence eventTitle, CharSequence eventDetail,
                          boolean successful) {
        this.icon = icon;
        this.tickerText = tickerText;
        this.eventTitle = eventTitle;
        this.eventDetail = eventDetail;
        this.successful = successful;
    }

    public int getDrawable() {
        return icon;
    }

    public CharSequence getTickerText() {
        return tickerText;
    }

    public CharSequence getEventTitle() {
        return eventTitle;
    }

    public CharSequence getEventDetail() {
        return eventDetail;
    }

    public static OpNotification alert(CharSequence eventTitle, CharSequence eventDetail) {
        return alert(eventDetail, eventTitle, eventDetail);
    }

    public static OpNotification alert(CharSequence tickerText, CharSequence eventTitle, CharSequence eventDetail) {
        return new OpNotification(stat_sys_warning, tickerText, eventTitle, eventDetail);
    }

}
