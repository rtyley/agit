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

package com.madgag.android.blockingprompt;

import android.os.Handler;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.madgag.agit.guice.RepositoryScoped;


/*
- ALL GATs for a single Repo should share the same BlockingPromptService, which blocks on showing a prompt until the current prompt has returned.
- When the handler receives a prompt request, it needs to know if an RMA is currently displaying. If it is, then it can poke the RMA to display, otherwise show the notification.
- When an RMA starts, it needs to be able to get a reference to the current data required by the BlockingPromptService, e.g. the ResponseInterface
- When an RMA ends, it needs to be able to tell someone that it's died. (because the handler thing needs to be able to route display requests properly)
*/
@RepositoryScoped
public class PromptUIRegistry {
    //private final PromptBroker promptHelper;
    private final PromptUI statusBarUI;
    private PromptUI activityPromptUI;
    private String TAG = "PromptUIRegistry";
    private final Handler uiThreadHandler;
    private final PromptBroker promptBroker;

    @Inject
    public PromptUIRegistry(
            @Named("uiThread") Handler uiThreadHandler,
            @Named("status-bar") PromptUI statusBarUI,
            PromptBroker promptBroker) {
        this.uiThreadHandler = uiThreadHandler;
        this.promptBroker = promptBroker;
        Log.d(TAG,"uiThreadHandler="+uiThreadHandler);
        this.statusBarUI = statusBarUI;
    }

    public void displayPrompt() {
        uiThreadHandler.post(new Runnable() {
            public void run() {
                uiThreadBroadcastOfPrompt();
            }
        });
    }

    private void uiThreadBroadcastOfPrompt() {
        PromptUI activeUI = activeUI();
        Log.d(TAG, "Broadcasting to activeUI="+activeUI);
        if (activeUI!= statusBarUI) {
            statusBarUI.clearPrompt();
        }
        activeUI.acceptPrompt(promptBroker);
    }

    private PromptUI activeUI() {
        return activityPromptUI ==null? statusBarUI : activityPromptUI;
    }

    public void setActivityPromptUI(PromptUI activityPromptUI) {
        this.activityPromptUI = activityPromptUI;
        displayPromptIfNecessary();
    }

    public void clearActivityUIProvider(PromptUI providerToClear) {
        if (activityPromptUI == providerToClear) {
            setActivityPromptUI(null);
        }
    }

    private void displayPromptIfNecessary() {
        if (promptBroker.hasPrompt()) {
            uiThreadBroadcastOfPrompt();
        }
    }
}
