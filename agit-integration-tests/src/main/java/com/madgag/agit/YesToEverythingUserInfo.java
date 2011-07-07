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

import roboguice.config.AbstractAndroidModule;
import android.util.Log;

import com.google.inject.Module;
import com.jcraft.jsch.UserInfo;

public final class YesToEverythingUserInfo implements UserInfo {

	public static Module module() {
		return new AbstractAndroidModule() {
			@Override
			protected void configure() {
				bind(UserInfo.class).toInstance(new YesToEverythingUserInfo());
			}
		};
	}

	private String TAG = "YTEUI";

	public void showMessage(String msg) {
		Log.i(TAG, msg);
	}

	public boolean promptYesNo(String msg) {
		Log.i(TAG, msg);
		return true;
	}

	public boolean promptPassword(String arg0) {
		return false;
	}

	public boolean promptPassphrase(String arg0) {
		return false;
	}

	public String getPassword() {
		return null;
	}

	public String getPassphrase() {
		return null;
	}
}