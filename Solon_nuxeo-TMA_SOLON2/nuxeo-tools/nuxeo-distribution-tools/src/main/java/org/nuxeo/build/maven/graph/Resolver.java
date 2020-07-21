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
package org.nuxeo.build.maven.graph;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Relocation;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.nuxeo.build.maven.MavenClientFactory;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class Resolver {

    protected Graph graph;

    public Resolver(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void resolve(Node node) {
        if (node.pom != null || node.artifact.isResolved()) {
            return;
        }
        MavenProject pom = loadPom(node.artifact);
        if (pom == null) {
            return;
        }
        pom = checkRelocation(node, pom);
        try {
            if (!node.artifact.isResolved()) {
                // TODO remote repos from pom
                graph.maven.resolve(node.artifact);
            }
        } catch (ArtifactNotFoundException e) {
            MavenClientFactory.getLog().warn(e.getMessage());
        }
        node.pom = pom;
    }

    private MavenProject checkRelocation(Node node, MavenProject pom) {
        DistributionManagement dm = pom.getDistributionManagement();
        if (dm != null) {
            Relocation reloc = dm.getRelocation(); // handle pom relocation
            if (reloc != null) {
                Artifact artifact = node.artifact;
                Artifact orig = artifact;
                String artifactId = reloc.getArtifactId();
                String groupId = reloc.getGroupId();
                String version = reloc.getVersion();
                if (artifactId == null) {
                    artifactId = artifact.getArtifactId();
                }
                if (groupId == null) {
                    groupId = artifact.getGroupId();
                }
                if (version == null) {
                    version = artifact.getVersion();
                }
                node.artifact = graph.maven.getArtifactFactory().createArtifact(
                        groupId, artifactId, version, artifact.getScope(),
                        artifact.getType());
                MavenClientFactory.getLog().info(
                        "Artifact " + orig + " was relocated to:  "
                                + node.artifact);
                pom = loadPom(node.artifact);
            }
        }
        return pom;
    }

    public MavenProject loadPom(Artifact artifact) {
        if ("system".equals(artifact.getScope()))
            return null;
        try {
            return graph.maven.getProjectBuilder().buildFromRepository(
                    // this create another Artifact instance whose type is 'pom'
                    graph.maven.getArtifactFactory().createProjectArtifact(
                            artifact.getGroupId(), artifact.getArtifactId(),
                            artifact.getVersion()),
                    graph.maven.getRemoteRepositories(),
                    graph.maven.getLocalRepository());
        } catch (ProjectBuildingException e) {
            MavenClientFactory.getLog().error(
                    "Error loading POM of " + artifact, e);
            return null;
        }
    }

}
