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

import static org.nuxeo.ecm.core.api.validation.DocumentValidationService.Forcing.TURN_OFF;
import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.CTX_FORCE_VIEWS_GENERATION;
import static org.nuxeo.ecm.platform.picture.api.ImagingDocumentConstants.PICTURE_FACET;
import static org.nuxeo.ecm.platform.video.VideoConstants.CTX_FORCE_INFORMATIONS_GENERATION;
import static org.nuxeo.ecm.platform.video.VideoConstants.VIDEO_FACET;

import java.util.Collections;

import org.dom4j.Element;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.validation.DocumentValidationService;
import org.nuxeo.ecm.core.api.versioning.VersioningService;
import org.nuxeo.ecm.core.io.ExportConstants;
import org.nuxeo.ecm.core.io.ExportedDocument;
import org.nuxeo.ecm.core.io.impl.plugins.DocumentModelWriter;

/**
 * @author <a href="mailto:ak@nuxeo.com">Arnaud Kervern</a>
 * @since 7.10
 */
public class ShowcaseWriter extends DocumentModelWriter {

    public ShowcaseWriter(CoreSession session, String parentPath, int saveInterval) {
        super(session, parentPath, saveInterval);
    }

    /**
     * Import a new document given its path keeping his id
     * <p>
     * The parent of this document is assumed to exist.
     *
     * @param xdoc the document containing
     * @param toPath the path of the doc to create
     */
    protected DocumentModel createDocument(ExportedDocument xdoc, Path toPath) {
        Path parentPath = toPath.removeLastSegments(1);

        DocumentModel doc = new DocumentModelImpl(null, xdoc.getType(), xdoc.getId(), toPath, null, null,
                new PathRef(parentPath.toString()), null, null, null, null);

        // set lifecycle state at creation
        Element system = xdoc.getDocument().getRootElement().element(ExportConstants.SYSTEM_TAG);
        String lifeCycleState = system.element(ExportConstants.LIFECYCLE_STATE_TAG).getText();
        doc.putContextData(CoreSession.IMPORT_LIFECYCLE_STATE, lifeCycleState);
        String lifeCyclePolicy = system.element(ExportConstants.LIFECYCLE_POLICY_TAG).getText();
        doc.putContextData(CoreSession.IMPORT_LIFECYCLE_POLICY, lifeCyclePolicy);
        doc.putContextData(DocumentValidationService.CTX_MAP_KEY, TURN_OFF);

        // loadFacets before schemas so that additional schemas are not skipped
        loadFacetsInfo(doc, xdoc.getDocument());

        // then load schemas data
        loadSchemas(xdoc, doc, xdoc.getDocument());

        if (doc.hasSchema("uid")) {
            doc.putContextData(VersioningService.SKIP_VERSIONING, true);
        }

        // XXX Not used, as we override the listener; but it is the right way to force video informations generation.
        if (doc.hasFacet(VIDEO_FACET)) {
            doc.putContextData(CTX_FORCE_INFORMATIONS_GENERATION, true);
        }

        if (doc.hasFacet(PICTURE_FACET)) {
            doc.putContextData(CTX_FORCE_VIEWS_GENERATION, true);
        }

        session.importDocuments(Collections.singletonList(doc));

        // load into the document the system properties, document needs to exist
        loadSystemInfo(doc, xdoc.getDocument());

        unsavedDocuments += 1;
        saveIfNeeded();

        return doc;
    }
}
