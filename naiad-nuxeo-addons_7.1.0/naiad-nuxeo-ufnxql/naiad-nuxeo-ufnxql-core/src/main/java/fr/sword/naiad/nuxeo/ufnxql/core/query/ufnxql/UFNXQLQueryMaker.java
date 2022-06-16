package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.ModelProperty;
import org.nuxeo.ecm.core.storage.sql.Session.PathResolver;
import org.nuxeo.ecm.core.storage.sql.jdbc.NXQLQueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.Query;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.ColumnMapMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;
import org.nuxeo.ecm.core.storage.sql.jdbc.dialect.DialectPostgreSQL;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromKeyList;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.SFromKeyList.SFromKey;
import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.parser.UFNXQLQueryParser;
import fr.sword.naiad.nuxeo.ufnxql.core.service.FNXQLConfigService;

/**
 * Genere une requete SQL depuis une requete UFNXQL
 * 
 * @author spesnel
 * 
 */
public class UFNXQLQueryMaker {

    private static final Log LOG = LogFactory.getLog(UFNXQLQueryMaker.class);

    private static final String COMMA_SEP = ", ";
    private static final String AND_SEP = " " + SQL.AND + " ";

    private static final String TYPE_PROXY = "Proxy";

    /**
     * signale si la classe est utilisé pour génerer une sous-requete interne
     * d'une requete principal les test ne sont pas alors les mêmes (notamment
     * pour le contrôle des paramètres utilisateurs utilisés)
     */
    public boolean usedToGenerateInternalQueryPart = false;

    private Model model;
    private Database database;
    private int usedParams = 0;

    private Table hierTable = null;

    private boolean boolAsInt = true;

    private Map<String, DocumentData> documentDataMapContext = null;

    /**
     * Un objet pour passer des informations u maker au builder.
     * 
     */
    private final UFNXQLInfos ufnxqlInfos;

    private FNXQLConfigService configService = null;

    public UFNXQLQueryMaker(final Map<String, DocumentData> documentDataMapContext) {
        this.documentDataMapContext = documentDataMapContext;
        this.ufnxqlInfos = new UFNXQLInfos();
    }

    public UFNXQLQueryMaker() {
        this(null);
    }

