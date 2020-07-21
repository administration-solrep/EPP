/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.ui.web.cache;

/**
 * Helper class to check if Seam s:cache tag can be used
 * (s:cache does not only require jboss-cache, but also some internal classes.
 *
 * @author Thierry Delprat
 */
public class SeamCacheHelper {

    protected static Boolean canUseSeamCache;

    private SeamCacheHelper() {
    }

    public static boolean canUseSeamCache() {
        if (canUseSeamCache == null) {
            canUseSeamCache = false;
            try {
                Class.forName("org.jboss.system.ServiceMBeanSupport");
                canUseSeamCache = true;
            } catch (ClassNotFoundException e) {
            }
        }
        return canUseSeamCache.booleanValue();
    }
}
