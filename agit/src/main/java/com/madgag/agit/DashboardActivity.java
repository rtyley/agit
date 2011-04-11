package com.madgag.agit;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.markupartist.android.widget.ActionBar;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class DashboardActivity extends RoboActivity {
    private static final String TAG = "DA";

    @InjectView(R.id.actionbar) ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        actionBar.setHomeLogo(R.drawable.actionbar_agit_logo);
    }

    // used by dashboard.xml
    public void startCloneLaunchActivity(View v) {
        startActivity(new Intent(this, CloneLauncherActivity.class));
    }
}
