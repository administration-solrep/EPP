/* 
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.ecm.automation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class OperationDocumentation implements
        Comparable<OperationDocumentation>, Serializable {

    private static final long serialVersionUID = 1L;

    public String id;

    /**
     * an array of size multiple of 2. Each pair in the array is the input and
     * output type of a method
     */
    public String[] signature;

    public String category;

    public String label;

    public String requires;

    public String since;

    public String description;

    public List<Param> params;

    // optional URL indicating the relative path (relative to the automation
    // service home)
    // of the page where the operation is exposed
    public String url;

    public OperationDocumentation(String id) {
        this.id = id;
        this.url = id;
    }

    public int compareTo(OperationDocumentation o) {
        String s1 = label == null ? id : label;
        String s2 = o.label == null ? o.id : o.label;
        return s1.compareTo(s2);
    }

    public String getDescription() {
        return description;
    }

    public String[] getSignature() {
        return signature;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    public String getRequires() {
        return requires;
    }

    public List<Param> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return category + " > " + label + " [" + id + ": "
                + Arrays.asList(signature) + "] (" + params + ")\n"
                + description;
    }

    public static class Param implements Serializable, Comparable<Param> {
        private static final long serialVersionUID = 1L;

        public String name;

        public String type; // the data type

        public String widget; // the widget type

        public String[] values; // the default values

        public int order;

        public boolean isRequired;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getWidget() {
            return widget;
        }

        public String[] getValues() {
            return values;
        }

        public boolean isRequired() {
            return isRequired;
        }

        public int getOrder() {
            return order;
        }

        @Override
        public String toString() {
            return name + " [" + type + "] "
                    + (isRequired ? "required" : "optional");
        }

        public int compareTo(Param o) {
            if (order != 0 && o.order != 0) {
                if (order < o.order) {
                    return -1;
                } else if (order > o.order) {
                    return 1;
                }
            }
            if (isRequired && !o.isRequired) {
                return -1;
            }
            if (o.isRequired && !isRequired) {
                return 1;
            }
            return name.compareTo(o.name);
        }
    }
}
