package com.madgag.agit;

import com.jcraft.jsch.UserInfo;

public class AndroidGUIUserInfoFactory implements UserInfoFactory {

	public UserInfo createUserInfoAssociatedWith(RepositoryOperationContext repositoryOperationContext) {
		return new AndroidUserInfo(repositoryOperationContext.getBlockingPromptService());
	}

}
