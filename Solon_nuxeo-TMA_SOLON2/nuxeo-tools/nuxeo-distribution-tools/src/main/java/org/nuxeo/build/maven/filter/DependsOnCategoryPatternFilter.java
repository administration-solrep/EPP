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
 *     Julien Carsique
 *
 * $Id$
 */

package org.nuxeo.build.maven.filter;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.nuxeo.build.maven.MavenClientFactory;
import org.nuxeo.build.maven.graph.Edge;
import org.nuxeo.build.maven.graph.Node;

/**
 * 
 * @author jcarsique An artifact satisfies this filter if at least one of its
 *         dependencies match ManifestBundleCategoryPatternFilter conditions
 * @see ManifestBundleCategoryPatternFilter
 * @deprecated replaced with {@link ManifestBundleCategoryFilter}
 * 
 */
public class DependsOnCategoryPatternFilter implements Filter {

    private AndFilter categoryFilter;

    private String pattern;

    public DependsOnCategoryPatternFilter(String pattern) {
        this.pattern = pattern;
        categoryFilter = new AndFilter();
        categoryFilter.addFilter(ManifestBundleCategoryPatternFilter.class,
                pattern);

    }

    public boolean accept(Node parent, Dependency dep) {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean accept(Artifact artifact) {
        return categoryFilter.accept(artifact);
    }

    public boolean accept(Edge edge) {
        throw new UnsupportedOperationException("Not supported");
    }

    public boolean accept(Node node) {
        // Exclude non Nuxeo artifacts
        if (!node.getArtifact().getGroupId().startsWith("org.nuxeo")) {
            return false;
        }
        boolean accept = accept(node.getArtifact());
        if (MavenClientFactory.getLog().isDebugEnabled()) {
            MavenClientFactory.getLog().debug(
                    DependsOnCategoryPatternFilter.class + " filtering "
                            + node.getArtifact() + " on pattern " + pattern
                            + " ...");
        }
        List<Edge> children = node.getEdgesOut();
        if (!accept && children != null) {
            for (Edge childEdge : children) {
                if (accept(childEdge.dst)) {
                    accept = true;
                    break;
                }
            }
        }
        if (MavenClientFactory.getLog().isDebugEnabled()) {
            MavenClientFactory.getLog().debug(
                    "Filtering on pattern " + pattern + " result for "
                            + node.getArtifact() + " : " + accept);
        }
        return accept;
    }

}