    public Query buildQuery(final SQLInfo sqlInfo, final Model model, final PathResolver pathResolver,
            final String query, final QueryFilter queryFilter, final Object... params) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("INPUT QUERY[" + query + "]");
        }

        final SQLQuery sqlQuery = UFNXQLQueryParser.parse(query);
        return buildQuery(sqlInfo, model, pathResolver, sqlQuery, queryFilter, params);
    }

    public Query buildQuery(final SQLInfo sqlInfo, final Model model, final PathResolver pathResolver,
            final SQLQuery sqlQuery, final QueryFilter queryFilter, final Object... params) {

        this.model = model;
        this.database = sqlInfo.database;
        this.hierTable = this.database.getTable(Model.HIER_TABLE_NAME);

        this.boolAsInt = true;
        if (sqlInfo.dialect instanceof DialectPostgreSQL) {
            this.boolAsInt = false;
        }

        // recuperation des document manipules et des champs accédés
        final QueryAnalyzer analyze = new QueryAnalyzer(documentDataMapContext);
        sqlQuery.accept(analyze);

        final Map<String, DocumentData> documentDataMap = analyze.getDocumentDataMap();

        //
        for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
            final DocumentData d = e.getValue();
            if (d.isSubquery()) {

                for (final Field f : d.getFields()) {
                    f.setInSubQuery(true);
                    // TODO SPL : extends support for other type than string
                    f.setColumn(hierTable.getColumn(Model.MAIN_KEY));
                }

            } else {
                // recherche des primary type pour les document accédé
                retrieveTypeForDocument(d);

                // recherche des colonnes accédés nécessaire pour attribute de
                // schema présent dans la requete
                retrieveColumnForField(d);

                // construit la liste des tables nécessaires et leur crée un
                // alias
                buildTableKeySet(d);

                // trace
                if (LOG.isDebugEnabled()) {
                    final StringBuilder strBuilder = new StringBuilder("key[").append(e.getKey()).append("]->")
                            .append(d.getType()).append("-->(");
                    for (final String st : d.getSubTypes()) {
                        strBuilder.append(st).append(", ");
                    }
                    strBuilder.append(")");
                    LOG.debug(strBuilder.toString());

                    for (final Field f : d.getFields()) {
                        LOG.debug("  -->[" + f.getName() + "-->" + f.toSql() + "]");
                    }
                    for (final TableKey tk : d.getTableKeySet().getTableKeySet()) {
                        LOG.debug("[" + tk.getKey() + ":" + tk.getTableName() + "]");
                    }
                }
            }
        }

        // crée une map d'accés au object field depuis leur nom
        final Map<String, Field> fieldMap = new HashMap<String, Field>();
        for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
            final String key = e.getValue().getKey();
            for (final Field f : e.getValue().getFields()) {
                final String qualifiedName = key + "." + f.getName();
                fieldMap.put(qualifiedName, f);
            }
        }

        // ajout à la field map les objet de la requete parente
        if (documentDataMapContext != null) {
            for (final Entry<String, DocumentData> e : documentDataMapContext.entrySet()) {
                final String key = e.getValue().getKey();
                for (final Field f : e.getValue().getFields()) {
                    final String qualifiedName = key + "." + f.getName();
                    fieldMap.put(qualifiedName, f);
                }
            }
        }

        // construit les morceaux de la requete SQL
        // les objets documentData peuvent être mise à jour avec de nouvelle
        // table
        // liée à des expression NXQL (ecm:xxx)
        final QueryBuilder qb = new QueryBuilder(documentDataMap, fieldMap, params, model, sqlInfo, queryFilter,
                documentDataMapContext);
        sqlQuery.accept(qb);

        // construit la partie de requete contenant la contrainte
        // sur les type de document (PRIMARYTYPE)
        final ClauseParams typeConstraints = buildTypeConstraints(documentDataMap);

        // construit la partie FROM
        final ClauseParams fromclause = buildFromClause(sqlInfo, documentDataMap);

        if (StringUtils.isNotBlank(qb.getFromClause().getClause())) {
            if (StringUtils.isNotBlank(fromclause.getClause())) {
                fromclause.setClause(fromclause.getClause() + ", ");
            } else {
                fromclause.setClause(" FROM ");
            }
            fromclause.setClause(fromclause.getClause() + qb.getFromClause().getClause());
            fromclause.getParams().addAll(qb.getFromClause().getParams());

            final ClauseParams fromclauseComp = buildFromClauseComplForSubQuery(sqlInfo, documentDataMap);
            if (fromclauseComp != null) {
                fromclause.setClause(fromclause.getClause() + fromclauseComp.getClause());
                fromclause.getParams().addAll(fromclauseComp.getParams());
            }
        }

        // recupere la partie WHERE
        final ClauseParams whereClause = qb.getWhereClause();

        if (LOG.isDebugEnabled()) {
            LOG.debug("WHERE=[" + whereClause.getClause() + "]");
            LOG.debug("ORDER=[" + qb.getOrderPart().toString() + "]");
            LOG.debug("SELECT=[" + qb.getSelectPart() + "]");
            LOG.debug("GROUP=[" + qb.getGroupPart() + "]");
            LOG.debug("FROM=[" + fromclause.getClause() + "]");
        }

        usedParams = qb.getNbParamsRequired();
        if (!usedToGenerateInternalQueryPart) {
            // test que tous les paramètres données en arguments sont utilisé
            // par la requete principal
            int nbGivenParams = 0;
            if (params != null) {
                nbGivenParams = params.length;
            }
            if (usedParams != nbGivenParams) {
                throw new QueryParseException("Incorrect params number : requested=" + usedParams + ", available="
                        + params.length);
            }
        }

        // assemblage de la requete SQL
        final StringBuilder generated = new StringBuilder();
        if (StringUtils.isNotEmpty(qb.getSelectPart().toString())) {
            generated.append(SQL.SELECT).append(" ").append(qb.getSelectPart()).append(fromclause.getClause());
            if (whereClause.hasNotEmptyClause() || typeConstraints.hasNotEmptyClause()) {
                generated.append(" ").append(SQL.WHERE).append(" ");
            }
        }

        generated.append(typeConstraints.getClause());

        if (whereClause.hasNotEmptyClause() && typeConstraints.hasNotEmptyClause()) {
            // un AND pour lié contrainte de type et contrainte du where
            // nécessaire seulement si l'un et l'autre existe
            generated.append(" ").append(SQL.AND).append(" ");
        }
        generated.append(whereClause.getClause());

        if (qb.getRecStartWithPart().length() > 0) {
            generated.append(qb.getRecStartWithPart());
        }

        if (qb.getGroupPart().length() > 0) {
            generated.append(" ").append(SQL.GROUPBY).append(" ").append(qb.getGroupPart());
        }

        if (qb.getOrderPart().length() > 0) {
            generated.append(" ").append(SQL.ORDERBY).append(" ").append(qb.getOrderPart().toString());
        }

        final String generatedQueryStr = generated.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("GENERATED=[" + generatedQueryStr + "]");

            final StringBuilder paramStr = new StringBuilder();
            paramStr.append("FROM PARAMS :");
            for (final Serializable s : fromclause.getParams()) {
                paramStr.append("[").append(s).append("] ");
            }
            paramStr.append("\n");
            paramStr.append("CONSTRAINT PARAMS :");
            for (final Serializable s : typeConstraints.getParams()) {
                paramStr.append("[").append(s).append("] ");
            }
            paramStr.append("\n");
            paramStr.append("WHERE PARAMS :");
            for (final Serializable s : whereClause.getParams()) {
                paramStr.append("[").append(s).append("] ");
            }
            paramStr.append("\n");
            paramStr.append("REC START PARAMS :");
            for (final Serializable s : qb.getRecStartWithParams()) {
                paramStr.append("[").append(s).append("] ");
            }
            LOG.debug(paramStr);
        }

        // creation de l'objet Query
        final Query generatedQuery = new Query();
        final ColumnMapMaker mapMaker = new ColumnMapMaker(qb.getWhatColumns(), qb.getWhatKeys());

        generatedQuery.selectInfo = new SQLInfoSelect(generatedQueryStr, mapMaker);
        generatedQuery.selectParams = new ArrayList<Serializable>();
        generatedQuery.selectParams.addAll(fromclause.getParams());
        generatedQuery.selectParams.addAll(typeConstraints.getParams());
        generatedQuery.selectParams.addAll(whereClause.getParams());
        generatedQuery.selectParams.addAll(qb.getRecStartWithParams());

        return generatedQuery;
    }

    public void retrieveColumnForField(final DocumentData d) {
        for (final Field f : d.getFields()) {

            final String name = f.getName();
            Column column = null;
            ModelProperty propertyInfo = null;
            if (name.startsWith(NXQL.ECM_PREFIX)) {
                column = getSpecialColumn(name, d.getReferenceTable(), f.isInSelect());
            } else {
                propertyInfo = model.getPropertyInfo(name);
                if (propertyInfo != null) {
                    if (!propertyInfo.propertyType.isArray()) {
                        final Table table = database.getTable(propertyInfo.fragmentName);
                        column = table.getColumn(propertyInfo.fragmentKey);
                    } else {
                        final Table table = database.getTable(propertyInfo.fragmentName);
                        column = table.getColumn(Model.COLL_TABLE_VALUE_KEY);
                    }
                }
            }
            f.setColumn(column);
            f.setModelProperty(propertyInfo);
        }
    }

    public void buildTableKeySet(final DocumentData d) {

        final TableKeySet tks = new TableKeySet(d.getKey(), d.getReferenceTableName());
        for (final Field f : d.getFields()) {
            if (f.getColumn() != null
                    && !(f.getModelProperty() != null && f.getModelProperty().propertyType.isArray() && !f.isInSelect())) {
                final TableKey tk = tks.retrieveAndAddIfNotExistTable(f.getColumn().getTable().getPhysicalName());
                f.setTableKey(tk);
            }
        }
        d.setTableKeySet(tks);
    }

    /**
     * Sort Document data by key to avoid an alea in the query generation enable
     * to
     * 
     * @param documentDataMap
     * @return
     */
    private List<DocumentData> getSortedDocumentData(final Map<String, DocumentData> documentDataMap) {

        final List<DocumentData> datas = new ArrayList<DocumentData>(documentDataMap.values());
        Collections.sort(datas, new Comparator<DocumentData>() {

            @Override
            public int compare(final DocumentData data1, final DocumentData data2) {
                return data1.getKey().compareTo(data2.getKey());
            }

        });
        return datas;

    }

    public ClauseParams buildFromClause(final SQLInfo sqlInfo, final Map<String, DocumentData> documentDataMap) {

        final ClauseParams cp = new ClauseParams();

        generateJoinConstraints(documentDataMap, this.hierTable.getColumn(Model.MAIN_KEY));

        final String init = " FROM ";
        final StringBuilder strBuilder = new StringBuilder(init);

        // FROM hierarchy _H
        // JOIN proxies ON _H.id = proxies.id -- proxy full join
        // JOIN hierarchy ON hierarchy.id = proxies.targetid -- proxy full join

        for (final DocumentData docData : getSortedDocumentData(documentDataMap)) {
            if (!docData.isSubquery()) {
                final TableKeySet tks = docData.getTableKeySet();
                final TableKey tableKey = tks.retrieveReferenceTable();
                strBuilder.append(tableKey.getTableName()).append(" ").append(tableKey.getKey()).append(" ");
                final List<Join> joins = new ArrayList<Join>(docData.getJoins());
                Collections.sort(joins);
                for (final Join join : joins) {
                    String joinStr = join.toSql(sqlInfo.dialect);
                    // remove quote around alias
                    joinStr = joinStr.replaceAll("\"", "");
                    strBuilder.append(joinStr).append(" ");
                    if (join.tableParam != null) {
                        cp.getParams().add(join.tableParam);
                    }
                }

                strBuilder.append(COMMA_SEP);
            }
        }

        if (strBuilder.length() > init.length()) {
            // remove the last ", "
            cp.setClause(strBuilder.substring(0, strBuilder.length() - COMMA_SEP.length()));
        } else {
            cp.setClause("");
        }

        return cp;
    }

    public ClauseParams buildFromClauseComplForSubQuery(final SQLInfo sqlInfo,
            final Map<String, DocumentData> documentDataMap) {

        final ClauseParams cp = new ClauseParams();

        final StringBuilder strBuilder = new StringBuilder();

        for (final DocumentData docData : getSortedDocumentData(documentDataMap)) {
            if (docData.isSubquery()) {
                final List<Join> joins = new ArrayList<Join>(docData.getJoins());
                Collections.sort(joins);
                for (final Join join : joins) {
                    String joinStr = join.toSql(sqlInfo.dialect);
                    // remove quote around alias
                    joinStr = joinStr.replaceAll("\"", "");
                    strBuilder.append(joinStr).append(" ");
                    if (join.tableParam != null) {
                        cp.getParams().add(join.tableParam);
                    }
                }

                strBuilder.append(COMMA_SEP);
            }
        }

        if (strBuilder.length() > 0) {
            // remove the last ", "
            cp.setClause(strBuilder.substring(0, strBuilder.length() - COMMA_SEP.length()));
            return cp;
        } else {
            return null;
        }
    }

    /**
     * Build the constraint for document types and between table linked to the
     * different schemas
     * 
     * @param documentDataMap
     * @return
     */
    public ClauseParams buildTypeConstraints(final Map<String, DocumentData> documentDataMap) {

        final Column primaryTypeCol = hierTable.getColumn(Model.MAIN_PRIMARY_TYPE_KEY);
        final Column propertyCol = hierTable.getColumn(Model.HIER_CHILD_ISPROPERTY_KEY);
        final String hierTableName = hierTable.getPhysicalName();
        final String primaryTypeColName = primaryTypeCol.getQuotedName();
        final String propertyColName = propertyCol.getQuotedName();

        final ClauseParams cp = new ClauseParams();

        final StringBuilder strBuilder = new StringBuilder();
        // CREATE CONSTRAINT ON DOCUMENT TYPES
        for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
            final DocumentData dd = e.getValue();

            // pas de test sur le primarytype si subtypes est vide et la table
            // de reference n'est pas hierarchy :
            // dans ce cas le fait que l'id appartiennent a la table de
            // reference
            // valide le type de document
            if (!dd.isSubquery() && !(dd.getSubTypes().isEmpty() && !hasHierarchyAsReferenceTable(dd))
                    && !hasProxyAsReferenceTable(dd)) {

                final TableKey tk = dd.getTableKeySet().retrieveAndAddIfNotExistTable(hierTableName);
                strBuilder.append(tk.getKey()).append('.');
                if (!dd.getSubTypes().isEmpty()) {
                    // test le primary type
                    strBuilder.append(primaryTypeColName).append(" " + SQL.IN + " ( ");

                    for (final Iterator<String> it = dd.getSubTypes().iterator(); it.hasNext();) {
                        cp.getParams().add(it.next());
                        strBuilder.append("?");
                        if (it.hasNext()) {
                            strBuilder.append(", ");
                        }
                    }
                    strBuilder.append(" )");
                } else {
                    // pas de test sur le primary type
                    // cas d'un select * from Document
                    // par contre test l'attribut IS_PROPERTY pour ne pas
                    // inclure
                    // les types complexes
                    String boolstr = "false";
                    if (boolAsInt) {
                        boolstr = "0";
                    }
                    strBuilder.append(propertyColName).append(" = ").append(boolstr);

                }
                strBuilder.append(AND_SEP);
            }

        }
        if (strBuilder.length() == 0) {
            cp.setClause("");
        } else {
            cp.setClause(strBuilder.substring(0, strBuilder.length() - AND_SEP.length()));
        }
        return cp;
    }

    private boolean hasProxyAsReferenceTable(final DocumentData dd) {
        return dd.getType().toString().equals("Proxy");
    }

    public void generateJoinConstraints(final Map<String, DocumentData> documentDataMap, final Column column) {
        // CREATE CONSTRAINTS BETWEEN TABLE FOR THE DIFFERENT SCHEMA

        final String attr = "." + column.getQuotedName();
        for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
            if (!e.getValue().isSubquery()) {
                final TableKeySet tks = e.getValue().getTableKeySet();
                final TableKey tkRef = tks.retrieveReferenceTable();
                for (final TableKey tk : tks.getTableKeySet()) {
                    if (!tk.getKey().equals(tkRef.getKey())) {
                        final Join j = new Join(Join.LEFT, tk.getTableName(), tk.getKey(), null, tk.getKey() + attr,
                                tkRef.getKey() + attr);
                        e.getValue().addJoin(j);
                    }
                }
            }
        }
    }

    protected Column getSpecialColumn(final String name, final Table referenceTable, final boolean isInSelect) {
        final Table dataHierTable = hierTable;
        if (NXQL.ECM_PRIMARYTYPE.equals(name)) {
            return dataHierTable.getColumn(Model.MAIN_PRIMARY_TYPE_KEY);
        }
        if (NXQL.ECM_MIXINTYPE.equals(name)) {
            return dataHierTable.getColumn(Model.MAIN_MIXIN_TYPES_KEY);
        }
        if (NXQL.ECM_UUID.equals(name)) {
            // return dataHierTable.getColumn(Model.MAIN_KEY);
            // peut etre accéder sans la table hierarchy
            return referenceTable.getColumn(Model.MAIN_KEY);
        }
        if (NXQL.ECM_NAME.equals(name)) {
            return dataHierTable.getColumn(Model.HIER_CHILD_NAME_KEY);
        }
        if (NXQL.ECM_POS.equals(name)) {
            return dataHierTable.getColumn(Model.HIER_CHILD_POS_KEY);
        }
        if (NXQL.ECM_PARENTID.equals(name)) {
            return dataHierTable.getColumn(Model.HIER_PARENT_KEY);
        }
        if (NXQL.ECM_LIFECYCLESTATE.equals(name)) {
            return database.getTable(Model.MISC_TABLE_NAME).getColumn(Model.MISC_LIFECYCLE_STATE_KEY);
        }
        if (NXQL.ECM_FULLTEXT_JOBID.equals(name)) {
            return database.getTable(Model.FULLTEXT_TABLE_NAME).getColumn(Model.FULLTEXT_JOBID_KEY);
        }
        // if (name.startsWith(NXQL.ECM_FULLTEXT)) {
        // throw new QueryParseException(NXQL.ECM_FULLTEXT
        // + " must be used as left-hand operand");
        // }
        if (NXQL.ECM_VERSIONLABEL.equals(name)) {
            return database.getTable(Model.VERSION_TABLE_NAME).getColumn(Model.VERSION_LABEL_KEY);
        }
        if (NXQL.ECM_LOCK_OWNER.equals(name)) {
            return database.getTable(Model.LOCK_TABLE_NAME).getColumn(Model.LOCK_OWNER_KEY);
        }
        if (NXQL.ECM_LOCK_CREATED.equals(name)) {
            return database.getTable(Model.LOCK_TABLE_NAME).getColumn(Model.LOCK_CREATED_KEY);
        }

        if (NXQL.ECM_ISVERSION.equals(name) || NXQL.ECM_ISVERSION_OLD.equals(name)) {
            if (isInSelect) {
                throw new QueryParseException(NXQL.ECM_ISVERSION + " cannot be used in SELECT");
            }
            return database.getTable(Model.VERSION_TABLE_NAME).getColumn(Model.MAIN_KEY);
        }

        if (Model.VERSION_VERSIONABLE_PROP.equals(name)) {
            if (isInSelect) {
                throw new QueryParseException(Model.VERSION_VERSIONABLE_PROP + " cannot be used in SELECT");
            }
            return database.getTable(Model.VERSION_TABLE_NAME).getColumn(Model.VERSION_VERSIONABLE_KEY);
        }

        if (NXQL.ECM_PROXY_TARGETID.equals(name)) {
            return database.getTable(Model.PROXY_TABLE_NAME).getColumn(Model.PROXY_TARGET_KEY);
        }

        return null;
    }

    protected Set<String> resolveFromKeyList(final SFromKeyList typeKeyList) {

        Set<String> primaryTypes = null;
        for (final SFromKey key : typeKeyList.getKeylist()) {
            Set<String> subtypes;
            if (key.isFacet()) {
                if (!getConfigService().isMixinTypeGlobalToTypes(key.getName())) {
                    throw new QueryParseException("Facet : [" + key.getName() + "] must be global");
                }
                subtypes = model.getMixinDocumentTypes(key.getName());
                if (subtypes == null) {
                    throw new QueryParseException("Unknown facet : [" + key.getName() + "]");
                } else if (subtypes.isEmpty()) {
                    throw new QueryParseException("facet associated to no types : [" + key.getName() + "]");
                }
            } else if (key.isStrict()) {
                if (!model.getDocumentTypes().contains(key.getName())) {
                    throw new QueryParseException("Unknown type : [" + key.getName() + "]");
                }
                subtypes = Collections.singleton(key.getName());
            } else {
                if (UFNXQLQueryMaker.TYPE_PROXY.equalsIgnoreCase(key.getName())) {
                    subtypes = Collections.<String> emptySet();
                } else {
                    subtypes = model.getDocumentSubTypes(key.getName());
                    if (subtypes == null) {
                        throw new QueryParseException("Unknown type : [" + key.getName() + "]");
                    }
                }
            }
            switch (key.getOperator()) {
                case AND:
                    primaryTypes.retainAll(subtypes);
                    break;
                case NOT:
                    primaryTypes.removeAll(subtypes);
                    break;
                case OR:
                    primaryTypes.addAll(subtypes);
                    break;
                case NONE:
                    primaryTypes = new HashSet<String>(subtypes);
                    break;
                default:
                    throw new QueryParseException("No support for this operator " + key.getOperator());
            }
        }

        return primaryTypes;
    }

    /**
     * retrieve the primarytype to check according the type of document
     * 
     * For Document return an empty list then the primarytype will not be
     * checked
     * 
     * if the document type
     * 
     * stocke les info dans l'objet documentData
     * 
     * @param d
     */
    protected void retrieveTypeForDocument(final DocumentData d) {
        if (d.isSubquery()) {
            return;
        }
        final SFromKeyList typeKeyList = d.getType();

        final Set<String> primaryTypes = resolveFromKeyList(typeKeyList);

        if (primaryTypes.size() == 1) {
            final String[] types = primaryTypes.toArray(new String[1]);

            final String schema = getConfigService().getSchemaForType(types[0]);
            if (schema != null) {
                final Table table = database.getTable(schema);
                if (table == null) {
                    throw new QueryParseException("No table for schema named [" + schema + "]");
                }
                d.setReferenceTable(table);
                d.setSubTypes(Collections.<String> emptySet());
                return;
            }

            if (NXQLQueryMaker.TYPE_DOCUMENT.equalsIgnoreCase(types[0])
                    || UFNXQLQueryMaker.TYPE_PROXY.equalsIgnoreCase(types[0])) {
                d.setReferenceTable(hierTable);
                d.setSubTypes(Collections.<String> emptySet());
                return;
            }
        }

        d.setReferenceTable(hierTable);
        d.setSubTypes(primaryTypes);

    }

    protected boolean hasHierarchyAsReferenceTable(final DocumentData d) {
        final Table ref = d.getReferenceTable();
        if (ref == null) {
            throw new IllegalArgumentException("Object DocumentData should have a reference table");
        }
        return hierTable.getPhysicalName().equals(ref.getPhysicalName());
    }

    public int getUsedParams() {
        return usedParams;
    }

    public UFNXQLInfos getUnfxqlInfos() {
        return ufnxqlInfos;
    }

    private FNXQLConfigService getConfigService() {
        if (configService == null) {
            try {
                configService = ServiceUtil.getService(FNXQLConfigService.class);
            } catch (final NuxeoException e) {
                LOG.error("Failed to retrieve FNXQLConfigService", e);
                throw new QueryParseException("Failed to retrieve FNXQLConfigService", e);
            }
        }
        return configService;
    }

}
