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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.madgag.agit.guice.OperationScoped;
import com.madgag.android.blockingprompt.BlockingPromptService;
import com.madgag.android.blockingprompt.PromptBroker;

import android.content.ComponentName;

public class AgitProductionModule extends AbstractModule {

	@Override
    protected void configure() {
		bind(BlockingPromptService.class).to(PromptBroker.class).in(OperationScoped.class);
    }

    @Provides @Named("authAgent")
    ComponentName authAgentComponentName() {
        return null;
    }
}
