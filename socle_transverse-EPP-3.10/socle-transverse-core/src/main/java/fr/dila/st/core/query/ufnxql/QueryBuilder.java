package fr.dila.st.core.query.ufnxql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.FullTextUtils;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.query.sql.model.DoubleLiteral;
import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.FromClause;
import org.nuxeo.ecm.core.query.sql.model.Function;
import org.nuxeo.ecm.core.query.sql.model.HavingClause;
import org.nuxeo.ecm.core.query.sql.model.IntegerLiteral;
import org.nuxeo.ecm.core.query.sql.model.Literal;
import org.nuxeo.ecm.core.query.sql.model.LiteralList;
import org.nuxeo.ecm.core.query.sql.model.Operand;
import org.nuxeo.ecm.core.query.sql.model.Operator;
import org.nuxeo.ecm.core.query.sql.model.OrderByClause;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.OrderByList;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.query.sql.model.Reference;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.query.sql.model.SelectClause;
import org.nuxeo.ecm.core.query.sql.model.SelectList;
import org.nuxeo.ecm.core.query.sql.model.StringLiteral;
import org.nuxeo.ecm.core.query.sql.model.WhereClause;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.PropertyType;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.Query;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect.FulltextMatchInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;

import fr.dila.st.core.query.ColumnUtil;
import fr.dila.st.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.dila.st.core.query.ufnxql.parser.SFunction;
import fr.dila.st.core.query.ufnxql.parser.SGroupByClause;
import fr.dila.st.core.query.ufnxql.parser.SParamLiteral;

/**
 * Parcours la requete UFNXQL pour construire les différents morceaux de la requete SQL
 * 
 * @author spesnel
 * 
 */
public class QueryBuilder extends SDefaultQueryVisitor {

	private static final Log				LOG					= LogFactory.getLog(QueryBuilder.class);

	/**
     * 
     */
	private static final long				serialVersionUID	= 1L;

	private static final String				DUMMY_EXPRESSION	= " 1=1 ";

	public Map<String, DocumentData>		documentDataMap;
	public Map<String, Field>				fieldMap;
	private final Object[]					availableParams;
	private final SQLInfo					sqlInfo;
	private final Model						model;
	private final Dialect					dialect;
	private final Database					database;
	private final QueryFilter				queryFilter;
	private final Column					countColumn;

	/**
	 * true => appel a la fonction exist fait un EXIST false => appel a la fonction exist fait un 'NOT EXIST'
	 * 
	 * positionner par test de la valeur de la fonction exist(...) = (0 ou 1)
	 */
	private boolean							doExist				= true;

	/**
	 * Utilisé pour initialiser un UNXFQLQueryMaker si besoin est
	 */
	private final Map<String, String>		mapTypeSchema;

	private final Map<String, DocumentData>	documentDataMapContext;

	public int								nbParamsRequired;

	private int								fulltextIndexNumber	= 1;

	private final StringBuilder				wherePart;
	private final List<Serializable>		whereParams;

	public StringBuilder					orderPart;
	public StringBuilder					selectPart;
	public List<Column>						whatColumns;
	public List<String>						whatKeys;
	public StringBuilder					groupPart;
	public StringBuilder					recStartWithPart;
	public List<Serializable>				recStartWithParams;

	// contient les documentdata qui ont une jointure vers la table
	// fulltext
	// utilise pour oracle car une seule jointure est nécessaire par type de document
	private final List<DocumentData>		docWithFulltextJoin	= new ArrayList<DocumentData>();

	public static enum POS {
		UNDEFINED, IN_SELECT, IN_WHERE, IN_ORDER, IN_GROUP,
	};

	public POS							pos	= POS.UNDEFINED;
	private final Map<String, Integer>	mapPoidsSchemaForJoin;

	public QueryBuilder(final Map<String, DocumentData> documentDataMap, final Map<String, Field> fieldMap,
			final Object[] availableParams, final Model model, final SQLInfo sqlInfo, final QueryFilter queryFilter,
			final Map<String, String> mapTypeSchema, final Map<String, DocumentData> documentDataMapContext,
			final Map<String, Integer> mapPoidsSchemaForJoin) {
		super();
		this.documentDataMap = documentDataMap;
		this.fieldMap = fieldMap;
		this.availableParams = availableParams;
		this.model = model;
		this.sqlInfo = sqlInfo;
		this.dialect = sqlInfo.dialect;
		this.database = sqlInfo.database;
		this.queryFilter = queryFilter;
		this.countColumn = ColumnUtil.createCountColumn(dialect);
		this.mapTypeSchema = mapTypeSchema;
		this.documentDataMapContext = documentDataMapContext;
		this.nbParamsRequired = 0;
		this.mapPoidsSchemaForJoin = mapPoidsSchemaForJoin;

		this.wherePart = new StringBuilder();
		this.whereParams = new ArrayList<Serializable>();

		this.selectPart = new StringBuilder();

		this.whatColumns = new ArrayList<Column>();
		this.whatKeys = new ArrayList<String>();

		this.orderPart = new StringBuilder();

		this.groupPart = new StringBuilder();

		this.recStartWithPart = new StringBuilder();
		this.recStartWithParams = new ArrayList<Serializable>();

	}

