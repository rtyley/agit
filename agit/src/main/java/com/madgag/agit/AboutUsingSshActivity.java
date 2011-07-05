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

import com.markupartist.android.widget.ActionBar;

import static com.madgag.agit.R.string.about_activity_title;

public class AboutUsingSshActivity extends MarkdownActivityBase {

    @Override
    protected String markdownFile() {
        return "Using-SSH.markdown";
    }

    @Override
    protected void configureActionBar(ActionBar actionBar) {
        actionBar.setTitle(R.string.using_ssh_activity_title);
    }
}