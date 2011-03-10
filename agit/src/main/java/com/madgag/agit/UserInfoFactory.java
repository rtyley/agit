package com.madgag.agit;

import com.jcraft.jsch.UserInfo;

public interface UserInfoFactory {
	UserInfo createUserInfoAssociatedWith(RepositoryOperationContext repositoryOperationContext); 
}
