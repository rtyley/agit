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
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import com.madgag.android.lazydrawables.ImageSession;
import org.eclipse.jgit.lib.PersonIdent;
import roboguice.inject.InjectorProvider;

import java.text.SimpleDateFormat;

import static com.madgag.agit.Time.timeSinceMS;
import static com.madgag.android.lazydrawables.gravatar.Gravatars.gravatarIdFor;

public class PersonIdentDetailView extends RelativeLayout {

	private static final String TAG = "PIDV";
    public static final String ITALIC_CLIPPING_BUFFER = " ";

    private PersonIdent ident;
    private final ImageView avatarView;
	private final TextView nameView, whenView;

	@Inject ImageSession avatarSession;

    public PersonIdentDetailView(Context context) {
		super(context);
		((InjectorProvider)context).getInjector().injectMembers(this);
		LayoutInflater.from(context).inflate(R.layout.person_ident_detail_view, this);
		
		avatarView = (ImageView) findViewById(R.id.person_ident_avatar);
		nameView = (TextView) findViewById(R.id.person_ident_name);
		whenView = (TextView) findViewById(R.id.person_ident_when);
	}
	
	
	public void setIdent(String title, PersonIdent ident) {
        this.ident = ident;
		Drawable avatar = avatarSession.get(gravatarIdFor(ident.getEmailAddress()));
		avatarView.setImageDrawable(avatar);
		nameView.setText(ident.getName()+ " "+ident.getEmailAddress());
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.FULL,java.text.DateFormat.FULL);

        String dateString= dateFormat.format(ident.getWhen());
		whenView.setText(dateString);
    }

    public PersonIdent getIdent() {
        return ident;
    }

    public void setIdent(PersonIdent ident) {
        this.ident = ident;
    }
}
