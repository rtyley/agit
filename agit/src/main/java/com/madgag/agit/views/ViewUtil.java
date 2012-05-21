package com.madgag.agit.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import roboguice.inject.InjectorProvider;

public class ViewUtil {
    public static void injectFor(ViewGroup view, int layoutId) {
        Context context = view.getContext();
        LayoutInflater.from(context).inflate(layoutId, view);
        ((InjectorProvider) context).getInjector().injectMembers(view);
    }
}