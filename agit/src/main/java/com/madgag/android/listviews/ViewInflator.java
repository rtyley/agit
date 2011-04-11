package com.madgag.android.listviews;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ViewInflator implements ViewCreator {
    private final LayoutInflater inflater;
    private final int resId;

    public ViewInflator(LayoutInflater inflater, int resId) {
        this.inflater = inflater;
        this.resId = resId;
    }

    public View createBlankView() {
        return inflater.inflate(resId, null);
    }

    public static ViewInflator viewInflatorFor(Context context, int resId) {
        return new ViewInflator(LayoutInflater.from(context), resId);

    }
}
