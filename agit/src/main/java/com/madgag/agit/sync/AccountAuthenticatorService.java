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
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountAuthenticatorService";
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    static Method methodContentResolver_addPeriodicSync;

    static {
        initCompatibility();
    }

    ;

    private static void initCompatibility() {
        try {
            methodContentResolver_addPeriodicSync = ContentResolver.class.getMethod(
                    "addPeriodicSync", new Class[] { Account.class, String.class, Bundle.class, Long.TYPE });
        } catch (NoSuchMethodException nsme) {
            Log.w(TAG, "Periodic sync not available - addPeriodicSync() method not found.", nsme);
        }
    }

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
        configureSyncFor(account);
        return result;
    }

    private static void configureSyncFor(Account account) {
        Log.d(TAG, "Trying to configure account for sync...");
        setIsSyncable(account, AGIT_PROVIDER_AUTHORITY, 1);
        setSyncAutomatically(account, AGIT_PROVIDER_AUTHORITY, true);
        addPeriodicSyncIfSupported(account, 15 * 60);
    }

    private static void addPeriodicSyncIfSupported(Account account, long pollPeriodInSeconds) {
        if (methodContentResolver_addPeriodicSync == null) {
            return;
        }
        try {
            methodContentResolver_addPeriodicSync.invoke(null, account, AGIT_PROVIDER_AUTHORITY, new Bundle(),
                    pollPeriodInSeconds);
        } catch (InvocationTargetException ite) {
            /* unpack original exception when possible */
            Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                /* unexpected checked exception; wrap and re-throw */
                throw new RuntimeException(ite);
            }
        } catch (IllegalAccessException ie) {
            Log.e(TAG, "Unexpected exception adding periodic sync", ie);
        }
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