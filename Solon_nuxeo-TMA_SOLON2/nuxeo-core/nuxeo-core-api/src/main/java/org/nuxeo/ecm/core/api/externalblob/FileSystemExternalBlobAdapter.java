/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.core.api.externalblob;

import java.io.File;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * File system external adapter that takes the "container" property to set the
 * absolute path of the container folder on the file system.
 *
 * @author Anahide Tchertchian
 */
public class FileSystemExternalBlobAdapter extends AbstractExternalBlobAdapter {

    private static final long serialVersionUID = 1L;

    public static final String CONTAINER_PROPERTY_NAME = "container";

    public String getFileAbsolutePath(String localPath)
            throws PropertyException {
        String container = getProperty(CONTAINER_PROPERTY_NAME);
        if (container == null) {
            throw new PropertyException(String.format(
                    "External blob adapter with prefix '%s' "
                            + "and class '%s' is missing the '%s' property",
                    getPrefix(), getClass().getName(), CONTAINER_PROPERTY_NAME));
        }
        container = container.trim();
        if (!container.endsWith(File.separator)) {
            return String.format("%s%s%s", container, File.separator, localPath);
        } else {
            return String.format("%s%s", container, localPath);
        }
    }

    @Override
    public Blob getBlob(String uri) throws PropertyException {
        String localPath = getLocalName(uri);
        String path = getFileAbsolutePath(localPath);
        File file = new File(path);
        if (!file.exists()) {
            throw new PropertyException(String.format(
                    "Cannot find file at '%s'", path));
        }
        Blob blob = new FileBlob(file);
        blob.setFilename(file.getName());
        return blob;
    }
}
