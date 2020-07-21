package fr.dila.st.core.service;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.service.STExportService;
import fr.dila.st.core.STRepositoryTestCase;
import fr.dila.st.core.constant.STTestConstant;
import fr.dila.st.core.util.DateUtil;

public class TestExportService extends STRepositoryTestCase {

	private STExportService	sTExportService;
	private String			folderExportName	= "folderExport";
	private String			folderExportType	= STExportConstants.EXPORT_DOC_FOLDER_ROOT_TYPE;
	private String			exportDocumentName	= "exportDocument";
	private String			exportDocumentType	= STExportConstants.EXPORT_DOC_TYPE;

	public void setUp() throws Exception {
		super.setUp();
		deployContrib(STTestConstant.ST_CORE_BUNDLE, "OSGI-INF/service/st-export-framework.xml");

		sTExportService = STServiceLocator.getSTExportService();
		assertNotNull(sTExportService);
	}

	public void testGetOrCreateExportDocumentRootPath() throws ClientException {
		try {
			openSession();
			String rootPath = sTExportService.getOrCreateExportDocumentRootPath(session, "/", folderExportName,
					folderExportType);
			assertNotNull(rootPath);
			assertEquals(rootPath, "/" + folderExportName);
		} finally {
			closeSession();
		}
	}

	public void testGetOrCreateExportDocumentRootDoc() throws ClientException {
		try {
			openSession();
			DocumentModel exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(session, "",
					folderExportName, folderExportType);
			assertNotNull(exportDocumentFolderRoot);
			exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(session, "/", folderExportName,
					folderExportType);
			assertNotNull(exportDocumentFolderRoot);
		} finally {
			closeSession();
		}
	}

	public void testIsCurrentlyExporting() throws ClientException {
		try {
			openSession();

			DocumentModel exportDocumentDoc = null;
			assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
			String rootPath = sTExportService.getOrCreateExportDocumentRootPath(session, "/", folderExportName,
					folderExportType);
			assertNotNull(rootPath);
			assertEquals(rootPath, "/" + folderExportName);

			exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);
			assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
			ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
			assertNotNull(exportDocument);
			exportDocument.setExporting(true);
			assertTrue(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
		} finally {
			closeSession();
		}
	}

