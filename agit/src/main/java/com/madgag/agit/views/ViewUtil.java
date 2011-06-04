package com.madgag.agit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import roboguice.inject.InjectorProvider;

import static com.madgag.agit.R.id.iv_commit_list_item_gravatar;
import static com.madgag.agit.R.id.tv_commit_list_item_commit_date;
import static com.madgag.agit.R.id.tv_commit_list_item_shortdesc;
import static com.madgag.agit.R.layout.commit_summary_view;

public class ViewUtil {
    public static void injectFor(ViewGroup view, int layoutId) {
        Context context = view.getContext();
        LayoutInflater.from(context).inflate(layoutId, view);
        ((InjectorProvider) context).getInjector().injectMembers(view);
    }
}