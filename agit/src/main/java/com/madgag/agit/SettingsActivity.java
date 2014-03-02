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
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        addPreferencesFromResource(R.layout.settings_activity);
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

    private static final String TAG = "SettingsActivity";
}
