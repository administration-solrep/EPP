package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.FullTextUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.DateLiteral;
import org.nuxeo.ecm.core.query.sql.model.DoubleLiteral;
import org.nuxeo.ecm.core.query.sql.model.Expression;
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
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.Dialect.FulltextMatchInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectOracle;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectPostgreSQL;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ColumnUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SDefaultQueryVisitor;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFunction;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SGroupByClause;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SOrderByExpr;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SParamLiteral;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SSQLQuery;
import fr.sword.naiad.nuxeo.ufnxql.core.service.FNXQLConfigService;

/**
 * Parcours la requete UFNXQL pour construire les différents morceaux de la
 * requete SQL
 * 
 * @author spesnel
 * 
 */
public class QueryBuilder extends SDefaultQueryVisitor {

    public static final String DUMMY_EXPRESSION = " 1=1 ";

    private static final Log LOG = LogFactory.getLog(QueryBuilder.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String STR_SPACE = " ";
    private static final String STR_POINT = ".";

    private final Map<String, DocumentData> documentDataMap;
    private final Map<String, Field> fieldMap;
    private final Object[] availableParams;
    private final SQLInfo sqlInfo;
    private final Model model;
    private final Dialect dialect;
    private final Database database;
    private final QueryFilter queryFilter;
    private final Column countColumn;

    /**
     * true =&gt; appel a la fonction exist fait un EXIST false =&gt; appel a la
     * fonction exist fait un 'NOT EXIST'
     * 
     * positionner par test de la valeur de la fonction exist(...) = (0 ou 1)
     */
    // private boolean doExist = true;

    private final Map<String, DocumentData> documentDataMapContext;

    private int nbParamsRequired;

    private int fulltextIndexNumber = 1;

    private int pgfulltextAliasNumber = 1;

    private final StringBuilder wherePart;
    private final List<Serializable> whereParams;

    private final StringBuilder orderPart;
    private final StringBuilder selectPart;
    private final StringBuilder fromPart;
    private final List<Serializable> fromParams;
    private final List<Column> whatColumns;
    private final List<String> whatKeys;
    private final StringBuilder groupPart;
    private final StringBuilder recStartWithPart;
    private final List<Serializable> recStartWithParams;

    // contient les documentdata qui ont une jointure vers la table
    // fulltext
    // utilise pour oracle car une seule jointure est nécessaire par type de
    // document
    private final List<DocumentData> docWithFulltextJoin = new ArrayList<DocumentData>();

    /**
     * Position possible dans le parsing de la requete
     */
    private static enum POS {
        UNDEFINED,
        IN_SELECT,
        IN_WHERE,
        IN_ORDER,
        IN_GROUP,
    };

    private POS pos = POS.UNDEFINED;

    private Map<String, UfnxqlFunction> declaredFunctions = null;

    public QueryBuilder(final Map<String, DocumentData> documentDataMap, final Map<String, Field> fieldMap,
            final Object[] availableParams, final Model model, final SQLInfo sqlInfo, final QueryFilter queryFilter,
            final Map<String, DocumentData> documentDataMapContext) {
        super();

        this.documentDataMap = documentDataMap;
        this.fieldMap = fieldMap;
        this.availableParams = ArrayUtils.clone(availableParams);
        this.model = model;
        this.sqlInfo = sqlInfo;
        this.dialect = sqlInfo.dialect;
        this.database = sqlInfo.database;
        this.queryFilter = queryFilter;
        this.countColumn = ColumnUtil.createCountColumn(dialect);
        this.documentDataMapContext = documentDataMapContext;
        this.nbParamsRequired = 0;

        this.wherePart = new StringBuilder();
        this.whereParams = new ArrayList<Serializable>();

        this.selectPart = new StringBuilder();

        this.fromPart = new StringBuilder();
        this.fromParams = new ArrayList<Serializable>();

        this.whatColumns = new ArrayList<Column>();
        this.whatKeys = new ArrayList<String>();

        this.orderPart = new StringBuilder();

        this.groupPart = new StringBuilder();

        this.recStartWithPart = new StringBuilder();
        this.recStartWithParams = new ArrayList<Serializable>();

    }

    @Override
    public void visitDateLiteral(final DateLiteral dateLit) {
        if (isInWhere()) {
            wherePart.append('?');
            whereParams.add(dateLit.toCalendar());
        } else {
            throw new QueryParseException("Not supported operation : visitDateLiteral in " + pos);
        }
    }

    @Override
    public void visitDoubleLiteral(final DoubleLiteral doubleLit) {
        if (isInWhere()) {
            wherePart.append('?');
            whereParams.add(Double.valueOf(doubleLit.value));
        } else {
            throw new QueryParseException("Not supported operation : visitDoubleLiteral in " + pos);
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
            throw new QueryParseException("Not supported operation : visitExpression in " + pos);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitExpression(" + expr + ")");
        }
    }

    protected void visitBooleanLiteral(final BooleanLiteral node) {
        wherePart.append('?');
        whereParams.add(Boolean.valueOf(node.getValue()));
    }

    protected void processExpressionInWhere(final Expression expr) {
        if (expr.lvalue instanceof Function) {
            processExpressionWithFunction(expr);
            return;
        }

        wherePart.append('(');

        String name = null;
        if (expr.lvalue instanceof Reference) {
            name = ((Reference) expr.lvalue).name;
        }

        Field field = null;
        String fieldName = null;
        if (name != null) {
            field = fieldMap.get(name);
            if (field == null) {
                throw new QueryParseException("Cannot retrieve field for name[" + name + "]");
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
        // } else if (name != null && name.startsWith(NXQL.ECM_FULLTEXT)
        // && !NXQL.ECM_FULLTEXT_JOBID.equals(name)) {
        // visitExpressionFulltext(expr, name);
        if (operator == Operator.STARTSWITH || NXQL.ECM_ISPROXY.equals(fieldName) || NXQL.ECM_PATH.equals(fieldName)) {
            throw new QueryParseException("OPERATION NOT SUPPORTED (" + name + ")");
        } else if (NXQL.ECM_MIXINTYPE.equals(fieldName)) {
            visitExpressionMixinType(field, expr);
        } else if (NXQL.ECM_ISVERSION.equals(fieldName) || NXQL.ECM_ISVERSION_OLD.equals(fieldName)) {
            visitExpressionIsVersion(field, expr);
        } else if (fieldName != null && fieldName.startsWith(NXQL.ECM_FULLTEXT)) {
            processExpressionFullText(expr, field);
        } else if (operator == Operator.EQ || operator == Operator.NOTEQ || operator == Operator.IN
                || operator == Operator.NOTIN || operator == Operator.LIKE || operator == Operator.NOTLIKE
                || operator == Operator.ILIKE || operator == Operator.NOTILIKE) {

            if (field.getColumn() != null
                    && !(field.getModelProperty() != null && field.getModelProperty().propertyType.isArray() && !field
                            .isInSelect())) {
                Expression node = expr;
                if (field.getModelProperty() != null && field.getModelProperty().propertyType == PropertyType.BOOLEAN) {
                    if (!(node.rvalue instanceof SParamLiteral)) {
                        // si param on fait rien le driver reconnait déjà les
                        // boolean true/false et 0/1
                        // sinon on doit le faire nous meme sur la valeur

                        if (!(node.rvalue instanceof IntegerLiteral)) {
                            throw new QueryParseException(
                                    "Boolean expressions require literal 0 or 1 as right argument");
                        }
                        final long val = ((IntegerLiteral) node.rvalue).value;
                        if (val != 0 && val != 1) {
                            throw new QueryParseException(
                                    "Boolean expressions require literal 0 or 1 as right argument");
                        }
                        node = new Predicate(node.lvalue, node.operator, new BooleanLiteral(val == 1));
                    }
                }

                if (operator == Operator.ILIKE || operator == Operator.NOTILIKE) {
                    visitExpressionIlike(node);
                } else {
                    super.visitExpression(node);
                }
            } else if (field.getModelProperty() == null) {
                throw new QueryParseException("Unknown field: " + name);
            } else {
                if (field.getModelProperty().propertyType.isArray()) {
                    // use EXISTS with subselect clause
                    final boolean direct = operator == Operator.EQ || operator == Operator.IN
                            || operator == Operator.LIKE || operator == Operator.ILIKE;
                    final Operator directOp = direct ? operator : operator == Operator.NOTEQ ? Operator.EQ
                            : operator == Operator.NOTIN ? Operator.IN : operator == Operator.NOTLIKE ? Operator.LIKE
                                    : Operator.ILIKE;

                    if (!direct) {
                        wherePart.append("NOT ");
                    }
                    final DocumentData docData = documentDataMap.get(field.getPrefix());
                    final Table table = database.getTable(field.getModelProperty().fragmentName);
                    final TableKey refTk = docData.getReferenceTableKey();

                    wherePart.append(String.format("EXISTS (SELECT 1 FROM %s WHERE %s = %s.%s AND (",
                            table.getQuotedName(), docData.getReferenceTableColumnIdName(), refTk.getKey(),
                            docData.getReferenceTableColumnIdName()));

                    if (directOp == Operator.ILIKE) {
                        visitExpressionIlike(expr, directOp);
                    } else {
                        expr.lvalue.accept(this);
                        directOp.accept(this);
                        expr.rvalue.accept(this);
                    }
                    wherePart.append("))");
                } else {
                    throw new QueryParseException("field with column and property : " + name);
                }
            }
        } else if (operator == Operator.BETWEEN || operator == Operator.NOTBETWEEN) {
            final LiteralList litList = (LiteralList) expr.rvalue;
            expr.lvalue.accept(this);
            wherePart.append(' ');
            expr.operator.accept(this);
            wherePart.append(' ');
            litList.get(0).accept(this);
            wherePart.append(STR_SPACE).append(SQL.AND).append(STR_SPACE);
            litList.get(1).accept(this);
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

    protected Set<String> getStringLiterals(final LiteralList list) {
        final Set<String> set = new HashSet<String>();
        for (final Literal literal : list) {
            if (!(literal instanceof StringLiteral)) {
                throw new QueryParseException("requires string literals");
            }
            set.add(((StringLiteral) literal).value);
        }
        return set;
    }

    protected void visitExpressionMixinType(final Field field, final Expression node) {
        boolean include;
        Set<String> mixins;

        final Expression expr = node;
        final Operator operator = expr.operator;
        if (operator == Operator.EQ || operator == Operator.NOTEQ) {
            include = operator == Operator.EQ;
            if (expr.rvalue instanceof SParamLiteral) {
                final Serializable value = extractProcessedParam();
                if (value instanceof String) {
                    mixins = Collections.singleton((String) value);
                } else {
                    throw new QueryParseException(NXQL.ECM_FULLTEXT
                            + " requires param literal corresponding to a string");
                }
            } else if (expr.rvalue instanceof StringLiteral) {
                final String value = ((StringLiteral) expr.rvalue).value;
                mixins = Collections.singleton(value);
            } else {
                throw new QueryParseException(NXQL.ECM_MIXINTYPE
                        + " = requires literal string or param as right argument");
            }
        } else if (operator == Operator.IN || operator == Operator.NOTIN) {
            include = operator == Operator.IN;
            if (!(expr.rvalue instanceof LiteralList)) {
                throw new QueryParseException(NXQL.ECM_MIXINTYPE + " = requires string list as right argument");
            }
            mixins = getStringLiterals((LiteralList) expr.rvalue);
        } else {
            throw new QueryParseException(NXQL.ECM_MIXINTYPE + " unknown operator: " + operator);
        }

        /*
         * Primary types
         */

        Set<String> types;
        if (include) {
            types = new HashSet<String>();
            for (final String mixin : mixins) {
                types.addAll(model.getMixinDocumentTypes(mixin));
            }
        } else {
            types = new HashSet<String>(model.getDocumentTypes());
            for (final String mixin : mixins) {
                types.removeAll(model.getMixinDocumentTypes(mixin));
            }
        }

        /*
         * Instance mixins
         */

        FNXQLConfigService configService;
        try {
            configService = ServiceUtil.getService(FNXQLConfigService.class);
        } catch (final NuxeoException e) {
            throw new QueryParseException("Failed to retrieve FNXQLConfigService", e);
        }
        final Set<String> instanceMixins = configService.extractMixinTypePerInstance(mixins);

        /*
         * SQL generation
         */

        final Table dataHierTable = database.getTable(Model.HIER_TABLE_NAME);
        if (!types.isEmpty()) {
            final Column col = dataHierTable.getColumn(Model.MAIN_PRIMARY_TYPE_KEY);
            wherePart.append(field.getTableKey().getKey() + STR_POINT + col.getQuotedName());
            wherePart.append(" IN ");
            wherePart.append('(');
            for (final Iterator<String> it = types.iterator(); it.hasNext();) {
                visitStringLiteral(it.next());
                if (it.hasNext()) {
                    wherePart.append(", ");
                }
            }
            wherePart.append(')');

            if (!instanceMixins.isEmpty()) {
                wherePart.append(include ? " OR " : " AND ");
            }
        }

        if (!instanceMixins.isEmpty()) {
            wherePart.append('(');
            final Column mixinsColumn = dataHierTable.getColumn(Model.MAIN_MIXIN_TYPES_KEY);
            final String[] returnParam = new String[1];
            for (final Iterator<String> it = instanceMixins.iterator(); it.hasNext();) {
                final String mixin = it.next();
                final String sql = getMatchMixinType(dialect, field.getTableKey().getKey(), mixinsColumn, mixin,
                        returnParam, include);
                wherePart.append(sql);
                if (returnParam[0] != null) {
                    whereParams.add(returnParam[0]);
                }
                if (it.hasNext()) {
                    wherePart.append(include ? " OR " : " AND ");
                }
            }
            if (!include) {
                wherePart.append(" OR ");
                wherePart.append(field.getTableKey().getKey() + STR_POINT + mixinsColumn.getQuotedName());
                wherePart.append(" IS NULL");
            }
            wherePart.append(')');
        }

        if (types.isEmpty() && instanceMixins.isEmpty()) {
            wherePart.append(include ? "0=1" : "0=0");
        }
    }

    private String getMatchMixinType(final Dialect dialect, final String tableKey, final Column column,
            final String mixin, final String[] returnParam, final boolean positive) {
        if (dialect instanceof DialectPostgreSQL) {
            returnParam[0] = mixin;
            final String sql = "ARRAY[?] <@ " + tableKey + STR_POINT + column.getQuotedName();
            return positive ? sql : "NOT(" + sql + ")";
        } else {
            returnParam[0] = "%" + Dialect.ARRAY_SEP + mixin + Dialect.ARRAY_SEP + "%";
            return String.format("%s %s ?", tableKey + STR_POINT + column.getQuotedName(), positive ? "LIKE"
                    : "NOT LIKE");
        }
    }

    protected void visitExpressionIsVersion(final Field field, final Expression node) {
        if (node.operator != Operator.EQ && node.operator != Operator.NOTEQ) {
            throw new QueryParseException(NXQL.ECM_ISVERSION + " requires = or <> operator");
        }
        if (!(node.rvalue instanceof IntegerLiteral)) {
            throw new QueryParseException(NXQL.ECM_ISVERSION + " requires literal 0 or 1 as right argument");
        }
        final long val = ((IntegerLiteral) node.rvalue).value;
        if (val != 0 && val != 1) {
            throw new QueryParseException(NXQL.ECM_ISVERSION + " requires literal 0 or 1 as right argument");
        }
        final boolean bool = node.operator == Operator.EQ ^ val == 0;
        wherePart.append(field.toSql()).append(bool ? " IS NOT NULL" : " IS NULL");
    }

    protected void processExpressionWithFunction(final Expression expr) {

        final Function func = (Function) expr.lvalue;

        final UfnxqlFunction ufnxqlFunction = getDeclaredFunctions().get(func.name);
        if (ufnxqlFunction != null) {
            if (ufnxqlFunction.checkExpression(this, expr)) {
                expr.lvalue.accept(this);
                return;
            } else {
                throw new QueryParseException("for function " + ufnxqlFunction.getName() + " expression should be "
                        + ufnxqlFunction.getExpression());
            }
        }

        expr.lvalue.accept(this);
        expr.operator.accept(this);
        expr.rvalue.accept(this);
    }

    protected void visitExpressionIlike(final Expression expr) {
        visitExpressionIlike(expr, expr.operator);
    }

    public void processExpressionFullText(final Expression expr, final Field field) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("in processExpressionFullText(" + expr + ", " + field + ")");
        }
        String name = field.getName();
        final String[] nameref = new String[] { name };
        final boolean useIndex = findFulltextIndexOrField(model, nameref);
        name = nameref[0];
        if (expr.operator != Operator.EQ && expr.operator != Operator.LIKE) {
            throw new QueryParseException(NXQL.ECM_FULLTEXT + " requires = or LIKE operator");
        }

        if (useIndex) {
            String fulltextQuery;
            if (expr.rvalue instanceof SParamLiteral) {
                final Serializable value = extractProcessedParam();
                if (value instanceof String) {
                    fulltextQuery = (String) value;
                } else {
                    throw new QueryParseException(NXQL.ECM_FULLTEXT
                            + " requires param literal corresponding to a string");
                }
            } else if (expr.rvalue instanceof StringLiteral) {
                fulltextQuery = ((StringLiteral) expr.rvalue).value;
            } else {
                throw new QueryParseException(NXQL.ECM_FULLTEXT + " requires literal string as right argument or ?");
            }

            // use actual fulltext query using a dedicated index
            fulltextQuery = dialect.getDialectFulltextQuery(fulltextQuery);

            final DocumentData docData = documentDataMap.get(field.getPrefix());

            final Table refTable = docData.getReferenceTable();
            final Column mainColumn = docData.getReferenceTableColumnId();
            final TableKey tkRef = docData.getReferenceTableKey();
            final Table aliasTable = new TableSymbolicKey(refTable, tkRef.getKey());
            final Column idCol = new Column(mainColumn, aliasTable);

            if (dialect instanceof DialectOracle) {

                // pour oracle,il faut seulement une jointure
                // avec la table fulltext par document referencé

                final Table fulltextTable = database.getTable(Model.FULLTEXT_TABLE_NAME);

                final String tableAlias = docData.getKey() + "_fulltext";

                if (!docWithFulltextJoin.contains(docData)) {
                    final Join join = new Join(Join.INNER, //
                            fulltextTable.getQuotedName(), tableAlias, // alias
                            null, // param
                            tableAlias + STR_POINT + docData.getReferenceTableColumnIdName(), // on1
                            idCol.getFullQuotedName()); // on2
                    docData.addJoin(join);
                    docWithFulltextJoin.add(docData);
                }

                final String indexSuffix = model.getFulltextIndexSuffix(name);
                final Table fulltextAliasTable = new TableSymbolicKey(fulltextTable, tableAlias);
                final Column ftColumn = new Column(fulltextTable.getColumn(Model.FULLTEXT_FULLTEXT_KEY + indexSuffix),
                        fulltextAliasTable);

                wherePart.append(String.format("CONTAINS(%s, ?, %d) > 0", ftColumn.getFullQuotedName(),
                        fulltextIndexNumber));
                ++fulltextIndexNumber;

                whereParams.add(fulltextQuery);

            } else {

                final FulltextMatchInfo info = dialect.getFulltextScoredMatchInfo(fulltextQuery, name,
                        fulltextIndexNumber, idCol, model, database);
                ++fulltextIndexNumber;
                docData.addJoins(info.joins);

                wherePart.append(info.whereExpr);
                if (info.whereExprParam != null) {
                    whereParams.add(info.whereExprParam);
                }
            }
        } else {
            if (!(expr.rvalue instanceof StringLiteral)) {
                throw new QueryParseException(NXQL.ECM_FULLTEXT + " requires literal string as right argument");
            }

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
                throw new QueryParseException("Unknown field: " + name);
            }
            useIndex = sep == '_';
            name = name.substring(NXQL.ECM_FULLTEXT.length() + 1);
            if (useIndex) {
                if (!model.getFulltextConfiguration().indexNames.contains(name)) {
                    throw new QueryParseException("No such fulltext index: " + name);
                }
            } else {
                // find if there's an index holding just that field
                final String index = model.getFulltextConfiguration().fieldToIndexName.get(name);
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
    public void visitSFromClause(final SFromClause clause) {

        final List<SFromList.AliasElementList> fromkeylist = clause.getFromList().getFromList();

        for (final SFromList.AliasElementList alk : fromkeylist) {
            if (alk instanceof SFromList.AliasQueryList) {
                final ClauseParams clauseParams = new ClauseParams();
                nbParamsRequired += computeInternalQuery(((SFromList.AliasQueryList) alk).getQuery(), clauseParams);
                fromPart.append("(").append(clauseParams.getClause()).append(") ").append(SQL.AS).append(STR_SPACE)
                        .append(alk.getAlias());
                fromParams.addAll(clauseParams.getParams());
            }
        }
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

        if (isInSelect() && func.name.equalsIgnoreCase("coalesce") && func.args != null && !func.args.isEmpty()) {
            processCoalesceFunction(func);
            return;
        }

        StringBuilder strBuilder = null;
        if (isInSelect()) {
            strBuilder = selectPart;
        } else if (isInWhere()) {
            strBuilder = wherePart;
        } else if (isInOrder()) {
            strBuilder = orderPart;
        } else if(isInGroup()){
            strBuilder = groupPart;
        } else {
            throw new QueryParseException("Function [" + func.name + "] not supported in this part (" + pos + ")");
        }

        final UfnxqlFunction ufnxqlFunction = getDeclaredFunctions().get(func.name);
        if (ufnxqlFunction != null) {
            ufnxqlFunction.visitFunction(this, func);
        } else if (UFNXQLConstants.COUNT_DISTINCT_FUNCTION.equals(func.name)) {
            if (!isInSelect()) {
                throw new QueryParseException("count_distinct authorized only in select");
            }
            processCountDistinctFunction(func);
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
                final SQLQuery query = ((SFunction) func).getQuery();
                if (query != null) {
                    if (func.args != null) {
                        strBuilder.append(", ");
                    }
                    final ClauseParams internalClause = new ClauseParams();
                    nbParamsRequired += computeInternalQuery(query, internalClause);
                    strBuilder.append(internalClause.getClause());
                    whereParams.addAll(internalClause.getParams());
                }
            }
            strBuilder.append(')');
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitFunction(" + func + ")");
        }
    }

    private void processCountDistinctFunction(final Function func) {
        final int initsize = whatColumns.size();
        selectPart.append(SQL.COUNT).append('(').append(SQL.DISTINCT).append(' ');
        if (func.args != null) {
            for (final Iterator<Operand> it = func.args.iterator(); it.hasNext();) {
                it.next().accept(this);
                if (it.hasNext()) {
                    selectPart.append(", ");
                }
            }
        }

        selectPart.append(')');
        while (whatColumns.size() > initsize) {
            whatColumns.remove(whatColumns.size() - 1);
        }
        whatColumns.add(countColumn);
    }

    private void processCoalesceFunction(final Function func) {
        final int initsize = whatColumns.size();
        selectPart.append("coalesce(");
        for (final Operand op : func.args) {
            op.accept(this);
            selectPart.append(",");
        }
        while (whatColumns.size() > initsize + 1) {
            whatColumns.remove(whatColumns.size() - 1);
        }
        selectPart.replace(selectPart.length() - 1, selectPart.length(), ")");

    }

    /**
     * return the number of used params, fill the cp object
     * 
     * @param query
     * @param clauseParams
     * @return
     */
    public int computeInternalQuery(final SQLQuery query, final ClauseParams clauseParams) {
        final UFNXQLQueryMaker maker = new UFNXQLQueryMaker(mergedDocumentDataMapContext());
        maker.usedToGenerateInternalQueryPart = true;
        final Object[] params = availableParams == null ? null : Arrays.copyOfRange(availableParams, nbParamsRequired,
                availableParams.length);

        final Query genquery = maker.buildQuery(sqlInfo, model, null, query, queryFilter, params);
        clauseParams.setClause(genquery.selectInfo.sql);
        clauseParams.setParams(genquery.selectParams);

        return maker.getUsedParams();
    }

    /**
     * met le contenu de documentDataMap et documentDataMapContext dans une meme
     * map
     * 
     * @return
     */
    protected Map<String, DocumentData> mergedDocumentDataMapContext() {
        final Map<String, DocumentData> merged = new HashMap<String, DocumentData>();
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
        throw new QueryParseException("visitHavingClause not supported");
    }

    @Override
    public void visitIntegerLiteral(final IntegerLiteral intLit) {
        if (isInWhere()) {
            wherePart.append('?');
            whereParams.add(Long.valueOf(intLit.value));
        } else if(isInSelect()){
            selectPart.append(intLit.value);
        } else {
            throw new QueryParseException("Not supported operation : visitIntegerLiteral in " + pos);
        }
    }

    // @Override
    // public void visitLiteral(Literal l) {
    // log.info("in visitLiteral("+l+")");
    // super.visitLiteral(l);
    // log.info("out visitLiteral("+l+")");
    // }

    @Override
    public void visitLiteralList(final LiteralList list) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("in visitLiteralList(" + list + ")");
        }
        if (isInWhere()) {
            wherePart.append('(');
            for (final Iterator<Literal> it = list.iterator(); it.hasNext();) {
                it.next().accept(this);
                if (it.hasNext()) {
                    wherePart.append(", ");
                }
            }
            wherePart.append(')');
        } else {
            throw new QueryParseException("No support for literal list in another part than where (" + pos + ")");
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitLiteralList(" + list + ")");
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
            throw new QueryParseException("Not supported operation : visitOperator in " + pos);
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
        if (expr instanceof SOrderByExpr) {
            ((SOrderByExpr) expr).getOperand().accept(this);
        } else {
            expr.reference.accept(this);
        }
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
        int orderPartLength;
        for (final Iterator<OrderByExpr> it = list.iterator(); it.hasNext();) {
            orderPartLength = orderPart.length();
            it.next().accept(this);
            if (it.hasNext() && orderPart.length() > orderPartLength) {
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

    public void visitReference(final Column column) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("in visitReference(" + column + ")");
        }

        String qname = column.getFullQuotedName();
        // some databases (Derby) can't do comparisons on CLOB
        if (column.getJdbcType() == Types.CLOB) {
            final String colFmt = dialect.getClobCast(isInOrder());
            if (colFmt != null) {
                qname = String.format(colFmt, qname, Integer.valueOf(255));
            }
        }
        if (isInSelect()) {
            selectPart.append(qname);
        } else if (isInOrder()) {
            orderPart.append(qname);
        } else if (isInWhere()) {
            wherePart.append(qname);
        } else if (isInGroup()) {
            if (groupPart.length() > 0) {
                groupPart.append(", ");
            }
            groupPart.append(qname);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitReference(" + column + ")");
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
        	Iterator<Map.Entry<String, Operand>> iter = elements.entrySet().iterator();
            processASelectEntry(iter.next());
            while(iter.hasNext()){
            	Map.Entry<String, Operand> entry = iter.next();
                selectPart.append(", ");
                processASelectEntry(entry);
            }
        }

        pos = POS.UNDEFINED;

        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitSelectClause(" + clause + ")");
        }
    }

    private void processASelectEntry(final Map.Entry<String, Operand> entry) {
        final Operand operator = entry.getValue();
        final String alias = entry.getKey();
        operator.accept(this);
        if (!operator.toString().equals(alias)) {
            selectPart.append(STR_SPACE).append(SQL.AS).append(STR_SPACE).append(alias);
        }
        whatKeys.add(alias);
    }

    @Override
    public void visitStringLiteral(final StringLiteral strLit) {
        visitStringLiteral(strLit.value);
    }

    public void visitStringLiteral(final String str) {
        processStringLiteral(str);
    }

    public void processStringLiteral(final String str) {
        if(isInWhere()){
            wherePart.append('?');
            whereParams.add(str);
        } else if (isInSelect()){
            selectPart.append('\'');
            selectPart.append(str);
            selectPart.append('\'');
        } else {
            throw new QueryParseException("Not supported operation : processStringLiteral in " + pos);
        }
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
        boolean first = true;
        for(Operand operand : clause.getGroupByList()){
            if(first){
                first = false;
            } else {
                groupPart.append(", ");
            }
            operand.accept(this);
            
        }
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
                throw new QueryParseException("No parameter in entry");
            }
            if (nbParamsRequired >= availableParams.length) {
                throw new QueryParseException("Not enough parameter in entry [available=" + availableParams.length
                        + "]");
            }
            whereParams.add((Serializable) availableParams[nbParamsRequired]);
            ++nbParamsRequired;
        } else {
            throw new QueryParseException("param supported only in where part");
        }
    }

    @Override
    public void visitSSQLQuery(final SSQLQuery query) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("in visitSSQlQuery(" + query + ")");
        }

