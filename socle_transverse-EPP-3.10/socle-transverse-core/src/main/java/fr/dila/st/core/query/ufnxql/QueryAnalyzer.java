package fr.dila.st.core.query.ufnxql;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.query.sql.model.DoubleLiteral;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.FromClause;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.GroupByClause;
import org.nuxeo.ecm.core.query.sql.model.HavingClause;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.LiteralList;
import org.nuxeo.ecm.core.query.sql.model.MultiExpression;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.OperandList;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.OrderByClause;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.OrderByList;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.ReferenceList;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SelectClause;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;

import fr.dila.st.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.dila.st.core.query.ufnxql.parser.SGroupByClause;

/**
 * Parcours la requete UFNXQL pour recuperer les type de document manipulés et leur alias ainsi que les champs
 * referencés
 * 
 * @author spesnel
 */
public class QueryAnalyzer extends SDefaultQueryVisitor {

	private static final Log				LOGGER				= LogFactory.getLog(QueryAnalyzer.class);

	/**
     * 
     */
	private static final long				serialVersionUID	= 1L;

	private boolean							isInSelect			= false;

	private final Map<String, DocumentData>	documentDataMap;
	private final Map<String, DocumentData>	documentDataMapContext;

	public QueryAnalyzer(final Map<String, DocumentData> documentDataMapContext) {
		super();
		this.documentDataMapContext = documentDataMapContext;
		documentDataMap = new LinkedHashMap<String, DocumentData>();
	}

	public QueryAnalyzer() {
		this(null);
	}

