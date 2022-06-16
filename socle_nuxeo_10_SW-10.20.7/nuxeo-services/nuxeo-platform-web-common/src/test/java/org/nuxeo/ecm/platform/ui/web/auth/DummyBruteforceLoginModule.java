/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.platform.ui.web.auth;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.platform.api.login.UserIdentificationInfoCallback;

/**
 * Dummy login module that just uses the UserIdentificationInfoCallback.
 *
 * @since 10.2
 */
public class DummyBruteforceLoginModule extends DummyLoginModule {

    
    @Override
    public boolean login() throws LoginException {
        // UserIdentificationInfoCallback is recognized by the Nuxeo UserIdentificationInfoCallbackHandler
        UserIdentificationInfoCallback uiic = new UserIdentificationInfoCallback();
        try {
            callbackHandler.handle(new Callback[] { uiic });
        } catch (UnsupportedCallbackException | IOException e) {
            throw new LoginException(e.toString());
        }
        userIdent = uiic.getUserInfo();
        
        if(userIdent.getPassword()==null || !userIdent.getPassword().equals(userIdent.getUserName())) {
        	throw new LoginException("Wrong password");
        }
        return true;
    }

}
