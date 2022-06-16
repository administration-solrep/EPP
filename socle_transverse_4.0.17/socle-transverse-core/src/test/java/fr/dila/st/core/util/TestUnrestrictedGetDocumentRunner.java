package fr.dila.st.core.util;

import static fr.dila.st.api.constant.STAlertConstant.ALERT_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.DUBLINCORE_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.FILE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.core.schema.AlertSchemaUtils;
import fr.dila.st.core.schema.FileSchemaUtils;
import fr.dila.st.core.test.STCommonFeature;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STCommonFeature.class)
public class TestUnrestrictedGetDocumentRunner {
    private static final String USER1 = "user1";

    @Inject
    private CoreSession session;

    @Inject
    UserManager um;

    @Test
    public void testConstructor() {
        NuxeoPrincipal user1 = um.getPrincipal(USER1);
        try (CloseableCoreSession sessionUser1 = CoreInstance.openCoreSession(session.getRepositoryName(), user1)) {
            UnrestrictedGetDocumentRunner getDoc = new UnrestrictedGetDocumentRunner(
                sessionUser1,
                ALERT_SCHEMA,
                DUBLINCORE_SCHEMA
            );
            assertThat(getDoc.getPrefetchInfo().getSchemas()).containsExactly(ALERT_SCHEMA, DUBLINCORE_SCHEMA);
        }
    }

    @Test
    public void testGetDocumentFail() {
        final String reqId = "req1";
        DocumentModel alert1 = session.createDocumentModel("/", "alert1", STAlertConstant.ALERT_DOCUMENT_TYPE);
        AlertSchemaUtils.setRequeteId(alert1, reqId);
        alert1 = session.createDocument(alert1);

        NuxeoPrincipal user1 = um.getPrincipal(USER1);
        try (CloseableCoreSession sessionUser1 = CoreInstance.openCoreSession(session.getRepositoryName(), user1)) {
            // unrestricted get without prefetch
            UnrestrictedGetDocumentRunner getDoc = new UnrestrictedGetDocumentRunner(sessionUser1);
            final DocumentModel doc = getDoc.getById(alert1.getId());
            Assert.assertNotNull(doc);
            Throwable throwable = catchThrowable(() -> AlertSchemaUtils.getRequeteId(doc));
            assertThat(throwable)
                .isInstanceOf(NuxeoException.class)
                .hasMessageStartingWith("The DocumentModel is not associated to an open CoreSession: %s", doc);
        }
    }

    @Test
    public void testGetDocumentSuccess() {
        final String reqId = "req1";
        DocumentModel alert1 = session.createDocumentModel("/", "alert1", STAlertConstant.ALERT_DOCUMENT_TYPE);
        AlertSchemaUtils.setRequeteId(alert1, reqId);
        alert1 = session.createDocument(alert1);

        NuxeoPrincipal user1 = um.getPrincipal(USER1);
        try (CloseableCoreSession sessionUser1 = CoreInstance.openCoreSession(session.getRepositoryName(), user1)) {
            UnrestrictedGetDocumentRunner getDoc = new UnrestrictedGetDocumentRunner(sessionUser1);
            // unrestricted get with the alert schema as prefetch
            getDoc.setPrefetchInfo(STAlertConstant.ALERT_SCHEMA);
            final DocumentModel doc = getDoc.getById(alert1.getId());
            Assert.assertEquals(reqId, AlertSchemaUtils.getRequeteId(doc));
        }
    }

    @Test
    public void testGetChildren() throws IOException {
        // create folder in root
        DocumentModel folder = session.createDocumentModel("/", "folder", "Folder");
        folder = session.createDocument(folder);
        final String folderPath = folder.getPathAsString();

        // create file in folder
        DocumentModel file1 = session.createDocumentModel(folderPath, "file1", "File");
        FileSchemaUtils.setContent(file1, Blobs.createBlob(File.createTempFile("temp", ".txt")));
        file1 = session.createDocument(file1);

        // create alert doc in folder
        final String reqId = "req1";
        DocumentModel alert1 = session.createDocumentModel(folderPath, "alert1", STAlertConstant.ALERT_DOCUMENT_TYPE);
        AlertSchemaUtils.setRequeteId(alert1, reqId);
        alert1 = session.createDocument(alert1);

        NuxeoPrincipal user1 = um.getPrincipal(USER1);
        try (CloseableCoreSession sessionUser1 = CoreInstance.openCoreSession(session.getRepositoryName(), user1)) {
            // unrestricted get without prefetch
            UnrestrictedGetDocumentRunner getDoc = new UnrestrictedGetDocumentRunner(sessionUser1);
            DocumentModelList children = getDoc.getChildrenById(folder.getId());
            Assert.assertNotNull(children);
            Assert.assertEquals(2, children.size());
            for (DocumentModel child : children) {
                if (StringUtils.equals("File", child.getType())) {
                    Throwable throwable = catchThrowable(() -> FileSchemaUtils.getContent(child));
                    assertThat(throwable)
                        .isInstanceOf(NuxeoException.class)
                        .hasMessageStartingWith(
                            "The DocumentModel is not associated to an open CoreSession: %s",
                            child
                        );
                }
                if (StringUtils.equals(STAlertConstant.ALERT_DOCUMENT_TYPE, child.getType())) {
                    Throwable throwable = catchThrowable(() -> AlertSchemaUtils.getRequeteId(child));
                    assertThat(throwable)
                        .isInstanceOf(NuxeoException.class)
                        .hasMessageStartingWith(
                            "The DocumentModel is not associated to an open CoreSession: %s",
                            child
                        );
                }
            }

            // unrestricted get with the alert and file schemas as prefetch
            getDoc = new UnrestrictedGetDocumentRunner(sessionUser1, ALERT_SCHEMA, FILE_SCHEMA);
            children = getDoc.getChildrenById(folder.getId());
            Assert.assertNotNull(children);
            Assert.assertEquals(2, children.size());
            for (DocumentModel child : children) {
                if (StringUtils.equals("File", child.getType())) {
                    Assert.assertNotNull(FileSchemaUtils.getContent(child));
                }
                if (StringUtils.equals(STAlertConstant.ALERT_DOCUMENT_TYPE, child.getType())) {
                    Assert.assertNotNull(AlertSchemaUtils.getRequeteId(child));
                }
            }
        }
    }
}
