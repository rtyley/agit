package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RDTypeListAdapter<E> extends BaseAdapter {
	private final LayoutInflater layoutInflater;
	private final RepoDomainType<E> rdt;
	private final List<E> list;
	private static String TAG="RDTLA";

	public RDTypeListAdapter(LayoutInflater layoutInflater, RepoDomainType<E> rdt) {
		this.layoutInflater = layoutInflater;
		this.rdt = rdt;
		list = newArrayList(rdt.getAll());
		Log.i(TAG, "Got "+rdt.name()+" list of size "+list.size());
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int index) {
		return list.get(index);
	}

	public long getItemId(int i) {
		return i;
	}
	
	public View getView(int index, View convertView, ViewGroup parent) {
		
		convertView = createViewIfNecessary(convertView);
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		holder.updateViewFor(list.get(index));

		return convertView;
	}

	private View createViewIfNecessary(View convertView) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, null);
			convertView.setTag(new ViewHolder(convertView));
		}
		return convertView;
	}


	public class ViewHolder {
		private final TextView title,detail;
		
		public ViewHolder(View v) {
			detail = (TextView) v.findViewById(android.R.id.text1);
			title = (TextView) v.findViewById(android.R.id.text2);
		}
		
		public void updateViewFor(E e) {
			detail.setText(rdt.idFor(e));
			title.setText(rdt.shortDescriptionOf(e));
		}
	}
}