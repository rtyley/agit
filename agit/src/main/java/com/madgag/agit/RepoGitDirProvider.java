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

import java.io.File;

import com.google.inject.internal.Nullable;
import roboguice.inject.InjectExtra;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;


public class RepoGitDirProvider implements Provider<File> {

	@InjectExtra(value="gitdir", optional=true) String gitdirStringFromContext;
	@Inject(optional=true) @Nullable
    @Named(value="gitdir-from-operation") File gitdirFromOperation;
	
	public File get() {
		if (gitdirFromOperation!=null) {
			return gitdirFromOperation;
		}
		return new File(gitdirStringFromContext);
	}

}
