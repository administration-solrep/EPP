package fr.dila.st.core.service;

import fr.dila.st.api.constant.STExportConstants;
import fr.dila.st.api.domain.ExportDocument;
import fr.dila.st.api.service.STExportService;
import fr.dila.st.core.constant.STTestConstant;
import fr.dila.st.core.test.STFeature;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
@Deploy(STTestConstant.ST_CORE_BUNDLE + ":OSGI-INF/service/st-export-framework.xml")
public class TestExportService {
    @Inject
    private CoreFeature coreFeature;

    private STExportService sTExportService;
    private String folderExportName = "folderExport";
    private String folderExportType = STExportConstants.EXPORT_DOC_FOLDER_ROOT_TYPE;
    private String exportDocumentName = "exportDocument";
    private String exportDocumentType = STExportConstants.EXPORT_DOC_TYPE;

    @Before
    public void setUp() throws Exception {
        sTExportService = STServiceLocator.getSTExportService();
        Assert.assertNotNull(sTExportService);
    }

    @Test
    public void testGetOrCreateExportDocumentRootPath() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String rootPath = sTExportService.getOrCreateExportDocumentRootPath(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(rootPath);
            Assert.assertEquals(rootPath, "/" + folderExportName);
        }
    }

    @Test
    public void testGetOrCreateExportDocumentRootDoc() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(
                session,
                "",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(exportDocumentFolderRoot);
            exportDocumentFolderRoot =
                sTExportService.getOrCreateExportDocumentRootDoc(session, "/", folderExportName, folderExportType);
            Assert.assertNotNull(exportDocumentFolderRoot);
        }
    }

    @Test
    public void testIsCurrentlyExporting() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel exportDocumentDoc = null;
            Assert.assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
            String rootPath = sTExportService.getOrCreateExportDocumentRootPath(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(rootPath);
            Assert.assertEquals(rootPath, "/" + folderExportName);

            exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);
            Assert.assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            Assert.assertNotNull(exportDocument);
            exportDocument.setExporting(true);
            Assert.assertTrue(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));
        }
    }

    @Test
    public void testFlagInitExport() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String rootPath = sTExportService.getOrCreateExportDocumentRootPath(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(rootPath);
            Assert.assertEquals(rootPath, "/" + folderExportName);

            DocumentModel exportDocumentDoc = null;
            Assert.assertFalse(sTExportService.flagInitExport(session, exportDocumentDoc));

            exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            Assert.assertNotNull(exportDocument);
            Assert.assertFalse(exportDocument.isExporting());

            Assert.assertTrue(sTExportService.flagInitExport(session, exportDocument.getDocument()));
            Assert.assertFalse(sTExportService.flagInitExport(session, exportDocument.getDocument()));
        }
    }

    @Test
    public void testFlagEndExport() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            sTExportService.flagEndExport(session, null);
            String rootPath = sTExportService.getOrCreateExportDocumentRootPath(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(rootPath);
            Assert.assertEquals(rootPath, "/" + folderExportName);
            DocumentModel exportDocumentDoc = null;

            exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);
            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            Assert.assertNotNull(exportDocument);
            Assert.assertFalse(exportDocument.isExporting());
            sTExportService.flagEndExport(session, exportDocument.getDocument());
            // Rien n'a changé
            Assert.assertFalse(exportDocument.isExporting());
            exportDocument.setExporting(true);
            Assert.assertTrue(exportDocument.isExporting());

            sTExportService.flagEndExport(session, exportDocument.getDocument());
            Assert.assertFalse(exportDocument.isExporting());
        }
    }

    @Test
    public void testGetExportHorodatageRequest() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            String result = sTExportService.getExportHorodatageRequest(session, null);
            Assert.assertEquals("Aucune donnée trouvée pour les paramètres fournis", result);

            Calendar date = Calendar.getInstance();

            String rootPath = sTExportService.getOrCreateExportDocumentRootPath(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(rootPath);
            Assert.assertEquals(rootPath, "/" + folderExportName);
            DocumentModel exportDocumentDoc = null;

            exportDocumentDoc = session.createDocumentModel(rootPath, exportDocumentName, exportDocumentType);
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);

            Assert.assertTrue(sTExportService.flagInitExport(session, exportDocumentDoc));

            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);

            Calendar calRequest = exportDocument.getDateRequest();
            Assertions.assertThat(calRequest).isBetween(date, Calendar.getInstance());

            // On vérifie avec la méthode présente dans le service
            String dateRequestString = SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(calRequest);
            String dateRequestByService = sTExportService.getExportHorodatageRequest(session, exportDocumentDoc);
            Assert.assertEquals(dateRequestString, dateRequestByService);
        }
    }

    @Test
    public void testGetExportDocumentDocs() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel exportDocumentFolderRoot = null;
            Assert.assertNull(sTExportService.getExportDocumentDocs(session, null));

            exportDocumentFolderRoot =
                sTExportService.getOrCreateExportDocumentRootDoc(session, "/", folderExportName, folderExportType);
            Assert.assertNotNull(exportDocumentFolderRoot);

            DocumentModelList exportDocumentDocs = sTExportService.getExportDocumentDocs(
                session,
                exportDocumentFolderRoot.getRef()
            );
            Assert.assertEquals(0, exportDocumentDocs.size());

            DocumentModel exportDocumentDoc = session.createDocumentModel(
                exportDocumentFolderRoot.getPathAsString(),
                exportDocumentName,
                exportDocumentType
            );
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);

            exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
            Assert.assertEquals(1, exportDocumentDocs.size());

            for (int i = 0; i < 10; i++) {
                exportDocumentDoc =
                    session.createDocumentModel(
                        exportDocumentFolderRoot.getPathAsString(),
                        exportDocumentName + i,
                        exportDocumentType
                    );
                exportDocumentDoc = session.createDocument(exportDocumentDoc);
                Assert.assertNotNull(exportDocumentDoc);
            }

            exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
            Assert.assertEquals(11, exportDocumentDocs.size());
        }
    }

    @Test
    public void testRemoveOldExport() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            Calendar dateLimit = Calendar.getInstance();
            // On retire 1 jour à la date limite
            dateLimit.add(Calendar.DAY_OF_MONTH, -1);

            int nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
            // Pas de document existant, donc suppression à 0
            Assert.assertEquals(0, nbSuppression);

            DocumentModel exportDocumentFolderRoot = sTExportService.getOrCreateExportDocumentRootDoc(
                session,
                "/",
                folderExportName,
                folderExportType
            );
            Assert.assertNotNull(exportDocumentFolderRoot);

            DocumentModel exportDocumentDoc = session.createDocumentModel(
                exportDocumentFolderRoot.getPathAsString(),
                exportDocumentName,
                exportDocumentType
            );
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            session.save();
            Assert.assertNotNull(exportDocumentDoc);

            DocumentModelList exportDocumentDocs = sTExportService.getExportDocumentDocs(
                session,
                exportDocumentFolderRoot.getRef()
            );
            Assert.assertEquals(1, exportDocumentDocs.size());

            // On teste le cas où la dateRequest is null
            nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
            // document existant, donc suppression à 1
            Assert.assertEquals(1, nbSuppression);

            exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
            Assert.assertEquals(0, exportDocumentDocs.size());

            // On recréé le document
            // On appelle la méthode de récupération ou création => le document doit exister après
            exportDocumentDoc =
                session.createDocumentModel(
                    exportDocumentFolderRoot.getPathAsString(),
                    exportDocumentName,
                    exportDocumentType
                );
            exportDocumentDoc = session.createDocument(exportDocumentDoc);
            Assert.assertNotNull(exportDocumentDoc);

            // On simule un export
            Assert.assertTrue(sTExportService.flagInitExport(session, exportDocumentDoc));
            sTExportService.flagEndExport(session, exportDocumentDoc);
            Assert.assertFalse(sTExportService.isCurrentlyExporting(session, exportDocumentDoc));

            nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
            // document existant, mais trop récent donc suppression à 0
            Assert.assertEquals(0, nbSuppression);

            ExportDocument exportDocument = exportDocumentDoc.getAdapter(ExportDocument.class);
            // On change la dateRequest pour le test
            Calendar oldDate = Calendar.getInstance();
            oldDate.add(Calendar.YEAR, -1);
            exportDocument.setDateRequest(oldDate);
            exportDocument.save(session);
            session.save();

            nbSuppression = sTExportService.removeOldExport(session, dateLimit, exportDocumentType);
            // document existant, et ancien donc suppression à 1
            Assert.assertEquals(1, nbSuppression);
            exportDocumentDocs = sTExportService.getExportDocumentDocs(session, exportDocumentFolderRoot.getRef());
            Assert.assertEquals(0, exportDocumentDocs.size());
        }
    }
}
