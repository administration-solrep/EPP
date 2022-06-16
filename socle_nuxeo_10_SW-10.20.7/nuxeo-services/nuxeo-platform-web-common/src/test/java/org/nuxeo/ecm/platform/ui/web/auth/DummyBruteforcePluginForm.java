package org.nuxeo.ecm.platform.ui.web.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;

public class DummyBruteforcePluginForm extends DummyAuthPluginForm {
	@Override
    public UserIdentificationInfo handleRetrieveIdentity(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter(DUMMY_AUTH_FORM_USERNAME_KEY);
        String password = request.getParameter(DUMMY_AUTH_FORM_PASSWORD_KEY);
        
        return new UserIdentificationInfo(username, password);
    }
}
