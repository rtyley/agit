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

package com.madgag.android.listviews;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class BigListAdapter<T> extends BaseAdapter {
	private final ViewCreator creator;
    private final ViewHolderFactory<T> viewHolderFactory;
	private List<T> itemList;

	public BigListAdapter(List<T> itemList, ViewCreator creator, ViewHolderFactory<T> viewHolderFactory) {
		this.itemList = itemList;
        this.creator = creator;
        this.viewHolderFactory = viewHolderFactory;
    }


	@Override
	public boolean hasStableIds() {
		return true;
	}
    
	public void setList(List<T> itemList) {
		this.itemList=itemList;
		notifyDataSetChanged();
	}

	public int getCount() {
		return itemList.size();
	}

	public T getItem(int index) {
		return itemList.get(index);
	}

	public long getItemId(int i) {
		return getItem(i).hashCode();
	}
	
	public View getView(int index, View view, ViewGroup parent) {
		if (view==null) {
            view = creator.createBlankView();
            view.setTag(viewHolderFactory.createViewHolderFor(view));
        }

		ViewHolder<T> holder = (ViewHolder<T>) view.getTag();

		holder.updateViewFor(itemList.get(index));
		return view;
	}
}