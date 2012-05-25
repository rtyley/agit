package com.madgag.android;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.app.Activity;
import android.content.Intent;

public class ActionBarUtil {

    public static boolean homewardsWith(Activity activity, Intent homeIntent) {
        homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(homeIntent);
        return true;
    }
}
