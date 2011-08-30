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

import android.content.Context;
import android.text.*;
import android.text.style.AlignmentSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.madgag.agit.R;
import org.eclipse.jgit.lib.ObjectId;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static android.text.Html.fromHtml;
import static android.text.Layout.Alignment.ALIGN_CENTER;
import static android.widget.Toast.LENGTH_SHORT;
import static com.madgag.agit.views.TextUtil.centered;

public class ObjectIdView extends TextView {

	private static final String TAG = "OIV";
    private final ClipboardManager clipboardManager;

    public ObjectIdView(Context context, AttributeSet attrs) {
		super(context, attrs);
        clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
	}
	
	public void setObjectId(final ObjectId objectId) {
        setText(objectId.abbreviate(8).name());
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String idText = objectId.name();
                clipboardManager.setText(idText);
				String copiedText = getContext().getString(R.string.object_id_copied);
                String htmlMessage = "<small><small><b><tt>"+objectId.name()+"</tt></b></small><br />("+copiedText+")</small>";
                Toast.makeText(getContext(), centered(htmlMessage), LENGTH_SHORT).show();
            }
        });
    }



}
