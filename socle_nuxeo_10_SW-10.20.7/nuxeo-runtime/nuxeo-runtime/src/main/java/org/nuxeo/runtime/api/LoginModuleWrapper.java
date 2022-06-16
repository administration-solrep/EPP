/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     bstefanescu
 */
package org.nuxeo.runtime.api;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * A login module wrapper to overcome the class loading issues on OSGi frameworks.
 * <p>
 * A login module is specified using the class name - and when doing a login against that module the login context will
 * get a class instance for the module by using Clas.forName and the current thread context class loader. This means in
 * an OSGi application the class loader of the caller bundle will be used to resolve the login module. But the caller
 * bundle may not have any dependency on the login module since it is not aware about which login module implementation
 * is used underneath - so in most cases the login module class will not be found.
 * <p>
 * For this reason all the contributed login modules will be wrapped using this class. As almost any Nuxeo bundle have a
 * dependency on runtime this class will be visible from almost any Nuxeo bundle. So, the login context will instantiate
 * this wrapper instead of the real login module and the wrapper will use OSGi logic to instantiate the wrapped login
 * module. (by using the bundle that contributed the login module)
 * <p>
 * <b>IMPORTANT</b>
 * <p>
 * This class is in org.nuxeo.runtime.api package to be visible to any bundle that uses Nuxeo runtime. This is because
 * this package is imported by almost any bundle using Nuxeo runtime - so you don't need to declare the import package
 * in the OSGi manifest. In the case you don't use runtime but you are doing logins in the context of your bundle you
 * must import the package <code>org.nuxeo.runtime.api</code> in your manifest.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class LoginModuleWrapper implements LoginModule {

    public static final String DELEGATE_CLASS_KEY = LoginModuleWrapper.class.getName() + ".delegate";

    protected LoginModule delegate;

    protected void initDelegate(Map<String, ?> options) {
        if (delegate == null) {
            try {
                Class<?> clazz = (Class<?>) options.get(DELEGATE_CLASS_KEY);
                delegate = (LoginModule) clazz.newInstance();
            } catch (NullPointerException e) {
                throw new RuntimeException("Should be a bug: No DELEGATE_CLASS_KEY found in login module options", e);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Invalid login module class: " + options.get(DELEGATE_CLASS_KEY)
                        + ". Should implement LoginModule.", e);
            }
        }
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        initDelegate(options);
        delegate.initialize(subject, callbackHandler, sharedState, options);
    }

    @Override
    public boolean login() throws LoginException {
        return delegate.login();
    }

    @Override
    public boolean commit() throws LoginException {
        return delegate.commit();
    }

    @Override
    public boolean abort() throws LoginException {
        return delegate.abort();
    }

    @Override
    public boolean logout() throws LoginException {
        return delegate.logout();
    }

}
