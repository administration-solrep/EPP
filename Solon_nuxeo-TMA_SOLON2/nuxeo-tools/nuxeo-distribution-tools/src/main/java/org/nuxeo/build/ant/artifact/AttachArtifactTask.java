/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *
 * bstefanescu, jcarsique, $Id$
 */

package org.nuxeo.build.ant.artifact;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.nuxeo.build.maven.AntBuildMojo;
import org.nuxeo.build.maven.MavenClient;
import org.nuxeo.build.maven.MavenClientFactory;
import org.nuxeo.build.maven.graph.Node;

/**
 * Attaches the artifact to Maven.
 * 
 * @author Kohsuke Kawaguchi
 */
public class AttachArtifactTask extends Task {

    private File file;

    private String classifier;

    private String type;

    private String target;

    /**
     * The file to be treated as an artifact.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Optional classifier. If left unset, the task will attach the main
     * artifact.
     */
    public void setClassifier(String classifier) {
        this.classifier = "".equals(classifier) ? null : classifier;
    }

    public void setTarget(String artifactKey) {
        this.target = artifactKey;
    }

    /**
     * Artifact type. Think of it as a file extension. Optional, and if omitted,
     * inferred from the file extension.
     */
    public void setType(String type) {
        this.type = type;
    }

    public void execute() throws BuildException {
        final MavenClient maven = MavenClientFactory.getInstance();

        if (target == null) {
            throw new BuildException("Target artifact not set");
        }
        final Node node = maven.getGraph().findFirst(target, true);
        if (node == null) {
            throw new BuildException("No such artifact found: " + target);
        }

        log("Attaching " + file + " to " + target, Project.MSG_INFO);
        if (type == null) {
            type = getExtension(file.getName());
            log("Unspecified type, guessing is: " + type, Project.MSG_WARN);
        }
        if (classifier != null) {
            maven.getProjectHelper().attachArtifact(node.getPom(), type,
                    classifier, file);
        } else {
            node.getPom().getArtifact().setFile(file);
            node.getPom().getArtifact().setResolved(true);
//            maven.getProjectHelper().attachArtifact(node.getPom(), type, file);
        }
    }

    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        return name.substring(idx + 1);
    }
}
