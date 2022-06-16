package fr.dila.ss.ui.contentview;

import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.helper.PaginationHelper;
import fr.dila.st.core.query.QueryHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.contentview.AbstractDTOPageProvider;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

public class ModeleFDRPageProvider extends AbstractDTOPageProvider {
    private static final long serialVersionUID = 1L;

    @Override
    protected void fillCurrentPageMapList(CoreSession coreSession) {
        currentItems = new ArrayList<>();

        resultsCount = QueryUtils.doCountQuery(coreSession, query);
        // recupere la liste des ids triés
        // se baser sur ce tri pour retourner une liste de map triée dans le bon
        // ordre
        if (resultsCount > 0) {
            // récupération page courante
            offset = PaginationHelper.calculeOffSet(offset, getPageSize(), resultsCount);

            List<String> ids = QueryUtils.doQueryForIds(coreSession, query, getPageSize(), offset);
            populateFromFeuilleRouteIds(coreSession, ids);
        }
    }

    protected void populateFromFeuilleRouteIds(CoreSession coreSession, List<String> ids) {
        if (!ids.isEmpty()) {
            PrefetchInfo prefetchInfo = new PrefetchInfo(
                STSchemaConstant.DUBLINCORE_SCHEMA + "," + SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA
            );
            List<DocumentModel> dml = QueryHelper.retrieveDocuments(coreSession, ids, prefetchInfo);

            for (DocumentModel dm : dml) {
                if (dm != null) {
                    String etat = StatutModeleFDR.getStatutFromDoc(dm);
                    String intitule = (String) dm.getPropertyValue(STSchemaConstant.DUBLINCORE_TITLE_XPATH);
                    STUserService userService = STServiceLocator.getSTUserService();
                    String auteur = userService.getUserFullName(
                        (String) dm.getPropertyValue(STSchemaConstant.DUBLINCORE_CREATOR_XPATH)
                    );
                    Calendar modifiedDate = (Calendar) dm.getPropertyValue(STSchemaConstant.DUBLINCORE_MODIFIED_XPATH);
                    Boolean lock = dm.getLockInfo() != null;
                    String lockOwner = STActionsServiceLocator
                        .getSTLockActionService()
                        .getLockOwnerName(dm, coreSession);
                    final String lockOwnerFullName = STServiceLocator.getSTUserService().getUserFullName(lockOwner);
                    final Calendar date = modifiedDate;
                    FeuilleRouteDTO fdrdto = new FeuilleRouteDTO(
                        dm.getId(),
                        etat,
                        intitule,
                        null,
                        auteur,
                        SolonDateConverter.DATE_SLASH.format(date),
                        lock,
                        lockOwnerFullName
                    );

                    if (currentItems != null) {
                        currentItems.add(fdrdto);
                    }
                }
            }
        }
    }
}
