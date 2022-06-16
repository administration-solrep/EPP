package fr.dila.st.core.export;

import fr.dila.st.api.user.STUser;
import fr.dila.st.core.export.enums.STExcelSheetName;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public class UsersConfig extends AbstractEnumExportConfig<STUser> {

    public UsersConfig(List<STUser> users) {
        super(users, STExcelSheetName.IDENTIFIANT);
    }

    @Override
    protected String[] getDataCells(CoreSession session, STUser item) {
        return new String[] {
            item.getUsername(),
            item.getTitle(),
            item.getFirstName(),
            item.getLastName(),
            item.getEmail()
        };
    }
}
