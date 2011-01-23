package com.madgag.agit;

import java.util.List;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.madgag.gravatar.android.GravatarProvider;
import com.madgag.gravatar.android.GravatarSession;

public class RevCommitListAdapter extends BaseAdapter {
	private final Context m_context;
	private final LayoutInflater m_inflater;
	private final GravatarSession gravatarSession;
	private List<RevCommit> commits;

	public RevCommitListAdapter(final Context context, List<RevCommit> commits) {
		this.commits = commits;
		m_context = context;
		gravatarSession=new GravatarSession(new GravatarProvider(),m_context.getResources(), 32);
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
			PersonIdent committer = commit.getCommitterIdent();
			commit_date.setText(commitDateTextFor(commit)+" - "+committer.getName());
			
			Drawable gravatarBitmap = gravatarSession.getGravatar(committer.getEmailAddress(), new Runnable() {
				public void run() {
					notifyDataSetChanged();
				}
			});
			gravatar.setImageDrawable(gravatarBitmap);

			
			commit_shortdesc.setText(commit.getShortMessage());
		}

		private String commitDateTextFor(RevCommit revCommit) {		
			long ms = System.currentTimeMillis() - (revCommit.getCommitTime()*1000L);
			long sec = ms / 1000;
			long min = sec / 60;
			long hour = min / 60;
			long day = hour / 24;
			String end;
			if (day > 0) {
				if (day == 1) {
					end = " day ago";
				} else {
					end = " days ago";
				}
				return (day + end);
			}
			if (hour > 0) {
				if (hour == 1) {
					end = " hour ago";
				} else {
					end = " hours ago";
				}
				return (hour + end);
			} 
			if (min > 0) {
				if (min == 1) {
					end = " minute ago";
				} else {
					end = " minutes ago";
				}
				return (min + end);
			} 
			if (sec == 1) {
				end = " second ago";
			} else {
				end = " seconds ago";
			}
			return (sec + end);
		}

	}
}