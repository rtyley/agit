package com.madgag.agit.ssh;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;

import java.util.Arrays;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.madgag.agit.util.DigestUtils.encodeHex;
import static com.madgag.agit.util.DigestUtils.md5;

@Singleton
public class CuriousHostKeyRepository implements HostKeyRepository {
    Map<String, byte[]> knownKeys = newHashMap();
    private final Provider<UserInfo> userInfo;

    @Inject
    public CuriousHostKeyRepository(Provider<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public int check(String host, byte[] key) {
        byte[] knownKey = knownKeys.get(host);
        if (knownKey==null) {
            return userCheckKey(host, key);
        }
        return Arrays.equals(knownKey, key)? OK:CHANGED;
    }

    private int userCheckKey(String host, byte[] key) {
        boolean userConfirmKeyGood=userInfo.get().promptYesNo("Server "+host+" has key fingerprint "+ encodeHex(md5(key)));
        if (userConfirmKeyGood) {
            knownKeys.put(host,key);
            return OK;
        } else {
            return NOT_INCLUDED;
        }
    }

    public void add(HostKey hostkey, UserInfo ui) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void remove(String host, String type) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void remove(String host, String type, byte[] key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getKnownHostsRepositoryID() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HostKey[] getHostKey() {
        return new HostKey[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public HostKey[] getHostKey(String host, String type) {
        return new HostKey[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
