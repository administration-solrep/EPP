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
package org.nuxeo.shell.cmds;

/**
 * Give a chance to embedded shells to do some customization before shell is started and when exit is required.
 * <p>
 * This is currently used by shell applet to correctly handle start and stop.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface InteractiveShellHandler {

    /**
     * Interactive shell will be started.
     */
    void enterInteractiveMode();

    /**
     * Interactive shell should be disposed. This method should dispose the current session. If no handler is defined
     * the Java process will exit with the given code (if <= 0 - normal exit, otherwise an exit with given code is
     * performed). Return true to exit the shell loop, false otherwise.
     *
     * @param code
     */
    boolean exitInteractiveMode(int code);

}
