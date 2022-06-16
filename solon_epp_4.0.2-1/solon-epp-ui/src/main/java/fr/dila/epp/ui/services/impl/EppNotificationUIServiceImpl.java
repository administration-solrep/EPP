package fr.dila.epp.ui.services.impl;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.NotificationDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.NotificationUIService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * Service d'alerte utilisateur lorsque la corbeille et/ou la communication
 * courante sont modifiées.
 */
public class EppNotificationUIServiceImpl implements NotificationUIService {
    private static final int DELAI_CHECK_NOTIF_DEFAULT = 30000; // en millisecondes

    public EppNotificationUIServiceImpl() {
        // Default constructor
    }

    @Override
    public NotificationDTO getNotificationDTO(SpecificContext context) {
        CoreSession session = context.getSession();
        String corbeilleId = context.getFromContextData(STContextDataKey.CORBEILLE_ID);
        String evenementId = context.getFromContextData(STContextDataKey.EVENEMENT_ID);

        NotificationDTO notificationDTO = new NotificationDTO();
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();

        // Modifications sur la corbeille
        if (StringUtils.isNotEmpty(corbeilleId)) {
            Long count = jetonService.getCountJetonsCorbeilleSince(session, corbeilleId, getLastUpdate(context));
            if (count > 0) {
                notificationDTO.setCorbeilleModified(true);
            }
        }

        // Modifications sur l'évènement
        if (StringUtils.isNotEmpty(evenementId)) {
            DocumentModel evenementDoc = context.getSession().getDocument(new IdRef(evenementId));
            if (evenementDoc.hasSchema(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
                Evenement evenement = evenementDoc.getAdapter(Evenement.class);
                Long count = jetonService.getCountJetonsEvenementSince(
                    session,
                    evenement.getTitle(),
                    getLastUpdate(context)
                );
                if (count > 0) {
                    notificationDTO.setEvenementModified(true);
                }
            }
        }

        UserSessionHelper.putUserSessionParameter(
            context,
            STUserSessionKey.LAST_USER_NOTIFICATION,
            Calendar.getInstance()
        );

        return notificationDTO;
    }

    @Override
    public long getNotificationDelai(SpecificContext context) {
        return DELAI_CHECK_NOTIF_DEFAULT;
    }

    @Override
    public void reloadCacheTdrEppIfNecessary(SpecificContext context) {}
}
