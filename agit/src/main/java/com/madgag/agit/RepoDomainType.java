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

import static android.R.layout.simple_list_item_2;
import static android.text.Html.fromHtml;
import static com.madgag.android.listviews.ViewInflator.viewInflatorFor;

import java.util.Collection;
import java.util.List;

import android.*;
import android.content.Context;
import android.view.View;
import com.madgag.android.listviews.ViewFactory;
import com.madgag.android.listviews.ViewHolder;
import com.madgag.android.listviews.ViewHolderFactory;
import org.eclipse.jgit.lib.Repository;

import android.content.Intent;

public abstract class RepoDomainType<E> { 
	
	protected final Repository repository;

	public RepoDomainType(Repository repository) {
		this.repository = repository;
	}
	
	public abstract String name();
	
	abstract List<E> getAll();
	
	public abstract CharSequence conciseSummaryTitle();
	
	abstract CharSequence conciseSummary(E e);
	
	abstract CharSequence shortDescriptionOf(E e);
	
	
	public CharSequence summarise(Collection<E> list) {
		if (list.isEmpty()) {
			return fromHtml("<i>« none »</i>");
		}
		StringBuilder sb = new StringBuilder();
		for (E e : list) {
			if (sb.length()>0) {
				sb.append(conciseSeparator());
			}
			sb.append(conciseSummary(e));
		}
		return sb.toString();
	}

	abstract String conciseSeparator();

	public CharSequence summariseAll() {
		return summarise(getAll());
	}

	public Intent listIntent() {
		return action(name()+".LIST").toIntent();
	}

	
	public Intent viewIntentFor(E e) {
		return action(name()+".VIEW").add(name(),idFor(e)).toIntent();
	}
	
	abstract String idFor(E e);

	private GitIntentBuilder action(String action) {
		return new GitIntentBuilder(action).repository(repository);
	}

}
