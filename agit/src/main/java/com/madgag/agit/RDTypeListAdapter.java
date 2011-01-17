package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RDTypeListAdapter<E> extends BaseAdapter {
	private Context m_context;
	private LayoutInflater m_inflater;
	private final RepoDomainType<E> rdt;
	private final List<E> list;

	public RDTypeListAdapter(final Context context, RepoDomainType<E> rdt) {
		m_context = context;
		this.rdt = rdt;
		list = newArrayList(rdt.getAll());
		m_inflater = LayoutInflater.from(m_context);
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
			convertView = m_inflater.inflate(android.R.layout.simple_list_item_2, null);
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