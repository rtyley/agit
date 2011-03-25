package com.madgag.agit.ssh;

import com.jcraft.jsch.UserInfo;
import com.madgag.agit.RepositoryOperationContext;

public class AndroidGUIUserInfoFactory implements UserInfoFactory {

	public UserInfo createUserInfoAssociatedWith(RepositoryOperationContext repositoryOperationContext) {
		return new AndroidUserInfo(repositoryOperationContext.getBlockingPromptService());
	}

}
