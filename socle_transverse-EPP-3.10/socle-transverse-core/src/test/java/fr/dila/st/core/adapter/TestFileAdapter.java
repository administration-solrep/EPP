package fr.dila.st.core.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.ui.web.util.files.FileUtils;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.file.File;

public class TestFileAdapter extends SQLRepositoryTestCase {

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
	 * Test les getter/setter de l'apdapter File
	 * 
	 * @throws ClientException
	 * @throws IOException
	 */
	public void testFileImpl() throws ClientException, IOException {
		openSession();

		final String filename = "Spirou";
		final String digest = "digest";
		final String mimeType = "mimetype";
		final java.io.File tempFile = java.io.File.createTempFile("fileBlobTemp", ".tmp");
		final InputStream is = new FileInputStream(tempFile);
		final Blob content = FileUtils.createSerializableBlob(is, tempFile.getName(), null);

		DocumentModel doc = session.createDocumentModel(STConstant.FILE_DOCUMENT_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		File file = doc.getAdapter(File.class);
		assertNotNull(file);

		assertEquals(null, file.getContent());
		assertEquals(null, file.getFilename());
		assertEquals(null, file.getMimeType());

		file.setContent(content);
		file.setFilename(filename);
		file.setMimeType(mimeType);

		assertEquals(content, file.getContent());
		assertEquals(filename, file.getFilename());
		assertEquals(mimeType, file.getMimeType());

		// check persistance
		session.saveDocument(doc);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		file = doc.getAdapter(File.class);

		assertEquals(filename, file.getFilename());
		assertEquals(mimeType, file.getMimeType());
		assertEquals(content.getLength(), file.getContent().getLength());
		assertEquals(content.getDigest(), file.getContent().getDigest());
		assertEquals(content.getEncoding(), file.getContent().getEncoding());
		assertEquals(content.getFilename(), file.getContent().getFilename());
		assertEquals(content.getMimeType(), file.getContent().getMimeType());

		is.close();
		closeSession();
	}
}
