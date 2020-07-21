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
package org.nuxeo.build.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.tools.ant.BuildException;
import org.nuxeo.build.ant.AntClient;
import org.nuxeo.build.ant.profile.AntProfileManager;
import org.nuxeo.build.maven.graph.Graph;
import org.nuxeo.build.maven.graph.Node;

/**
 * 
 * @goal build
 * @phase package
 * 
 * @requiresDependencyResolution runtime
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class AntBuildMojo extends AbstractMojo implements MavenClient {

    protected Graph graph;

    protected AntProfileManager antProfileManager;

    /**
     * Location of the build file, if unique
     * 
     * @parameter expression="${buildFile}" default-value="build.xml"
     * @deprecated prefer use of buildFiles
     */
    protected File buildFile;

    /**
     * Location of the build files.
     * 
     * @parameter expression="${buildFiles}"
     */
    protected String[] buildFiles;

    /**
     * Location of the build file.
     * 
     * @parameter expression="${target}" default-value=""
     * @required
     */
    protected String target;

    /**
     * How many levels the graph must be expanded before running ant.
     * 
     * @parameter expression="${expand}" default-value="0"
     */
    protected int expand;

    /**
     * Location of the file.
     * 
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    /**
     * Maven ProjectHelper
     * 
     * @component
     * @readonly
     */
    protected MavenProjectHelper projectHelper;

    /**
     * Location of the local repository.
     * 
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected org.apache.maven.artifact.repository.ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     * 
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected java.util.List<ArtifactRepository> remoteRepositories;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.factory.ArtifactFactory factory;

    /**
     * Used to look up Artifacts in the remote repository.
     * 
     * @component
     * @required
     * @readonly
     */
    protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

    /**
     * 
     * @component
     * @readonly
     */
    protected MavenProjectBuilder projectBuilder;

    private Logger logger;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {
        AntClient ant = new AntClient();
        MavenClientFactory.setInstance(this);
        logger = new Logger() {

            public void debug(String message) {
                getLog().debug(message);
            }

            public void debug(String message, Throwable error) {
                getLog().debug(message, error);
            }

            public void error(String message) {
                getLog().error(message);
            }

            public void error(String message, Throwable error) {
                getLog().error(message, error);
            }

            public void info(String message) {
                getLog().info(message);
            }

            public void info(String message, Throwable error) {
                getLog().info(message, error);
            }

            public void warn(String message) {
                getLog().warn(message);
            }

            public void warn(String message, Throwable error) {
                getLog().warn(message, error);
            }

            public boolean isDebugEnabled() {
                return getLog().isDebugEnabled();
            }
        };
        antProfileManager = new AntProfileManager();

        // add project properties
        HashMap<String, String> props = new HashMap<String, String>();
        for (Map.Entry entry : project.getProperties().entrySet()) {
            props.put(entry.getKey().toString(), entry.getValue().toString());
        }
        props.put("maven.basedir", project.getBasedir().getAbsolutePath());
        props.put("maven.project.name", project.getName());
        props.put("maven.project.artifactId", project.getArtifactId());
        props.put("maven.project.groupId", project.getGroupId());
        props.put("maven.project.version", project.getVersion());
        props.put("maven.project.packaging", project.getPackaging());
        props.put("maven.project.id", project.getId());
        props.put("maven.project.build.directory",
                project.getBuild().getDirectory());
        props.put("maven.project.build.outputDirectory",
                project.getBuild().getOutputDirectory());
        props.put("maven.project.build.finalName",
                project.getBuild().getFinalName());

        // add active maven profiles to ant
        List<Profile> profiles = getActiveProfiles();
        for (Profile profile : profiles) {
            antProfileManager.activateProfile(profile.getId(), true);
            // define a property for each activate profile (so you can use it in
            // ant conditional expression)
            props.put("maven.profile." + profile.getId(), "true");
        }

        ant.setGlobalProperties(props);

        if (buildFile != null && (buildFiles == null || buildFiles.length == 0)) {
            buildFiles = new String[] { buildFile.getPath() };
        }
        for (String file : buildFiles) {
            graph = new Graph(this);

            Node root = graph.addRootNode(project);
            if (expand > 0) {
                root.expand(expand, null);
            }

            try {
                if (target != null && target.length() > 0) {
                    ArrayList<String> targets = new ArrayList<String>();
                    targets.add(target);
                    ant.run(new File(file), targets);
                } else {
                    ant.run(new File(file));
                }
            } catch (BuildException e) {
                throw new MojoExecutionException("Failed to run " + file, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Profile> getActiveProfiles() {
        return project.getActiveProfiles();
    }

    public MavenProject getProject() {
        return project;
    }

    public ArtifactFactory getArtifactFactory() {
        return factory;
    }

    public ArtifactResolver getArtifactResolver() {
        return resolver;
    }

    public ArtifactRepository getLocalRepository() {
        return localRepository;
    }

    public MavenProjectBuilder getProjectBuilder() {
        return projectBuilder;
    }

    public MavenProjectHelper getProjectHelper() {
        return projectHelper;
    }

    public List<ArtifactRepository> getRemoteRepositories() {
        return remoteRepositories;
    }

    public void resolve(Artifact artifact,
            List<ArtifactRepository> otherRemoteRepositories)
            throws ArtifactNotFoundException {
        try {
            resolver.resolve(artifact, otherRemoteRepositories, localRepository);
        } catch (ArtifactResolutionException e) {
            throw new RuntimeException(e);
        } catch (ArtifactNotFoundException e) {
            throw e;
        }
    }

    public void resolve(Artifact artifact) throws ArtifactNotFoundException {
        try {
            resolver.resolve(artifact, remoteRepositories, localRepository);
        } catch (ArtifactResolutionException e) {
            throw new RuntimeException(e);
        } catch (ArtifactNotFoundException e) {
            throw e;
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public AntProfileManager getAntProfileManager() {
        return antProfileManager;
    }

    public Logger getCommonLogger() {
        return logger;
    }

}
