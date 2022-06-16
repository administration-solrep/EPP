package fr.dila.ss.api.service;

import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface du service de gestion du changement de gouvernement.
 *
 * @author arolin
 */
public interface SSChangementGouvernementService {
    /**
     * Migre les fils d'un noeud organigramme vers un autre noeud organigramme et migre le numéro NOR des directions dans le cas d'une migration de ministère.
     *
     * @param session
     * @param oldNode
     * @param newNode
     * @param migrationLoggerModel
     * @
     */
    void migrerElementsFils(
        CoreSession session,
        OrganigrammeNode oldNode,
        OrganigrammeNode newNode,
        MigrationLoggerModel migrationLoggerModel
    );

    /**
     * Migre les postes des étapes de feuille de route des instances et des modèles vers un autre poste.
     *
     * @param session
     * @param oldNode
     * @param newNode
     * @
     */
    void migrerModeleStepFdr(
        CoreSession session,
        OrganigrammeNode oldNode,
        OrganigrammeNode newNode,
        MigrationLoggerModel migrationLoggerModel
    );

    /**
     * Met à jour les corbeilles du poste : change les droits et le nom. (on déplace les dossiers links de l'ancienne mailbox dans la nouvelle)
     *
     * @param session
     * @param oldNode
     * @param newNode
     * @
     */
    void updateMailBox(
        CoreSession session,
        OrganigrammeNode oldNode,
        OrganigrammeNode newNode,
        MigrationLoggerModel migrationLoggerModel
    );

    /**
     * Migre les modèles de feuille de route d'un ministère ou une direction vers un autre ministère / direction.
     *
     * @param session
     * @param oldNode
     * @param newNode
     * @param migrationLoggerModel
     * @
     */
    void migrerModeleFdrMinistere(
        CoreSession session,
        EntiteNode oldNode,
        EntiteNode newNode,
        MigrationLoggerModel migrationLoggerModel,
        Boolean desactivateModelFdr
    );

    /**
     * Migre les modèles de feuille de route d'une direction vers une autre direction.
     *
     * @param session
     * @param oldMinistereNode
     * @param oldDirectionNode
     * @param newMinistereNode
     * @param newDirectionNode
     * @
     */
    void migrerModeleFdrDirection(
        CoreSession session,
        EntiteNode oldMinistereNode,
        UniteStructurelleNode oldDirectionNode,
        EntiteNode newMinistereNode,
        UniteStructurelleNode newDirectionNode,
        MigrationLoggerModel migrationLoggerModel,
        Boolean desactivateModelFdr
    );

    /**
     * Modifie le ministère de rattachement pour les dossiers à l'état clos, NOR attribué ou abandonné.
     *
     * @param session
     * @param oldNode
     * @param newNode
     * @
     */
    void updateDossierMinistereRattachement(
        CoreSession session,
        OrganigrammeNode oldNode,
        OrganigrammeNode newNode,
        MigrationLoggerModel migrationLoggerModel
    );

    /**
     * Modifie la direction de rattachement pour les dossiers à l'état clos, NOR attribué ou abandonné.
     *
     * @param session
     * @param oldMinistereNode
     * @param oldDirectionNode
     * @param newMinistereNode
     * @param newDirectionNode
     * @
     */
    void updateDossierDirectionRattachement(
        CoreSession session,
        OrganigrammeNode oldMinistereNode,
        OrganigrammeNode oldDirectionNode,
        OrganigrammeNode newMinistereNode,
        OrganigrammeNode newDirectionNode,
        MigrationLoggerModel migrationLoggerModel
    );

    /**
     * Creation d'un logger pour une migration
     *
     * @param ssPrincipal
     *
     * @return
     * @
     */
    MigrationLoggerModel createMigrationLogger(final String ssPrincipal);

    /**
     * Rechercche de logger de migration par id
     *
     * @param idLogger
     * @return
     * @
     */
    MigrationLoggerModel findMigrationById(final Long idLogger);

    /**
     * Sauvegarde du {@link MigrationLoggerModel}
     *
     * @param migrationLoggerModel
     * @
     */
    void flushMigrationLogger(final MigrationLoggerModel migrationLoggerModel);

    /**
     * Creation d'un detail de logger de migration au statut OK
     *
     * @param migrationLoggerModel
     * @param type
     * @param detail
     * @
     */
    MigrationDetailModel createMigrationDetailFor(
        MigrationLoggerModel migrationLoggerModel,
        String type,
        String detail
    );

    /**
     * Creation d'un detail de logger de migration
     *
     * @param migrationLoggerModel
     * @param type
     * @param detail
     * @param statut
     * @
     */
    MigrationDetailModel createMigrationDetailFor(
        MigrationLoggerModel migrationLoggerModel,
        String type,
        String detail,
        String statut
    );

    /**
     * @return list of MigrationLoggerModel
     * @
     */
    List<MigrationLoggerModel> getMigrationWithoutEndDate();

    /**
     * return list of MigrationLoggerMode
     *
     * @return
     * @
     */
    List<MigrationLoggerModel> getMigrationLoggerModelList();

    /**
     * @param loggerId
     * @return
     * @
     */
    List<MigrationDetailModel> getMigrationDetailModelList(final Long loggerId);

    /**
     * updateMigrationDetail
     *
     * @param migrationDetailModel
     * @
     */
    void flushMigrationDetail(final MigrationDetailModel migrationDetailModel);

    /**
     * Retourne le nom de la migration
     *
     * @param migrationLoggerModel
     * @return
     * @
     */
    String getLogMessage(MigrationLoggerModel migrationLoggerModel);
}
