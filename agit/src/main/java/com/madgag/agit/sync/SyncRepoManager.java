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

package com.madgag.agit.sync;

import static com.madgag.agit.sync.Constants.AGIT_ACCOUNT_NAME;
import static com.madgag.agit.sync.Constants.AGIT_ACCOUNT_TYPE;
import static com.madgag.agit.sync.Constants.AGIT_PROVIDER_AUTHORITY;
import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Manager class for daily synchronization settings. Because periodic sync at time-of-day intervals is handled
 * differently than periodic sync on a regular schedule, we use this class as a receiver of alerts on a regular
 * interval from AlarmManager.
 */
public class SyncRepoManager extends BroadcastReceiver {

    public SyncRepoManager() {

    }

    /**
     * Setup a sync at a specific hour and minute of the day. Every effort is made to make the sync happen at the time
     * of day specified, but it's not guaranteed. Because we use inexact repeating alarms, it will never happen before
     * the specified time, but it may happen quite a while afterwards. This is because the Android operating system
     * tries to phase alarms so they don't happen at the same time.
     *
     * @param aContext The context from which the daily sync is being set up.
     * @param aHour The hour of the day (from 0 to 24) at which the sync should happen
     * @param aMin The minute of the hour at which the sync should happen.
     */
    public static void setDailySync(Context aContext, int aHour, int aMin) {
        AlarmManager mgr =(AlarmManager)aContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.madgag.agit.sync.SET_DAILY_SYNC");
        PendingIntent pend = PendingIntent.getBroadcast(aContext, 0, intent, 0);

        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, aHour);
        updateTime.set(Calendar.MINUTE, aMin);
        long msTimeToRepeat = updateTime.getTimeInMillis();

        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, msTimeToRepeat, AlarmManager.INTERVAL_DAY, pend);
    }

    /**
     * Cancel a daily sync already set up.
     *
     * @param aContext The context from which the daily sync cancellation is being requested.
     */
    public static void cancelDailySync(Context aContext) {
        Log.d(TAG, "Canceling daily sync");
        AlarmManager mgr =(AlarmManager)aContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.madgag.agit.sync.SET_DAILY_SYNC");
        PendingIntent pend = PendingIntent.getBroadcast(aContext, 0, intent, 0);
        mgr.cancel(pend);
    }

    @Override
    public void onReceive(Context aContext, Intent aIntent) {
        Calendar rightNow = Calendar.getInstance();
        long timeInMs = rightNow.getTimeInMillis();
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long sinceMidnight = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);
        int hoursSinceMidnight = (int)(sinceMidnight / (60*60*1000));
        boolean isPM = hoursSinceMidnight > 12;
        int clockHour = hoursSinceMidnight % 12;
        int minutesSinceMidnight = (int)((sinceMidnight % (hoursSinceMidnight * (60 * 60 * 1000))) / (60*1000));

        long msAtMidnight = (rightNow.getTimeInMillis() + offset) - sinceMidnight;

        Log.d(TAG, "onReceive called at " + clockHour + ":" + minutesSinceMidnight + " " + (isPM ? "pm" : "am"));
        doSyncNow();
    }

    /**
     * Triggers a manual synchronization of the repositories. This the main event that is performed
     * after onReceive() is seen.
     */
    private void doSyncNow() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account account = new Account(AGIT_ACCOUNT_NAME, AGIT_ACCOUNT_TYPE);
        ContentResolver.requestSync(account, AGIT_PROVIDER_AUTHORITY, settingsBundle);
    }

    private static final String TAG = "SyncRepoManager";
}
