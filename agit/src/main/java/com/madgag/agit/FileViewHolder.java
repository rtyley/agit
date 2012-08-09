/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;


import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static android.text.style.CharacterStyle.wrap;
import static com.madgag.agit.R.id.commit_subject;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.madgag.agit.filepath.FilePathMatcher;
import com.madgag.agit.filepath.FilterableFileListAdapter;
import com.madgag.android.listviews.ViewHolder;

import java.util.concurrent.atomic.AtomicReference;

public class FileViewHolder implements ViewHolder<CharSequence> {

    private static ForegroundColorSpan highlightStyle = new ForegroundColorSpan(0x880000ff);

    private final TextView detail;
    private final AtomicReference<FilePathMatcher> filePathMatcher;

    public FileViewHolder(View v, AtomicReference<FilePathMatcher> filePathMatcher) {
        this.filePathMatcher = filePathMatcher;
        detail = (TextView) v.findViewById(commit_subject);
    }

    public void updateViewFor(CharSequence fileName) {
        FilePathMatcher currentFPM = filePathMatcher.get();
        if (currentFPM!=null) {
            Spannable highlightedFilePath = new SpannableStringBuilder(fileName);
            for (int index : currentFPM.match(fileName)) {
                highlightedFilePath.setSpan(wrap(highlightStyle), index, index + 1, SPAN_INCLUSIVE_INCLUSIVE);
            }
            fileName = highlightedFilePath;
        }
        detail.setText(fileName);
    }
}
