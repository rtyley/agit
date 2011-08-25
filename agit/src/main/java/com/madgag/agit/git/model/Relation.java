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

package com.madgag.agit.git.model;

import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;

import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static java.util.Arrays.asList;

public enum Relation {
	PARENT  {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			return (List) asList(commit.getParents());
		}
	},
	CHILD {
		@SuppressWarnings({ "unchecked" })
		public List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			List<PlotCommit<PlotLane>> commitChildren = newArrayListWithExpectedSize(commit.getParentCount());
			for (int i=0;i<commit.getChildCount();++i) {
				commitChildren.add(commit.getChild(i));
			}
			return commitChildren;
		}
	};
	
	public abstract List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit);
}
