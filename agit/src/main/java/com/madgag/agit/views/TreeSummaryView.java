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

import static com.madgag.agit.R.drawable.tree_icon;
import android.view.View;
import android.widget.TextView;

import com.madgag.agit.R;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

public class TreeSummaryView extends OSV<RevTree> {

    TextView treeTextView;

    public void setObject(RevTree tree, View view, Repository repo) {
        TreeWalk treeWalk = new TreeWalk(repo);
        StringBuilder sb = new StringBuilder();
        try {
            int treeIndex = treeWalk.addTree(tree);
            while (treeWalk.next()) {
                ObjectId newObjectId = treeWalk.getObjectId(treeIndex);
                String rawPath = new String(treeWalk.getRawPath());
                sb.append(rawPath).append(" - ");
                //System.out.println(newObjectId+" rawPath="+rawPath+" subTree="+ tw.isSubtree());
            }
            ((TextView) view.findViewById(R.id.osv_tree_description)).setText(sb);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public int iconId() {
        return tree_icon;
    }

    @Override
    public int layoutId() {
        return R.layout.osv_tree_summary_view;
    }

    @Override
    public CharSequence getTypeName() {
        return "Tree";
    }
}
