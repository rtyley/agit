package com.madgag.agit;

import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

import org.eclipse.jgit.lib.PersonIdent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madgag.android.lazydrawables.ImageSession;

public class PersonIdentView extends RelativeLayout {
	
	private static final String TAG = "PIV";
	
	private final ImageView avatarView;
	private final TextView nameView, titleView;
	
	public PersonIdentView(Context context, AttributeSet attrs) {
		super(context, attrs);	
		LayoutInflater.from(context).inflate(R.layout.person_ident_view, this);
		
		titleView = (TextView) findViewById(R.id.person_ident_title);
		avatarView = (ImageView) findViewById(R.id.person_ident_avatar);
		nameView = (TextView) findViewById(R.id.person_ident_name);
	}
	
	
	public void setIdent(ImageSession<String,Bitmap> is, String title, PersonIdent ident) {
		Drawable avatar = is.get(gravatarIdFor(ident.getEmailAddress()));
		titleView.setText(title);
		avatarView.setImageDrawable(avatar);
		nameView.setText(ident.getName());
	}	
}
