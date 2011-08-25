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

package com.madgag.agit.git;

import org.eclipse.jgit.revwalk.*;

import static org.eclipse.jgit.lib.Constants.*;

public class GitObjects {
	public static <T> T evaluate(RevObject revObject, GitObjectFunction<T> f) {
		switch (revObject.getType()) {
			case OBJ_COMMIT:
				return f.apply((RevCommit) revObject);
			case OBJ_TREE:
				return f.apply((RevTree) revObject);
			case OBJ_BLOB:
				return f.apply((RevBlob) revObject);
			case OBJ_TAG:
				return f.apply((RevTag) revObject);
			default:
				throw new IllegalArgumentException("Git object type '"+revObject.getType()+"' unknown");
		}
	}
}
