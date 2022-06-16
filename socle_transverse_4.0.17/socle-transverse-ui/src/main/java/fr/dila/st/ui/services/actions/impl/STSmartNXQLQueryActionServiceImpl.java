/**
 *
 * Surcharge le beam seam SmartNXQLQueryActions du package smartSearch de Nuxeo,
 * afin d'apporter d'avantages de fonctionnalité.
 * Correction de problème d'échappement dans la requête et traduction d'une requête en language
 * utilisateur.
 *
 */
package fr.dila.st.ui.services.actions.impl;

import fr.dila.st.core.query.Requeteur;
import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.smartquery.HistoryList;
import fr.dila.st.core.smartquery.IncrementalSmartNXQLQuery;
import fr.dila.st.ui.services.actions.STSmartNXQLQueryActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class STSmartNXQLQueryActionServiceImpl implements STSmartNXQLQueryActionService {
    private static final Log LOG = LogFactory.getLog(STSmartNXQLQueryActionServiceImpl.class);

    public static final int HISTORY_CAPACITY = 20;

    public STSmartNXQLQueryActionServiceImpl() {
        super();
    }

    @Override
    public Requeteur initCurrentSmartQuery(
        String existingQueryPart,
        boolean resetHistory,
        HistoryList<String> queryPartHistory
    ) {
        if (resetHistory) {
            addToHistory(existingQueryPart, new HistoryList<String>(HISTORY_CAPACITY));
        }

        Requeteur requeteur = new Requeteur();
        String completeQuery = "SELECT * FROM Dossier AS d WHERE " + existingQueryPart;
        requeteur.setQuery(completeQuery);
        return requeteur;
    }

    private void addToHistory(String queryPart, HistoryList<String> queryPartHistory) {
        if (queryPartHistory == null) {
            return;
        }
        if (queryPart == null) {
            queryPart = "";
        }
        if (queryPartHistory.size() == 0) {
            queryPartHistory.addLast(queryPart);
        } else {
            String lastQueryPart = queryPartHistory.getLast();
            if (!queryPart.equals(lastQueryPart)) {
                queryPartHistory.addLast(queryPart);
            }
        }
    }

    @Override
    public void buildQueryPart(IncrementalSmartNXQLQuery currentSmartQuery, HistoryList<String> queryPartHistory) {
        String newQuery = currentSmartQuery.buildQuery();
        addToHistory(newQuery, queryPartHistory);
    }

    /**
     * Met le libellé utilisateur correspondant à la portion de requête ajouté.
     */
    @Override
    public void updateQuery(Requeteur requeteur, String completeQuery, IncrementalSmartNXQLQuery currentSmartQuery) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Update User Info");
        }
        updateCompleteQuery(currentSmartQuery);
        requeteur.setQuery(completeQuery);
        requeteur.updateTranslation();
        if (LOG.isDebugEnabled()) {
            LOG.debug("User info : " + completeQuery);
        }
    }

    /**
     * Met à jour l'attribut completeQuery qui contient la requête.
     */
    protected String updateCompleteQuery(IncrementalSmartNXQLQuery currentSmartQuery) {
        return "SELECT * FROM Dossier AS d WHERE " + currentSmartQuery.getExistingQueryPart();
    }

    @Override
    public List<TranslatedStatement> getUserInfo(SpecificContext context, Requeteur requeteur) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Get User Info");
        }
        List<TranslatedStatement> statements = new ArrayList<TranslatedStatement>();
        try {
            requeteur.updateTranslation();
            statements = requeteur.getStatements();
            if (LOG.isDebugEnabled()) {
                LOG.debug("QUERY = " + requeteur.getQuery());
            }
        } catch (Exception e) {
            // Erreur de parsing qui ne devrait pas arriver dans cette méthode,
            // l'utilisateur ne devrait pouvoir
            // constituer que des
            // requêtes valides.
            // On ne peut pas récupérer l'erreur à ce stade. On réinitialise la
            // requête et le tableau de traduction.
            // La requête de l'utilisateur est perdue.
            context.getMessageQueue().addInfoToQueue("error.smart.query.invalidQuery");
            return statements;
        }
        return statements;
    }

    private void reinitialiseSmartQuery(
        Requeteur requeteur,
        HistoryList<String> queryPartHistory,
        IncrementalSmartNXQLQuery currentSmartQuery
    ) {
        addToHistory(requeteur.getWherePart(), queryPartHistory);
        currentSmartQuery.setExistingQueryPart(requeteur.getWherePart());
        initCurrentSmartQuery(requeteur.getWherePart(), false, queryPartHistory);
    }

    @Override
    public void delete(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    ) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Supprimer à l'index " + index);
        }
        requeteur.remove(index);
        requeteur.updateTranslation();
        LOG.info("Met la requête à vide");
        reinitialiseSmartQuery(requeteur, queryPartHistory, currentSmartQuery);
    }

    @Override
    public void up(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    ) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Monter à l'index " + index);
        }
        requeteur.up(index);
        requeteur.updateTranslation();
        reinitialiseSmartQuery(requeteur, queryPartHistory, currentSmartQuery);
    }

    @Override
    public void down(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    ) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Descendre à l'index " + index);
        }
        requeteur.down(index);
        requeteur.updateTranslation();
        reinitialiseSmartQuery(requeteur, queryPartHistory, currentSmartQuery);
    }
}
