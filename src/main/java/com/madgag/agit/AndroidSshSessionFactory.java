package com.madgag.agit;

import org.connectbot.service.PromptHelper;
import org.eclipse.jgit.transport.SshConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;

import com.jcraft.jsch.Session;

public class AndroidSshSessionFactory extends SshConfigSessionFactory {

	private final PromptHelper promptHelper;
	private final RepositoryOperationContext operationContext;
	
	AndroidSshSessionFactory(RepositoryOperationContext operationContext, PromptHelper promptHelper) {
		this.operationContext = operationContext;
		this.promptHelper = promptHelper;
	}
	
	@Override
	protected void configure(Host host, Session session) {
		session.setUserInfo(new AndroidUserInfo(operationContext, promptHelper));
	}

}
