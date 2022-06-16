package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



/**
 * Construit une requête UFNXQL à partir de listes de champs / tables / critères de sélection / critères de tri / ...
 * 
 * @author fmh
 */
public class UFNXQLQueryBuilder extends AbstractQueryBuilder {
	private static final String LIST_SEPARATOR = ", ";

	private final List<UFNXQLQueryBuilder> union = new ArrayList<>();
	
	/**
	 * Constructeur par défaut.
	 */
	public UFNXQLQueryBuilder() {
		super();
	}

	/**
	 * Constructeur par copie.
	 * 
	 * @param queryBuilder
	 *            UFNXQLQueryBuilder à copier.
	 */
	public UFNXQLQueryBuilder(UFNXQLQueryBuilder queryBuilder) {
		super(queryBuilder);
	}
	
	/**
	 * Constructeur pour union.
	 * 
	 * @param queryBuilderList
	 *            Liste de UFNXQLQueryBuilder à regrouper par union.
	 */
	public UFNXQLQueryBuilder(List<UFNXQLQueryBuilder> queryBuilderList) {
		super();
		this.union.addAll(queryBuilderList);
	}
	
	/**
	 * Ajoute une sous-requete et son alias à la requête.
	 * 
	 * @param query contenu de la sous requête
	 *            (ex : SELECT d.ecm:uuid AS id FROM Document AS d).
	 * @param alias
	 *            Alias de la table (ex : doc1).
	 * @return la requete
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T fromQuery(UFNXQLQueryBuilder query, String alias) {
		getFroms().add(new SubQueryElement(query, alias));
		return (T) this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String query() {
		if(union.isEmpty()){
			return querySimple();
		} else {
			return queryUnion();
		}
	}
	
	private String queryUnion() {
		StringBuilder query = new StringBuilder();
		boolean first = true;
		for(UFNXQLQueryBuilder qbuilder : union){
			if(first){
				first = false;
			} else {
				query.append(" UNION ");		
			}
			query.append("(").append(qbuilder.query()).append(")");
		}
		return query.toString();
	}
	
	private String querySimple() {
		StringBuilder query = new StringBuilder();
		List<AliasElement> selectRows = getSelectRows();
		boolean distinct = isDistinct();
		List<AliasElement> tables = getFroms();
		WhereCriterion where = getWhere();
		List<String> groups = getGroups();
		List<OrderDirective> orders = getOrders();

		checkParams(selectRows, tables);

		

		appendSelect(query, selectRows, distinct);
		appendTables(query, tables);

		if (where != null) {
			appendWhere(query, where);
		}

		if (groups != null && !groups.isEmpty()) {
			appendGroupBy(query, groups);
		}

		if (orders != null && !orders.isEmpty()) {
			appendOrderBy(query, orders);
		}
		return query.toString();
	}
	
	@Override
	public Object[] getParams() {
		if(!union.isEmpty()){
			List<Object> params = new ArrayList<>();
			for(UFNXQLQueryBuilder qbuilder : union){
				params.addAll(Arrays.asList(qbuilder.getParams()));
			}
			return params.toArray();
		} else {
			return super.getParams();
		}
	}

	private void checkParams(List<AliasElement> selectRows, List<AliasElement> tables) {
		if (selectRows == null || selectRows.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one field to build the query");
		}

		if (tables == null || tables.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one table to build the query");
		}
	}

	private void appendSelect(StringBuilder query, List<AliasElement> selectRows, boolean distinct) {
		query.append("SELECT ");

		if (distinct) {
			query.append("DISTINCT ");
		}

		query.append(StringUtils.join(selectRows, LIST_SEPARATOR));
	}

	private void appendTables(StringBuilder query, List<AliasElement> tables) {
		query.append(" FROM ");
		query.append(StringUtils.join(tables, LIST_SEPARATOR));
	}

	private void appendWhere(StringBuilder query, WhereCriterion where) {
		String whereClause = where.where();
		if (StringUtils.isNotEmpty(whereClause)) {
			query.append(" WHERE ");
			query.append(whereClause);
		}
	}

	private void appendGroupBy(StringBuilder query, List<String> groups) {
		query.append(" GROUP BY ");
		query.append(StringUtils.join(groups, LIST_SEPARATOR));
	}

	private void appendOrderBy(StringBuilder query, List<OrderDirective> orders) {
		query.append(" ORDER BY ");
		query.append(StringUtils.join(orders, LIST_SEPARATOR));
	}
	
}