	@Override
	public void visitDateLiteral(final DateLiteral dateLiteral) {
		if (isInWhere()) {
			wherePart.append('?');
			whereParams.add(dateLiteral.toCalendar());
		} else {
			throw new QueryMakerException("Not supported operation : visitDateLiteral in " + pos);
		}
	}

	@Override
	public void visitDoubleLiteral(final DoubleLiteral doubleLiteral) {
		if (isInWhere()) {
			wherePart.append('?');
			whereParams.add(Double.valueOf(doubleLiteral.value));
		} else {
			throw new QueryMakerException("Not supported operation : visitDoubleLiteral in " + pos);
		}
	}

	@Override
	public void visitExpression(final Expression expr) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitExpression(" + expr + ")");
		}
		if (isInWhere()) {
			processExpressionInWhere(expr);
		} else {
			throw new QueryMakerException("Not supported operation : visitExpression in " + pos);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitExpression(" + expr + ")");
		}
	}

	protected void visitBooleanLiteral(final BooleanLiteral node) {
		wherePart.append('?');
		whereParams.add(Boolean.valueOf(node.value));
	}

	protected void processExpressionInWhere(final Expression expr) {
		if (expr.lvalue instanceof Function) {
			processExpressionWithFunction(expr);
			return;
		}

		wherePart.append('(');

		final String name = expr.lvalue instanceof Reference ? ((Reference) expr.lvalue).name : null;

		Field field = null;
		String fieldName = null;
		if (name != null) {
			field = fieldMap.get(name);
			if (field == null) {
				throw new QueryMakerException("Cannot retrieve field for name[" + name + "]");
			} else {
				fieldName = field.getName();
			}
		}

		final Operator operator = expr.operator;
		// if (op == Operator.STARTSWITH) {
		// visitExpressionStartsWith(expr, name);
		// } else if (NXQL.ECM_PATH.equals(name)) {
		// visitExpressionEcmPath(expr);
		// } else if (NXQL.ECM_ISPROXY.equals(name)) {
		// visitExpressionIsProxy(expr);
		// } else if (NXQL.ECM_MIXINTYPE.equals(name)) {
		// visitExpressionMixinType(expr);
		// } else if (name != null && name.startsWith(NXQL.ECM_FULLTEXT)
		// && !NXQL.ECM_FULLTEXT_JOBID.equals(name)) {
		// visitExpressionFulltext(expr, name);
		if (operator == Operator.STARTSWITH || NXQL.ECM_PATH.equals(fieldName) || NXQL.ECM_ISPROXY.equals(fieldName)
				|| NXQL.ECM_MIXINTYPE.equals(fieldName)) {
			throw new QueryMakerException("OPERATION NOT SUPPORTED (" + name + ")");
		} else if (NXQL.ECM_ISVERSION.equals(fieldName)) {
			visitExpressionIsVersion(field, expr);
		} else if (fieldName != null && fieldName.startsWith(NXQL.ECM_FULLTEXT)) {
			processExpressionFullText(expr, field);
		} else if (operator == Operator.EQ || operator == Operator.NOTEQ || operator == Operator.IN
				|| operator == Operator.NOTIN || operator == Operator.LIKE || operator == Operator.NOTLIKE
				|| operator == Operator.ILIKE || operator == Operator.NOTILIKE) {

			final Field f = fieldMap.get(name);

			if (f.getColumn() != null
					&& !(f.getModelProperty() != null && f.getModelProperty().propertyType.isArray() && !f.isInSelect())) {
				Expression node = expr;
				if (f.getModelProperty() != null && f.getModelProperty().propertyType == PropertyType.BOOLEAN) {
					// le driver reconnait déjà les boolean true/false et 0/1
					if (!(node.rvalue instanceof SParamLiteral)) {
						if (!(node.rvalue instanceof IntegerLiteral)) {
							throw new QueryMakerException(
									"Boolean expressions require literal 0 or 1 as right argument");
						}
						final long v = ((IntegerLiteral) node.rvalue).value;
						if (v != 0 && v != 1) {
							throw new QueryMakerException(
									"Boolean expressions require literal 0 or 1 as right argument");
						}
						node = new Predicate(node.lvalue, node.operator, new BooleanLiteral(v == 1));
					}
				}

				if (operator == Operator.ILIKE || operator == Operator.NOTILIKE) {
					visitExpressionIlike(node);
				} else {
					super.visitExpression(node);
				}
			} else if (f.getModelProperty() == null) {
				throw new QueryMakerException("Unknown field: " + name);
			} else {
				if (f.getModelProperty().propertyType.isArray()) {
					// use EXISTS with subselect clause
					final boolean direct = operator == Operator.EQ || operator == Operator.IN
							|| operator == Operator.LIKE || operator == Operator.ILIKE;
					final Operator directOp = direct ? operator : operator == Operator.NOTEQ ? Operator.EQ
							: operator == Operator.NOTIN ? Operator.IN : operator == Operator.NOTLIKE ? Operator.LIKE
									: Operator.ILIKE;

					if (!direct) {
						wherePart.append("NOT ");
					}
					final DocumentData documentData = documentDataMap.get(field.getPrefix());
					final Table table = database.getTable(f.getModelProperty().fragmentName);
					final TableKey refTk = documentData.getReferenceTableKey();

					wherePart.append(String.format("EXISTS (SELECT 1 FROM %s WHERE %s = %s.%s AND (",
							table.getQuotedName(), "ID", refTk.getKey(), documentData.getReferenceTableColumnIdName()));

					if (directOp == Operator.ILIKE) {
						visitExpressionIlike(expr, directOp);
					} else {
						expr.lvalue.accept(this);
						directOp.accept(this);
						expr.rvalue.accept(this);
					}
					wherePart.append("))");
				} else {
					throw new QueryMakerException("field with column and property : " + name);
				}
			}
		} else if (operator == Operator.BETWEEN || operator == Operator.NOTBETWEEN) {
			final Field f = fieldMap.get(name);
			if (f.getModelProperty().propertyType.isArray()) {
				// use EXISTS with subselect clause

				final DocumentData documentData = documentDataMap.get(field.getPrefix());
				final Table table = database.getTable(f.getModelProperty().fragmentName);
				final TableKey refTk = documentData.getReferenceTableKey();

				wherePart.append(String.format("EXISTS (SELECT 1 FROM %s WHERE %s = %s.%s AND (",
						table.getQuotedName(), "ID", refTk.getKey(), documentData.getReferenceTableColumnIdName()));

				final LiteralList literalList = (LiteralList) expr.rvalue;
				expr.lvalue.accept(this);
				wherePart.append(' ');
				expr.operator.accept(this);
				wherePart.append(' ');
				literalList.get(0).accept(this);
				wherePart.append(" ").append(SQL.AND).append(" ");
				literalList.get(1).accept(this);

				wherePart.append("))");
			} else {
				final LiteralList literalList = (LiteralList) expr.rvalue;
				expr.lvalue.accept(this);
				wherePart.append(' ');
				expr.operator.accept(this);
				wherePart.append(' ');
				literalList.get(0).accept(this);
				wherePart.append(" ").append(SQL.AND).append(" ");
				literalList.get(1).accept(this);
			}
		} else {
			super.visitExpression(expr);
		}

		wherePart.append(')');
	}

	protected void visitExpressionIlike(final Expression expr, final Operator operator) {
		if (dialect.supportsIlike()) {
			expr.lvalue.accept(this);
			operator.accept(this);
			expr.rvalue.accept(this);
		} else {
			wherePart.append("LOWER(");
			expr.lvalue.accept(this);
			wherePart.append(") ");
			if (operator == Operator.NOTILIKE) {
				wherePart.append("NOT ");
			}
			wherePart.append(SQL.LIKE);
			wherePart.append(" LOWER(");
			expr.rvalue.accept(this);
			wherePart.append(")");
		}
	}

	protected void visitExpressionIsVersion(final Field field, final Expression node) {
		if (node.operator != Operator.EQ && node.operator != Operator.NOTEQ) {
			throw new QueryMakerException(NXQL.ECM_ISVERSION + " requires = or <> operator");
		}
		if (!(node.rvalue instanceof IntegerLiteral)) {
			throw new QueryMakerException(NXQL.ECM_ISVERSION + " requires literal 0 or 1 as right argument");
		}
		final long value = ((IntegerLiteral) node.rvalue).value;
		if (value != 0 && value != 1) {
			throw new QueryMakerException(NXQL.ECM_ISVERSION + " requires literal 0 or 1 as right argument");
		}
		final boolean bool = node.operator == Operator.EQ ^ value == 0;
		wherePart.append(field.toSql()).append(bool ? " IS NOT NULL" : " IS NULL");
	}

	protected void processExpressionWithFunction(final Expression expr) {

		final Function func = (Function) expr.lvalue;
		if (UFNXQLConstants.IS_CHILD_OF_FUNCTION.equals(func.name)) {

			if (Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral
					&& ((IntegerLiteral) expr.rvalue).value == 1) {
				expr.lvalue.accept(this);
				return;
			} else {
				throw new QueryMakerException("for function " + UFNXQLConstants.IS_CHILD_OF_FUNCTION
						+ " expression should " + UFNXQLConstants.IS_CHILD_OF_FUNCTION + "(id, query) = 1");
			}
		} else if (UFNXQLConstants.TEST_ACL_FUNCTION.equals(func.name)) {

			if (Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral
					&& ((IntegerLiteral) expr.rvalue).value == 1) {
				expr.lvalue.accept(this);
				return;
			} else {
				throw new QueryMakerException("for function " + UFNXQLConstants.TEST_ACL_FUNCTION
						+ " expression should " + UFNXQLConstants.TEST_ACL_FUNCTION + "(id) = 1");
			}
		} else if (UFNXQLConstants.TEST_EXIST_FUNCTION.equals(func.name)) {

			if (Operator.EQ.equals(expr.operator) && expr.rvalue instanceof IntegerLiteral
					&& (((IntegerLiteral) expr.rvalue).value == 1 || ((IntegerLiteral) expr.rvalue).value == 0)) {
				doExist = ((IntegerLiteral) expr.rvalue).value == 1;
				expr.lvalue.accept(this);
				return;
			} else {
				throw new QueryMakerException("for function " + UFNXQLConstants.TEST_EXIST_FUNCTION
						+ " expression should " + UFNXQLConstants.TEST_EXIST_FUNCTION + "(id) = 0 ou 1");
			}
		}

		expr.lvalue.accept(this);
		expr.operator.accept(this);
		expr.rvalue.accept(this);
	}

	protected void visitExpressionIlike(final Expression expr) {
		visitExpressionIlike(expr, expr.operator);
	}

	protected void processExpressionFullText(final Expression expr, final Field field) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in processExpressionFullText(" + expr + ", " + field + ")");
		}
		String name = field.getName();
		final String[] nameref = new String[] { name };
		final boolean useIndex = findFulltextIndexOrField(model, nameref);
		name = nameref[0];
		if (expr.operator != Operator.EQ && expr.operator != Operator.LIKE) {
			throw new QueryMakerException(NXQL.ECM_FULLTEXT + " requires = or LIKE operator");
		}
		if (!(expr.rvalue instanceof StringLiteral)) {
			throw new QueryMakerException(NXQL.ECM_FULLTEXT + " requires literal string as right argument");
		}
		if (useIndex) {
			// use actual fulltext query using a dedicated index
			String fulltextQuery = ((StringLiteral) expr.rvalue).value;
			fulltextQuery = dialect.getDialectFulltextQuery(fulltextQuery);

			final DocumentData documentData = documentDataMap.get(field.getPrefix());

			final Table refTable = documentData.getReferenceTable();
			final Column mainColumn = documentData.getReferenceTableColumnId();
			final TableKey tkRef = documentData.getReferenceTableKey();
			final Table aliasTable = new TableSymbolicKey(refTable, tkRef.getKey());
			final Column idCol = new Column(mainColumn, aliasTable);

			if (dialect instanceof DialectOracle) {

				// pour oracle,il faut seulement une jointure
				// avec la table fulltext par document referencé

				final Table fullTextTable = database.getTable(Model.FULLTEXT_TABLE_NAME);

				final String tableAlias = documentData.getKey() + "_fulltext";

				if (!docWithFulltextJoin.contains(documentData)) {
					final Join join = new Join(Join.INNER, //
							fullTextTable.getQuotedName(), tableAlias, // alias
							null, // param
							String.format("%s.ID", tableAlias), // on1
							idCol.getFullQuotedName()); // on2
					documentData.addJoin(join);
					docWithFulltextJoin.add(documentData);
				}

				final String indexSuffix = model.getFulltextIndexSuffix(name);
				final Table fulltextAliasTable = new TableSymbolicKey(fullTextTable, tableAlias);
				final Column ftColumn = new Column(fullTextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY + indexSuffix),
						fulltextAliasTable);

				wherePart.append(String.format("CONTAINS(%s, ?, %d) > 0", ftColumn.getFullQuotedName(),
						fulltextIndexNumber++));

				whereParams.add(fulltextQuery);

			} else {

				final FulltextMatchInfo info = dialect.getFulltextScoredMatchInfo(fulltextQuery, name,
						fulltextIndexNumber++, idCol, model, database);

				documentData.addJoins(info.joins);

				wherePart.append(info.whereExpr);
				if (info.whereExprParam != null) {
					whereParams.add(info.whereExprParam);
				}
			}
		} else {
			// single field matched with ILIKE
			LOG.warn("No fulltext index configured for field " + name + ", falling back on LIKE query");
			String value = ((StringLiteral) expr.rvalue).value;

			// fulltext translation into pseudo-LIKE syntax
			final Set<String> words = FullTextUtils.parseFullText(value, false);
			if (words.isEmpty()) {
				// only stop words or empty
				value = "DONTMATCHANYTHINGFOREMPTYQUERY";
			} else {
				value = "%" + StringUtils.join(new ArrayList<String>(words), "%") + "%";
			}

			if (dialect.supportsIlike()) {
				processReference(field);
				wherePart.append(" ILIKE ");
				processStringLiteral(value);
			} else {
				wherePart.append("LOWER(");
				processReference(field);
				wherePart.append(") LIKE ");
				processStringLiteral(value);
			}
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("out processExpressionFullText()");
		}
	}

	// copied from NXQLQueryBuilder
	protected static boolean findFulltextIndexOrField(final Model model, final String[] nameref) {
		boolean useIndex;
		String name = nameref[0];
		if (name.equals(NXQL.ECM_FULLTEXT)) {
			name = Model.FULLTEXT_DEFAULT_INDEX;
			useIndex = true;
		} else {
			// ecm:fulltext_indexname
			// ecm:fulltext.field
			final char sep = name.charAt(NXQL.ECM_FULLTEXT.length());
			if (sep != '.' && sep != '_') {
				throw new QueryMakerException("Unknown field: " + name);
			}
			useIndex = sep == '_';
			name = name.substring(NXQL.ECM_FULLTEXT.length() + 1);
			if (useIndex) {
				if (!model.fulltextInfo.indexNames.contains(name)) {
					throw new QueryMakerException("No such fulltext index: " + name);
				}
			} else {
				// find if there's an index holding just that field
				final String index = model.fulltextInfo.fieldToIndexName.get(name);
				if (index != null) {
					name = index;
					useIndex = true;
				}
			}
		}
		nameref[0] = name;
		return useIndex;
	}

	@Override
	public void visitFromClause(final FromClause clause) {
		// do nothing
	}

	@Override
	public void visitFunction(final Function func) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitFunction(" + func + ")");
		}

		if (isInSelect() && func.name.equalsIgnoreCase("count") && func.args == null) {
			selectPart.append("count(*)");
			whatColumns.add(countColumn);
			return;
		}

		StringBuilder strBuilder = null;
		if (isInSelect()) {
			strBuilder = selectPart;
		} else if (isInWhere()) {
			strBuilder = wherePart;
		} else {
			throw new QueryMakerException("Function [" + func.name + "] not supported in this part (" + pos + ")");
		}

		if (UFNXQLConstants.IS_CHILD_OF_FUNCTION.equals(func.name)) {
			processIsChildOfFunction(func);
		} else if (UFNXQLConstants.TEST_ACL_FUNCTION.equals(func.name)) {
			processTestAclFunction(func);
		} else if (UFNXQLConstants.TEST_EXIST_FUNCTION.equals(func.name)) {
			processExistFunction(func, doExist);
		} else {
			strBuilder.append(func.name).append('(');
			if (func.args != null) {
				for (final Iterator<Operand> it = func.args.iterator(); it.hasNext();) {
					it.next().accept(this);
					if (it.hasNext()) {
						strBuilder.append(", ");
					}
				}
			}
			if (func instanceof SFunction) {
				final SQLQuery query = ((SFunction) func).query;
				if (query != null) {
					if (func.args != null) {
						strBuilder.append(", ");
					}
					final ClauseParams clauseParams = new ClauseParams();
					nbParamsRequired += computeInternalQuery(query, clauseParams);
					strBuilder.append(clauseParams.clause);
					whereParams.addAll(clauseParams.params);
				}
			}
			strBuilder.append(')');
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitFunction(" + func + ")");
		}
	}

	private void processIsChildOfFunction(final Function func) {
		if (!(func instanceof SFunction
				&& ((SFunction) func).query != null
				&& (func.args.size() == 1 && func.args.get(0) instanceof Reference || func.args.size() == 2
						&& func.args.get(0) instanceof Reference && func.args.get(1) instanceof Reference) && isInWhere())) {
			throw new QueryMakerException(
					"function "
							+ UFNXQLConstants.IS_CHILD_OF_FUNCTION
							+ " expects one or two arguments (: the id to test, [ and the parent id to navigate in hierarchy ]) and a query and should be in where clause");
		}
		final SQLQuery query = ((SFunction) func).query;

		final ClauseParams cp = new ClauseParams();
		nbParamsRequired += computeInternalQuery(query, cp);

		// extrait la réference à l'id et extrait ou construit la reference au parentId
		// meme si ne sert que sous oracle.
		// Cela evite de decouvrir certaine erreur que sous oracle, les exceptions sont aussi lancé
		// sous h2
		int len = wherePart.length();
		func.args.get(0).accept(this);
		final String refToId = wherePart.substring(len);
		wherePart.delete(len, wherePart.length());

		String refToParentId = null;
		if (func.args.size() == 2) {
			len = wherePart.length();
			func.args.get(1).accept(this);
			refToParentId = wherePart.substring(len);
			wherePart.delete(len, wherePart.length());
		} else {
			// test que la table concerne est bien hierarchy pour s'assurer que la colonne PARENT ID existe
			final Reference ref = (Reference) func.args.get(0);
			final Field field = fieldMap.get(ref.name);
			final Table hierTable = this.database.getTable(Model.HIER_TABLE_NAME);
			if (!hierTable.getPhysicalName().equals(field.getTableKey().getTableName())) {
				throw new QueryMakerException(
						"field for recursive test should be ID in HIERARCHY TABLE ["
								+ ref.name
								+ "]."
								+ "This problem can appear with optimisation on some document types. Try to use isChildOf with three arguments (id, parentid, query)");
			}
			if (!refToId.contains(".\"ID\"")) {
				throw new QueryMakerException("field for recursive test on path should contains ID [" + refToId + "]");
			}
			refToParentId = refToId.replace(".\"ID\"", ".\"PARENTID\"");
		}

		if (dialect instanceof DialectOracle) {

			wherePart.append(DUMMY_EXPRESSION); // pour que l'expression soit valide avec les ET, OU
			recStartWithPart.append(SQL.genRecExpr(cp.clause, refToId, refToParentId));
			recStartWithParams.addAll(cp.params);

		} else {
			wherePart.append(SQL.EXISTS).append("(").append(SQL.SELECT).append(" 1 ").append(SQL.FROM).append(" (")
					.append(cp.clause);
			whereParams.addAll(cp.params);
			wherePart.append(") TMPTABLE WHERE NX_IN_TREE(");
			wherePart.append(refToId);
			wherePart.append(", TMPTABLE.id) )");
		}

	}

	/**
	 * Function qui test les acl
	 * 
	 * @param func
	 */
	private void processTestAclFunction(final Function func) {
		if (!(func.args.size() == 1 && func.args.get(0) instanceof Reference && isInWhere())) {
			throw new QueryMakerException("function testAcl expects one argument : the id test");
		}

		if (queryFilter.getPrincipals() != null) {

			Serializable paramPrincipals = queryFilter.getPrincipals();
			Serializable paramPermissions = queryFilter.getPermissions();

			if (!sqlInfo.dialect.supportsArrays()) {
				paramPrincipals = StringUtils.join((String[]) paramPrincipals, '|');
				paramPermissions = StringUtils.join((String[]) paramPermissions, '|');
			}

			final Reference ref = (Reference) func.args.get(0);
			final Field field = fieldMap.get(ref.name);
			final String idField = field.toSql();

			if (sqlInfo.dialect.supportsReadAcl()) {
				/* optimized read acl */
				final String readAclTableAlias = "read_acl";
				wherePart.append(dialect.getReadAclsCheckSql(readAclTableAlias + ".acl_id"));
				whereParams.add(paramPrincipals);
				final Join securityJoin = new Join(Join.INNER, Model.HIER_READ_ACL_TABLE_NAME, readAclTableAlias, null,
						idField, readAclTableAlias + ".id");

				final DocumentData docData = documentDataMap.get(field.getPrefix());
				docData.getOrderedJoins(mapPoidsSchemaForJoin).add(securityJoin);
			} else {
				wherePart.append(dialect.getSecurityCheckSql(idField));
				whereParams.add(paramPrincipals);
				whereParams.add(paramPermissions);
			}
		} else {
			wherePart.append(DUMMY_EXPRESSION);
		}
	}

	private void processExistFunction(final Function func, final boolean doExist) {
		if (!(func instanceof SFunction && ((SFunction) func).query != null
				&& (func.args == null || func.args.isEmpty()) && isInWhere())) {
			throw new QueryMakerException("function " + UFNXQLConstants.IS_CHILD_OF_FUNCTION
					+ " expects 0 argument and a query and should be in where clause");
		}

		final SQLQuery query = ((SFunction) func).query;

		final ClauseParams clauseParams = new ClauseParams();
		nbParamsRequired += computeInternalQuery(query, clauseParams);

		wherePart.append(SQL.existPart(clauseParams.clause, doExist));
		whereParams.addAll(clauseParams.params);

	}

	/**
	 * return the number of used params, fill the cp object
	 * 
	 * @param query
	 * @param clauseParams
	 * @return
	 */
	protected int computeInternalQuery(final SQLQuery query, final ClauseParams clauseParams) {
		final UFNXQLQueryMaker maker = new UFNXQLQueryMaker(this.mapTypeSchema, mergedDocumentDataMapContext(),
				mapPoidsSchemaForJoin);
		maker.usedToGenerateInternalQueryPart = true;
		final Object[] p = availableParams == null ? null : Arrays.copyOfRange(availableParams, nbParamsRequired,
				availableParams.length);

		final Query genquery = maker.buildQuery(sqlInfo, model, null, query, queryFilter, p);
		clauseParams.clause = genquery.selectInfo.sql;
		clauseParams.params = genquery.selectParams;

		return maker.getUsedParams();
	}

	/**
	 * met le contenu de documentDataMap et documentDataMapContext dans une meme map
	 * 
	 * @return
	 */
	protected Map<String, DocumentData> mergedDocumentDataMapContext() {
		final Map<String, DocumentData> merged = new LinkedHashMap<String, DocumentData>();
		if (documentDataMapContext != null) {
			for (final String key : documentDataMapContext.keySet()) {
				merged.put(key, documentDataMapContext.get(key));
			}
		}
		if (documentDataMap != null) {
			for (final String key : documentDataMap.keySet()) {
				merged.put(key, documentDataMap.get(key));
			}
		}
		return merged;
	}

	// @Override
	// public void visitGroupByClause(GroupByClause clause) {
	// log.info("in visitGroupByClause("+clause+")");
	// super.visitGroupByClause(clause);
	// log.info("out visitGroupByClause("+clause+")");
	// }

	@Override
	public void visitHavingClause(final HavingClause clause) {
		throw new QueryMakerException("visitHavingClause not supported");
	}

	@Override
	public void visitIntegerLiteral(final IntegerLiteral integerLiteral) {
		if (isInWhere()) {
			wherePart.append('?');
			whereParams.add(Long.valueOf(integerLiteral.value));
		} else {
			throw new QueryMakerException("Not supported operation : visitIntegerLiteral in " + pos);
		}
	}

	// @Override
	// public void visitLiteral(Literal l) {
	// log.info("in visitLiteral("+l+")");
	// super.visitLiteral(l);
	// log.info("out visitLiteral("+l+")");
	// }

	@Override
	public void visitLiteralList(final LiteralList listLiteral) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitLiteralList(" + listLiteral + ")");
		}
		if (isInWhere()) {
			wherePart.append('(');
			for (final Iterator<Literal> it = listLiteral.iterator(); it.hasNext();) {
				it.next().accept(this);
				if (it.hasNext()) {
					wherePart.append(", ");
				}
			}
			wherePart.append(')');
		} else {
			throw new QueryMakerException("No support for literal list in another part than where (" + pos + ")");
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitLiteralList(" + listLiteral + ")");
		}
	}

	// @Override
	// public void visitMultiExpression(MultiExpression expr) {
	// log.info("in visitMultiExpression("+expr+")");
	// super.visitMultiExpression(expr);
	// log.info("out visitMultiExpression("+expr+")");
	// }

	// @Override
	// public void visitOperandList(OperandList opList) {
	// log.info("in visitOperandList("+opList+")");
	// super.visitOperandList(opList);
	// log.info("out visitOperandList("+opList+")");
	// }

	@Override
	public void visitOperator(final Operator operator) {
		if (isInWhere()) {
			wherePart.append(' ').append(operator.toString()).append(' ');
		} else {
			throw new QueryMakerException("Not supported operation : visitOperator in " + pos);
		}
	}

	@Override
	public void visitOrderByClause(final OrderByClause clause) {
		pos = POS.IN_ORDER;
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitOrderByClause(" + clause + ")");
		}
		super.visitOrderByClause(clause);
		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitOrderByClause(" + clause + ")");
		}
		pos = POS.UNDEFINED;

	}

	@Override
	public void visitOrderByExpr(final OrderByExpr expr) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitOrderByExpr(" + expr + ")");
		}
		expr.reference.accept(this);
		if (expr.isDescending) {
			orderPart.append(" DESC");
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitOrderByExpr(" + expr + ")");
		}
	}

	@Override
	public void visitOrderByList(final OrderByList list) {
		assert isInOrder();
		for (final Iterator<OrderByExpr> it = list.iterator(); it.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				orderPart.append(", ");
			}
		}
	}

	@Override
	public void visitQuery(final SQLQuery query) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitQuery(" + query + ")");
		}

		super.visitQuery(query);

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitQuery(" + query + ")");
		}
	}

	@Override
	public void visitReference(final Reference ref) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitReference(" + ref + ")");
		}
		// super.visitReference(ref);
		final String name = ref.name;

		final Field field = fieldMap.get(name);
		processReference(field);
		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitReference(" + ref + ")");
		}
	}

	protected void processReference(final Field field) {
		if (isInSelect()) {
			selectPart.append(field.toSql());
			whatColumns.add(field.getColumn());
		} else if (isInOrder()) {
			orderPart.append(field.toSql());
		} else if (isInWhere()) {
			wherePart.append(field.toSql());
		} else if (isInGroup()) {
			groupPart.append(field.toSql());
		}
	}

	// @Override
	// public void visitReferenceList(ReferenceList refList) {
	// log.info("in visitReferenceList("+refList+")");
	// super.visitReferenceList(refList);
	// log.info("out visitReferenceList("+refList+")");
	// }

	@Override
	public void visitSelectClause(final SelectClause clause) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitSelectClause(" + clause + ")");
		}
		pos = POS.IN_SELECT;

		if (clause.distinct) {
			selectPart.append("DISTINCT ");
		}

		final SelectList elements = clause.elements;

		if (!elements.isEmpty()) {
			processASelectEntry(elements, 0);

			for (int i = 1; i < elements.size(); i++) {
				selectPart.append(", ");

				processASelectEntry(elements, i);
			}
		}

		pos = POS.UNDEFINED;

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitSelectClause(" + clause + ")");
		}
	}

	private void processASelectEntry(final SelectList elements, final int index) {
		final Operand operator = elements.get(index);
		final String alias = elements.getKey(index);
		operator.accept(this);
		if (!operator.toString().equals(alias)) {
			selectPart.append(" ").append(SQL.AS).append(" ").append(alias);
		}
		whatKeys.add(elements.getKey(index));
	}

	@Override
	public void visitStringLiteral(final StringLiteral stringLiteral) {
		processStringLiteral(stringLiteral.value);
	}

	public void processStringLiteral(final String str) {
		wherePart.append('?');
		whereParams.add(str);
	}

	@Override
	public void visitWhereClause(final WhereClause clause) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitWhereClause(" + clause + ")");
		}

		pos = POS.IN_WHERE;
		super.visitWhereClause(clause);
		pos = POS.UNDEFINED;

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitWhereClause(" + clause + ")");
		}
	}

	@Override
	public void visitSGroupByClause(final SGroupByClause clause) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("in visitSGroupByClause(" + clause + ")");
		}

		pos = POS.IN_GROUP;
		super.visitSGroupByClause(clause);
		pos = POS.UNDEFINED;

		if (LOG.isTraceEnabled()) {
			LOG.trace("out visitSGroupByClause(" + clause + ")");
		}
	}

	@Override
	public void visitSParamLiteral() {
		if (isInWhere()) {
			wherePart.append("?");
			if (availableParams == null) {
				throw new QueryMakerException("No parameter in entry");
			}
			if (nbParamsRequired >= availableParams.length) {
				throw new QueryMakerException("Not enough parameter in entry [available=" + availableParams.length
						+ "]");
			}
			whereParams.add((Serializable) availableParams[nbParamsRequired]);
			nbParamsRequired++;
		} else {
			throw new QueryMakerException("param supported only in where part");
		}
	}

	protected boolean isInSelect() {
		return POS.IN_SELECT.equals(pos);
	}

	protected boolean isInOrder() {
		return POS.IN_ORDER.equals(pos);
	}

	protected boolean isInWhere() {
		return POS.IN_WHERE.equals(pos);
	}

	protected boolean isInGroup() {
		return POS.IN_GROUP.equals(pos);
	}

	public ClauseParams getWhereClause() {
		return new ClauseParams(wherePart.toString(), whereParams);
	}

}
