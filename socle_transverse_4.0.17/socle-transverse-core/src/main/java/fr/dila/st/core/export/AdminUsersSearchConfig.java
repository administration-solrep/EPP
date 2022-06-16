package fr.dila.st.core.export;

import fr.dila.st.api.user.STUser;
import fr.dila.st.core.export.enums.STExcelSheetName;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class AdminUsersSearchConfig extends AbstractEnumExportConfig<DocumentModel> {

    public AdminUsersSearchConfig(List<DocumentModel> usersDocs) {
        super(usersDocs, STExcelSheetName.USER);
    }

    @Override
    protected String[] getDataCells(CoreSession session, DocumentModel item) {
        STUser user = item.getAdapter(STUser.class);
        String dateDebut = SolonDateConverter.DATE_SLASH.format(user.getDateDebut());
        String postes = STServiceLocator.getSTUserService().getUserPostes(item.getId());

        return new String[] {
            user.getUsername(),
            user.getLastName(),
            user.getFirstName(),
            user.getEmail(),
            user.getTelephoneNumber(),
            dateDebut,
            postes
        };
    }
}
