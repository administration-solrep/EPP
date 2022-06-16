package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.service.version.CreerVersionContext;
import fr.dila.solonepp.api.service.version.CreerVersionResponse;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service permettant de gérer la logique de création des versions d'événements.
 *
 * @author jtremeaux
 */
public interface VersionCreationService extends Serializable {
    /**
     * Crée / modifie une nouvelle version / événement / dossier.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création / modification de version
     */
    void createVersion(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Complète une version publiée existante.
     * Cette méthode permet de créer une version brouillon (pour complétion) à partir de la version publiée, ou
     * de modifier cette version brouillon autant de fois que nécessaire avant de la publier
     * à nouveau.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    void completerBrouillon(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Complète une version publiée existante.
     * Cette méthode permet de publier une version brouillon (pour complétion), ou de créer directement
     * une nouvelle version à l'état publié.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    void completerPublier(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Complète et publie une version existante en mode delta.
     * Sur une version publiée : crée une nouvelle version publiée complétée.
     * Sur une version brouillon : crée une nouvelle version publiée complétée + une version brouillon complétée.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    void completerPublierDelta(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Rectifier une version publiée existante.
     * Cette méthode permet de créer une version brouillon (pour rectification) à partir de la version publiée, ou
     * de modifier cette version brouillon autant de fois que nécessaire avant de la publier
     * à nouveau.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    CreerVersionResponse rectifierBrouillon(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Complète une version publiée existante.
     * Cette méthode permet de publier une version brouillon (pour rectification), ou de créer directement
     * une nouvelle version à l'état publié.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    CreerVersionResponse rectifierPublier(CoreSession session, CreerVersionContext creerVersionContext);

    /**
     * Rectifie et publie une version existante en mode delta.
     * Sur une version publiée : crée une nouvelle version publiée rectifiée.
     * Sur une version brouillon : crée une nouvelle version publiée rectifiée + une version brouillon rectifiée.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    void rectifierPublierDelta(CoreSession session, CreerVersionContext creerVersionContext);
}
