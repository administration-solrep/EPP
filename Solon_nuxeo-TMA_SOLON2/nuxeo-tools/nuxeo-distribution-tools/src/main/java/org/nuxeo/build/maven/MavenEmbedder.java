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
 *     bstefanescu, jcarsique
 */

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nuxeo.build.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.BuildFailureException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.embedder.MavenEmbedderLoggerManager;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.embedder.SummaryPluginDescriptor;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.lifecycle.LifecycleExecutor;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.monitor.event.DefaultEventDispatcher;
import org.apache.maven.monitor.event.EventDispatcher;
import org.apache.maven.monitor.event.EventMonitor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptorBuilder;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MissingProjectException;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.RuntimeInfo;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Class intended to be used by clients who wish to embed Maven into their
 * applications
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 */
public class MavenEmbedder {
    public static final String userHome = System.getProperty("user.home");

    // ----------------------------------------------------------------------
    // Embedder
    // ----------------------------------------------------------------------

    protected Embedder embedder;

    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    protected MavenProjectBuilder mavenProjectBuilder;

    protected ArtifactRepositoryFactory artifactRepositoryFactory;

    protected MavenSettingsBuilder settingsBuilder;

    protected LifecycleExecutor lifecycleExecutor;

    protected WagonManager wagonManager;

    protected MavenXpp3Reader modelReader;

    protected MavenXpp3Writer modelWriter;

    protected ProfileManager profileManager;

    protected PluginDescriptorBuilder pluginDescriptorBuilder;

    protected ArtifactFactory artifactFactory;

    protected ArtifactResolver artifactResolver;

    protected ArtifactRepositoryLayout defaultArtifactRepositoryLayout;

    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    protected Settings settings;

    protected ArtifactRepository localRepository;

    protected File localRepositoryDirectory = new File(userHome, ".m2/repository"); // the default local repository

    protected ClassLoader classLoader;

    protected MavenEmbedderLogger logger;

    // ----------------------------------------------------------------------
    // User options
    // ----------------------------------------------------------------------

    // release plugin uses this but in IDE there will probably always be some
    // form of interaction.
    protected boolean interactiveMode;

    protected boolean offline;

    protected String globalChecksumPolicy;

