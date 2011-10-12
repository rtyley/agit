package com.madgag.agit.ssh;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import com.madgag.android.blockingprompt.BlockingPromptService;

import java.util.Arrays;
import java.util.Map;

import static android.text.Html.fromHtml;
import static com.google.common.collect.Maps.newHashMap;
import static com.madgag.agit.operations.OpNotification.alert;
import static com.madgag.agit.operations.OpPrompt.promptYesOrNo;
import static com.madgag.agit.util.DigestUtils.encodeHex;
import static com.madgag.agit.util.DigestUtils.md5;
import static com.madgag.agit.views.TextUtil.centered;
import static com.madgag.agit.R.string.ask_host_key_ok;
import static com.madgag.agit.R.string.ask_host_key_ok_ticker;
import static java.lang.Boolean.TRUE;

@Singleton
public class CuriousHostKeyRepository implements HostKeyRepository {
    Map<String, byte[]> knownKeys = newHashMap();
    private final Context context;
    private final Provider<BlockingPromptService> blockingPromptService;

    @Inject
    public CuriousHostKeyRepository(Context context, Provider<BlockingPromptService> blockingPromptService) {
        this.context = context;
        this.blockingPromptService = blockingPromptService;
    }

    public int check(String host, byte[] key) {
        byte[] knownKey = knownKeys.get(host);
        if (knownKey==null) {
            return userCheckKey(host, key);
        }
        return Arrays.equals(knownKey, key)? OK:CHANGED;
    }

    private int userCheckKey(String host, byte[] key) {
        String keyFingerprint = "<small>"+code(encodeHex(md5(key)))+"</small><br />";
        String ticker = context.getString(ask_host_key_ok_ticker, code(host));
        String message = context.getString(ask_host_key_ok, code(host)+"<br />", keyFingerprint);
        boolean userConfirmKeyGood = TRUE == blockingPromptService.get().request(promptYesOrNo(alert(fromHtml(ticker), "SSH", centered(message))));
        if (userConfirmKeyGood) {
            knownKeys.put(host,key);
            return OK;
        } else {
            return NOT_INCLUDED;
        }
    }

    private String code(String s) {
        return "<b><tt>"+s+"</tt></b>";
    }

    public void add(HostKey hostkey, UserInfo ui) {}

    public void remove(String host, String type) {}

    public void remove(String host, String type, byte[] key) {}

    public String getKnownHostsRepositoryID() {
        return null;
    }

    public HostKey[] getHostKey() {
        return new HostKey[0];
    }

    public HostKey[] getHostKey(String host, String type) {
        return new HostKey[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
