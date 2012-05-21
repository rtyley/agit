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

package com.madgag.agit.ssh.jsch;

import static com.madgag.agit.operations.OpNotification.alert;
import static com.madgag.agit.operations.OpPrompt.prompt;
import static com.madgag.agit.operations.OpPrompt.promptYesOrNo;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.jcraft.jsch.UserInfo;
import com.madgag.android.blockingprompt.BlockingPromptService;

public class GUIUserInfo implements UserInfo {
    private final static String TAG = "GUIUI";

    private final Provider<BlockingPromptService> blockingPrompt;

    @Inject
    public GUIUserInfo(Provider<BlockingPromptService> blockingPrompt) {
        this.blockingPrompt = blockingPrompt;
    }

    private String password, passphrase;

    public String getPassphrase() {
        return passphrase;
    }

    public String getPassword() {
        return password;
    }

    public boolean promptPassphrase(String msg) {
        Log.d(TAG, "promptPassphrase : " + msg);
        passphrase = blockingPrompt.get().request(prompt(String.class, alert("Passphrase required",
                "Please enter your passphrase", msg)));
        return passphrase != null;
    }


    public boolean promptPassword(String msg) {
        Log.d(TAG, "promptPassword : " + msg);
        password = blockingPrompt.get().request(prompt(String.class, alert("Password required",
                "Please enter your password", msg)));
        return password != null;
    }

    public boolean promptYesNo(String msg) {
        Log.d(TAG, "promptYesNo : " + msg);
        Boolean bool = blockingPrompt.get().request(promptYesOrNo(alert("SSH", msg)));
        return bool != null ? bool : false;
    }

    public void showMessage(String msg) {
        Log.i(TAG, "Should show this: " + msg);
    }

}
