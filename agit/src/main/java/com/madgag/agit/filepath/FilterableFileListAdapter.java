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

package com.madgag.agit.filepath;

import static com.google.common.collect.Lists.newArrayList;
import static com.madgag.agit.R.layout.file_list_item;
import static com.madgag.android.listviews.ReflectiveHolderFactory.reflectiveFactoryFor;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;
import static java.lang.System.currentTimeMillis;
import android.content.Context;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.google.common.base.Stopwatch;
import com.madgag.agit.FileViewHolder;
import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FilterableFileListAdapter extends ViewHoldingListAdapter<FilePath> implements Filterable {

    private static final String TAG = "FilterableFileListAdapter";

    /**
     * Lock used to modify the content of mObjects. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the filter ArrayFilter is used. mObjects will then only contain the filtered values.
    private List<FilePath> originalValues;
    private final List<FilePath> items;
    private final AtomicReference<FilePathMatcher> visibleFilePathMatcher;

    private CachingFilePathListMatcher cachingFilePathListMatcher;
    private Filter filter;

    public FilterableFileListAdapter(final List<FilePath> items, Context context, AtomicReference<FilePathMatcher>
            visibleFilePathMatcher) {
        super(items, viewInflatorFor(context, file_list_item), reflectiveFactoryFor(FileViewHolder.class,
                visibleFilePathMatcher));
        this.items = items;
        this.visibleFilePathMatcher = visibleFilePathMatcher;
        cachingFilePathListMatcher = new CachingFilePathListMatcher(this.items);
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (originalValues == null) {
                        synchronized (mLock) {
                            originalValues = newArrayList(items);
                        }
                    }

                    List<FilePath> originalValuesCopy;
                    synchronized (mLock) {
                        originalValuesCopy = newArrayList(originalValues);
                    }

                    FilterResults results = new FilterResults();
                    if (TextUtils.isEmpty(constraint)) {
                        results.values = originalValuesCopy;
                        results.count = originalValuesCopy.size();
                    } else {
//                        String tn = "FLA." + originalValuesCopy.size() + "." + constraint + "." + currentTimeMillis();
//                        Debug.startMethodTracing(tn, 64 * 1024 * 1024);
                        Stopwatch stopwatch = new Stopwatch().start();
                        List<FilePath> matchingFiles = cachingFilePathListMatcher.get(constraint.toString());
                        Log.d(TAG, "Filtered with '" + constraint + "' to " + matchingFiles.size() + " files " +
                                stopwatch.stop());
//                        Debug.stopMethodTracing();

                        results.values = matchingFiles;
                        results.count = matchingFiles.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    visibleFilePathMatcher.set(TextUtils.isEmpty(constraint) ? null : new FilePathMatcher(constraint
                            .toString()));
                    setList((List<FilePath>) results.values);
                }
            };
        }
        return filter;
    }


}
