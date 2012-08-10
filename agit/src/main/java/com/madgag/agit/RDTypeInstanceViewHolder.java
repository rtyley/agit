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

package com.madgag.agit;

import android.view.View;
import android.widget.TextView;

import com.madgag.agit.git.model.RepoDomainType;
import com.madgag.android.listviews.ViewHolder;

public class RDTypeInstanceViewHolder<B> implements ViewHolder<B> {
    private final TextView title, detail;
    private final RepoDomainType<B> rdt;

    public RDTypeInstanceViewHolder(RepoDomainType<B> rdt, View v) {
        this.rdt = rdt;
        detail = (TextView) v.findViewById(android.R.id.text1);
        title = (TextView) v.findViewById(android.R.id.text2);
    }

    public void updateViewFor(B e) {
        detail.setText(rdt.idFor(e));
        title.setText(rdt.shortDescriptionOf(e));
    }
}
