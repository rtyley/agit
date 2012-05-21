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
