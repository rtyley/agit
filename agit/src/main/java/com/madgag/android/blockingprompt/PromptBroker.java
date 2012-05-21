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

import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.OpPrompt;

import java.util.concurrent.Semaphore;

/**
 * PromptBroker is an adaptation of org.connectbot.service.PromptHelper
 * by Jeffrey Sharkey. PromptHelper is released under the Apache License as
 * part of ConnectBot, the open-source SSH client for Android.
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 */

@RepositoryScoped
public class PromptBroker implements ResponseInterface, BlockingPromptService {

    private Semaphore promptToken = new Semaphore(1);
    private Semaphore promptResponse = new Semaphore(0);

    private OpPrompt<?> opPrompt;

    private Object response = null;
    private static final String TAG = "PB";
    private final Provider<PromptUIRegistry> promptUIRegistry;

    @Inject
    public PromptBroker(Provider<PromptUIRegistry> promptUIRegistry) {
        this.promptUIRegistry = promptUIRegistry;
    }

    /**
     * Set an incoming value from an above user interface. Will automatically
     * notify any waiting requests.
     */
    public void setResponse(Object value) {
        response = value;
        opPrompt = null;
        promptResponse.release();
    }

    /**
     * Return the internal response value just before erasing and returning it.
     */
    protected Object popResponse() {
        Object value = response;
        response = null;
        return value;
    }


    /**
     * Request a prompt response from parent. This is a blocking call until user
     * interface returns a value.
     * Only one thread can call this at a time. cancelPrompt() will force this to
     * immediately return.
     */
    public <T> T request(OpPrompt<T> opPrompt) {
        Object response = null;
        Log.d(TAG, "request for " + opPrompt);
        try {
            promptToken.acquire();
            this.opPrompt = opPrompt;

            promptUIRegistry.get().displayPrompt();

            // acquire lock until user passes back value
            promptResponse.acquire();

            response = popResponse();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException while waiting for " + opPrompt, e);
        } finally {
            promptToken.release();
        }

        return (T) response;
    }

    /**
     * Cancel an in-progress prompt.
     */
    public void cancelPrompt() {
        if (!promptToken.tryAcquire()) {
            // A thread has the token, so try to interrupt it
            response = null;
            promptResponse.release();
        } else {
            // No threads have acquired the token
            promptToken.release();
        }
    }


    public OpPrompt<?> getOpPrompt() {
        return opPrompt;
    }

    public boolean hasPrompt() {
        return opPrompt != null;
    }
}
