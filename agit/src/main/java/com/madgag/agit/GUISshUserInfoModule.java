package com.madgag.agit;

import roboguice.config.AbstractAndroidModule;

public class GUISshUserInfoModule extends AbstractAndroidModule {

	@Override
    protected void configure() {
    	bind(UserInfoFactory.class).to(AndroidGUIUserInfoFactory.class);
    }
}
