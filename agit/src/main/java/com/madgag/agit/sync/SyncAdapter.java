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

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import roboguice.inject.ContextScope;

@Singleton
class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "AgitSync";

    @Inject
    ContextScope contextScope;
    @Inject
    SyncCampaignFactory syncCampaignFactory;

    private SyncCampaign currentSyncCampaign = null;

    @Inject
    public SyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        cancelAnyCurrentCampaign();
        contextScope.enter(getContext());
        try {
            currentSyncCampaign = syncCampaignFactory.createCampaignFor(syncResult);
            currentSyncCampaign.run();
        } finally {
            contextScope.exit(getContext());
        }
    }

    @Override
    public void onSyncCanceled() {
        cancelAnyCurrentCampaign();
    }

    private void cancelAnyCurrentCampaign() {
        if (currentSyncCampaign != null) {
            currentSyncCampaign.cancel();
        }
    }

}
