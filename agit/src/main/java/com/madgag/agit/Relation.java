package com.madgag.agit;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.util.Map;

import org.eclipse.jgit.revplot.PlotCommit;
import org.eclipse.jgit.revplot.PlotLane;

public enum Relation {
	PARENT  {
		@Override
		Map<String, PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			Map<String, PlotCommit<PlotLane>> commitParents = newHashMapWithExpectedSize(commit.getParentCount());
			for (int i=0;i<commit.getParentCount();++i) {
				PlotCommit parent = (PlotCommit) commit.getParent(i);
				commitParents.put(parent.getName(), parent);
			}
			return commitParents;
		}
	},
	CHILD {
		@Override
		Map<String, PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit) {
			Map<String, PlotCommit<PlotLane>> commitChildren = newHashMapWithExpectedSize(commit.getParentCount());
			for (int i=0;i<commit.getChildCount();++i) {
				PlotCommit child = commit.getChild(i);
				commitChildren.put(child.getName(), child);
			}
			return commitChildren;
		}
	};
	
	abstract Map<String, PlotCommit<PlotLane>> relationsOf(PlotCommit<PlotLane> commit);
}
