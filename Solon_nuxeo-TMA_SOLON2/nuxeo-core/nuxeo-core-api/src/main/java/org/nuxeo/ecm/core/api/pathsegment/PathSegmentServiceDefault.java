/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.api.pathsegment;

import java.util.regex.Pattern;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service generating a path segment from the title by just removing slashes and
 * limiting size.
 */
public class PathSegmentServiceDefault implements PathSegmentService {

    public Pattern stupidRegexp = Pattern.compile("^[- .,;?!:/\\\\'\"]*$");

    public int maxSize = 24;

    @Override
    public String generatePathSegment(DocumentModel doc) throws ClientException {
        String s = doc.getTitle();
        if (s == null) {
            s = "";
        }
        s = s.trim();
        if (s.length() > maxSize) {
            s = s.substring(0, maxSize).trim();
        }
        s = s.replace('/', '-');
        s = s.replace('\\', '-');
        if (stupidRegexp.matcher(s).matches()) {
            return IdUtils.generateStringId();
        }
        return s;
    }

}
