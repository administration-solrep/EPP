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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.nuxeo.build.maven.ArtifactDescriptor;
import org.nuxeo.build.maven.EmbeddedMavenClient;
import org.nuxeo.build.maven.MavenClientFactory;
import org.nuxeo.build.maven.graph.Graph;
import org.nuxeo.build.maven.graph.Node;
import org.nuxeo.build.util.FileUtils;
import org.nuxeo.build.util.ZipUtils;




/**
 * A Nuxeo application that can be embedded in an IDE like eclipse to be able to launch
 * an embedded Nuxeo in debug mode.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class NuxeoApp {


    public final static String CORE_SERVER_531_SNAPSHOT = "core-5.3.1-SNAPSHOT";
    public final static String CORE_SERVER_531 = "core-5.3.1";
    public final static String CORE_SERVER_530 = "core-5.3.0";

    public final static String DEFAULT = CORE_SERVER_531;


    protected File home;
    protected EmbeddedMavenClient maven;
    protected Map<String, File> bundles;

    protected List<String> bundlePatterns;
    protected MyFrameworkBootstrap bootstrap;
    protected boolean isVerbose;
    protected boolean isOffline;
    protected ConfigurationLoader loader;

    protected String updatePolicy = "daily";


    public static NuxeoApp createTestNuxeoApp() throws Exception {
        File file = File.createTempFile("nuxeo-app", ".tmp");
        file.delete();
        file.mkdirs();
        return new NuxeoApp(file);
    }

    public NuxeoApp(File home) throws Exception {
        this (home, null);
    }

    public NuxeoApp(File home, ClassLoader cl) throws Exception {
        this.home = home;
        bundlePatterns = new ArrayList<String>();
        bundlePatterns.add("nuxeo-");
        bundles = new LinkedHashMap<String, File>(); // map symName to bundle file path
        this.bootstrap = new MyFrameworkBootstrap(this, cl == null ? findContextClassLoader() : cl);
    }

    public void setUpdatePolicy(String updatePolicy) {
        this.updatePolicy = updatePolicy;
    }

    public void clearBundlePatterns() {
        bundlePatterns.clear();
    }

    public void addBundlePattern(String pattern) {
        bundlePatterns.add(pattern);
    }

    public EmbeddedMavenClient getMaven() {
        return maven;
    }

    public File getHome() {
        return home;
    }

    public void setOffline(boolean isOffline) {
        this.isOffline = isOffline;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void build(URL config) throws Exception {
        build(config, false);
    }

    public void build(String profile) throws Exception {
        build(profile, false);
    }

    public void build(String profile, boolean enableCache) throws Exception {
        URL url = NuxeoApp.class.getResource(profile+".cfg");
        if (url == null) {
            throw new IllegalArgumentException("profile is not known: "+profile);
        }
        build(url, enableCache);
    }

    public void build(URL url, boolean enableCache) throws Exception {
        if (enableCache) {
            File cacheFile = new File(home, "tmp/build.cache");
            if (cacheFile.isFile()) {
                loadConfigurationFromCache(cacheFile);
                bootstrap.initialize();
                return;
            }
        }
        loadConfiguration(url);
        bootstrap.initialize();
    }

    public void loadConfiguration(File file) throws Exception {
        FileInputStream in = new FileInputStream(file);
        try {
            loadConfiguration(in);
        } finally {
            in.close();
        }
    }

    public void loadConfiguration(URL url) throws Exception {
        InputStream in = url.openStream();
        try {
            loadConfiguration(in);
        } finally {
            in.close();
        }
    }

    protected boolean acceptClassPathBundle(URL url) {
        for (String pattern : bundlePatterns) {
            if (url.getPath().contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * When creating the graph the build will print on console any resolved artifact
     * @param isVerbose
     */
    public void setVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
    }


    public void loadConfiguration(InputStream in) throws Exception {
        System.out.println("Building Application ...");

        double s = System.currentTimeMillis();

        initializeMaven();

        //load configuration
        loader = new ConfigurationLoader();
        loader.getReader().read(in);

        initializeGraph();

        Graph graph = maven.getGraph();
        ClassLoaderDelegate delegate = bootstrap.getLoader();
        // first unzip the configuration over the home directory
        copyTemplateFiles(loader.getTemplateArtifact(), loader.getTemplatePrefix(), home);

        StringBuilder cache = new StringBuilder();
        // second build a map with symbolicName -> url from all bundles in the classpath
        URL[] urls = delegate.getURLs(); // TODO only urls in this cl are returned. by bundles may exists in descendents or parents of the cl
        for (URL url : urls) {
            if ("file".equals(url.getProtocol()) && acceptClassPathBundle(url)) {
                File jar = FileUtils.urlToFile(url);
                String symName = null;
                try {
                    Manifest mf = getManifest(jar);
                    symName = getSymbolicName(mf);
                    if (symName != null) { // a bundle
                        bundles.put(symName, jar);
                        cache.append(symName+"@"+jar.getAbsolutePath()).append("\n");
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }
        // third iterate over the target NUXEO distribution bundles
        // and add them to the map built in phase 2 if not yet present
        Set<String> builtinBundles = loader.getBundles();
        for (String key : builtinBundles) {
            Node node = null;
            String classifier = null;
            if (key.startsWith("!")) {
                node = graph.addRootNode(key.substring(1));
            } else {
                int p = key.indexOf(';');
                if (p > -1) {
                    classifier = key.substring(p+1);
                    key = key.substring(0, p);
                }
                node = graph.findFirst(key);
            }
            if (node == null) {
                throw new RuntimeException("Failed to lookup artifact in graph: "+key);
            }
            if (isVerbose) {
                System.out.println("Resolving artifact: "+node);
            }
            File jar = classifier == null ?
                    node.getFile() : node.getFile(classifier);
            if (jar != null) {
                Manifest mf = getManifest(jar);
                String symbolicName = getSymbolicName(mf);
                if (symbolicName != null && !bundles.containsKey(symbolicName)) { // does not override if bundle already exists in the project classpath
                    bundles.put(symbolicName, jar);
                    URL url = jar.toURI().toURL();
                    delegate.addURL(url);
                    cache.append(symbolicName+"@"+jar.getAbsolutePath()).append("\n");
                } else if (symbolicName == null) {
                    URL url = jar.toURI().toURL();
                    delegate.addURL(url);
                    cache.append(jar.getAbsolutePath()).append("\n");
                }
            }
        }
        cache.append("\n");
        // add libs to classpath
        Set<String> builtinLibs = loader.getLibs();
        for (String key : builtinLibs) {
            Node node = null;
            if (key.startsWith("!")) {
                node = graph.addRootNode(key.substring(1));
            } else {
                node = graph.findFirst(key);
            }
            if (node == null) {
                throw new RuntimeException("Failed to lookup artifact in graph: "+key);
            }
            File jar = node.getFile();
            if (jar != null) {
                URL url = jar.toURI().toURL();
                delegate.addURL(url);
                cache.append(url.toExternalForm()).append("\n");
            }
        }
        // write build cache
        File cacheFile = new File(home, "tmp/build.cache");
        cacheFile.getParentFile().mkdirs();
        FileUtils.writeFile(cacheFile, cache.toString());
        System.out.println("Build took: "+(System.currentTimeMillis()-s)/1000);
        buildDone();
        loader = null;
    }

    public void loadConfigurationFromCache(File cacheFile) throws Exception {
        System.out.print("Building Application from Cache ... ");
        double s = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(cacheFile));
        String line = reader.readLine();
        boolean isBundle = true;
        ClassLoaderDelegate delegate = bootstrap.getLoader();
        while (line != null) {
            line = line.trim();
            if (line.length() == 0) { // switch to libs
                isBundle = false;
                line = reader.readLine();
                continue;
            }
            if (isBundle) {
                int p = line.indexOf('@');
                if (p == -1) {
                    delegate.addURL(new URL(line));
                } else {
                    File file = new File(line.substring(p+1));
                    bundles.put(line.substring(0, p), file);
                    delegate.addURL(file.toURI().toURL());
                }
            } else {
                delegate.addURL(new URL(line));
            }
            line = reader.readLine();
        }
        System.out.println((System.currentTimeMillis()-s)/1000);
    }

    public void start() throws Exception {
        aboutToStartFramework();
        bootstrap.start();
        frameworkStarted();

    }

    protected void aboutToStartFramework() throws Exception {
        // avoid redirecting logs -> maven comes with a JCL to JUL redirection that will generate infintie loops.
        // this was also fixed by excluding from eclipse dependencies the sl4j-jdk14 jar.
        System.setProperty("org.nuxeo.runtime.redirectJUL", "false");
        String h2 = System.getProperty("h2.baseDir");
        if (h2 == null) {
            h2 = new File(home, "data/h2").getAbsolutePath();
            System.setProperty("h2.baseDir", h2);
        }
        String homePath = home.getAbsolutePath();
        System.setProperty("nuxeo.home", homePath);
        if (System.getProperty("jetty.home") == null) {
            System.setProperty("jetty.home", homePath);
        }
        if (System.getProperty("jetty.logs") == null) {
            System.setProperty("jetty.logs", homePath + "/log");
        }
        if (System.getProperty("derby.system.home") == null) {
            System.setProperty("derby.system.home",
                    homePath+"/data/derby");
        }
    }

    protected void frameworkStarted() throws Exception {
//      System.out.println(System.getProperties().remove("jetty.home"));
//      System.out.println(System.getProperties().remove("jetty.logs"));
        // do nothing
    }


    public static ClassLoader findContextClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = NuxeoApp.class.getClassLoader();
        }
        return cl;
    }


    protected EmbeddedMavenClient createEmbeddedMaven() {
        return new EmbeddedMavenClient();
    }

    /**
     * TODO put configuration in a resource file?
     * @throws Exception
     */
    protected void initializeMaven() throws Exception {
        maven = createEmbeddedMaven();
        MavenClientFactory.setInstance(maven);
        maven.setOffline(isOffline);
        maven.start();
        maven.getGraph().setShouldLoadDependencyManagement(true);

        Repository repo = new Repository();
        repo.setId("public");
        repo.setName("Nuxeo Public Repository");
        repo.setLayout("default");
        repo.setUrl("http://maven.nuxeo.org/public");
        RepositoryPolicy policy = new RepositoryPolicy();
        policy.setEnabled(true);
        policy.setUpdatePolicy("never");
        policy.setChecksumPolicy("warn");
        repo.setReleases(policy);
        policy = new RepositoryPolicy();
        policy.setEnabled(false);
        repo.setSnapshots(policy);
        maven.addRemoteRepository(repo);

        repo = new Repository();
        repo.setId("public-snapshot");
        repo.setName("Nuxeo Public Snapshot Repository");
        repo.setLayout("default");
        repo.setUrl("http://maven.nuxeo.org/public-snapshot");
        policy = new RepositoryPolicy();
        policy.setEnabled(false);
        repo.setReleases(policy);
        policy = new RepositoryPolicy();
        policy.setEnabled(true);
        policy.setUpdatePolicy(updatePolicy);
        policy.setChecksumPolicy("warn");
        repo.setSnapshots(policy);
        maven.addRemoteRepository(repo);
    }

    protected void initializeGraph() throws Exception {
        for (String pom : loader.getPoms()) {
            addPom(pom);
        }
//        addPom("org.nuxeo", "nuxeo-ecm", platformVersion);
//        Node node = addPom("org.nuxeo.ecm.platform", "nuxeo-services-parent", platformVersion);
//        // find the core version corresponding to services pom
//        String coreVersion = node.getPom().getProperties().getProperty("nuxeo.core.version");
//        addPom("org.nuxeo.common", "nuxeo-common", coreVersion, 1);
//        addPom("org.nuxeo.runtime", "nuxeo-runtime-parent", coreVersion, 1);
//        addPom("org.nuxeo.ecm.core", "nuxeo-core-parent", coreVersion, 1);
//        node.expand(1, null); // now we have all the core dependencies -> expand the services pom
//        addPom("org.nuxeo.ecm.platform", "nuxeo-features-parent", platformVersion, 1);
//        addPom("org.nuxeo.ecm.webengine", "nuxeo-webengine-parent", platformVersion, 1);
//        // the other poms are not included by default - you can include them by overriding this method
    }

    protected Node addPom(String groupId, String artifactId, String version) {
        return addArtifact(groupId, artifactId, version, "pom", null, 1);
    }

    protected Node addPom(String groupId, String artifactId, String version, int expandDepth) {
        return addArtifact(groupId, artifactId, version, "pom", null, expandDepth);
    }

    protected Node addArtifact(String groupId, String artifactId, String version) {
        return addArtifact(groupId, artifactId, version, null, null, 0);
    }

    protected Node addArtifact(String groupId, String artifactId, String version, String type) {
        return addArtifact(groupId, artifactId, version, type, null, 0);
    }

    protected Node addArtifact(String groupId, String artifactId, String version, String type, String classifier) {
        return addArtifact(groupId, artifactId, version, type, classifier, 0);
    }

    protected Node addArtifact(String groupId, String artifactId, String version, String type, String classifier, int expandDepth) {
        if (groupId == null || artifactId == null || version == null) {
            throw new IllegalArgumentException("You must specify at least the groupId, artifactId and version when explicitelly adding an artifact to the graph");
        }
        String key = groupId+":"+artifactId+":"+version;
        if (type != null) {
            key = key+":"+type;
        }
        if (classifier != null) {
            if (type == null) {
                type = "jar";
            }
            key = key+":"+classifier;
        }
        return addArtifact(key, expandDepth);
    }


    protected Node addPom(String key) {
        return addArtifact(key, 1);
    }

    protected Node addArtifact(String key) {
        return addArtifact(key, 0);
    }

    protected Node addArtifact(String key, int expandDepth) {
        Node node = maven.getGraph().addRootNode(key);
        if (expandDepth > 0) {
            node.expand(expandDepth, null);
        }
        return node;
    }

    /**
     * Must be called to diable the http server. Has effect only when called
     * before starting the application
     */
    public static void disableHttpServer() {
        System.setProperty("jetty.disable", "true");
    }

    public static void setHttpServerAddress(String host, int port) {
        if (port > 0) {
            System.setProperty("jetty.port", String.valueOf(port));
        }
        if (host != null) {
            System.setProperty("jetty.host", host);
        }
    }


    protected void buildDone() {
        // do nothing
    }

    public void shutdown() throws Exception {
        bootstrap.stop();
    }


    protected void copyTemplateFiles(ArtifactDescriptor template, String templatePrefix, File targetDir) throws Exception {
        if (template.version == null) {
            throw new IllegalArgumentException("template artifact version cannot be null");
        }
        Artifact artifact = template.classifier == null ? template.toBuildArtifact() : template.toArtifactWithClassifier();
        maven.resolve(artifact);
        File file = artifact.getFile();
        if (file == null) {
            throw new FileNotFoundException("No such artifact file: "+file);
        }
        ZipUtils.unzip(templatePrefix, file, home);
    }



    public static Manifest getManifest(File file) {
        try {
            if (file.isDirectory()) {
                file = new File(file, "META-INF/MANIFEST.MF");
                FileInputStream in = new FileInputStream(file);
                try {
                    return new Manifest(in);
                } finally {
                    in.close();
                }
            } else {
                JarFile jar = new JarFile(file);
                try {
                    return jar.getManifest();
                } finally {
                    jar.close();
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSymbolicName(Manifest mf) {
        if (mf == null) {
            return null;
        }
        String value = mf.getMainAttributes().getValue(
                "Bundle-SymbolicName");
        if (value == null) {
            return null;
        }
        int p = value.indexOf(';');
        if (p > 0) {
            return value.substring(0, p).trim();
        } else {
            return value;
        }
    }

}
