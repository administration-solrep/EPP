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
 * $Id: PermissionProviderBean.java 28583 2008-01-08 20:00:27Z sfermigier $
 */

package org.nuxeo.ecm.core.security.ejb;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.security.PermissionProvider;
import org.nuxeo.ecm.core.api.security.UserVisiblePermission;
import org.nuxeo.ecm.core.security.SecurityService;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:ogrisel@nuxeo.com">Olivier Grisel</a>
 *
 */
@Stateless
@Remote(PermissionProvider.class)
@Local(PermissionProviderLocal.class)
public class PermissionProviderBean implements PermissionProviderLocal {

    private final PermissionProvider permissionProvider;

    public PermissionProviderBean() {
        // use the local runtime service as the backend
        SecurityService sservice = Framework.getLocalService(SecurityService.class);
        permissionProvider = sservice.getPermissionProvider();
    }

    @Override
    public String[] getAliasPermissions(String perm) throws ClientException {
        return permissionProvider.getAliasPermissions(perm);
    }

    @Override
    public String[] getPermissionGroups(String perm) {
        return permissionProvider.getPermissionGroups(perm);
    }

    @Override
    public String[] getPermissions() {
        return permissionProvider.getPermissions();
    }

    @Override
    public String[] getSubPermissions(String perm) throws ClientException {
        return permissionProvider.getSubPermissions(perm);
    }

    @Override
    public List<UserVisiblePermission> getUserVisiblePermissionDescriptors()
            throws ClientException {
        return permissionProvider.getUserVisiblePermissionDescriptors();
    }

    @Override
    public List<UserVisiblePermission> getUserVisiblePermissionDescriptors(
            String typeName) throws ClientException {
        return permissionProvider.getUserVisiblePermissionDescriptors(typeName);
    }

}
