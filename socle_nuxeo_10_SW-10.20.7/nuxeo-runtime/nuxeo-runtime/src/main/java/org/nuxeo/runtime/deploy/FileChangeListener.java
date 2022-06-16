/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * $Id$
 */

package org.nuxeo.runtime.deploy;

// FIXME: interface has changed and this example is no more appropriate.
/**
 * An example of listener implementation:
 *
 * <pre>
 * public class MyListener implements FileChangeListener {
 *     long lastNotif = 0;
 *
 *     public void fileChanged(File file, long since, long now) {
 *         if (now == lastNotifFlush)
 *             return;
 *         if (isIntersetedInFile(file)) {
 *             lastNotif = now;
 *             flushCache(); // flush internal cache because file on disk changed
 *         }
 *     }
 * }
 * </pre>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public interface FileChangeListener {

    /**
     * Notifies that the given file changed.
     *
     * @param entry
     * @param now the time stamp when the change was detected. This value can be used as a notification ID by listeners
     *            to avoid multiple processing for notification that will send multiple events
     */
    void fileChanged(FileChangeNotifier.FileEntry entry, long now);

}
