package com.madgag.android;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static android.graphics.Shader.TileMode.REPEAT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
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

    public static void fixImageTilingOn(ActionBar actionBar) {
        //This is a workaround for http://b.android.com/15340 from http://stackoverflow.com/a/5852198/132047
        if (Build.VERSION.SDK_INT < ICE_CREAM_SANDWICH) {
            Resources resources = actionBar.getThemedContext().getResources();
            BitmapDrawable bg = (BitmapDrawable) resources.getDrawable(R.drawable.actionbar_background);
            bg.setTileModeXY(REPEAT, REPEAT);

            actionBar.setBackgroundDrawable(bg);
        }
    }
}
