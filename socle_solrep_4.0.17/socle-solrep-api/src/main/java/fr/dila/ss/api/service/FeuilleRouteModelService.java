package fr.dila.ss.api.service;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer un catalogue de modèles de feuille de route.
 *
 * @author jtremeaux
 */
public interface FeuilleRouteModelService extends Serializable {
    /**
     * Retourne le répertoire racine des modèles de feuilles de route.
     *
     * @return Répertoire des modèles de feuilles de route
     *
     */
    DocumentModel getFeuilleRouteModelFolder(CoreSession session);

    /**
     * Retourne l'id du répertoire racine des modèles de feuilles de route.
     *
     * @return Id du répertoire des modèles de feuilles de route
     *
     */
    String getFeuilleRouteModelFolderId(CoreSession session);

    /**
     * Substitue un poste dans les modèles de feuilles de route.
     *
     * @param session
     *            Session
     * @param feuilleRouteDocList
     *            Liste des feuilles de route à traiter
     * @param ancienPosteId
     *            Identifiant technique de l'ancien poste
     * @param nouveauPosteId
     *            Identifiant technique du nouveau poste
     *
     */
    void substituerPoste(
        CoreSession session,
        List<DocumentModel> feuilleRouteDocList,
        String ancienPosteId,
        String nouveauPosteId
    );

    /**
     * Construit le prédicat pour restreindre les feuilles de routes à celles que l'utilisateur peut voir.
     *
     * @param ssPrincipal
     *            Principal
     * @param ministereField
     *            Nom du champ (ex. fdr:ministere)
     * @return Prédicat
     */
    String getMinistereCriteria(SSPrincipal ssPrincipal, String ministereField);

    /**
     * Détermine si l'intitulé d'un modèle de feuille de route est unique.
     *
     * @param session
     *            Session
     * @param route
     *            Modèle de feuille de route
     * @return Intitulé unique ou non
     *
     */
    boolean isIntituleUnique(CoreSession session, SSFeuilleRoute route);

    /**
     * Renvoie les modèles de feuille de route rattaché à l'entité et à la direction
     * si <b>hasDirection</b> est vrai.
     *
     * @param session
     * @param ministereId
     * @param directionId
     * @param hasDirection
     * @return les modèles de feuille de route rattaché à l'entité et à la direction
     *         si <b>hasDirection</b> est vrai.
     */
    List<DocumentModel> getFdrModelFromMinistereAndDirection(
        CoreSession session,
        String ministereId,
        String directionId,
        boolean hasDirection
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

    void migrerModeleFdrMinistere(
        final CoreSession session,
        final EntiteNode oldNode,
        final EntiteNode newNode,
        final MigrationLoggerModel migrationLoggerModel,
        Boolean desactivateModelFdr
    );
}
