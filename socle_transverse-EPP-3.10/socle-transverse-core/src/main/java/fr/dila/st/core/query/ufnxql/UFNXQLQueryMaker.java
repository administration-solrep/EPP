package fr.dila.st.core.query.ufnxql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.storage.sql.Model;
import org.nuxeo.ecm.core.storage.sql.ModelProperty;
import org.nuxeo.ecm.core.storage.sql.Session.PathResolver;
import org.nuxeo.ecm.core.storage.sql.jdbc.NXQLQueryMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.Query;
import org.nuxeo.ecm.core.storage.sql.jdbc.QueryMaker.QueryMakerException;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.ColumnMapMaker;
import org.nuxeo.ecm.core.storage.sql.jdbc.SQLInfo.SQLInfoSelect;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Column;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Database;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Join;
import org.nuxeo.ecm.core.storage.sql.jdbc.db.Table;

import fr.dila.st.core.query.ufnxql.parser.UFNXQLQueryParser;

/**
 * Genere une requete SQL depuis une requete UFNXQL
 * 
 * @author spesnel
 * 
 */
public class UFNXQLQueryMaker {

	private static final Log			LOG								= LogFactory.getLog(UFNXQLQueryMaker.class);

	private Model						model;
	private Database					database;
	private int							usedParams						= 0;
	public boolean						usedToGenerateInternalQueryPart	= false;

	private Table						hierTable						= null;

	/**
	 * Indique pour certain type un schema dont la table associé servira de table de reference pour trouver les
	 * documents au lieu d'utiliser la table hierarchy
	 */
	private Map<String, String>			mapTypeSchema					= null;

	private Map<String, DocumentData>	documentDataMapContext			= null;

	/**
	 * Un objet pour passer des informations u maker au builder.
	 * 
	 */
	private final UFNXQLInfos			ufnxqlInfos;
	private final Map<String, Integer>	mapPoidsSchemaForJoin;

	public UFNXQLQueryMaker(final Map<String, String> mapTypeSchema,
			final Map<String, DocumentData> documentDataMapContext, final Map<String, Integer> mapPoidsSchemaForJoin) {
		this.mapTypeSchema = mapTypeSchema;
		this.documentDataMapContext = documentDataMapContext;
		this.ufnxqlInfos = new UFNXQLInfos();
		this.mapPoidsSchemaForJoin = mapPoidsSchemaForJoin;
	}

