/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.views;

import static com.madgag.agit.R.drawable.tag_icon;
import static com.madgag.agit.R.id.message_text;
import static com.madgag.agit.R.id.tagged_object;
import static com.madgag.agit.R.id.tagger_ident;
import static com.madgag.agit.R.layout.osv_tag_summary_view;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class TagSummaryView extends OSV<RevTag> {

    private final static String TAG = "TSV";

    public void setObject(RevTag tag, View view, Repository repo) {
        ((PersonIdentView) view.findViewById(tagger_ident)).setIdent("Tagger", tag.getTaggerIdent());
        ((TextView) view.findViewById(message_text)).setText(tag.getFullMessage());
        try {
            RevObject taggedObject = new RevWalk(repo).parseAny(tag.getObject());
            ((ObjectSummaryView) view.findViewById(tagged_object)).setObject(taggedObject, repo);
            Log.d(TAG, "Successfully set taggedObject=" + taggedObject);
        } catch (IOException e) {
            Log.e(TAG, "Couldn't set the tagged object...", e);
            e.printStackTrace();
        }
    }

    @Override
    public int iconId() {
        return tag_icon;
    }

    @Override
    public int layoutId() {
        return osv_tag_summary_view;
    }

    @Override
    public CharSequence getTypeName() {
        return "Annotated Tag";
    }
}
