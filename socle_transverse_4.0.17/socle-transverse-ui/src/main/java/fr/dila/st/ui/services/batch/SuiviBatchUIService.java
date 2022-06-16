package fr.dila.st.ui.services.batch;

import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchLoggerResultModel;
import fr.dila.st.api.event.batch.QuartzInfo;
import fr.dila.st.api.model.Page;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.ui.bean.BatchDetailListe;
import fr.dila.st.ui.bean.BatchListe;
import fr.dila.st.ui.bean.BatchPlanificationListe;
import fr.dila.st.ui.th.bean.BatchNotifForm;
import fr.dila.st.ui.th.bean.BatchSearchForm;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface SuiviBatchUIService {
    Date getDefaultCurrentDateDebut();

    /**
     * Controle l'accès à la vue correspondante
     */
    boolean isAccessAuthorized(STPrincipal currentUser);

    /**
     * Retourne la liste des logs
     */
    Page<BatchLoggerModel> getAllBatchLogger(
        Date currentDateDebut,
        Date currentDateFin,
        boolean isFirstAccess,
        Integer page,
        Integer pageSize
    );

    List<BatchLoggerResultModel> getCurrentBatchResult(BatchLoggerModel currentBatchLogger);

    List<QuartzInfo> getAllPlanification(CoreSession session);

    /**
     * Initialise le form BatchNotifForm avec les données de la BDD
     * @param session CoreSession
     * @return un objet BatchNotifForm complet
     */
    BatchNotifForm initBatchNotifForm(CoreSession session);

    /**
     * Mise à jour des données de la page notification : activation et liste des users
     * @param batchNotifForm le formulaire
     * @param session la CoreSession
     */
    void updateNotification(BatchNotifForm batchNotifForm, CoreSession session);

    String getTypeBatch(String typeBatch);

    BatchListe getBatchListe(BatchSearchForm batchSearchForm);

    BatchDetailListe getBatchDetailListe(String batchLoggerModelId);

    BatchPlanificationListe getBatchPlanificationListe(CoreSession session);
}
