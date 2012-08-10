/*
 * Copyright (c) 2011, 2012 Roberto Tyley
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
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit.views;

import static roboguice.RoboGuice.getBaseApplicationInjector;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ViewUtil {
    public static void whileStillInContextScopeLayoutAndInject(ViewGroup view, int layoutId) {
        Context context = view.getContext();
        LayoutInflater.from(context).inflate(layoutId, view);

        // already in the context scope, can't re-enter it with RoboGuice.getInjector(context).injectMembers(view)
        getBaseApplicationInjector((Application) context.getApplicationContext()).injectMembers(view);
    }
}