package org.nuxeo.ecm.platform.ui.web.auth.service;

import org.nuxeo.ecm.platform.ui.web.auth.BruteforceUserInfo;

public interface BruteforceSecurityService {
	BruteforceUserInfo getLoginInfos(String username);

	void newAttempt(String username);

	void deleteLoginInfos(String username);

	void setUserInfos(BruteforceUserInfo userInfos);

}
