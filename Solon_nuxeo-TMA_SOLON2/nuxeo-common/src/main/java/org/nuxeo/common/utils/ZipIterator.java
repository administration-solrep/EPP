/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *
 * $Id$
 */

package org.nuxeo.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An iterator over the entries in a ZIP file.
 * <p>
 * The iterator support filtering using {@link ZipEntryFilter}
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ZipIterator implements Iterator<ZipEntry> {

    private static final Log log = LogFactory.getLog(ZipIterator.class);

    private final ZipInputStream zin;
    private final ZipEntryFilter filter;
    private ZipEntry zentry;

    public ZipIterator(ZipInputStream zin, ZipEntryFilter filter) throws IOException {
        this.zin = zin;
        this.filter = filter;
        zentry = zin.getNextEntry();
    }

    public ZipIterator(URL url) throws IOException {
        this(url.openStream(), null);
    }

    public ZipIterator(URL url, ZipEntryFilter filter) throws IOException {
        this(url.openStream(), filter);
    }

    public ZipIterator(File file) throws IOException {
        this(new FileInputStream(file), null);
    }

    public ZipIterator(File file, ZipEntryFilter filter) throws IOException {
        this(new FileInputStream(file), filter);
    }

    public ZipIterator(InputStream in) throws IOException {
        this(new ZipInputStream(in), null);
    }

    public ZipIterator(InputStream in, ZipEntryFilter filter) throws IOException {
        this(new ZipInputStream(in), filter);
    }


    public ZipEntry getNextEntry() throws IOException {
        ZipEntry ze = zin.getNextEntry();
        if (ze == null) {
            return null;
        }
        if (filter != null) {
            while (!filter.accept(ze.getName())) {
                ze = zin.getNextEntry();
                if (ze == null) {
                    return null;
                }
            }
        }
        return ze;
    }

    public void remove() {
        throw new UnsupportedOperationException("remove is not supported by this iterator");
    }

    public boolean hasNext() {
        return zentry != null;
    }

    public ZipEntry next() {
        ZipEntry oldEntry = zentry;
        try {
            zentry = getNextEntry();
        } catch (IOException e) {
            log.error(e);
            zentry = null;
        }
        return oldEntry;
    }

    public InputStream getInputStream() {
        return zin;
    }

    public void close() {
        if (zin != null) {
            try {
                zin.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

}
