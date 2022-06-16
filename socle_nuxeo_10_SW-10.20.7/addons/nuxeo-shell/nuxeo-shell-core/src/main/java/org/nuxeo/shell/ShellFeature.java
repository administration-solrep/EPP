/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.shell;

import java.util.ServiceLoader;

/**
 * Services implementing this interface will be used to add new features to the shell, like custom command namespaces,
 * completors etc. Registered (i.e. available) features are exposed by the Shell#getFeatures() method Registration of a
 * feature implementation is done as described by the Java {@link ServiceLoader} mechanism that is used for service
 * discovery.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface ShellFeature {

    /**
     * Install the feature in the given shell instance. This is typically registering new global commands, namespaces,
     * value adapters or completors.
     *
     * @param shell
     */
    void install(Shell shell);

}
