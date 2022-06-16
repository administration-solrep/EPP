package fr.dila.ss.core.export;

import fr.dila.ss.core.export.enums.SSExcelSheetName;
import java.util.Collections;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class UsersSimpleConfig extends UsersAdminConfig {

    public UsersSimpleConfig(List<DocumentModel> usersDocs) {
        super(usersDocs, false, false, Collections.emptySet(), Collections.emptyMap(), SSExcelSheetName.USER_SIMPLE);
    }
}
