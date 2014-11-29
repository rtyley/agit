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

import com.madgag.agit.sync.SyncRepoManager;

public class SettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        addPreferencesFromResource(R.layout.settings_activity);
        ListPreference syncFreq = (ListPreference) findPreference(getString(R.string.setting_sync_frequency_key));
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        setSummary();
        syncFreq.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = (String)o;
                final SharedPreferences.Editor edtr = prefs.edit();

                if (value.equals(getString(R.string.setting_sync_frequency_daily))) {
                    // Check if our previous value was the same thing, so we can set it in the time
                    // picker dialog.
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
                            String subTitle = "Sync daily at " + hourOfDay + ":" + (minOfHour < 10 ? "0" : "") + minOfHour;
                            edtr.putInt(getString(R.string.setting_sync_frequency_daily_hour_key), hourOfDay);
                            edtr.putInt(getString(R.string.setting_sync_frequency_daily_min_key), minOfHour);
                            edtr.putString(getString(R.string.setting_sync_frequency_subtitle_key), subTitle);
                            edtr.commit();

                            SyncRepoManager manager = new SyncRepoManager();
                            manager.setDailySync(SettingsActivity.this, hourOfDay, minOfHour);
                            SettingsActivity.this.setSummary();
                        }
                    };

                    // Display new dialog with the available options.
                    TimePickerDialog timePicker = new TimePickerDialog(SettingsActivity.this, timeListener, hourDefault, minDefault, false);
                    timePicker.setTitle(getString(R.string.setting_sync_frequency_daily_title));
                    timePicker.show();
                } else {
                    // Loop to find out what the index of the chosen value is in the array - it should be the same
                    // as the chosen value in the array of possible choices (see comment in strings.xml).
                    String[] indexArray = getResources().getStringArray(R.array.setting_sync_choices_values);
                    String[] subtitleArray = getResources().getStringArray(R.array.setting_sync_choices);
                    int subtitleIndex = 0;
                    for (String s : indexArray) {
                        if (s.equals(o)) {
                            break;
                        }
                        subtitleIndex++;
                    }

                    String subTitle = subtitleArray[subtitleIndex];
                    edtr.putString(getString(R.string.setting_sync_frequency_subtitle_key), subTitle);
                    edtr.commit();
                    SyncRepoManager manager = new SyncRepoManager();
                    manager.cancelDailySync(SettingsActivity.this);
                }

                SettingsActivity.this.setSummary();

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

    /**
     * Displays the value stored in the preference setting_sync_frequency_subtitle_key in the summary line of the
     * list preference, so the user doesn't have to click through to view which option is selected.
     */
    private void setSummary() {
        ListPreference syncFreq = (ListPreference) findPreference(getString(R.string.setting_sync_frequency_key));
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        syncFreq.setSummary(prefs.getString(getString(R.string.setting_sync_frequency_subtitle_key),
                getString(R.string.setting_instruction_sync_frequency)));
    }

    private static final String TAG = "SettingsActivity";
}
