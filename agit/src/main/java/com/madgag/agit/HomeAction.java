package com.madgag.agit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import com.markupartist.android.widget.ActionBar;

public class HomeAction implements ActionBar.Action {
    private final Activity activity;

    public HomeAction(Activity activity) {
        this.activity = activity;
    }

    public int getDrawable() {
        return R.drawable.icon;
    }

    public void performAction(View view) {
        activity.startActivity(new Intent(activity, DashboardActivity.class));
        activity.finish();
    }
    
}
