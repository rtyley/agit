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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

public class SuggestedReposListAdapter extends BaseAdapter {
	private final Context m_context;
	private final LayoutInflater m_inflater;
	private List<SuggestedRepo> suggestedRepos;

	public SuggestedReposListAdapter(final Context context, List<SuggestedRepo> suggestedRepos) {
		this.suggestedRepos = suggestedRepos;
		m_context = context;
		m_inflater = LayoutInflater.from(m_context);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	public long getItemId(int i) {
		return suggestedRepos.get(i).hashCode();
	}
	
	public int getCount() {
		return suggestedRepos.size();
	}

	public Object getItem(int index) {
		return suggestedRepos.get(index);
	}

	public View getView(int index, View convertView, ViewGroup parent) {
		
		convertView = createViewIfNecessary(convertView);
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		SuggestedRepo repo = suggestedRepos.get(index);
		holder.updateViewFor(repo);

		return convertView;
	}

	private View createViewIfNecessary(View convertView) {
		if (convertView == null) {
			convertView = m_inflater.inflate(android.R.layout.two_line_list_item, null);
			convertView.setTag(new ViewHolder(convertView));
		}
		return convertView;
	}


	public class ViewHolder {
		private final TextView title,detail;
		
		public ViewHolder(View v) {
			title = (TextView) v.findViewById(android.R.id.text1);
			detail = (TextView) v.findViewById(android.R.id.text2);
		}
		
		public void updateViewFor(SuggestedRepo repo) {
			title.setText(repo.getName());
            detail.setText(repo.getURI());
		}
	}
}