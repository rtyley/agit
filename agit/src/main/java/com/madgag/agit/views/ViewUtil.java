package com.madgag.agit.views;

import static roboguice.RoboGuice.getBaseApplicationInjector;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ViewUtil {
    public static void whileStillContextScopeInjectFor(ViewGroup view, int layoutId) {
        Context context = view.getContext();
        LayoutInflater.from(context).inflate(layoutId, view);

        // we are already in the context scope, can't re-enter it, so...
        getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(view);
    }
}