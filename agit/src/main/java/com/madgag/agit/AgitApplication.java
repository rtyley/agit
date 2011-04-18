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

import static java.util.Arrays.asList;

import java.util.List;

import roboguice.application.RoboApplication;
import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import com.google.inject.Module;
import com.madgag.agit.ssh.jsch.GUIUserInfo;

public class AgitApplication extends RoboApplication {
	
	public AgitApplication() {}
	
	public AgitApplication(Context context) {
		attachBaseContext(context);
	}
	
	public AgitApplication(Instrumentation instrumentation) {
		attachBaseContext(instrumentation.getTargetContext());
	}

	protected void addApplicationModules(List<Module> modules) {
		Log.i("AA", "Adding application modules...");
        modules.addAll(asList(new AgitModule(), new AgitProductionModule()));
    }
}
