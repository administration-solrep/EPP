package fr.dila.st.core.query.translation;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryAnalyzer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.LiteralList;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.Reference;

/**
 * Un analyseur de requete UFNXQL et NXQL qui permet de parser la requête et d'extraire des informations nécessaires à
 * une traduction en language utilisateur.
 *
 * @author jgomez
 *
 */
public class QueryTranslatorAnalyzer extends QueryAnalyzer {
    private static final String DEFAULT_REFERENCE_NAME = "REF";
    private static final String AND = "AND";
    private static final String OR = "OR";

    private static final Log LOG = LogFactory.getLog(QueryTranslatorAnalyzer.class);

    private TranslatedStatement currentStat;
    private List<TranslatedStatement> statements;

    public QueryTranslatorAnalyzer() {
        super();
        statements = new ArrayList<TranslatedStatement>();
        currentStat = new TranslatedStatement("", DEFAULT_REFERENCE_NAME, "", "");
    }

    /**
     * Retourne une liste de TranslatedStatement contenant les informations de traduction de la requête.
     *
     * @return une liste d'objet traduction
     */
    public List<TranslatedStatement> getStatements() {
        return statements;
    }

    /**
     * Sur une requête du type : SELECT * FROM Dossier dos as d WHERE q.qu:numeroQuestion = 3, la référence est :
     * q.qu:question. Met à jour la référence de la clause dans la clause courante et ajoute celle-ci à la liste des
     * clauses.
     */
    @Override
    public void visitReference(Reference ref) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in visitReference translator (" + ref + ")");
        }
        if (currentStat.getSearchField().equals(DEFAULT_REFERENCE_NAME)) {
            currentStat.setSearchField(ref.name);
        } else {
            currentStat = new TranslatedStatement();
            currentStat.setSearchField(ref.name);
        }
        statements.add(currentStat);
    }

    /**
     * Sur une requête du type : SELECT * FROM Dossier dos as d WHERE q.qu:numeroQuestion = 3, le litéral est 3.
     */
    @Override
    public void visitLiteral(Literal l) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in visitLiteral translator (" + l + ")");
        }
        currentStat.setValue(l.toString());
    }

    /**
     * Sur une requête du type : SELECT * FROM Dossier dos as d WHERE q.qu:numeroQuestion IN ('154','155'), la liste est
     * ('154','155').
     */
    @Override
    public void visitLiteralList(LiteralList l) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("in visitLiteralList translator (" + l + ")");
        }
        currentStat.setValue(getString(l));
    }

    private String getString(LiteralList l) {
        StringBuilder buf = new StringBuilder();
        if (l.isEmpty()) {
            return "";
        }
        buf.append(l.get(0).toString());
        for (int i = 1, size = l.size(); i < size; i++) {
            buf.append(",").append(l.get(i).toString());
        }
        return buf.toString();
    }

    /**
     * Sur une requête du type : SELECT * FROM Dossier dos as d WHERE q.qu:numeroQuestion = 3, l'opérateur est =.
     */
    @Override
    public void visitOperator(Operator o) {
        if (o.toString().equals(OR) || o.toString().equals(AND)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("in visitLogical Operator translator (" + o + ") : " + currentStat.getSearchField());
            }
            currentStat.setLogicalOperator(o.toString());
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("in visitCond Operator translator (" + o + ") " + currentStat.getSearchField());
            }
            currentStat.setConditionalOperator(o.toString());
        }
    }
}