    /**
     * This option determines whether the embedder is to be aligned to the user
     * installation.
     */
    protected boolean alignWithUserInstallation;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public void setInteractiveMode(boolean interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

    public boolean isInteractiveMode() {
        return interactiveMode;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setGlobalChecksumPolicy(String globalChecksumPolicy) {
        this.globalChecksumPolicy = globalChecksumPolicy;
    }

    public String getGlobalChecksumPolicy() {
        return globalChecksumPolicy;
    }

    public boolean isAlignWithUserInstallation() {
        return alignWithUserInstallation;
    }

    public void setAlignWithUserInstallation(boolean alignWithUserInstallation) {
        this.alignWithUserInstallation = alignWithUserInstallation;
    }

    /**
     * Set the classloader to use with the maven embedder.
     * 
     * @param classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setLocalRepositoryDirectory(File localRepositoryDirectory) {
        this.localRepositoryDirectory = localRepositoryDirectory;
    }

    public File getLocalRepositoryDirectory() {
        return localRepositoryDirectory;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public MavenEmbedderLogger getLogger() {
        return logger;
    }

    public void setLogger(MavenEmbedderLogger logger) {
        this.logger = logger;
    }

    // ----------------------------------------------------------------------
    // Embedder Client Contract
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Model
    // ----------------------------------------------------------------------

    public Model readModel(File model) throws XmlPullParserException,
            FileNotFoundException, IOException {
        return modelReader.read(new FileReader(model));
    }

    public void writeModel(Writer writer, Model model) throws IOException {
        modelWriter.write(writer, model);
    }

    // ----------------------------------------------------------------------
    // Project
    // ----------------------------------------------------------------------

    public MavenProject readProject(File mavenProject)
            throws ProjectBuildingException {
        return mavenProjectBuilder.build(mavenProject, localRepository,
                profileManager);
    }

    public MavenProject readProjectWithDependencies(File mavenProject,
            TransferListener transferListener) throws ProjectBuildingException,
            ArtifactResolutionException, ArtifactNotFoundException {
        return mavenProjectBuilder.buildWithDependencies(mavenProject,
                localRepository, profileManager, transferListener);
    }

    public MavenProject readProjectWithDependencies(File mavenProject)
            throws ProjectBuildingException, ArtifactResolutionException,
            ArtifactNotFoundException {
        return mavenProjectBuilder.buildWithDependencies(mavenProject,
                localRepository, profileManager);
    }

    public List<MavenProject> collectProjects(File basedir, String[] includes,
            String[] excludes) throws MojoExecutionException {
        List<MavenProject> projects = new ArrayList<MavenProject>();

        List<File> poms = getPomFiles(basedir, includes, excludes);

        for (Iterator<File> i = poms.iterator(); i.hasNext();) {
            File pom = i.next();

            try {
                MavenProject p = readProject(pom);

                projects.add(p);

            } catch (ProjectBuildingException e) {
                throw new MojoExecutionException("Error loading " + pom, e);
            }
        }

        return projects;
    }

    // ----------------------------------------------------------------------
    // Artifacts
    // ----------------------------------------------------------------------

    public Artifact createArtifact(String groupId, String artifactId,
            String version, String scope, String type) {
        return artifactFactory.createArtifact(groupId, artifactId, version,
                scope, type);
    }

    public Artifact createArtifactWithClassifier(String groupId,
            String artifactId, String version, String type, String classifier) {
        return artifactFactory.createArtifactWithClassifier(groupId,
                artifactId, version, type, classifier);
    }

    public void resolve(Artifact artifact, List<?> remoteRepositories,
            ArtifactRepository localRepositoryToResolveFrom)
            throws ArtifactResolutionException, ArtifactNotFoundException {
        artifactResolver.resolve(artifact, remoteRepositories,
                localRepositoryToResolveFrom);
    }

    // ----------------------------------------------------------------------
    // Plugins
    // ----------------------------------------------------------------------

    public List<SummaryPluginDescriptor> getAvailablePlugins() {
        List<SummaryPluginDescriptor> plugins = new ArrayList<SummaryPluginDescriptor>();

        plugins.add(makeMockPlugin("org.apache.maven.plugins",
                "maven-jar-plugin", "Maven Jar Plug-in"));

        plugins.add(makeMockPlugin("org.apache.maven.plugins",
                "maven-compiler-plugin", "Maven Compiler Plug-in"));

        return plugins;
    }

    public PluginDescriptor getPluginDescriptor(
            SummaryPluginDescriptor summaryPluginDescriptor)
            throws MavenEmbedderException {
        PluginDescriptor pluginDescriptor;

        try {
            InputStream is = classLoader.getResourceAsStream("/plugins/"
                    + summaryPluginDescriptor.getArtifactId() + ".xml");

            pluginDescriptor = pluginDescriptorBuilder.build(new InputStreamReader(
                    is));
        } catch (PlexusConfigurationException e) {
            throw new MavenEmbedderException(
                    "Error retrieving plugin descriptor.", e);
        }

        return pluginDescriptor;
    }

    private SummaryPluginDescriptor makeMockPlugin(String groupId,
            String artifactId, String name) {
        return new SummaryPluginDescriptor(groupId, artifactId, name);
    }

    // ----------------------------------------------------------------------
    // Execution of phases/goals
    // ----------------------------------------------------------------------

    // TODO: should we allow the passing in of a settings object so that
    // everything can be taken from the client env
    // TODO: transfer listener
    // TODO: logger

    public void execute(MavenProject project, List<?> goals,
            EventMonitor eventMonitor, TransferListener transferListener,
            Properties properties, File executionRootDirectory)
            throws CycleDetectedException, LifecycleExecutionException,
            BuildFailureException, DuplicateProjectException, MissingProjectException {
        execute(Collections.singletonList(project), goals, eventMonitor,
                transferListener, properties, executionRootDirectory);
    }

    public void execute(List<MavenProject> projects, List<?> goals,
            EventMonitor eventMonitor, TransferListener transferListener,
            Properties properties, File executionRootDirectory)
            throws CycleDetectedException, LifecycleExecutionException,
            BuildFailureException, DuplicateProjectException, MissingProjectException {
        ReactorManager rm = new ReactorManager(projects);

        EventDispatcher eventDispatcher = new DefaultEventDispatcher();

        eventDispatcher.addEventMonitor(eventMonitor);

        // If this option is set the exception seems to be hidden ...

        // rm.setFailureBehavior( ReactorManager.FAIL_AT_END );

        rm.setFailureBehavior(ReactorManager.FAIL_FAST);

        MavenSession session = new MavenSession(embedder.getContainer(),
                settings, localRepository, eventDispatcher, rm, goals,
                executionRootDirectory.getAbsolutePath(), properties,
                new Date());

        session.setUsingPOMsFromFilesystem(true);

        if (transferListener != null) {
            wagonManager.setDownloadMonitor(transferListener);
        }

        // ----------------------------------------------------------------------
        // Maven should not be using system properties internally but because
        // it does for now I'll just take properties that are handed to me
        // and set them so that the plugin expression evaluator will work
        // as expected.
        // ----------------------------------------------------------------------

        if (properties != null) {
            for (Iterator<?> i = properties.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();

                String value = properties.getProperty(key);

                System.setProperty(key, value);
            }
        }

        lifecycleExecutor.execute(session, rm, session.getEventDispatcher());
    }

    // ----------------------------------------------------------------------
    // Lifecycle information
    // ----------------------------------------------------------------------

    public List<String> getLifecyclePhases() throws MavenEmbedderException {
        List<String> phases = new ArrayList<String>();

        ComponentDescriptor descriptor = embedder.getContainer().getComponentDescriptor(
                LifecycleExecutor.ROLE);

        PlexusConfiguration configuration = descriptor.getConfiguration();

        PlexusConfiguration[] phasesConfigurations = configuration.getChild(
                "lifecycles").getChild(0).getChild("phases").getChildren(
                "phase");

        try {
            for (int i = 0; i < phasesConfigurations.length; i++) {
                phases.add(phasesConfigurations[i].getValue());
            }
        } catch (PlexusConfigurationException e) {
            throw new MavenEmbedderException(
                    "Cannot retrieve default lifecycle phasesConfigurations.",
                    e);
        }

        return phases;
    }

    // ----------------------------------------------------------------------
    // Remote Repository
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Local Repository
    // ----------------------------------------------------------------------

    public static final String DEFAULT_LOCAL_REPO_ID = "local";

    public static final String DEFAULT_LAYOUT_ID = "default";

    public ArtifactRepository createLocalRepository(File repository) {
        return createLocalRepository(repository.getAbsolutePath(),
                DEFAULT_LOCAL_REPO_ID);
    }

    public ArtifactRepository createLocalRepository() {
        return createLocalRepository(settings.getLocalRepository(),
                DEFAULT_LOCAL_REPO_ID);
    }

    public ArtifactRepository createLocalRepository(String url,
            String repositoryId) {
        if (!url.startsWith("file:")) {
            url = "file://" + url;
        }

        return createRepository(url, repositoryId);
    }

    public ArtifactRepository createRepository(String url, String repositoryId) {
        // snapshots vs releases
        // offline = to turning the update policy off

        // TODO: we'll need to allow finer grained creation of repositories but
        // this will do for now

        String updatePolicyFlag = ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

        String checksumPolicyFlag = ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN;

        ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy(
                true, updatePolicyFlag, checksumPolicyFlag);

        ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy(
                true, updatePolicyFlag, checksumPolicyFlag);

        return artifactRepositoryFactory.createArtifactRepository(repositoryId,
                url, defaultArtifactRepositoryLayout, snapshotsPolicy,
                releasesPolicy);
    }

    // ----------------------------------------------------------------------
    // Internal utility code
    // ----------------------------------------------------------------------

    private RuntimeInfo createRuntimeInfo() {
        RuntimeInfo runtimeInfo = new RuntimeInfo(settings);

        runtimeInfo.setPluginUpdateOverride(Boolean.FALSE);

        return runtimeInfo;
    }

    private List<File> getPomFiles(File basedir, String[] includes,
            String[] excludes) {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir(basedir);

        scanner.setIncludes(includes);

        scanner.setExcludes(excludes);

        scanner.scan();

        List<File> poms = new ArrayList<File>();

        for (int i = 0; i < scanner.getIncludedFiles().length; i++) {
            poms.add(new File(basedir, scanner.getIncludedFiles()[i]));
        }

        return poms;
    }

    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void start() throws MavenEmbedderException {
        detectUserInstallation();

        // ----------------------------------------------------------------------
        // Set the maven.home system property which is need by components like
        // the plugin registry builder.
        // ----------------------------------------------------------------------

        if (classLoader == null) {
            throw new IllegalStateException(
                    "A classloader must be specified using setClassLoader(ClassLoader).");
        }

        embedder = new Embedder();

        if (logger != null) {
            embedder.setLoggerManager(new MavenEmbedderLoggerManager(
                    new PlexusLoggerAdapter(logger)));
        }

        try {
            ClassWorld classWorld = new ClassWorld();

            classWorld.newRealm("plexus.core", classLoader);

            embedder.start(classWorld);

            // ----------------------------------------------------------------------
            // Lookup each of the components we need to provide the desired
            // client interface.
            // ----------------------------------------------------------------------

            modelReader = new MavenXpp3Reader();

            modelWriter = new MavenXpp3Writer();

            pluginDescriptorBuilder = new PluginDescriptorBuilder();

            profileManager = new DefaultProfileManager(embedder.getContainer(),
                    (Properties) null);

            mavenProjectBuilder = (MavenProjectBuilder) embedder.lookup(MavenProjectBuilder.ROLE);

            // ----------------------------------------------------------------------
            // Artifact related components
            // ----------------------------------------------------------------------

            artifactRepositoryFactory = (ArtifactRepositoryFactory) embedder.lookup(ArtifactRepositoryFactory.ROLE);

            artifactFactory = (ArtifactFactory) embedder.lookup(ArtifactFactory.ROLE);

            artifactResolver = (ArtifactResolver) embedder.lookup(ArtifactResolver.ROLE);

            defaultArtifactRepositoryLayout = (ArtifactRepositoryLayout) embedder.lookup(
                    ArtifactRepositoryLayout.ROLE, DEFAULT_LAYOUT_ID);

            lifecycleExecutor = (LifecycleExecutor) embedder.lookup(LifecycleExecutor.ROLE);

            wagonManager = (WagonManager) embedder.lookup(WagonManager.ROLE);

            createMavenSettings();

            profileManager.loadSettingsProfiles(settings);

            localRepository = createLocalRepository();
        } catch (PlexusContainerException e) {
            throw new MavenEmbedderException("Cannot start Plexus embedder.", e);
        } catch (DuplicateRealmException e) {
            throw new MavenEmbedderException(
                    "Cannot create Classworld realm for the embedder.", e);
        } catch (ComponentLookupException e) {
            throw new MavenEmbedderException(
                    "Cannot lookup required component.", e);
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void detectUserInstallation() {
        if (new File(userHome, ".m2").exists()) {
            alignWithUserInstallation = true;
        }
    }
    
    protected void createM2WorkingDir() {
        
        new File(userHome, ".m2/repository").mkdirs();
    }

    /**
     * Create the Settings that will be used with the embedder. If we are
     * aligning with the user installation then we lookup the standard settings
     * builder and use that to create our settings. Otherwise we constructs a
     * settings object and populate the information ourselves.
     * 
     * @throws MavenEmbedderException
     * @throws ComponentLookupException
     */
    protected void createMavenSettings() throws MavenEmbedderException,
            ComponentLookupException {
        if (alignWithUserInstallation) {
            // ----------------------------------------------------------------------
            // We will use the standard method for creating the settings. This
            // method reproduces the method of building the settings from the
            // CLI
            // mode of operation.
            // ----------------------------------------------------------------------

            settingsBuilder = (MavenSettingsBuilder) embedder.lookup(MavenSettingsBuilder.ROLE);

            try {
                settings = settingsBuilder.buildSettings();
            } catch (IOException e) {
                throw new MavenEmbedderException("Error creating settings.", e);
            } catch (XmlPullParserException e) {
                throw new MavenEmbedderException("Error creating settings.", e);
            }
        } else {
            if (localRepository == null) {
                localRepository = createLocalRepository(localRepositoryDirectory);
//                throw new IllegalArgumentException(
//                        "When not aligning with a user install you must specify a local repository location using the setLocalRepositoryDirectory( File ) method.");
            }

            settings = new Settings();

            settings.setLocalRepository(localRepositoryDirectory.getAbsolutePath());

            settings.setRuntimeInfo(createRuntimeInfo());

            settings.setOffline(offline);

            settings.setInteractiveMode(interactiveMode);
            
        }
                
        //bs: correctly init wagon 
        initWagonFromSettings();
        // end bs
    }

    // bs: init wagon manager from user settings 
    protected void initWagonFromSettings() {
        wagonManager.setOnline(!isOffline());
        Proxy proxy = settings.getActiveProxy();
        if (proxy != null) {
            wagonManager.addProxy(proxy.getProtocol(), proxy.getHost(), proxy.getPort(), proxy.getUsername(), proxy.getPassword(), proxy.getNonProxyHosts());
        }
    }
    // end bs 
    
    // ----------------------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------------------

    public void stop() throws MavenEmbedderException {
        try {
            embedder.release(mavenProjectBuilder);

            embedder.release(artifactRepositoryFactory);

            embedder.release(settingsBuilder);

            embedder.release(lifecycleExecutor);
        } catch (ComponentLifecycleException e) {
            throw new MavenEmbedderException("Cannot stop the embedder.", e);
        }
    }
}