	public UFNXQLQueryMaker(final Map<String, String> mapTypeSchema, final Map<String, Integer> mapPoidsSchemaForJoin) {
		this(mapTypeSchema, null, mapPoidsSchemaForJoin);
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

		// recuperation des document manipules et des champs accédés
		final QueryAnalyzer analyze = new QueryAnalyzer(documentDataMapContext);
		sqlQuery.accept(analyze);

		final Map<String, DocumentData> documentDataMap = analyze.getDocumentDataMap();

		//
		for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
			final DocumentData docData = e.getValue();

			// recherche des primary type pour les document accédé
			retrieveTypeForDocument(docData);
			// recherche des colonnes accédés nécessaire pour attribute de schema présent dans la requete
			retrieveColumnForField(docData);

			// construit la liste des tables nécessaires et leur crée un alias
			buildTableKeySet(docData);

			// trace
			if (LOG.isDebugEnabled()) {
				final StringBuilder strBuilder = new StringBuilder("key[").append(e.getKey()).append("]->")
						.append(docData.getType()).append("-->(");
				for (final String st : docData.getSubTypes()) {
					strBuilder.append(st).append(", ");
				}
				strBuilder.append(")");
				LOG.debug(strBuilder.toString());

				for (final Field f : docData.getFields()) {
					LOG.debug("  -->[" + f.getName() + "-->" + f.toSql() + "]");
				}
				for (final TableKey tk : docData.getTableKeySet().getTableKeySet()) {
					LOG.debug("[" + tk.getKey() + ":" + tk.getTableName() + "]");
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
		// les objets documentData peuvent être mise à jour avec de nouvelle table
		// liée à des expression NXQL (ecm:xxx)
		final QueryBuilder queryBuilder = new QueryBuilder(documentDataMap, fieldMap, params, model, sqlInfo,
				queryFilter, mapTypeSchema, documentDataMapContext, mapPoidsSchemaForJoin);
		sqlQuery.accept(queryBuilder);

		// construit la partie de requete contenant la contrainte
		// sur les type de document (PRIMARYTYPE)
		final ClauseParams typeConstraints = buildTypeConstraints(documentDataMap);

		// construit la partie FROM
		final ClauseParams fromclause = buildFromClause(documentDataMap);

		// recupere la partie WHERE
		final ClauseParams whereClause = queryBuilder.getWhereClause();

		if (LOG.isDebugEnabled()) {
			LOG.debug("WHERE=[" + whereClause.clause + "]");
			LOG.debug("ORDER=[" + queryBuilder.orderPart.toString() + "]");
			LOG.debug("SELECT=[" + queryBuilder.selectPart + "]");
			LOG.debug("GROUP=[" + queryBuilder.groupPart + "]");
			LOG.debug("FROM=[" + fromclause.clause + "]");
		}

		usedParams = queryBuilder.nbParamsRequired;
		if (!usedToGenerateInternalQueryPart) {
			// test que tous les paramètres données en arguments sont utilisé
			// par la requete
			int paramLength = params == null ? 0 : params.length;
			if (usedParams != paramLength) {
				throw new QueryMakerException(
						"Incorrect params number : requested=" + usedParams + ", available=" + paramLength);
			}
		}

		// assemblage de la requete SQL
		final StringBuilder generated = new StringBuilder(SQL.SELECT).append(" ").append(queryBuilder.selectPart)
				.append(fromclause.clause);

		if (whereClause.clause.length() > 0 || typeConstraints.clause.length() > 0) {
			generated.append(" ").append(SQL.WHERE).append(" ");
		}

		generated.append(typeConstraints.clause);

		if (whereClause.clause.length() > 0 && typeConstraints.clause.length() > 0) {
			// un AND pour lié contrainte de type et contrainte du where
			// nécessaire seulement si l'un et l'autre existe
			generated.append(" ").append(SQL.AND).append(" ");
		}
		generated.append(whereClause.clause);

		if (queryBuilder.recStartWithPart.length() > 0) {
			generated.append(queryBuilder.recStartWithPart);
		}

		if (queryBuilder.groupPart.length() > 0) {
			generated.append(" ").append(SQL.GROUPBY).append(" ").append(queryBuilder.groupPart);
		}

		if (queryBuilder.orderPart.length() > 0) {
			generated.append(" ").append(SQL.ORDERBY).append(" ").append(queryBuilder.orderPart.toString());
		}

		final String generatedQueryStr = generated.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug("GENERATED=[" + generatedQueryStr + "]");

			final StringBuilder paramStr = new StringBuilder();
			paramStr.append("FROM PARAMS :");
			for (final Serializable s : fromclause.params) {
				paramStr.append("[").append(s).append("] ");
			}
			paramStr.append("\n");
			paramStr.append("CONSTRAINT PARAMS :");
			for (final Serializable s : typeConstraints.params) {
				paramStr.append("[").append(s).append("] ");
			}
			paramStr.append("\n");
			paramStr.append("WHERE PARAMS :");
			for (final Serializable s : whereClause.params) {
				paramStr.append("[").append(s).append("] ");
			}
			paramStr.append("REC START PARAMS :");
			for (final Serializable s : queryBuilder.recStartWithParams) {
				paramStr.append("[").append(s).append("] ");
			}
			LOG.debug(paramStr);
		}

		// creation de l'objet Query
		final Query generatedQuery = new Query();
		final ColumnMapMaker mapMaker = new ColumnMapMaker(queryBuilder.whatColumns, queryBuilder.whatKeys);

		generatedQuery.selectInfo = new SQLInfoSelect(generatedQueryStr, mapMaker);
		generatedQuery.selectParams = new ArrayList<Serializable>();
		generatedQuery.selectParams.addAll(fromclause.params);
		generatedQuery.selectParams.addAll(typeConstraints.params);
		generatedQuery.selectParams.addAll(whereClause.params);
		generatedQuery.selectParams.addAll(queryBuilder.recStartWithParams);

		return generatedQuery;
	}

	public void retrieveColumnForField(final DocumentData docData) {
		for (final Field f : docData.getFields()) {

			final String name = f.getName();
			Column column = null;
			ModelProperty propertyInfo = null;
			if (name.startsWith(NXQL.ECM_PREFIX)) {
				column = getSpecialColumn(name, docData.getReferenceTable(), f.isInSelect());
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

	public void buildTableKeySet(final DocumentData docData) {

		final TableKeySet tks = new TableKeySet(docData.getKey(), docData.getReferenceTableName());
		for (final Field f : docData.getFields()) {
			if (f.getColumn() != null) {
				if (!(f.getModelProperty() != null && f.getModelProperty().propertyType.isArray() && !f.isInSelect())) {
					final TableKey tk = tks.retrieveAndAddIfNotExistTable(f.getColumn().getTable().getPhysicalName());
					f.setTableKey(tk);
				}
			}
		}
		docData.setTableKeySet(tks);
	}

	/**
	 * Sort Document data by key to avoid an alea in the query generation enable to
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

	public ClauseParams buildFromClause(final Map<String, DocumentData> documentDataMap) {

		final ClauseParams clauseParams = new ClauseParams();

		generateJoinConstraints(documentDataMap);
		final String SEPARATOR = ", ";
		final StringBuilder strBuilder = new StringBuilder(" FROM ");

		// use sorted list to always have the same generated query
		for (final DocumentData docData : getSortedDocumentData(documentDataMap)) {
			final TableKeySet tableKeySet = docData.getTableKeySet();
			final TableKey tableKey = tableKeySet.retrieveReferenceTable();
			final String tdecl = tableKey.getTableName() + " " + tableKey.getKey();
			strBuilder.append(tdecl).append(" ");

			for (final Join j : docData.getOrderedJoins(mapPoidsSchemaForJoin)) {
				strBuilder.append(j).append(" ");
				if (j.tableParam != null) {
					clauseParams.params.add(j.tableParam);
				}
			}

			strBuilder.append(SEPARATOR);
		}

		// remove the last ", "
		clauseParams.clause = strBuilder.substring(0, strBuilder.length() - SEPARATOR.length());

		return clauseParams;
	}

	/**
	 * Build the constraint for document types and between table linked to the different schemas
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

		final ClauseParams clauseParams = new ClauseParams();

		final String SEPARATOR = " " + SQL.AND + " ";

		final StringBuilder strBuilder = new StringBuilder();
		// CREATE CONSTRAINT ON DOCUMENT TYPES
		for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
			final DocumentData docData = e.getValue();

			if (!docData.getSubTypes().isEmpty() || hasHierarchyAsReferenceTable(docData)) {

				final TableKey tableKey = docData.getTableKeySet().retrieveAndAddIfNotExistTable(hierTableName);
				strBuilder.append(tableKey.getKey()).append('.');
				if (!docData.getSubTypes().isEmpty()) {
					// test le primary type
					strBuilder.append(primaryTypeColName).append(" " + SQL.IN + " ( ");

					for (final Iterator<String> it = docData.getSubTypes().iterator(); it.hasNext();) {
						clauseParams.params.add(it.next());
						strBuilder.append("?");
						if (it.hasNext()) {
							strBuilder.append(", ");
						}
					}
					strBuilder.append(" )");
				} else {
					// pas de test sur le primary type
					// cas d'un select * from Document
					// par contre test l'attribut IS_PROPERTY pour ne pas inclure
					// les types complexes
					strBuilder.append(propertyColName).append(" = 0");
				}
				strBuilder.append(SEPARATOR);
			}

		}
		if (strBuilder.length() == 0) {
			clauseParams.clause = "";
		} else {
			clauseParams.clause = strBuilder.substring(0, strBuilder.length() - SEPARATOR.length());
		}
		return clauseParams;
	}

	public void generateJoinConstraints(final Map<String, DocumentData> documentDataMap) {
		// CREATE CONSTRAINTS BETWEEN TABLE FOR THE DIFFERENT SCHEMA

		final String attr = ".ID";
		for (final Entry<String, DocumentData> e : documentDataMap.entrySet()) {
			final TableKeySet tks = e.getValue().getTableKeySet();
			final TableKey tkRef = tks.retrieveReferenceTable();
			for (final TableKey tk : tks.getTableKeySet()) {
				if (!tk.getKey().equals(tkRef.getKey())) {
					final Join join = new Join(Join.LEFT, tk.getTableName(), tk.getKey(), null, tk.getKey() + attr,
							tkRef.getKey() + attr);
					e.getValue().addJoin(join);
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
			// toplevel ones have been extracted by the analyzer
			throw new QueryMakerException("Cannot use non-toplevel " + name + " in query");
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
		// throw new QueryMakerException(NXQL.ECM_FULLTEXT
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

		if (NXQL.ECM_ISVERSION.equals(name)) {
			if (isInSelect) {
				throw new QueryMakerException(NXQL.ECM_ISVERSION + " cannot be used in SELECT");
			}
			return database.getTable(Model.VERSION_TABLE_NAME).getColumn(Model.MAIN_KEY);
		}

		return null;
	}

	/**
	 * retrieve the primarytype to check according the type of document
	 * 
	 * For Document return an empty list then the primarytype will not be checked
	 * 
	 * if the document type
	 * 
	 * stocke les info dans l'objet documentData
	 * 
	 * @param docData
	 */
	protected void retrieveTypeForDocument(final DocumentData docData) {
		final String typeName = docData.getType();

		if (mapTypeSchema != null) {
			final String schema = mapTypeSchema.get(typeName);
			if (schema != null) {
				final Table table = database.getTable(schema);
				if (table == null) {
					throw new QueryMakerException("No table for schema named [" + schema + "]");
				}
				docData.setReferenceTable(table);
				final Set<String> subTypes = Collections.emptySet();
				docData.setSubTypes(subTypes);
				return;
			}
		}

		Set<String> subTypes = null;

		if (NXQLQueryMaker.TYPE_DOCUMENT.equalsIgnoreCase(typeName)) {
			subTypes = Collections.emptySet();
		} else {
			subTypes = model.getDocumentSubTypes(typeName);
			if (subTypes == null) {
				throw new QueryMakerException("Unknown type: " + typeName);
			}
		}

		docData.setReferenceTable(hierTable);
		docData.setSubTypes(subTypes);

	}

	protected boolean hasHierarchyAsReferenceTable(final DocumentData docData) {
		final Table ref = docData.getReferenceTable();
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

}
