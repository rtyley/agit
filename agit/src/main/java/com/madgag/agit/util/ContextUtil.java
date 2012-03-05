package com.madgag.agit.util;

import android.content.Context;
import android.view.ContextThemeWrapper;

public class ContextUtil {

    /**
     *see http://stackoverflow.com/a/8547823/438886
     */
    public static Context wrapWithDialogContext(Context context) {
        return new ContextThemeWrapper(context, android.R.style.Theme_Dialog);
    }
}
