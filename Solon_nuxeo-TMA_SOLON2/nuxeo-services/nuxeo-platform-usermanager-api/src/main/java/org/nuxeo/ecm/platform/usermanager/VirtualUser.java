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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.usermanager;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface for a virtual user.
 *
 * @author Anahide Tchertchian
 */
public interface VirtualUser extends Serializable {

    String getId();

    String getPassword();

    boolean isSearchable();

    List<String> getGroups();

    Map<String, Serializable> getProperties();

}
