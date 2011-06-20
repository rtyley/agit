package com.madgag.agit;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import com.google.inject.Inject;
import com.markupartist.android.widget.ActionBar;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class HomeAction implements ActionBar.Action {
    private final Activity activity;

    @Inject
    public HomeAction(Activity activity) {
        this.activity = activity;
    }

    public int getDrawable() {
        return R.drawable.icon;
    }

    public void performAction(View view) {
        Intent intent = new Intent(activity, DashboardActivity.class);
        intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }
    
}
