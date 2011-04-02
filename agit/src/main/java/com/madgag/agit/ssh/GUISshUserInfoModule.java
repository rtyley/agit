package com.madgag.agit.ssh;


import roboguice.config.AbstractAndroidModule;

import com.jcraft.jsch.UserInfo;

public class GUISshUserInfoModule extends AbstractAndroidModule {

	@Override
    protected void configure() {
    	bind(UserInfo.class).to(AndroidUserInfo.class);
    }
}
