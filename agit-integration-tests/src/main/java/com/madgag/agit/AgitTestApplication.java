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

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;
import com.google.inject.Module;

import java.util.List;

import static java.util.Arrays.asList;

public class AgitTestApplication extends AgitApplication {
	
	private static final String TAG = "AgitTestApplication";
	
	public AgitTestApplication(Instrumentation instrumentation) {
		super(instrumentation);
		Log.i(TAG,"GETTING CALLED with instrumentation...");
	}
	
	public AgitTestApplication(Context context) {
		super(context);
		Log.i(TAG,"REALLY GETTING CALLED!!");
	}
	
	protected void addApplicationModules(List<Module> modules) {
		modules.addAll(asList(
                new AgitModule(),
                new AgitIntegrationTestModule()));
	}
}