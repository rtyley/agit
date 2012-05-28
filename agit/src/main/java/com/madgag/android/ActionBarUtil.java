package com.madgag.android;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.madgag.agit.R;

public class ActionBarUtil {

    public static boolean homewardsWith(Activity activity, Intent homeIntent) {
        homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(homeIntent);
        return true;
    }

    public static void setPrefixedTitleOn(ActionBar actionBar, CharSequence prefixTitle, CharSequence title) {
        View customView = actionBar.getCustomView();
        ((TextView) customView.findViewById(R.id.prefix_title)).setText(prefixTitle);
        ((TextView) customView.findViewById(R.id.title)).setText(title);
    }
}
