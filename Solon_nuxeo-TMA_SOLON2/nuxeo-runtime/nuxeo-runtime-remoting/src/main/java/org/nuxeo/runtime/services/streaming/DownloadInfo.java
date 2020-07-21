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

package org.nuxeo.runtime.services.streaming;

import java.io.Serializable;

/**
 * Usefull for optimizing downloads. Apart the session iud a preferred download size is returned
 * so that the client is notified about the optimal transfer buffer size
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class DownloadInfo implements Serializable {

    private static final long serialVersionUID = 8039588599283147315L;

    public final long sid;
    public final int preferredSize;

    public DownloadInfo(long sid, int preferredSize) {
        this.sid = sid;
        this.preferredSize = preferredSize;
    }

}
