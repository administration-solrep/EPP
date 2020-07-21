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
 *     bstefanescu, jcarsique
 *
 * $Id$
 */

package org.nuxeo.common;

import java.io.File;
import java.util.Properties;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Environment {

    /**
     * Constants that identifies possible hosts for the framework.
     */
    public static final String JBOSS_HOST = "JBoss";

    // Jetty or GF3 embedded
    public static final String NXSERVER_HOST = "NXServer";

    public static final String TOMCAT_HOST = "Tomcat";

    public static final String NUXEO_HOME_DIR = "nuxeo.home.dir";

    public static final String NUXEO_DATA_DIR = "nuxeo.data.dir";

    public static final String NUXEO_LOG_DIR = "nuxeo.log.dir";

    public static final String NUXEO_TMP_DIR = "nuxeo.tmp.dir";

    public static final String NUXEO_CONFIG_DIR = "nuxeo.config.dir";

    public static final String NUXEO_WEB_DIR = "nuxeo.web.dir";

    /**
     * The home directory.
     *
     * @deprecated never defined; use {@link #NUXEO_HOME_DIR}
     */
    @Deprecated
    public static final String HOME_DIR = "org.nuxeo.app.home";

    /**
     * The web root.
     *
     * @deprecated never defined; use {@link #NUXEO_WEB_DIR}
     */
    @Deprecated
    public static final String WEB_DIR = "org.nuxeo.app.web";

    /**
     * The config directory.
     *
     * @deprecated never defined; use {@link #NUXEO_CONFIG_DIR}
     */
    @Deprecated
    public static final String CONFIG_DIR = "org.nuxeo.app.config";

    /**
     * The data directory.
     *
     * @deprecated never defined; use {@link #NUXEO_DATA_DIR}
     */
    @Deprecated
    public static final String DATA_DIR = "org.nuxeo.app.data";

    /**
     * The log directory.
     *
     * @deprecated never defined; use {@link #NUXEO_LOG_DIR}
     */
    @Deprecated
    public static final String LOG_DIR = "org.nuxeo.app.log";

    /**
     * The application layout (optional):
     * directory containing nuxeo runtime osgi bundles.
     */
    public static final String BUNDLES_DIR = "nuxeo.osgi.app.bundles";

    public static final String BUNDLES = "nuxeo.osgi.bundles";

    private static volatile Environment DEFAULT;

    protected final File home;

    protected File data;

    protected File log;

    protected File config;

    protected File web;

    protected File temp;

    protected final Properties properties;

    protected String[] args;

    protected boolean isAppServer;

    protected String hostAppName;

    protected String hostAppVersion;

    public Environment(File home) {
        this(home, null);
    }

    public Environment(File home, Properties properties) {
        this.home = home;
        this.properties = new Properties();
        if (properties != null) {
            loadProperties(properties);
        }
        this.properties.put(HOME_DIR, this.home.getAbsolutePath());
    }

    public static synchronized void setDefault(Environment env) {
        DEFAULT = env;
    }

    public static Environment getDefault() {
    	if (DEFAULT == null) {
    		tryInitEnvironment();
    	}
        return DEFAULT;
    }
    
    private static synchronized void tryInitEnvironment() {
    	String homeDir = System.getProperty("nuxeo.home");
    	if (homeDir != null) {
    		File home = new File(homeDir);
    		if (home.isDirectory()) {
    			DEFAULT = new Environment(home);
    		}
    	}
    }


    public File getHome() {
        return home;
    }

    public boolean isApplicationServer() {
        return isAppServer;
    }

    public void setIsApplicationServer(boolean isAppServer) {
        this.isAppServer = isAppServer;
    }

    public String getHostApplicationName() {
        return hostAppName;
    }

    public String getHostApplicationVersion() {
        return hostAppVersion;
    }

    public void setHostApplicationName(String name) {
        hostAppName = name;
    }

    public void setHostApplicationVersion(String version) {
        hostAppVersion = version;
    }

    public File getTemp() {
        if (temp == null) {
            temp = new File(home, "tmp");
        }
        return temp;
    }

    public void setTemp(File temp) {
        this.temp = temp;
        this.properties.put(NUXEO_TMP_DIR, temp.getAbsolutePath());
    }

    public File getConfig() {
        if (config == null) {
            config = new File(home, "config");
        }
        return config;
    }

    public void setConfig(File config) {
        this.config = config;
    }

    public File getLog() {
        if (log == null) {
            log = new File(home, "log");
        }
        return log;
    }

    public void setLog(File log) {
        this.log = log;
        this.properties.put(NUXEO_LOG_DIR, log.getAbsolutePath());
    }

    public File getData() {
        if (data == null) {
            data = new File(home, "data");
        }
        return data;
    }

    public void setData(File data) {
        this.data = data;
        this.properties.put(NUXEO_DATA_DIR, data.getAbsolutePath());
    }

    public File getWeb() {
        if (web == null) {
            web = new File(home, "web");
        }
        return web;
    }

    public void setWeb(File web) {
        this.web = web;
    }

    public String[] getCommandLineArguments() {
        return args;
    }

    public void setCommandLineArguments(String[] args) {
        this.args = args;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        String val = properties.getProperty(key);
        return val == null ? defaultValue : val;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public Properties getProperties() {
        return properties;
    }

    public void loadProperties(Properties properties) {
        this.properties.putAll(properties);
    }

    public boolean isJBoss() {
        return JBOSS_HOST.equals(hostAppName);
    }

    public boolean isJetty() {
        return NXSERVER_HOST.equals(hostAppName);
    }

    public boolean isTomcat() {
        return TOMCAT_HOST.equals(hostAppName);
    }

    /**
     * Initialization with System properties. Home must be set (it is usually
     * nuxeo runtime home, not nuxeo home).
     *
     * @since 5.4.1
     */
    public void init() {
        String dataDir = System.getProperty(NUXEO_DATA_DIR);
        String configDir = System.getProperty(NUXEO_CONFIG_DIR);
        String logDir = System.getProperty(NUXEO_LOG_DIR);
        String tmpDir = System.getProperty(NUXEO_TMP_DIR);

        if (dataDir != null && !dataDir.isEmpty()) {
            setData(new File(dataDir));
        }

        if (configDir != null && !configDir.isEmpty()) {
            setConfig(new File(configDir));
        }

        if (logDir != null && !logDir.isEmpty()) {
            setLog(new File(logDir));
        }

        if (tmpDir != null && !tmpDir.isEmpty()) {
            setTemp(new File(tmpDir));
        }
    }

}
