/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu, jcarsique
 *
 * $Id$
 */

package org.nuxeo.launcher.commons.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Text template processing.
 * <p>
 * Copy files or directories replacing parameters matching pattern
 * '${[a-zA-Z_0-9\-\.]+}' with values from a {@link Map} (deprecated) or a
 * {@link Properties}.
 * <p>
 * Method {@link #setParsingExtensions(String)} allow to set list of files being
 * processed when using {@link #processDirectory(File, File)} or #pro, others
 * are simply copied.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class TextTemplate {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([a-zA-Z_0-9\\-\\.]+)\\}");

    private final Properties vars;

    private boolean trim = false;

    private List<String> extensions;

    private boolean extensionsContainsDot = false;

    public boolean isTrim() {
        return trim;
    }

    /**
     * Set to true in order to trim invisible characters (spaces) from values.
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public TextTemplate() {
        vars = new Properties();
    }

    /**
     * @deprecated prefer use of {@link #TextTemplate(Properties)}
     */
    @Deprecated
    public TextTemplate(Map<String, String> vars) {
        this.vars = new Properties();
        this.vars.putAll(vars);
    }

    /**
     * @param vars Properties containing keys and values for template processing
     */
    public TextTemplate(Properties vars) {
        this.vars = vars;
    }

    /**
     * @deprecated prefer use of {@link #getVariables()} then {@link Properties}
     *             .load()
     */
    @Deprecated
    public void setVariables(Map<String, String> vars) {
        this.vars.putAll(vars);
    }

    public void setVariable(String name, String value) {
        vars.setProperty(name, value);
    }

    public String getVariable(String name) {
        return vars.getProperty(name);
    }

    public Properties getVariables() {
        return vars;
    }

    public String process(CharSequence text) {
        Matcher m = PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String var = m.group(1);
            String value = getVariable(var);
            if (value != null) {
                if (trim) {
                    value = value.trim();
                }
                // Allow use of backslash and dollars characters
                String valueL = Matcher.quoteReplacement(value);
                m.appendReplacement(sb, valueL);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public String process(InputStream in) throws IOException {
        String text = IOUtils.toString(in, "UTF-8");
        return process(text);
    }

    public void process(InputStream in, OutputStream out) throws IOException {
        process(in, out, true);
    }

    /**
     * Recursive call {@link #process(InputStream, OutputStream, boolean)} on
     * each file from "in" directory to "out" directory.
     *
     * @param in Directory to read files from
     * @param out Directory to write files to
     */
    public void processDirectory(File in, File out)
            throws FileNotFoundException, IOException {
        if (in.isFile()) {
            if (out.isDirectory()) {
                out = new File(out, in.getName());
            }

            boolean processText = false;
            if (!extensionsContainsDot) {
                int extIndex = in.getName().lastIndexOf('.');
                String extension = extIndex == -1 ? ""
                        : in.getName().substring(extIndex + 1).toLowerCase();
                processText = extensions == null
                        || extensions.contains(extension);
            } else {
                // Check for each extension if it matches end of filename
                String filename = in.getName().toLowerCase();
                for (String ext : extensions) {
                    if (filename.endsWith(ext)) {
                        processText = true;
                        break;
                    }
                }
            }

            FileInputStream is = null;
            FileOutputStream os = new FileOutputStream(out);
            try {
                is = new FileInputStream(in);
                process(is, os, processText);
            } finally {
                if (is != null) {
                    is.close();
                }
                os.close();
            }
        } else if (in.isDirectory()) {
            if (!out.exists()) {
                // allow renaming destination directory
                out.mkdirs();
            } else if (!out.getName().equals(in.getName())) {
                // allow copy over existing arborescence
                out = new File(out, in.getName());
                out.mkdir();
            }
            for (File file : in.listFiles()) {
                processDirectory(file, out);
            }
        }
    }

    /**
     * @param processText if true, text is processed for parameters replacement
     */
    public void process(InputStream is, OutputStream os, boolean processText)
            throws IOException {
        if (processText) {
            String text = IOUtils.toString(is, "UTF-8");
            text = process(text);
            os.write(text.getBytes());
        } else {
            IOUtils.copy(is, os);
        }
    }

    /**
     * @param extensionsList comma-separated list of files extensions to parse
     */
    public void setParsingExtensions(String extensionsList) {
        StringTokenizer st = new StringTokenizer(extensionsList, ",");
        extensions = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String extension = st.nextToken();
            extensions.add(extension);
            if (!extensionsContainsDot && extension.contains(".")) {
                extensionsContainsDot = true;
            }
        }
    }
}
