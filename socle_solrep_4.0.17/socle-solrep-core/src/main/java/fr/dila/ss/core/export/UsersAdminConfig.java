package fr.dila.ss.core.export;

import fr.dila.ss.core.export.enums.SSExcelSheetName;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.export.AbstractEnumExportConfig;
import fr.dila.st.core.export.enums.ExcelSheetName;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class UsersAdminConfig extends AbstractEnumExportConfig<DocumentModel> {
    private static final STLogger LOGGER = STLogFactory.getLog(UsersAdminConfig.class);

    private final boolean isAdmin;
    private final boolean isAdminMinisteriel;
    private final Set<String> adminMinisterielMinisteres;
    private final SimpleDateFormat dateFormat = SolonDateConverter.DATE_SLASH.simpleDateFormat();
    private final Map<String, Calendar> usernamesWithDateDerniereConnexion;

    public UsersAdminConfig(
        List<DocumentModel> usersDocs,
        boolean isAdmin,
        boolean isAdminMinisteriel,
        Set<String> adminMinisterielMinisteres,
        Map<String, Calendar> usernamesWithDateDerniereConnexion
    ) {
        this(
            usersDocs,
            isAdmin,
            isAdminMinisteriel,
            adminMinisterielMinisteres,
            usernamesWithDateDerniereConnexion,
            SSExcelSheetName.USER_ADMIN
        );
    }

    protected UsersAdminConfig(
        List<DocumentModel> usersDocs,
        boolean isAdmin,
        boolean isAdminMinisteriel,
        Set<String> adminMinisterielMinisteres,
        Map<String, Calendar> usernamesWithDateDerniereConnexion,
        ExcelSheetName sheetName
    ) {
        super(usersDocs, sheetName);
        this.isAdmin = isAdmin;
        this.isAdminMinisteriel = isAdminMinisteriel;
        this.adminMinisterielMinisteres = adminMinisterielMinisteres;
        this.usernamesWithDateDerniereConnexion = usernamesWithDateDerniereConnexion;
    }

    @Override
    protected String[] getDataCells(CoreSession session, DocumentModel item) {
        String[] dataCells;

        STUser user = item.getAdapter(STUser.class);
        String dateDebut = formatCalendar(user.getDateDebut(), dateFormat);
        String ministeres = "";
        List<String> ministeresIds = Collections.emptyList();
        String directions = "";
        String postes = "";
        try {
            STUserService userService = STServiceLocator.getSTUserService();
            String userId = item.getId();
            ministeres = userService.getUserMinisteres(userId);
            ministeresIds = userService.getAllUserMinisteresId(userId);
            directions = userService.getAllDirectionsRattachement(userId);
            postes = userService.getUserPostes(userId);
        } catch (NuxeoException exc) {
            LOGGER.warn(STLogEnumImpl.FAIL_GET_MINISTERE_TEC, exc);
        }

        boolean exportDateLastConnection = false;
        if (isAdmin) {
            exportDateLastConnection = true;
        } else if (isAdminMinisteriel) {
            // on exporte seulement si on a un élément en commun entre les ministères de l'utilisateur
            // et les ministères de l'utilisateur réalisant l'export
            exportDateLastConnection = !Collections.disjoint(ministeresIds, adminMinisterielMinisteres);
        }

        if (exportDateLastConnection) {
            String username = user.getUsername();
            String dateDerniereConnexion = formatCalendar(usernamesWithDateDerniereConnexion.get(username), dateFormat);

            dataCells =
                new String[] {
                    username,
                    user.getLastName(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getTelephoneNumber(),
                    dateDebut,
                    ministeres,
                    directions,
                    postes,
                    dateDerniereConnexion
                };
        } else if (isAdminMinisteriel) {
            // cas d'un administrateur ministériel sans autorisation d'accès à la date de dernière connexion
            // (utilisateur dans un ministère non géré)
            dataCells =
                new String[] {
                    user.getUsername(),
                    user.getLastName(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getTelephoneNumber(),
                    dateDebut,
                    ministeres,
                    directions,
                    postes,
                    ResourceHelper.getString("export.non.accessible")
                };
        } else {
            dataCells =
                new String[] {
                    user.getUsername(),
                    user.getLastName(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getTelephoneNumber(),
                    dateDebut,
                    ministeres,
                    directions,
                    postes
                };
        }

        return dataCells;
    }
}
