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

import static android.content.ContentResolver.setIsSyncable;
import static android.content.ContentResolver.setSyncAutomatically;
import static com.madgag.agit.sync.Constants.AGIT_ACCOUNT_NAME;
import static com.madgag.agit.sync.Constants.AGIT_ACCOUNT_TYPE;
import static com.madgag.agit.sync.Constants.AGIT_PROVIDER_AUTHORITY;
import static com.madgag.agit.sync.Constants.AUTHTOKEN_TYPE;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.madgag.agit.R;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountAuthenticatorService";
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    public AccountAuthenticatorService() {
        super();
    }

    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    private AccountAuthenticatorImpl getAuthenticator() {
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new AccountAuthenticatorImpl(this);
        return sAccountAuthenticator;
    }

    public static Bundle addAccount(Context ctx) {
        Bundle result = null;
        Account account = new Account(AGIT_ACCOUNT_NAME, AGIT_ACCOUNT_TYPE);
        AccountManager am = AccountManager.get(ctx);
        if (am.addAccountExplicitly(account, null, null)) {
            result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        int syncFreq = Integer.parseInt(prefs.getString("setting_sync_frequency", "15"));

        Resources res = ctx.getResources();
        int dailySentinel = Integer.parseInt(res.getString(R.string.setting_sync_frequency_daily));
        if (syncFreq == dailySentinel) {
            int hourOfDay = prefs.getInt(res.getString(R.string.setting_sync_frequency_daily_hour_key), -1);
            int minOfHour = prefs.getInt(res.getString(R.string.setting_sync_frequency_daily_min_key), -1);

            Log.d(TAG, "Configuring sync to run at " + hourOfDay + "h" + minOfHour);
            configureSyncFor(account, syncFreq, hourOfDay, minOfHour);
        }

        configureSyncFor(account, syncFreq);
        return result;
    }

    private static void configureSyncFor(Account aAccount, int aSyncFreq) {
        configureSyncFor(aAccount, aSyncFreq, -1, -1);
    }

    private static void configureSyncFor(Account aAccount, int aSyncFreq, int aHourOfDay, int aMinOfHour) {

        if (aSyncFreq < 1) {
            setIsSyncable(aAccount, AGIT_PROVIDER_AUTHORITY, 0);
            ContentResolver.removePeriodicSync(aAccount, AGIT_PROVIDER_AUTHORITY, new Bundle());
            return;
        }


        Log.d(TAG, "Trying to configure account for sync at rate of " + aSyncFreq + " minutes");
        setIsSyncable(aAccount, AGIT_PROVIDER_AUTHORITY, 1);
        setSyncAutomatically(aAccount, AGIT_PROVIDER_AUTHORITY, true);
        ContentResolver.addPeriodicSync(aAccount, AGIT_PROVIDER_AUTHORITY, new Bundle(), (long) (aSyncFreq * 60));
    }

    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            mContext = context;
        }


        /*
        *  The user has requested to add a new account to the system.  We return an intent that will launch our login
         *  screen if the user has not logged in yet,
        *  otherwise our activity will just pass the user's credentials on to the account manager.
        */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
                                 String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Log.d(TAG, "addAccount " + accountType + " authTokenType=" + authTokenType);
            return null;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
            return null;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
                                   Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            if (authTokenType.equals(AUTHTOKEN_TYPE)) {
                return "FooBooHurrah";
            }
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                                  String[] features) throws NetworkErrorException {
            final Bundle result = new Bundle();
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
            return result;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
            return null;
        }
    }
}