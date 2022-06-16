package fr.dila.ss.core.service;

import fr.dila.ss.api.service.SSExcelService;
import fr.dila.st.core.service.STExcelServiceImpl;
import fr.dila.st.core.util.STExcelUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SSExcelServiceImpl extends STExcelServiceImpl implements SSExcelService {
    public static final String DOSSIERS_SHEET_NAME = "Resultats_requête";

    /**
     * Ajoute un documentModel (dossier ou une question) à une feuille de calcul
     *
     * @param session
     * @param sheet
     * @param numRow
     *            la ligne où ajouter les données
     * @param document
     */
    protected void addDocumentToSheet(
        final CoreSession session,
        final HSSFSheet sheet,
        final int numRow,
        final DocumentModel document
    ) {
        throw new UnsupportedOperationException("A surcharger");
    }

    @Override
    public Consumer<OutputStream> creationExcelListDossiers(CoreSession session, List<DocumentRef> dossiersRefs) {
        try (HSSFWorkbook wb = initExcelFile(DOSSIERS_SHEET_NAME, getListDossiersHeader())) {
            HSSFSheet sheet = wb.getSheet(DOSSIERS_SHEET_NAME);

            List<DocumentRef> dossiersExistants = dossiersRefs
                .stream()
                .filter(session::exists)
                .collect(Collectors.toList());
            IntStream
                .range(0, dossiersExistants.size())
                .boxed()
                .map(i -> new AbstractMap.SimpleImmutableEntry<>(i + 1, session.getDocument(dossiersExistants.get(i))))
                .forEach(e -> addDocumentToSheet(session, sheet, e.getKey(), e.getValue()));

            STExcelUtil.formatStyle(wb, DOSSIERS_SHEET_NAME, getListDossiersHeader().length);

            return outputStream -> {
                try {
                    wb.write(outputStream);
                } catch (IOException exc) {
                    throw new NuxeoException(exc);
                }
            };
        } catch (IOException exc) {
            throw new NuxeoException(exc);
        }
    }

    protected String[] getListDossiersHeader() {
        throw new UnsupportedOperationException("A surcharger");
    }
}
