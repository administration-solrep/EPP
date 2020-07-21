package fr.dila.solonepp.core.piecejointe;

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
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.st.core.constant.STTestConstant;

public class TestFileAdapter extends SQLRepositoryTestCase {

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();
		deployBundle("org.nuxeo.ecm.directory.types.contrib");
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
		deployBundle(STTestConstant.ST_CORE_BUNDLE);
		deployBundle("fr.dila.ecm.platform.routing.core");
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-schema-contrib.xml");
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-core-type-contrib.xml");
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-adapter-contrib.xml");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testNothing() {
		
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

		DocumentModel doc = session.createDocumentModel(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		PieceJointeFichier file = doc.getAdapter(PieceJointeFichier.class);
		assertNotNull(file);

		assertEquals(null, file.getContent());
		assertEquals(null, file.getDigestSha512());
		assertEquals(null, file.getFilename());
		assertEquals(null, file.getMimeType());

		file.setContent(content);
		// dans le cas réel, cette valeur est mise en place par l'appelant,
		// soit l'interface (FileTreeManagerActionsBean) soit le service (PieceJointeAssembler)
		file.setDigestSha512(digest);
		file.setFilename(filename);
		file.setMimeType(mimeType);

		assertEquals(content, file.getContent());
		assertEquals(digest, file.getDigestSha512());
		assertEquals(filename, file.getFilename());
		assertEquals(mimeType, file.getMimeType());

		// check persistance
		session.saveDocument(doc);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		file = doc.getAdapter(PieceJointeFichier.class);

		assertEquals(digest, file.getDigestSha512());
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
