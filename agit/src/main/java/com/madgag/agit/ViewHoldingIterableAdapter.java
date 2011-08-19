/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.madgag.android.listviews.ViewCreator;
import com.madgag.android.listviews.ViewFactory;
import com.madgag.android.listviews.ViewHolderFactory;

import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ViewHoldingIterableAdapter<T> extends BaseAdapter {

    public static final int BUFFER_SIZE = 10;
    private final List<T> itemList = newArrayList();
    private final Iterator<T> itemIterator;
    private final ViewFactory<T> viewFactory;


    public ViewHoldingIterableAdapter(Iterator<T> itemIterator, ViewFactory<T> viewFactory) {
        this.itemIterator = itemIterator;
        this.viewFactory = viewFactory;
        fetchMoreData(BUFFER_SIZE*2);
    }

    public static <T> ViewHoldingIterableAdapter<T> create(
            Iterator<T> itemIterator,
            ViewCreator c,
            ViewHolderFactory<T> vhf) {
        return new ViewHoldingIterableAdapter<T>(itemIterator, new ViewFactory<T>(c,vhf));
    }

	@Override
	public boolean hasStableIds() {
		return true;
	}

	public int getCount() {
		return itemList.size();
	}

	public T getItem(int index) {
        if (index + BUFFER_SIZE > itemList.size() && itemIterator.hasNext()) {
            fetchMoreData(index + (BUFFER_SIZE*2));
        }
		return itemList.get(index);
	}

    private void fetchMoreData(int targetBufferEnd) {
        while (itemIterator.hasNext() && targetBufferEnd > itemList.size()) {
            itemList.add(itemIterator.next());
        }
        notifyDataSetChanged();
    }

    public long getItemId(int i) {
		return getItem(i).hashCode();
	}
	
	public View getView(int index, View view, ViewGroup parent) {
        return viewFactory.getView(view, getItem(index));
	}
}