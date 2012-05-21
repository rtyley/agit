package com.madgag.android.views;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

public class FixEclairTabHost extends TabHost {

    public FixEclairTabHost(Context context) {
        super(context);
    }

    public FixEclairTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        if (getCurrentView() != null) {
            super.dispatchWindowFocusChanged(hasFocus);
        }
    }

}
