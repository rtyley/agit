package com.madgag.android.listviews;


import android.view.View;

public interface ViewHolderFactory<T> {

    ViewHolder<T> createViewHolderFor(View view);
}
