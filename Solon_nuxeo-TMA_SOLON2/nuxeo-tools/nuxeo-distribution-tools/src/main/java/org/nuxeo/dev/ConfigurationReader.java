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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class ConfigurationReader {

    public interface SectionReader {
        public void readLine(String section, String line) throws IOException;
    }

    protected static class NullReader implements SectionReader {
        public void readLine(String section, String line) throws IOException {
            return;
        }
    }
    
    protected SectionReader defaultReader = new NullReader();
    protected Map<String,SectionReader> readers = new HashMap<String, SectionReader>();
    
    
    public void setDefaultReader(SectionReader reader) {
        this.defaultReader = reader;
    }
    
    public void addReader(String section, SectionReader reader) {
        readers.put(section, reader);
        if (section.length() == 0) {
            defaultReader = reader;
        }
    }
    
    public SectionReader getDefaultReader() {
        return defaultReader;
    }
    
    public SectionReader getReader(String section) {
        SectionReader reader = readers.get(section);
        return reader == null ? defaultReader : reader;
    }
    
    public void read(InputStream in) throws IOException {
        read(new BufferedReader(new InputStreamReader(in)));
    }
    
    public void read(BufferedReader reader) throws IOException {
        SectionReader creader = defaultReader;
        String section = "";
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                line = reader.readLine();
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) { // section start
                section = line.substring(1, line.length()-1);
                creader = getReader(section);
                line = reader.readLine();
                continue;
            }
            creader.readLine(section, line);
            line = reader.readLine();
        }
    }
    
}
