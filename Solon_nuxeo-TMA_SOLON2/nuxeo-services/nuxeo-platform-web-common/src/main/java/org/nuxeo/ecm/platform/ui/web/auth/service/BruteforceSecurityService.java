package org.nuxeo.ecm.platform.ui.web.auth.service;

import org.nuxeo.ecm.platform.ui.web.auth.BruteforceUserInfo;

public interface BruteforceSecurityService {

	public BruteforceUserInfo getLoginInfos(String username) throws Exception;

	public void newAttempt(String Username) throws Exception;

	public void deleteLoginInfos(String Username) throws Exception;

	public void setUserInfos(BruteforceUserInfo userInfos) throws Exception;
}
