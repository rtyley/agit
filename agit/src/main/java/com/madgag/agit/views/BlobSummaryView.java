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

import android.view.View;
import android.widget.TextView;
import com.madgag.agit.R;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevBlob;

import java.io.IOException;

import static com.madgag.agit.R.drawable.commit_24;
import static com.madgag.agit.R.id.blob_size;
import static com.madgag.agit.R.layout.osv_blob_summary_view;

public class BlobSummaryView extends OSV<RevBlob> {

	public void setObject(RevBlob blob, View view, Repository repo) {
        try {
            long size = repo.open(blob).getSize();
            ((TextView)view.findViewById(blob_size)).setText(size+" bytes");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public int iconId() {
        return commit_24;
    }

    @Override
    public int layoutId() {
        return osv_blob_summary_view;
    }
}
