package com.madgag.agit;


import static com.madgag.agit.sync.AccountAuthenticatorService.addAccount;
import static com.madgag.android.jgit.HarmonyFixInflater.checkHarmoniousRepose;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

public class DashboardActivity extends RoboSherlockFragmentActivity {

    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Inflater zero-byte inflation (HARMONY-6637/Android #11755) fixed: " + checkHarmoniousRepose());
        try {
            addAccount(this);
        } catch (Exception e) {
            Log.w(TAG, "Unable to add account for syncing", e);
        }
        setContentView(R.layout.dashboard_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clone:
                startActivity(new Intent(this, CloneLauncherActivity.class));
                return true;
            case R.id.about_app:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
