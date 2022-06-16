package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.core.test.STFeature;
import java.io.IOException;
import java.util.Calendar;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestExportDocumentAdapter { //extends SQLRepositoryTestCase {
    @Inject
    private CoreFeature coreFeature;

    //	@Override
    //	protected void deployRepositoryContrib() throws Exception {
    //		super.deployRepositoryContrib();
    //
    //		// deploy repository manager
    //		deployBundle("org.nuxeo.ecm.core.api");
    //
    //		// deploy api and core bundles
    //		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
    //		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
    //		deployBundle("fr.dila.ecm.platform.routing.core");
    //		deployBundle("org.nuxeo.ecm.automation.core");
    //		deployBundle("org.nuxeo.ecm.directory");
    //		deployBundle("org.nuxeo.ecm.platform.usermanager");
    //		deployBundle("org.nuxeo.ecm.directory.types.contrib");
    //
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-schema-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-core-type-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-adapter-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-type-contrib.xml");
    //
    //	}

    /**
     * Test les getter/setter de l'apdapter ExportDocument
     *
     * @throws ClientException
     * @throws IOException
     */
    @Test
    public void testExportDocumentImpl() throws IOException {
        final String owner = "Spirou";
        final boolean exporting = true;
        final Calendar dateRequest = Calendar.getInstance();
        final Blob content = new StringBlob("un contenu");

        DocumentRef docRef;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.createDocumentModel(STConstant.EXPORT_DOCUMENT_DOCUMENT_TYPE);
            Assert.assertNotNull(doc);

            doc = session.createDocument(doc);

            docRef = doc.getRef();

            ExportDocument exportDocument = doc.getAdapter(ExportDocument.class);
            Assert.assertNotNull(exportDocument);

            Assert.assertEquals(doc, exportDocument.getDocument());

            exportDocument.setDateRequest(dateRequest);
            exportDocument.setExporting(exporting);
            exportDocument.setFileContent(content);
            exportDocument.setOwner(owner);

            Assert.assertEquals(dateRequest, exportDocument.getDateRequest());
            Assert.assertEquals(exporting, exportDocument.isExporting());
            Assert.assertEquals(owner, exportDocument.getOwner());
            Assert.assertEquals(content, exportDocument.getFileContent());

            // check persistance
            exportDocument.save(session);
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.getDocument(docRef);
            ExportDocument exportDocument = doc.getAdapter(ExportDocument.class);

            Assert.assertEquals(doc, exportDocument.getDocument());
            Assert.assertEquals(dateRequest, exportDocument.getDateRequest());
            Assert.assertEquals(exporting, exportDocument.isExporting());
            Assert.assertEquals(owner, exportDocument.getOwner());
            Assert.assertEquals(content.getLength(), exportDocument.getFileContent().getLength());
            Assert.assertEquals(content.getEncoding(), exportDocument.getFileContent().getEncoding());
            Assert.assertEquals(content.getFilename(), exportDocument.getFileContent().getFilename());
            Assert.assertEquals(content.getMimeType(), exportDocument.getFileContent().getMimeType());
        }
    }
}
