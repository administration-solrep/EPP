package fr.dila.ss.core.export;

import fr.dila.ss.core.dto.admin.ExportJournalTechniqueListingDTO;
import fr.dila.ss.core.export.enums.SSExcelSheetName;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public class JournalTechniqueConfig extends AbstractHeaderWithBorderExportConfig<ExportJournalTechniqueListingDTO> {

    public JournalTechniqueConfig(List<ExportJournalTechniqueListingDTO> users) {
        super(users, SSExcelSheetName.JOURNAL_TECHNIQUE);
    }

    @Override
    protected String[] getDataCells(CoreSession session, ExportJournalTechniqueListingDTO item) {
        return new String[] {
            item.getDate(),
            item.getUtilisateur(),
            item.getPoste(),
            item.getCategorie(),
            item.getCommentaire(),
            item.getReferenceDossier()
        };
    }
}
