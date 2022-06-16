package fr.dila.st.ui.services.actions;

import fr.dila.st.core.query.Requeteur;
import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.smartquery.HistoryList;
import fr.dila.st.core.smartquery.IncrementalSmartNXQLQuery;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface STSmartNXQLQueryActionService {
    Requeteur initCurrentSmartQuery(
        String existingQueryPart,
        boolean resetHistory,
        HistoryList<String> queryPartHistory
    );

    void buildQueryPart(IncrementalSmartNXQLQuery currentSmartQuery, HistoryList<String> queryPartHistory);

    /**
     * Met le libellé utilisateur correspondant à la portion de requête ajouté.
     */
    void updateQuery(Requeteur requeteur, String completeQuery, IncrementalSmartNXQLQuery currentSmartQuery);

    List<TranslatedStatement> getUserInfo(SpecificContext context, Requeteur requeteur);

    void delete(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    );

    void up(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    );

    void down(
        Requeteur requeteur,
        Integer index,
        IncrementalSmartNXQLQuery currentSmartQuery,
        HistoryList<String> queryPartHistory
    );
}
