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

package com.madgag.agit.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.agit.R;
import com.madgag.android.lazydrawables.ImageSession;
import org.eclipse.jgit.lib.PersonIdent;
import roboguice.inject.InjectorProvider;

import static com.madgag.agit.util.Time.timeSinceMS;
import static com.madgag.agit.views.TextUtil.ITALIC_CLIPPING_BUFFER;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

public class PersonIdentView extends FrameLayout {
	
	private static final String TAG = "PIV";

    private PersonIdent ident;
    private final ImageView avatarView;
	private final TextView nameView, titleView, whenView;
	
	@Inject ImageSession avatarSession;

    public PersonIdentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((InjectorProvider)context).getInjector().injectMembers(this);
		LayoutInflater.from(context).inflate(com.madgag.agit.R.layout.person_ident_view, this);
		
		titleView = (TextView) findViewById(R.id.person_ident_title);
		avatarView = (ImageView) findViewById(R.id.person_ident_avatar);
		nameView = (TextView) findViewById(R.id.person_ident_name);
		whenView = (TextView) findViewById(R.id.person_ident_when);
	}
	
	
	public void setIdent(final String title, final PersonIdent ident) {
        this.ident = ident;
        titleView.setText(title);
		Drawable avatar = avatarSession.get(gravatarIdFor(ident.getEmailAddress()));
		avatarView.setImageDrawable(avatar);
		nameView.setText(ident.getName()+ ITALIC_CLIPPING_BUFFER);
		whenView.setText(timeSinceMS(ident.getWhen().getTime())+ITALIC_CLIPPING_BUFFER);
        setClickable(true);
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
                Log.d(TAG,"Clicked "+v);
                final PersonIdentDetailView view = new PersonIdentDetailView(getContext());
                view.setIdent(title, ident);
                Dialog dialog = new AlertDialog.Builder(getContext()).setView(view).show();
                view.setClickable(true);
                view.setOnClickListener(close(dialog));
            }
        });
    }

    private OnClickListener close(final Dialog dialog) {
        return new OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        };
    }

    public PersonIdent getIdent() {
        return ident;
    }

    public void setIdent(PersonIdent ident) {
        this.ident = ident;
    }
}
