package com.madgag.agit;

import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

import java.io.File;
import java.util.List;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.madgag.android.lazydrawables.BitmapFileStore;
import com.madgag.android.lazydrawables.ImageProcessor;
import com.madgag.android.lazydrawables.ImageResourceDownloader;
import com.madgag.android.lazydrawables.ImageResourceStore;
import com.madgag.android.lazydrawables.ImageSession;
import com.madgag.android.lazydrawables.ScaledBitmapDrawableGenerator;
import com.madgag.android.lazydrawables.gravatar.GravatarBitmapDownloader;

public class RevCommitListAdapter extends BaseAdapter {
	private final Context m_context;
	private final LayoutInflater m_inflater;
	private final ImageSession<String, Bitmap> avatarSession;
	private List<RevCommit> commits;

	public RevCommitListAdapter(final Context context, List<RevCommit> commits) {
		this.commits = commits;
		m_context = context;
		ImageProcessor<Bitmap> imageProcessor = new ScaledBitmapDrawableGenerator(34, context.getResources());
		ImageResourceDownloader<String, Bitmap> downloader = new GravatarBitmapDownloader();
		File file = new File(Environment.getExternalStorageDirectory(),"gravagroovy");
		ImageResourceStore<String, Bitmap> imageResourceStore = new BitmapFileStore<String>(file);
		avatarSession=new ImageSession<String, Bitmap>(imageProcessor, downloader, imageResourceStore, context.getResources().getDrawable(R.drawable.loading_34_centred));
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
			PersonIdent author = commit.getAuthorIdent();
			commit_date.setText(commitDateTextFor(commit)+" - "+author.getName());
			
			Drawable gravatarBitmap = avatarSession.get(gravatarIdFor(author.getEmailAddress()));
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