        final ClauseParams internalClause1 = new ClauseParams();
        nbParamsRequired += computeInternalQuery(query.getQuery1(), internalClause1);
        final ClauseParams internalClause2 = new ClauseParams();
        nbParamsRequired += computeInternalQuery(query.getQuery2(), internalClause2);

        wherePart.append(internalClause1.getClause());
        wherePart.append(" UNION ");
        wherePart.append(internalClause2.getClause());
        whereParams.addAll(internalClause1.getParams());
        whereParams.addAll(internalClause2.getParams());

        if (LOG.isTraceEnabled()) {
            LOG.trace("out visitSSQlQuery(" + query + ")");
        }
    }

    public boolean isInSelect() {
        return POS.IN_SELECT.equals(pos);
    }

    public boolean isInOrder() {
        return POS.IN_ORDER.equals(pos);
    }

    public boolean isInWhere() {
        return POS.IN_WHERE.equals(pos);
    }

    public boolean isInGroup() {
        return POS.IN_GROUP.equals(pos);
    }

    public ClauseParams getWhereClause() {
        return new ClauseParams(wherePart.toString(), whereParams);
    }

    public StringBuilder getRecStartWithPart() {
        return recStartWithPart;
    }

    public List<Serializable> getRecStartWithParams() {
        return recStartWithParams;
    }

    public StringBuilder getOrderPart() {
        return orderPart;
    }

    public StringBuilder getSelectPart() {
        return selectPart;
    }

    public ClauseParams getFromClause() {
        return new ClauseParams(fromPart.toString(), fromParams);
    }

    public StringBuilder getGroupPart() {
        return groupPart;
    }

    public int getNbParamsRequired() {
        return nbParamsRequired;
    }

    public List<Column> getWhatColumns() {
        return whatColumns;
    }

    public List<String> getWhatKeys() {
        return whatKeys;
    }

    public final QueryFilter getQueryFilter() {
        return queryFilter;
    }

    public SQLInfo getSqlInfo() {
        return sqlInfo;
    }

    public Model getModel() {
        return model;
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public Map<String, DocumentData> getDocumentDataMap() {
        return documentDataMap;
    }

    public StringBuilder getWherePart() {
        return wherePart;
    }

    public List<Serializable> getWhereParams() {
        return whereParams;
    }

    public void incrNbParamsRequired(final int val) {
        nbParamsRequired += val;
    }

    public int getPgfulltextAliasNumber() {
        return pgfulltextAliasNumber;
    }

    public void incrPgfulltextAliasNumber() {
        ++pgfulltextAliasNumber;
    }

    public Object getAvailableParams(final int index) {
        return availableParams[index];
    }

    private Map<String, UfnxqlFunction> getDeclaredFunctions() {
        if (declaredFunctions == null) {
            try {
                declaredFunctions = new HashMap<String, UfnxqlFunction>();
                final FNXQLConfigService service = ServiceUtil.getRequiredService(FNXQLConfigService.class);
                for (final UfnxqlFunction function : service.getUFNXQLFunctions()) {
                    declaredFunctions.put(function.getName(), function);
                }
            } catch (final NuxeoException e) {
                throw new QueryParseException("Failed to retrieve declared functions", e);
            }
        }
        return declaredFunctions;
    }

    /**
     * process the SParamLiteral : cancel the add of the marks and return the
     * param value;
     */
    private Serializable extractProcessedParam() {
        visitSParamLiteral();
        // remove the parameter
        // remove the ?
        wherePart.setLength(wherePart.length() - 1);
        return whereParams.remove(whereParams.size() - 1);
    }
}
