package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static java.util.Arrays.asList;

import java.util.List;

import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;

public enum Relation {
	PARENT  {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			return (List) asList(commit.getParents());
		}
	},
	CHILD {
		@SuppressWarnings({ "unchecked" })
		List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			List<PlotCommit<PlotLane>> commitChildren = newArrayListWithExpectedSize(commit.getParentCount());
			for (int i=0;i<commit.getChildCount();++i) {
				commitChildren.add(commit.getChild(i));
			}
			return commitChildren;
		}
	};
	
	abstract List<PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit);
}
