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

package com.madgag.agit;

import static com.madgag.agit.sync.AccountAuthenticatorService.addAccount;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TimePicker;

public class SettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        addPreferencesFromResource(R.layout.settings_activity);
        ListPreference syncFreq = (ListPreference) findPreference(getString(R.string.setting_sync_frequency_key));
        syncFreq.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = (String)o;
                if (value.equals(getString(R.string.setting_sync_frequency_daily))) {
                    // Check if our previous value was the same thing, so we can set it in the time
                    // picker dialog.
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    int hourDefault = 12;
                    int minDefault = 0;
                    String prevVal = prefs.getString(getString(R.string.setting_sync_frequency_key), "-1");
                    if (prevVal.equals(o)) {
                        hourDefault = prefs.getInt(getString(R.string.setting_sync_frequency_daily_hour_key), hourDefault);
                        minDefault = prefs.getInt(getString(R.string.setting_sync_frequency_daily_min_key), minDefault);
                    }

                    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minOfHour) {
                            SharedPreferences.Editor edtr = prefs.edit();
                            edtr.putInt(getString(R.string.setting_sync_frequency_daily_hour_key), hourOfDay);
                            edtr.putInt(getString(R.string.setting_sync_frequency_daily_min_key), minOfHour);
                            edtr.commit();
                        }
                    };

                    // Display new dialog with the available options.
                    TimePickerDialog timePicker = new TimePickerDialog(SettingsActivity.this, timeListener, hourDefault, minDefault, false);
                    timePicker.setTitle(getString(R.string.setting_sync_frequency_daily_title));
                    timePicker.show();
                }

                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            addAccount(this);
        } catch (Exception e) {
            Log.w(TAG, "Unable to re-add account for syncing after preference changes", e);
        }
    }

    private ListPreference mSyncPreferences;
    private static final String TAG = "SettingsActivity";
}