	public void testFlagInitExport() throws ClientException {
		try {
			openSession();
			String rootPath = sTExportService.getOrCreateExportDocumentRootPath(session, "/", folderExportName,
					folderExportType);
			assertNotNull(rootPath);
			assertEquals(rootPath, "/" + folderExportName);

			DocumentModel exportDocumentDoc = null;
			assertFalse(sTExportService.flagInitExport(session, exportDocumentDoc));

			exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);
			ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
			assertNotNull(exportDocument);
			assertFalse(exportDocument.isExporting());

			assertTrue(sTExportService.flagInitExport(session, exportDocument.getDocument()));
			assertFalse(sTExportService.flagInitExport(session, exportDocument.getDocument()));
		} finally {
			closeSession();
		}
	}

	public void testFlagEndExport() throws ClientException {
		try {
			openSession();
			sTExportService.flagEndExport(session, null);
			String rootPath = sTExportService.getOrCreateExportDocumentRootPath(session, "/", folderExportName,
					folderExportType);
			assertNotNull(rootPath);
			assertEquals(rootPath, "/" + folderExportName);
			DocumentModel exportDocumentDoc = null;

			exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);
			ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
			assertNotNull(exportDocument);
			assertFalse(exportDocument.isExporting());
			sTExportService.flagEndExport(session, exportDocument.getDocument());
			// Rien n'a changé
			assertFalse(exportDocument.isExporting());
			exportDocument.setExporting(true);
			assertTrue(exportDocument.isExporting());

			sTExportService.flagEndExport(session, exportDocument.getDocument());
			assertFalse(exportDocument.isExporting());
		} finally {
			closeSession();
		}
	}

	public void testGetExportHorodatageRequest() throws ClientException {
		try {
			openSession();
			String result = sTExportService.getExportHorodatageRequest(session, null);
			assertEquals("Aucune donnée trouvée pour les paramètres fournis", result);
			Calendar date = Calendar.getInstance();
			String nowString = DateUtil.formatWithHour(date.getTime());
			String rootPath = sTExportService.getOrCreateExportDocumentRootPath(session, "/", folderExportName,
					folderExportType);
			assertNotNull(rootPath);
			assertEquals(rootPath, "/" + folderExportName);
			DocumentModel exportDocumentDoc = null;

			exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);

			assertTrue(sTExportService.flagInitExport(session, exportDocumentDoc));

			ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
			String dateRequestString = DateUtil.formatWithHour(exportDocument.getDateRequest().getTime());
			assertEquals(nowString, dateRequestString);

			// On vérifie avec la méthode présente dans le service
			String dateRequestByService = sTExportService.getExportHorodatageRequest(session, exportDocumentDoc);
			assertEquals(dateRequestString, dateRequestByService);
		} finally {
			closeSession();
		}
	}

	public void testGetExportDocumentDocs() throws ClientException {
		try {
			openSession();
			DocumentModel exportDocumentFolderRoot = null;
			assertNull(sTExportService.getExportDocumentDocs(session, null));

			exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(session, "/", folderExportName,
					folderExportType);
			assertNotNull(exportDocumentFolderRoot);

			DocumentModelList exportDocumentDocs = sTExportService.getExportDocumentDocs(session,
					exportDocumentFolderRoot.getRef());
			assertEquals(0, exportDocumentDocs.size());

			DocumentModel exportDocumentDoc = session.createDocumentModel(exportDocumentFolderRoot.getPathAsString(),
					exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);

			exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
			assertEquals(1, exportDocumentDocs.size());

			for (int i = 0; i < 10; i++) {
				exportDocumentDoc = session.createDocumentModel(exportDocumentFolderRoot.getPathAsString(),
						exportDocumentName + i, exportDocumentType);
				exportDocumentDoc = session.createDocument(exportDocumentDoc);
				assertNotNull(exportDocumentDoc);
			}

			exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
			assertEquals(11, exportDocumentDocs.size());
		} finally {
			closeSession();
		}
	}

	public void testRemoveOldExport() throws ClientException {
		try {
			openSession();

			Calendar dateLimit = Calendar.getInstance();
			// On retire 1 jour à la date limite
			dateLimit.add(Calendar.DAY_OF_MONTH, -1);

			int nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
			// Pas de document existant, donc suppression à 0
			assertEquals(0, nbSuppression);

			DocumentModel exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(session, "/",
					folderExportName, folderExportType);
			assertNotNull(exportDocumentFolderRoot);

			DocumentModel exportDocumentDoc = session.createDocumentModel(exportDocumentFolderRoot.getPathAsString(),
					exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			session.save();
			assertNotNull(exportDocumentDoc);

			DocumentModelList exportDocumentDocs = sTExportService.getExportDocumentDocs(session,
					exportDocumentFolderRoot.getRef());
			assertEquals(1, exportDocumentDocs.size());

			// On teste le cas où la dateRequest is null
			nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
			// document existant, donc suppression à 1
			assertEquals(1, nbSuppression);

			exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
			assertEquals(0, exportDocumentDocs.size());

			// On recréé le document
			// On appelle la méthode de récupération ou création => le document doit exister après
			exportDocumentDoc = session.createDocumentModel(exportDocumentFolderRoot.getPathAsString(),
					exportDocumentName, exportDocumentType);
			exportDocumentDoc = session.createDocument(exportDocumentDoc);
			assertNotNull(exportDocumentDoc);

			// On simule un export
			assertTrue(sTExportService.flagInitExport(session, exportDocumentDoc));
			sTExportService.flagEndExport(session, exportDocumentDoc);
			assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));

			nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
			// document existant, mais trop récent donc suppression à 0
			assertEquals(0, nbSuppression);

			ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
			// On change la dateRequest pour le test
			Calendar oldDate = Calendar.getInstance();
			oldDate.add(Calendar.YEAR, -1);
			exportDocument.setDateRequest(oldDate);
			exportDocument.save(session);
			session.save();

			nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
			// document existant, et ancien donc suppression à 1
			assertEquals(1, nbSuppression);
			exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
			assertEquals(0, exportDocumentDocs.size());
		} finally {
			closeSession();
		}
	}

}
