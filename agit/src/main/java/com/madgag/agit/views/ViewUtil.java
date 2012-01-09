package com.madgag.agit.views;

import static roboguice.RoboGuice.getInjector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ViewUtil {
    public static void injectFor(ViewGroup view, int layoutId) {
        Context context = view.getContext();
        LayoutInflater.from(context).inflate(layoutId, view);
        // getInjector(context).injectMembers(view);
    }
}