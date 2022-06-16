/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo
 */

package org.nuxeo.ecm.showcase.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.io.DocumentPipe;
import org.nuxeo.ecm.core.io.DocumentReader;
import org.nuxeo.ecm.core.io.DocumentWriter;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.DocumentPipeImpl;
import org.nuxeo.ecm.core.io.impl.plugins.NuxeoArchiveReader;
import org.nuxeo.ecm.platform.audit.api.AuditLogger;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.api.Logs;
import org.nuxeo.ecm.platform.filemanager.service.extension.ExportedZipImporter;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.zip.ZipFile;

/**
 * @author <a href="mailto:ak@nuxeo.com">Arnaud Kervern</a>
 * @since 7.10
 */
public class ShowcaseContentImporter {

    /**
     * @deprecated
     */
    public static final String INITIALIZED_EVENT = "ShowcaseContentImported";

    /**
     * When computing event name for the "defaut" contribution; it uses the old global event name in case the showcase
     * content was already imported.
     */
    public static final String DEFAULT_NAME = "default";

    /**
     * @since 8.4
     */
    public static final String INITIALIZED_EVENT_FORMAT = "ShowcaseContentImported_%s";

    private static final Log log = LogFactory.getLog(ShowcaseContentImporter.class);

    protected String name;

    protected CoreSession session;

    protected ShowcaseContentImporter(CoreSession session, String name) {
        this.session = session;
        this.name = name;
    }

    public static void run(CoreSession session, String name, Blob blob) throws IOException {
        new ShowcaseContentImporter(session, name).create(blob);
    }

    public DocumentModel create(Blob blob) throws IOException {
        if (isImported()) {
            log.debug(String.format("Showcase Content '%s' already imported.", name));
            return null;
        }

        DocumentModel doc = create(session, blob, getImportPathRoot(), true);

        markImportDone();
        return doc;
    }

    protected DocumentModel create(CoreSession documentManager, Blob content, String path, boolean overwrite)
            throws IOException {
        try (CloseableFile source = content.getCloseableFile(".zip")) {
            try (ZipFile zip = ExportedZipImporter.getArchiveFileIfValid(source.getFile())) {
                if (zip == null) {
                    return null;
                }
            }

            boolean importWithIds = false;
            DocumentReader reader = new NuxeoArchiveReader(source.getFile());
            ExportedDocument root = reader.read();
            IdRef rootRef = new IdRef(root.getId());

            if (documentManager.exists(rootRef)) {
                DocumentModel target = documentManager.getDocument(rootRef);
                if (target.getPath().removeLastSegments(1).equals(new Path(path))) {
                    importWithIds = true;
                }
            }
            reader.close();

            DocumentRef resultingRef;
            if (overwrite && importWithIds) {
                resultingRef = rootRef;
            } else {
                String rootName = root.getPath().lastSegment();
                resultingRef = new PathRef(path, rootName);
            }

            DocumentWriter writer = new ShowcaseWriter(documentManager, path, 10);
            reader = new NuxeoArchiveReader(source.getFile());
            try {
                DocumentPipe pipe = new DocumentPipeImpl(10);
                pipe.setReader(reader);
                pipe.setWriter(writer);
                pipe.run();
            } catch (IOException e) {
                log.warn(e, e);
                return null;
            } finally {
                reader.close();
                writer.close();
            }
            return documentManager.getDocument(resultingRef);
        }
    }

    protected boolean isImported() {
        return Framework.getService(Logs.class).getEventsCount(getEventName()) > 0;
    }

    protected void markImportDone() {
        AuditLogger logger = Framework.getService(AuditLogger.class);
        LogEntry entry = logger.newLogEntry();
        entry.setEventId(getEventName());
        entry.setEventDate(Calendar.getInstance().getTime());

        logger.addLogEntries(Collections.singletonList(entry));
    }

    protected String getEventName() {
        if (DEFAULT_NAME.equals(name)) {
            return INITIALIZED_EVENT;
        }
        return String.format(INITIALIZED_EVENT_FORMAT, name);
    }

    protected String getImportPathRoot() {
        return session.query("Select * from Domain").get(0).getPathAsString();
    }
}
