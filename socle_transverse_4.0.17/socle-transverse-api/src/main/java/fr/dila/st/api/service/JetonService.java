package fr.dila.st.api.service;

import fr.dila.st.api.jeton.JetonServiceDto;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les jetons.
 *
 *
 */
public interface JetonService extends Serializable {
    /**
     * Cette fonction renvoie la liste de document appartenant à un jeton
     *
     * @param session
     *            Session
     * @param owner
     *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
     * @param numeroJeton
     *            numéro du jeton (null equivaut à 0)
     * @param typeWebservice
     *            le type de webservice auquel appartient le jeton
     * @return JetonServiceDto contient les informations récupérées (dernier envoi, liste de document, prochain numéro
     *         de jeton disponible contenant des données)
     */
    JetonServiceDto getDocuments(CoreSession session, String owner, Long numeroJeton, String typeWebservice);

    /**
     * Ajoute un dossier dans le panier à travers un jetonDoc
     *
     * @param session
     *            Session
     * @param typeWebservice
     *            le type de webservice auquel appartient le jeton
     * @param owner
     *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
     * @param dossierDoc
     *            le documentModel représentant le dossier à ajouter au panier
     * @param numeroDocument
     *            numéro du dossier compréhensible par un administrateur fonctionnel pour l'envoi de mail
     */
    void addDocumentInBasket(
        final CoreSession session,
        final String typeWebservice,
        final String owner,
        final DocumentModel dossierDoc,
        final String numeroDocument,
        final String typeModification,
        final List<String> idsComplementaires
    );

    void addDocumentInBasket(
        final CoreSession session,
        final String typeWebservice,
        final String owner,
        final List<DocumentModel> dossierDocs,
        final List<String> numeroDocuments,
        final String typeModification,
        final List<String> idsComplementaires
    );

    /**
     * Retourne la valeur du plus grand jeton utilisé pour un webservice et propriétaire donné Si le jeton n'a jamais
     * été créé, retourne -100 Sinon retourne le numéro de jeton
     *
     * @param session
     * @param owner
     * @param typeWebservice
     * @return numéro de jetonMax / numero error jeton sinon
     */
    Long getNumeroJetonMaxForWS(CoreSession session, String owner, String typeWebservice);
}
