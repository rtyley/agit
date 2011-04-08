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

import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

import roboguice.inject.InjectorProvider;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.madgag.android.lazydrawables.ImageSession;

public class RevCommitListAdapter extends BaseAdapter {
	private final Context m_context;
	private final LayoutInflater m_inflater;
	private List<RevCommit> commits;

	@Inject ImageSession avatarSession;

	public RevCommitListAdapter(final Context context, List<RevCommit> commits) {
		((InjectorProvider)context).getInjector().injectMembers(this);
		this.commits = commits;
		m_context = context;
		m_inflater = LayoutInflater.from(m_context);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	public long getItemId(int i) {
		return commits.get(i).hashCode();
	}
	
	public int getCount() {
		return commits.size();
	}

	public Object getItem(int index) {
		return commits.get(index);
	}

	
	public void updateWith(List<RevCommit> commits) {
		this.commits=commits;
		notifyDataSetChanged();
	}

	public View getView(int index, View convertView, ViewGroup parent) {
		
		convertView = createViewIfNecessary(convertView);
		
		ViewHolder holder = (ViewHolder) convertView.getTag();
		
		RevCommit commit = commits.get(index);
		holder.updateViewFor(commit);

		return convertView;
	}

	private View createViewIfNecessary(View convertView) {
		if (convertView == null) {
			convertView = m_inflater.inflate(R.layout.rev_commit_list_item, null);
			convertView.setTag(new ViewHolder(convertView));
		}
		return convertView;
	}


	public class ViewHolder {
		private final TextView commit_shortdesc,commit_date;
		private final ImageView gravatar;
		
		public ViewHolder(View v) {
			commit_date = (TextView) v.findViewById(R.id.tv_commit_list_item_commit_date);
			commit_shortdesc = (TextView) v.findViewById(R.id.tv_commit_list_item_shortdesc);
			gravatar = (ImageView) v.findViewById(R.id.iv_commit_list_item_gravatar);
		}
		
		public void updateViewFor(RevCommit commit) {
			commit_date.setText(Time.timeSinceSeconds(commit.getCommitTime()));
			
			Drawable avatarBitmap = avatarSession.get(gravatarIdFor(commit.getAuthorIdent().getEmailAddress()));
			gravatar.setImageDrawable(avatarBitmap);

			commit_shortdesc.setText(commit.getShortMessage());
		}



	}
}