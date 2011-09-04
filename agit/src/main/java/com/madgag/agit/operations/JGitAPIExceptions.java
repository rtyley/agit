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

package com.madgag.agit.operations;


import com.google.common.base.Throwables;
import com.jcraft.jsch.JSchException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import static com.google.common.base.Throwables.getRootCause;

public class JGitAPIExceptions {

	public static RuntimeException exceptionWithFriendlyMessageFor(Exception e) {
		Throwable cause = getRootCause(e);
		String message = cause.getMessage();
		if (cause instanceof JSchException) {
			message = "SSH: "+message;
		}
		return new RuntimeException(message,cause);
	}
}
