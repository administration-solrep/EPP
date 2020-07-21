/*
 * (C) Copyright 2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.api;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @deprecated use the new service API: {@link org.nuxeo.runtime.api.ServiceManagement}
 */
@Deprecated
@SuppressWarnings({"ALL"})
public class DefaultJndiContextFactory implements JndiContextFactory {

    private static final DefaultJndiContextFactory INSTANCE = new DefaultJndiContextFactory();

    public static DefaultJndiContextFactory getInstance() {
        return INSTANCE;
    }

    public InitialContext createJndiContext(String host, String port)
            throws NamingException {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        properties.setProperty("java.naming.provider.url", "jnp://" + host + ':' + port);
        properties.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        return new InitialContext(properties);
    }

}
