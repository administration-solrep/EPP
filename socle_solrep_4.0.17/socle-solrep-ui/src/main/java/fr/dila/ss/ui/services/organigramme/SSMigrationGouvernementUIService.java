package fr.dila.ss.ui.services.organigramme;

import fr.dila.ss.api.migration.MigrationInfo;
import fr.dila.ss.api.migration.MigrationLoggerInfos;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.service.SSChangementGouvernementService;
import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.bean.MigrationDetailDTO;
import fr.dila.ss.ui.bean.SSHistoriqueMigrationDetailDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface SSMigrationGouvernementUIService {
    boolean isAffichageEtat();

    void setAffichageEtat(boolean affichageEtat);

    void initialize(SpecificContext context);

    /**
     * Supprimer la migration courante
     *
     * @param migrationInfo
     * @throws ClientException
     */
    void supprimerMigration(MigrationInfo migrationInfo, SpecificContext context);

    /**
     * Ajouter une nouvelle migration
     */
    MigrationInfo ajouterMigration(SpecificContext context);

    SSChangementGouvernementService getChangementGouvernementService();

    /**
     * Lancer les migrations
     */
    String lancerMigration(SpecificContext context);

    /**
     * Controle l'accès à la vue correspondante
     *
     */
    boolean isAccessAuthorized(CoreSession session);

    MigrationLoggerInfos getMigrationLoggerInfos(String widgetName, Long loggerId);

    boolean getDeleteOldElementOrganigramme(String migrationId, SpecificContext context);

    boolean getMigrationWithDossierClos(String migrationId, SpecificContext context);

    boolean getMigrationModeleFdr(String migrationId, SpecificContext context);

    void resetElementOrganigramme(String migrationId, SpecificContext context);

    String getErrorName();

    List<MigrationLoggerModel> getMigrationEnCoursList();

    boolean candisplaySucceed(Long loggerId);

    boolean candisplayFailure(Long loggerId);

    boolean candisplayWaiter(Long loggerId);

    String getLogMessage(MigrationLoggerModel migrationLoggerModel);

    /**
     *  Reset l'affichage de la page / vide les champs à saisir
     */
    void resetData(SpecificContext context);

    /**
     * Lance l'événement qui va envoyer les résultats de la migration à l'utilisateur connecté (asynchrone)
     */
    void sendMailMigrationDetails(SpecificContext context);

    List<MigrationLoggerModel> getMigrationLoggerModelList();

    /**
     * Renvoie le détail d'un item de l'historique de migration.
     * @param migrationDetailId id de l'objet MigrationDetail
     * @return le détail SSHistoriqueMigrationDetailDTO construit à partir des données en base.
     */
    SSHistoriqueMigrationDetailDTO getHistoriqueMigrationDetailDTO(Long migrationDetailId);

    /**
     * Renvoie le détail des migrations en cours d'exécution.
     * @return un objet MigrationDTO
     */
    MigrationDTO getMigrationDTO(SpecificContext context);

    /**
     * Ajoute à la liste des ordres de migration les ordres correspondant aux
     * MigrationDetailDTO passés en paramètre.
     *
     * @param migrationDetailDTOs
     */
    void ajouterMigrations(List<MigrationDetailDTO> migrationDetailDTOs, SpecificContext context);
}
