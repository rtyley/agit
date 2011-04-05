/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.connectbot.service;

import java.util.concurrent.Semaphore;

import android.os.Handler;
import android.os.Message;

import android.util.Log;
import com.madgag.agit.BlockingPromptService;
import com.madgag.agit.ResponseInterface;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.OpPrompt;

/**
 * Helps provide a relay for prompts and responses between a possible user
 * interface and some underlying service.
 *
 * @author jsharkey, rtyley
 */
public class PromptHelper implements ResponseInterface, BlockingPromptService {

	private final Handler handler;
    private final Runnable promptBroadcast;

    private Semaphore promptToken = new Semaphore(1);
	private Semaphore promptResponse = new Semaphore(0);

	private OpPrompt<?> opPrompt;

	private Object response = null;
    private static final String TAG = "PH";

    public PromptHelper(Handler handler, Runnable promptBroadcast) {
        this.handler = handler;
        this.promptBroadcast = promptBroadcast;
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

            Log.d(TAG, "handler=" + handler);
			// notify any parent watching for live events
            handler.post(promptBroadcast);
			// Message.obtain(handler).sendToTarget();

			// acquire lock until user passes back value
			promptResponse.acquire();

			response = popResponse();
		} catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException while waiting for "+opPrompt, e);
        } finally {
			promptToken.release();
		}

		return (T) response;
	}

//	/**
//	 * Request a string response from parent. This is a blocking call until user
//	 * interface returns a value.
//	 * @return string user has entered
//	 */
//	public String requestStringPrompt(OpNotification opNotification) {
//		String value = null;
//		try {
//			value = this.request(new OpPrompt<String>(opNotification, String.class));
//		} catch(Exception e) {
//		}
//		return value;
//	}
//
//	/**
//	 * Request a boolean response from parent. This is a blocking call until user
//	 * interface returns a value.
//	 * @return choice user has made (yes/no)
//	 */
//	public Boolean requestBooleanPrompt(OpNotification opNotification) {
//		Boolean value = null;
//		try {
//			value = this.request(new OpPrompt<Boolean>(opNotification, Boolean.class));
//		} catch(Exception e) {
//		}
//		return value;
//	}

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
        return opPrompt!=null;
    }
}
