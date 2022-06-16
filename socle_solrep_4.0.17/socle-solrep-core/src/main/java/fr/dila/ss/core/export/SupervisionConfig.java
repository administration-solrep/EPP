package fr.dila.ss.core.export;

import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.ss.core.export.enums.SSExcelSheetName;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public class SupervisionConfig extends AbstractHeaderWithBorderExportConfig<SupervisionUserDTO> {

    public SupervisionConfig(List<SupervisionUserDTO> users, boolean isPdf) {
        super(users, SSExcelSheetName.SUPERVISION, isPdf);
    }

    @Override
    protected String[] getDataCells(CoreSession session, SupervisionUserDTO item) {
        String dateConnexion = formatCalendarWithHour(item.getDateConnexion());

        return new String[] { item.getUtilisateur(), item.getPrenom(), item.getNom(), dateConnexion };
    }
}
