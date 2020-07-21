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
package org.nuxeo.build.ant.artifact;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.nuxeo.build.maven.MavenClientFactory;
import org.nuxeo.build.maven.graph.Edge;
import org.nuxeo.build.maven.graph.Graph;
import org.nuxeo.build.maven.graph.Node;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * 
 */
public class PrintGraphTask extends Task {

    private OutputStream output = System.out;

    @Override
    public void execute() throws BuildException {
        HashSet<Node> colectedNodes = new HashSet<Node>();
        Graph graph = MavenClientFactory.getInstance().getGraph();
        for (Node node : graph.getRoots()) {
            try {
                print(" ", node, colectedNodes);
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }
    }

    protected void print(String tabs, Node node, Set<Node> collectedNodes)
            throws IOException {
        print(tabs + "" + node.toString()+System.getProperty("line.separator"));
        if (collectedNodes.contains(node)) {
            return;
        }
        collectedNodes.add(node);
        for (Edge edge : node.getEdgesOut()) {
            print(tabs + " |-- ", edge.dst, collectedNodes);
        }
    }

    public void setOutput(String output) throws FileNotFoundException {
        this.output = new FileOutputStream(output);
    }

    private void print(String message) throws IOException {
        output.write(message.getBytes());
    }
}
