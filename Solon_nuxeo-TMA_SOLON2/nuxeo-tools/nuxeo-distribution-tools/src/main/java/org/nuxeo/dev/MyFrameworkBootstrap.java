/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.osgi.application.FrameworkBootstrap;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class MyFrameworkBootstrap extends FrameworkBootstrap {

    protected NuxeoApp app;
    
    public MyFrameworkBootstrap(NuxeoApp app, ClassLoader cl) throws IOException {
        super(new ClassLoaderDelegate(cl), app.getHome());
        this.app = app;
    }
    
    @Override
    public ClassLoaderDelegate getLoader() {
        return (ClassLoaderDelegate)super.getLoader();
    }
    
    /**
     * The class path is built by the build process of the NuxeoApp
     * Here we simply return the list of bundles detected at build time
     */
    @Override
    protected List<File> buildClassPath() throws IOException {
        return new ArrayList<File>(app.bundles.values());
    }

}
