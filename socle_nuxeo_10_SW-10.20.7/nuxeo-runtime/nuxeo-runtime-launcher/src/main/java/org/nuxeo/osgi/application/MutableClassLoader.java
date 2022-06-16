/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     bstefanescu, jcarsique
 */
package org.nuxeo.osgi.application;

import java.net.URL;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @since 5.4.2
 */
public interface MutableClassLoader {

    void addURL(URL url);

    ClassLoader getClassLoader();

    /**
     * @param startupClass The binary name of the class
     * @return The resulting Class object
     * @throws ClassNotFoundException If the class was not found
     */
    Class<?> loadClass(String startupClass) throws ClassNotFoundException;

}
