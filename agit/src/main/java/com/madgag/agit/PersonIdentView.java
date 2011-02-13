package com.madgag.agit;

import static com.madgag.agit.Time.timeSinceMS;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

import org.eclipse.jgit.lib.PersonIdent;

import roboguice.inject.InjectorProvider;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.madgag.android.lazydrawables.ImageSession;

public class PersonIdentView extends RelativeLayout {
	
	private static final String TAG = "PIV";
	
	private final ImageView avatarView;
	private final TextView nameView, titleView, whenView;
	
	@Inject
	private ImageSession imos;
	
	public PersonIdentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((InjectorProvider)context).getInjector().injectMembers(this);
		LayoutInflater.from(context).inflate(R.layout.person_ident_view, this);
		
		titleView = (TextView) findViewById(R.id.person_ident_title);
		avatarView = (ImageView) findViewById(R.id.person_ident_avatar);
		nameView = (TextView) findViewById(R.id.person_ident_name);
		whenView = (TextView) findViewById(R.id.person_ident_when);
	}
	
	
	public void setIdent(String title, PersonIdent ident) {
		Log.i(TAG, "imos="+imos);
		titleView.setText(title);
		Drawable avatar = imos.get(gravatarIdFor(ident.getEmailAddress()));
		avatarView.setImageDrawable(avatar);
		nameView.setText(ident.getName());
		whenView.setText(timeSinceMS(ident.getWhen().getTime()));
	}	
}