	@Override
	public void visitDateLiteral(final DateLiteral dateLit) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitDateLiteral(" + dateLit + ")");
		}
		super.visitDateLiteral(dateLit);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitDateLiteral(" + dateLit + ")");
		}
	}

	@Override
	public void visitDoubleLiteral(final DoubleLiteral doubleLit) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitDoubleLiteral(" + doubleLit + ")");
		}
		super.visitDoubleLiteral(doubleLit);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitDoubleLiteral(" + doubleLit + ")");
		}

	}

	@Override
	public void visitExpression(final Expression expr) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitExpression(" + expr + ")");
		}
		super.visitExpression(expr);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitExpression(" + expr + ")");
		}
	}

	@Override
	public void visitFromClause(final FromClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitFromClause(" + clause + ")");
		}

		for (int i = 0; i < clause.elements.size(); ++i) {
			final String val = clause.elements.get(i);
			final String key = clause.elements.getKey(i);
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("[" + key + ", " + val + "]");
			}
			addDocumentData(key, val);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitFromClause(" + clause + ")");
		}
	}

	@Override
	public void visitFunction(final Function func) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitFunction(" + func + ")");
		}
		if (func.args != null) {
			for (final Operand operand : func.args) {
				operand.accept(this);
			}
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitFunction(" + func + ")");
		}
	}

	@Override
	public void visitGroupByClause(final GroupByClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitGroupByClause(" + clause + ")");
		}
		super.visitGroupByClause(clause);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitGroupByClause(" + clause + ")");
		}
	}

	@Override
	public void visitHavingClause(final HavingClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitHavingClause(" + clause + ")");
		}
		super.visitHavingClause(clause);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitHavingClause(" + clause + ")");
		}
	}

	@Override
	public void visitIntegerLiteral(final IntegerLiteral integerLit) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitIntegerLiteral(" + integerLit + ")");
		}
		super.visitIntegerLiteral(integerLit);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitIntegerLiteral(" + integerLit + ")");
		}

	}

	@Override
	public void visitLiteral(final Literal literal) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitLiteral(" + literal + ")");
		}
		super.visitLiteral(literal);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitLiteral(" + literal + ")");
		}
	}

	@Override
	public void visitLiteralList(final LiteralList listLit) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitLiteralList(" + listLit + ")");
		}
		super.visitLiteralList(listLit);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitLiteralList(" + listLit + ")");
		}
	}

	@Override
	public void visitMultiExpression(final MultiExpression expr) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitMultiExpression(" + expr + ")");
		}
		super.visitMultiExpression(expr);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitMultiExpression(" + expr + ")");
		}
	}

	@Override
	public void visitOperandList(final OperandList opList) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitOperandList(" + opList + ")");
		}
		super.visitOperandList(opList);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitOperandList(" + opList + ")");
		}
	}

	@Override
	public void visitOperator(final Operator operator) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitOperator(" + operator + ")");
		}
		super.visitOperator(operator);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitOperator(" + operator + ")");
		}

	}

	@Override
	public void visitOrderByClause(final OrderByClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitOrderByClause(" + clause + ")");
		}
		super.visitOrderByClause(clause);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitOrderByClause(" + clause + ")");
		}

	}

	@Override
	public void visitOrderByExpr(final OrderByExpr expr) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitOrderByExpr(" + expr + ")");
		}
		super.visitOrderByExpr(expr);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitOrderByExpr(" + expr + ")");
		}
	}

	@Override
	public void visitOrderByList(final OrderByList list) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitOrderByList(" + list + ")");
		}
		super.visitOrderByList(list);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitOrderByList(" + list + ")");
		}
	}

	@Override
	public void visitQuery(final SQLQuery query) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitQuery(" + query + ")");
		}

		// PROCESS FROM CLAUSE FIRST
		// IN ORDER TO CHECK REFERENCE IN SELECT
		query.from.accept(this);

		query.select.accept(this);

		if (query.where != null) {
			query.where.accept(this);
		}
		if (query.orderBy != null) {
			query.orderBy.accept(this);
		}
		if (query.groupBy != null) {
			query.groupBy.accept(this);
		}
		if (query.having != null) {
			query.having.accept(this);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitQuery(" + query + ")");
		}
	}

	@Override
	public void visitReference(final Reference ref) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitReference(" + ref + ")");
		}

		final String name = ref.name;
		if (!name.contains(".")) {
			throw new QueryMakerException("Reference without document specification [" + name + "]");
		} else {
			final String[] parts = name.split("\\.");
			if (parts.length <= 1) {
				throw new QueryMakerException("No support for less than one part. in reference [" + name + "] ("
						+ parts.length + ")");
			}
			final String prefix = parts[0];
			// ecm:fulltext peut etre suivi par '.<field name>'
			// si il n'est pas prefixé le prefix sera ecm:fulltext au lieu de la reference a un document
			if (prefix.startsWith("ecm:fulltext")) {
				throw new QueryMakerException("ecm:fulltext should be prefixed by document reference [" + name + "]");
			}

			addField(prefix, name.substring(prefix.length() + 1 /* for the '.' */), isInSelect);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitReference(" + ref + ")");
		}
	}

	@Override
	public void visitReferenceList(final ReferenceList refList) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitReferenceList(" + refList + ")");
		}
		super.visitReferenceList(refList);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitReferenceList(" + refList + ")");
		}

	}

	@Override
	public void visitSelectClause(final SelectClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitSelectClause(" + clause + ")");
		}
		isInSelect = true;
		super.visitSelectClause(clause);
		isInSelect = false;
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitSelectClause(" + clause + ")");
		}

	}

	@Override
	public void visitStringLiteral(final StringLiteral stringLit) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitStringLiteral(" + stringLit + ")");
		}
		super.visitStringLiteral(stringLit);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitStringLiteral(" + stringLit + ")");
		}

	}

	@Override
	public void visitWhereClause(final WhereClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitWhereClause(" + clause + ")");
		}
		super.visitWhereClause(clause);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitWhereClause(" + clause + ")");
		}
	}

	@Override
	public void visitSParamLiteral() {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitSParamLiteral()");
		}
	}

	@Override
	public void visitSGroupByClause(final SGroupByClause clause) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("in visitSGroupByClause(" + clause + ")");
		}
		super.visitSGroupByClause(clause);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("out visitSGroupByClause(" + clause + ")");
		}
	}

	/**
	 * Ajoute un nouvel objet DocumentData pour une clé donné. Si un objet est deja associé à cette clé : léve une
	 * exception (signifie que cet clé référence deja un objet dans la requete)
	 * 
	 * @param key
	 * @param type
	 */
	protected void addDocumentData(final String key, final String type) {
		if (documentDataMapContext != null && documentDataMapContext.containsKey(key)) {
			throw new QueryMakerException("duplicate key [" + key + "] in parent query");
		} else if (getDocumentDataMap().containsKey(key)) {
			throw new QueryMakerException("duplicate key [" + key + "]");
		} else {
			getDocumentDataMap().put(key, new DocumentData(key, type));
		}
	}

	/**
	 * Ajoute un nouvel objet Field a un DocumentData ou verifie qu'il est present dans un documentData d'une requete
	 * parente.
	 * 
	 * throw exception si ne peut trouver DocumentData ou si documentData est dans la requete parente mais que
	 * 
	 * @param key
	 * @param fieldName
	 * @param isInSelect
	 */
	protected void addField(final String key, final String fieldName, final boolean isInSelect) {
		DocumentData docData = getDocumentDataMap().get(key);
		if (docData != null) {
			docData.addField(fieldName, isInSelect);
			return;
		}

		if (documentDataMapContext != null) {
			docData = documentDataMapContext.get(key);
			if (docData.hasField(fieldName)) {
				return;
			} else {
				throw new QueryMakerException("Cannot manage inexisting field in parent query [" + key + "."
						+ fieldName + "]");
			}
		}
		throw new QueryMakerException("Cannot retrieve data for key [" + key + "]");
	}

	public Map<String, DocumentData> getDocumentDataMap() {
		return documentDataMap;
	}

}
