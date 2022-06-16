package fr.dila.st.core.query.translation;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.UFNXQLQueryParser;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;

/**
 *
 * 'Traducteur' de clause where NXSQL ou UFNXQL en langage humain.
 *
 * @author jgomez
 *
 */
public class QueryTranslator {
    private static final Log LOGGER = LogFactory.getLog(QueryTranslator.class);

    /**
     * Default constructor
     */
    public QueryTranslator() {
        // do nothing
    }

    /**
     * Traduit une clause where (sans order by) et renvoie une liste d'objet TranslatedStatement contenant des
     * informations pour afficher les clauses en 'langage compréhensible par l'utilisateur'
     *
     * @param whereClause
     * @return
     */
    public List<TranslatedStatement> translateUFNXL(String whereClause) throws QueryParseException {
        LOGGER.debug("Requête à traduire : " + whereClause);
        SQLQuery sqlQuery = UFNXQLQueryParser.parse(whereClause);
        QueryTranslatorAnalyzer analyze = new QueryTranslatorAnalyzer();
        sqlQuery.accept(analyze);
        return analyze.getStatements();
    }
}
