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

package com.madgag.agit.ssh;

import static android.text.Html.fromHtml;
import static com.google.common.collect.Maps.newHashMap;
import static com.madgag.agit.R.string.ask_host_key_ok;
import static com.madgag.agit.R.string.ask_host_key_ok_ticker;
import static com.madgag.agit.operations.OpNotification.alert;
import static com.madgag.agit.operations.OpPrompt.promptYesOrNo;
import static com.madgag.agit.util.DigestUtils.encodeHex;
import static com.madgag.agit.util.DigestUtils.md5;
import static com.madgag.agit.views.TextUtil.centered;
import static com.madgag.android.HtmlStyleUtil.boldCode;
import static java.lang.Boolean.TRUE;
import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import com.madgag.android.blockingprompt.BlockingPromptService;

import java.util.Arrays;
import java.util.Map;

@Singleton
public class CuriousHostKeyRepository implements HostKeyRepository {
    Map<String, byte[]> knownKeys = newHashMap();
    private final Application application;
    private final Provider<BlockingPromptService> blockingPromptService;

    @Inject
    public CuriousHostKeyRepository(Application application, Provider<BlockingPromptService> blockingPromptService) {
        this.application = application;
        this.blockingPromptService = blockingPromptService;
    }

    public int check(String host, byte[] key) {
        byte[] knownKey = knownKeys.get(host);
        if (knownKey == null) {
            return userCheckKey(host, key);
        }
        return Arrays.equals(knownKey, key) ? OK : CHANGED;
    }

    private int userCheckKey(String host, byte[] key) {
        String keyFingerprint = "<small>" + boldCode(encodeHex(md5(key))) + "</small><br />";
        String ticker = application.getString(ask_host_key_ok_ticker, boldCode(host));
        String message = application.getString(ask_host_key_ok, boldCode(host) + "<br />", keyFingerprint);
        boolean userConfirmKeyGood = TRUE == blockingPromptService.get().request(promptYesOrNo(alert(fromHtml(ticker)
                , "SSH", centered(message))));
        if (userConfirmKeyGood) {
            knownKeys.put(host, key);
            return OK;
        } else {
            return NOT_INCLUDED;
        }
    }

    public void add(HostKey hostkey, UserInfo ui) {
    }

    public void remove(String host, String type) {
    }

    public void remove(String host, String type, byte[] key) {
    }

    public String getKnownHostsRepositoryID() {
        return null;
    }

    public HostKey[] getHostKey() {
        return new HostKey[0];
    }

    public HostKey[] getHostKey(String host, String type) {
        return new HostKey[0];
    }
}
