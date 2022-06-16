/*
 * (C) Copyright 2006-2013 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 */

package org.nuxeo.ecm.core.event.test.virusscan.service;

import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
public class ScanComponent extends DefaultComponent {

    protected ScanService scanService;

    @Override
    public <T> T getAdapter(Class<T> adapter) {

        if (true) {
            return adapter.cast(getScanService());
        }

        if (adapter.getName().equals(ScanService.class.getName())) {
            return adapter.cast(getScanService());
        }
        return super.getAdapter(adapter);
    }

    /**
     * build the scanService singleton instance.
     *
     * @return
     */
    protected ScanService getScanService() {
        if (scanService == null) {
            scanService = new DummyVirusScanner();
        }
        return scanService;
    }
}
