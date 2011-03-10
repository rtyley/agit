package com.madgag.agit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.madgag.ssh.android.authagent.AndroidAuthAgent;

public class AndroidSshSessionFactoryFactory {
	
	private final UserInfoFactory userInfoFactory;
	private final Provider<AndroidAuthAgent> androidAuthAgentProvider;

	@Inject
	public AndroidSshSessionFactoryFactory(UserInfoFactory userInfoFactory, Provider<AndroidAuthAgent> androidAuthAgentProvider) {
		this.userInfoFactory = userInfoFactory;
		this.androidAuthAgentProvider = androidAuthAgentProvider;
	}
	
	AndroidSshSessionFactory createSshSessionFactoryFor(RepositoryOperationContext repositoryOperationContext) {
		return new AndroidSshSessionFactory(androidAuthAgentProvider, userInfoFactory.createUserInfoAssociatedWith(repositoryOperationContext));
	}
}
