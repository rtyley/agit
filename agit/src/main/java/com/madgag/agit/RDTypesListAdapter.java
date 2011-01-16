package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;

public class RDTypesListAdapter extends BaseAdapter {
	private Context m_context;
	private LayoutInflater m_inflater;
	private List<RepoDomainType<?>> rdtList;

	public RDTypesListAdapter(final Context context, Repository repository) {
		rdtList = newArrayList(new RDTRemote(repository), new RDTBranch(repository), new RDTTag(repository));
		m_context = context;
		m_inflater = LayoutInflater.from(m_context);
	}

	public int getCount() {
		return rdtList.size();
	}

	public Object getItem(int index) {
		return rdtList.get(index);
	}

	public long getItemId(int i) {
		return i;
	}
	
	public View getView(int index, View convertView, ViewGroup parent) {
		
		convertView = createViewIfNecessary(convertView);
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		RepoDomainType<?> repoDomainType = rdtList.get(index);
		holder.updateViewFor(repoDomainType);
		return convertView;
	}

	private View createViewIfNecessary(View convertView) {
		if (convertView == null) {
			convertView = m_inflater.inflate(android.R.layout.simple_list_item_2, null);
			convertView.setTag(new ViewHolder(convertView));
		}
		return convertView;
	}


	public static class ViewHolder {
		private final TextView title,detail;
		
		public ViewHolder(View v) {
			detail = (TextView) v.findViewById(android.R.id.text1);
			title = (TextView) v.findViewById(android.R.id.text2);
		}
		
		public void updateViewFor(RepoDomainType<?> repoDomainType) {
			detail.setText(repoDomainType.conciseSummaryTitle());
			title.setText(repoDomainType.summariseAll());
		}
	}
}