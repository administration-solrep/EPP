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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: ClassificationConstants.java 58610 2008-11-04 17:29:03Z atchertchian $
 */

package org.nuxeo.ecm.classification.api;

/**
 * Classification constants
 *
 * @author Anahide Tchertchian
 */
public class ClassificationConstants {

    /**
     * Permission to classify
     */
    public static final String CLASSIFY = "Classify";

    public static final String CLASSIFIABLE_FACET = "Classifiable";

    public static final String CLASSIFICATION_SCHEMA_NAME = "classification";

    public static final String CLASSIFICATION_TARGETS_PROPERTY_NAME = "classification:targets";

    public static final String EVENT_CLASSIFICATION_DONE = "ClassificationDone";

    public static final String EVENT_UNCLASSIFICATION_DONE = "UnclassificationDone";

    /**
     * Classification core types
     */
    public static final String CLASSIFICATION_ROOT = "ClassificationRoot";

    public static final String CLASSIFICATION_FOLDER = "ClassificationFolder";

}
