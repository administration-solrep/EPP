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
package org.nuxeo.ecm.automation.core.scripting;


/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class Functions {

    private static volatile Object fn = new CoreFunctions();

    private Functions() {
    }

    public static void setInstance(Object fn) {
        if (fn == null) {
            fn = new CoreFunctions();
        }
        synchronized (Functions.class) {
            Functions.fn = fn;
        }
    }

    public static Object getInstance() {
        Object o = fn;
        if (o == null) {
            synchronized (Functions.class) {
                o = new CoreFunctions();
                fn = o;
            }
        }
        return o;
    }

}
