package com.madgag.agit.ssh;

import com.jcraft.jsch.UserInfo;
import com.madgag.agit.RepositoryOperationContext;

public interface UserInfoFactory {
	UserInfo createUserInfoAssociatedWith(RepositoryOperationContext repositoryOperationContext); 
}
