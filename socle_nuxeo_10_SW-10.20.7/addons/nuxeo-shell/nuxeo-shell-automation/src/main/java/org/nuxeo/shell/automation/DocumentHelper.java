/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.shell.automation;

import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.shell.ShellConsole;
import org.nuxeo.shell.utils.Path;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DocumentHelper {

    public static void printName(ShellConsole console, Document doc) {
        String name = new Path(doc.getPath()).lastSegment();
        if (name == null) {
            name = "/";
        }
        console.println(name);
    }

    public static void printName(ShellConsole console, Document doc, String prefix) {
        String name = new Path(doc.getPath()).lastSegment();
        if (name == null) {
            name = "/";
        }
        console.println(prefix + name);
    }

    public static void printPath(ShellConsole console, Document doc) {
        console.println(doc.getPath());
    }

}
