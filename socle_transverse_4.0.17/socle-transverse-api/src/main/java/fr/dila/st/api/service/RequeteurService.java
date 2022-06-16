package fr.dila.st.api.service;

import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.requeteur.RequeteExperte;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 *
 * @author JGZ La classe de service pour le requeteur de réponses Manipule le fichier généré par l'écran de suivi.
 *
 */

public interface RequeteurService {
    /**
     * Retourne la liste des résultats de la requête experte.
     *
     *
     * @param session
     *            la session courante
     * @param requeteExperte
     * @return
     *
     */
    List<DocumentModel> query(CoreSession session, RequeteExperte requeteExperte);

    /**
     * Retourne nombre de résultats de la requete experte.
     *
     * @param session
     * @param requeteExperte
     * @return le nombre de résultats de la requête.
     *
     */
    Long countResults(CoreSession session, RequeteExperte requeteExperte);

    /**
     * Retourne la requête complète en UFNXQL de la requête experte.
     *
     *
     * @param session La
     *            session : la session est utilisé par les résolveurs de mots-clés spéciaux ( par exemple pour prendre
     *            le ministère du principal courant)
     * @param requeteExperte
     * @return La requête complète
     */
    String getPattern(CoreSession session, RequeteExperte requeteExperte);

    /**
     * Retourne la requête complète en UFNXQL de la portion de requête
     *
     * @param session
     * @param queryPart
     *            : la clause where de la requête.
     * @return La requête complète
     */
    String getPattern(CoreSession session, String queryPart);

    /**
     * Retourne la requête complète en UFNXQL de la requête experte, résolue avec l'environnement donné. (Cet
     * environnement sert à effectuer certains pré-traitements sur la requête par exemple, pour caculer la date du jour
     * plus un certains nombre de jours ou de mois)
     *
     * @param session
     * @param requeteExperte
     * @param env L'environnement de la requête.
     * @return La requête complète
     *
     */
    String getPattern(CoreSession session, RequeteExperte requeteExperte, Map<String, Object> env);

    /**
     * Retourne la requête complète en UFNXQL de la portion de requête, assemblé avec l'assembleur donné en paramêtre
     *
     * @param assembler
     *            l'assembleur de requête
     * @param queryPart
     *            : la clause where de la requête.
     * @return La requête complète
     */
    String getPattern(CoreSession session, QueryAssembler assembler, String queryPart);

    /**
     * Remplace les mots-clés par leur valeur
     *
     * @param session
     * @param query
     * @return
     */
    String resolveKeywords(CoreSession session, String query);
}
