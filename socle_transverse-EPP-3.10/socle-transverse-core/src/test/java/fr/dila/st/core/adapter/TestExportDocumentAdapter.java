package fr.dila.st.core.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.ExportDocument;

public class TestExportDocumentAdapter extends SQLRepositoryTestCase {

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();

		// deploy repository manager
		deployBundle("org.nuxeo.ecm.core.api");

		// deploy api and core bundles
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
		deployBundle("fr.dila.ecm.platform.routing.core");
		deployBundle("org.nuxeo.ecm.automation.core");
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.platform.usermanager");
		deployBundle("org.nuxeo.ecm.directory.types.contrib");

		deployContrib("fr.dila.st.core", "OSGI-INF/st-schema-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-core-type-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-adapter-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-type-contrib.xml");

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test les getter/setter de l'apdapter ExportDocument
	 * 
	 * @throws ClientException
	 * @throws IOException
	 */
	public void testExportDocumentImpl() throws ClientException, IOException {
		openSession();

		final String owner = "Spirou";
		final boolean exporting = true;
		final Calendar dateRequest = Calendar.getInstance();
		final java.io.File tempFile = java.io.File.createTempFile("fileBlobTemp", ".tmp");
		final InputStream is = new FileInputStream(tempFile);
		final Blob content = FileUtils.createSerializableBlob(is, tempFile.getName(), null);

		DocumentModel doc = session.createDocumentModel(STConstant.EXPORT_DOCUMENT_DOCUMENT_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		ExportDocument exportDocument = doc.getAdapter(ExportDocument.class);
		assertNotNull(exportDocument);

		assertEquals(doc, exportDocument.getDocument());

		exportDocument.setDateRequest(dateRequest);
		exportDocument.setExporting(exporting);
		exportDocument.setFileContent(content);
		exportDocument.setOwner(owner);

		assertEquals(dateRequest, exportDocument.getDateRequest());
		assertEquals(exporting, exportDocument.isExporting());
		assertEquals(owner, exportDocument.getOwner());
		assertEquals(content, exportDocument.getFileContent());

		// check persistance
		exportDocument.save(session);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		exportDocument = doc.getAdapter(ExportDocument.class);

		assertEquals(doc, exportDocument.getDocument());
		assertEquals(dateRequest, exportDocument.getDateRequest());
		assertEquals(exporting, exportDocument.isExporting());
		assertEquals(owner, exportDocument.getOwner());
		assertEquals(content.getLength(), exportDocument.getFileContent().getLength());
		assertEquals(content.getDigest(), exportDocument.getFileContent().getDigest());
		assertEquals(content.getEncoding(), exportDocument.getFileContent().getEncoding());
		assertEquals(content.getFilename(), exportDocument.getFileContent().getFilename());
		assertEquals(content.getMimeType(), exportDocument.getFileContent().getMimeType());

		closeSession();
	}
}
