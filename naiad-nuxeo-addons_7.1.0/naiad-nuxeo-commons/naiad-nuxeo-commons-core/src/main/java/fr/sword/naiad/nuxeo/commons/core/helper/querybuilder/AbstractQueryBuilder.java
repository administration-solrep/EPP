package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Méthodes basiques de création d'une requête.
 * 
 * @author fmh
 */
public abstract class AbstractQueryBuilder {
	/**
	 * Symbole désignant l'ensemble des colonnes.
	 */
	private static final String ALL_COLS = "*";

	/**
	 * Alias par défaut des champs de type COUNT().
	 */
	private static final String COUNT_ALIAS = "count";

	/**
	 * Opérateur par défaut de la clause WHERE.
	 */
	private static final QueryOperatorEnum DEFAULT_QUERY_OPERATOR = QueryOperatorEnum.AND;

	/**
	 * Liste des champs de la clause SELECT.
	 */
	private final List<AliasElement> selectFields;

	/**
	 * Présence ou non du flag DISTINCT.
	 */
	private boolean distinctFlag;

	/**
	 * Liste des tables.
	 */
	private final List<AliasElement> froms;

	/**
	 * Critères de sélection.
	 */
	private WhereCriterion whereCriterion;

	/**
	 * Liste des champs pour le regroupement.
	 */
	private final List<String> groups;

	/**
	 * Liste des directives de tri.
	 */
	private final List<OrderDirective> orders;

	/**
	 * Paramètres de la clause SELECT.
	 */
	private final List<Object> selectParams;

	/**
	 * Paramètres de la clause GROUP BY.
	 */
	private final List<Object> groupParams;

	/**
	 * Paramètres de la clause ORDER BY.
	 */
	private final List<Object> orderParams;

	/**
	 * Constructeur par défaut.
	 */
	public AbstractQueryBuilder() {
		selectFields = new ArrayList<>();
		distinctFlag = false;
		froms = new ArrayList<>();
		whereCriterion = new WhereCriterion(DEFAULT_QUERY_OPERATOR);
		groups = new ArrayList<>();
		orders = new ArrayList<>();
		selectParams = new ArrayList<>();
		groupParams = new ArrayList<>();
		orderParams = new ArrayList<>();
	}

	/**
	 * Constructeur par copie.
	 * 
	 * @param queryBuilder
	 *            AbstractQueryBuilder à copier.
	 */
	public AbstractQueryBuilder(AbstractQueryBuilder queryBuilder) {
		selectFields = new ArrayList<>(queryBuilder.getSelectRows());
		distinctFlag = queryBuilder.isDistinct();
		froms = new ArrayList<>(queryBuilder.getFroms());
		whereCriterion = new WhereCriterion(queryBuilder.getWhere());
		groups = new ArrayList<>(queryBuilder.getGroups());
		orders = new ArrayList<>(queryBuilder.getOrders());
		selectParams = new ArrayList<>(Arrays.asList(queryBuilder.getSelectParams()));
		groupParams = new ArrayList<>(Arrays.asList(queryBuilder.getGroupParams()));
		orderParams = new ArrayList<>(Arrays.asList(queryBuilder.getOrderParams()));
	}

	/**
	 * Ajoute un champ à sélectionner.
	 * 
	 * @param field
	 *            Nom du champ (ex : doc.ecm:uuid).
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder select(String field) {
		return select(field, null);
	}

	/**
	 * Ajoute un champ à sélectionner avec son alias.
	 * 
	 * @param field
	 *            Nom du champ (ex : doc.ecm:uuid).
	 * @param alias
	 *            Alias du champ (ex : id).
	 * @return L'objet courant.
	 */

	@SuppressWarnings("unchecked")
  public <T extends AbstractQueryBuilder> T select(String field, String alias) {
		selectFields.add(new Field(field, alias));
		return (T) this;
	}

	/**
	 * Génère un champ sélectionnant tous les champs de la table (SELECT * FROM ...).
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder selectAll() {
		selectFields.add(new Field(ALL_COLS));
		return this;
	}

	/**
	 * Génère un champ permettant de compter le nombre de lignes (SELECT COUNT(*) AS count FROM ...).
	 * 
	 * Le champ porte l'alias count.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder count() {
		return count(ALL_COLS);
	}

	/**
	 * Génère un champ permettant de compter le nombre de lignes (SELECT COUNT(doc.ecm:uuid) AS count FROM ...), en
	 * précisant le champ à compter.
	 * 
	 * Le champ porte l'alias count.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder count(String field) {
		return count(field, COUNT_ALIAS);
	}

	/**
	 * Génère un champ permettant de compter le nombre de lignes (SELECT COUNT(doc.ecm:uuid) AS nbcols FROM ...), en
	 * précisant le champ à compter et l'alias.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder count(String field, String alias) {
		selectFields.add(new Field("COUNT(" + field + ")", alias));
		return this;
	}

	/**
	 * Ajoute un paramètre à la clause SELECT.
	 * 
	 * @param param
	 *            Paramètre à ajouter.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder bindSelect(Object param) {
		selectParams.add(param);
		return this;
	}

	/**
	 * Retourne un tableau contenant les paramètres de la clause SELECT.
	 * 
	 * @return Tableau des paramètres de la clause SELECT.
	 */
	public Object[] getSelectParams() {
		return selectParams.toArray();
	}

	/**
	 * Supprime un champ à sélectionner de la liste à partir de son alias.
	 * 
	 * Seul le premier champ portant cet alias est supprimé.
	 * 
	 * @param alias
	 *            Alias du champ.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder removeSelectField(String alias) {
		removeFieldByAlias(selectFields, alias);
		return this;
	}

	/**
	 * Vide la liste des champs à sélectionner.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder dropSelectFields() {
		selectFields.clear();
		selectParams.clear();
		return this;
	}

	/**
	 * Définit que la requête doit porter le flag DISTINCT.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder distinct() {
		return distinct(true);
	}

	/**
	 * Définit si oui ou non la requête doit porter le flag DISTINCT.
	 * 
	 * @param distinct
	 *            Présence ou non du flag DISTINCT.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder distinct(boolean distinct) {
		this.distinctFlag = distinct;
		return this;
	}

	/**
	 * Ajoute une table à la requête.
	 * 
	 * @param table
	 *            Nom de la table (ex : Document).
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder from(String table) {
		return from(table, null);
	}

	/**
	 * Ajoute une table et son alias à la requête.
	 * 
	 * @param table
	 *            Nom de la table (ex : Document).
	 * @param alias
	 *            Alias de la table (ex : doc1).
	 * @return la requete
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T from(String table, String alias) {
		froms.add(new Field(table, alias));
		return (T) this;
	}


	/**
	 * Retourne un tableau contenant les paramètres de la clause FROM.
	 * 
	 * @return Tableau des paramètres de la clause FROM.
	 */
	public Object[] getFromParams() {
		List<Object> params = new ArrayList<>();
		for(AliasElement elt : froms){
			Object[] localParams = elt.getParams();
			if(localParams != null && localParams.length > 0){
				params.addAll(Arrays.asList(localParams));
			}
		}
		return params.toArray();
	}

	/**
	 * Supprime une table de la liste à partir de son alias.
	 * 
	 * Seul la première table portant cet alias est supprimée.
	 * 
	 * @param alias
	 *            Alias de la table.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder removeTable(String alias) {
		removeFieldByAlias(froms, alias);
		return this;
	}

	/**
	 * Vide la liste des tables.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder dropTables() {
		froms.clear();
		return this;
	}

	/**
	 * Ajoute une critère de sélection.
	 * 
	 * @param criterion
	 *            Critère de sélection, de type String (ex : doc1.relation:source = doc2.ecm:uuid) ou de type
	 *            WhereCriterion.
	 * @return L'objet courant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T where(Object criterion) {
		whereCriterion.addCriterion(criterion);
		return (T) this;
	}

	public void bindWhere(Object param){
		whereCriterion.bind(param);
	}
	
	/**
	 * Retourne un array contenant les paramètres de la clause WHERE.
	 * 
	 * @return Array des paramètres.
	 */
	public Object[] getWhereParams() {
		return whereCriterion.getParams().toArray();
	}

	/**
	 * Vide les critères de sélection.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder dropWhere() {
		whereCriterion = new WhereCriterion(DEFAULT_QUERY_OPERATOR);
		return this;
	}

	/**
	 * Ajoute un champ à utiliser pour le regroupement des résultats.
	 * 
	 * @param field
	 *            Nom du champ.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder groupBy(String field) {
		groups.add(field);
		return this;
	}

	/**
	 * Ajoute un paramètre à la clause GROUP BY.
	 * 
	 * @param param
	 *            Paramètre à ajouter.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder bindGroup(Object param) {
		groupParams.add(param);
		return this;
	}

	/**
	 * Retourne un tableau contenant les paramètres de la clause GROUP BY.
	 * 
	 * @return Tableau des paramètres de la clause GROUP BY.
	 */
	public Object[] getGroupParams() {
		return groupParams.toArray();
	}

	/**
	 * Vide la liste des champs à utiliser pour le regroupement des résultats.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder dropGroups() {
		groups.clear();
		groupParams.clear();
		return this;
	}

	/**
	 * Ajoute un critère de tri.
	 * 
	 * @param field
	 *            Champ du critère de tri.
	 * @return L'objet courant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T orderBy(String field) {
		return (T) orderBy(field, true);
	}

	/**
	 * Ajoute un critère de tri et son sens.
	 * 
	 * @param field
	 *            Champ du critère de tri.
	 * @param ascendant
	 *            True si le tri doit être ascendant, false sinon.
	 * @return L'objet courant.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractQueryBuilder> T orderBy(String field, boolean ascendant) {
		orders.add(new OrderDirective(field, ascendant));
		return (T) this;
	}

	/**
	 * Ajoute un paramètre à la clause ORDER BY.
	 * 
	 * @param param
	 *            Paramètre à ajouter.
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder bindOrder(Object param) {
		orderParams.add(param);
		return this;
	}

	/**
	 * Retourne un tableau contenant les paramètres de la clause ORDER BY.
	 * 
	 * @return Tableau des paramètres de la clause ORDER BY.
	 */
	public Object[] getOrderParams() {
		return orderParams.toArray();
	}

	/**
	 * Vide la liste des critères de tri.
	 * 
	 * @return L'objet courant.
	 */
	public AbstractQueryBuilder dropOrders() {
		orders.clear();
		orderParams.clear();
		return this;
	}

	/**
	 * Retourne un tableau contenant l'ensemble des paramètres de la requête.
	 * 
	 * @return Tableau des paramètres.
	 */
	public Object[] getParams() {
		List<Object> whereParams = whereCriterion.getParams();
		Object[] fromParams = getFromParams();
		int nbParams = selectParams.size() + fromParams.length + whereParams.size() + groupParams.size()
				+ orderParams.size();
		Object[] params = new Object[nbParams];
		int param = 0;

		for (int i = 0; i < selectParams.size(); ++i) {
			params[param] = selectParams.get(i);
			++param;
		}
		for (int i = 0; i < fromParams.length; ++i) {
			params[param] = fromParams[i];
			++param;
		}
		for (int i = 0; i < whereParams.size(); ++i) {
			params[param] = whereParams.get(i);
			++param;
		}
		for (int i = 0; i < groupParams.size(); ++i) {
			params[param] = groupParams.get(i);
			++param;
		}
		for (int i = 0; i < orderParams.size(); ++i) {
			params[param] = orderParams.get(i);
			++param;
		}

		return params;
	}

	/**
	 * Construit la requête.
	 * 
	 * @return Requête.
	 * @throws IllegalArgumentException
	 *             Si aucun champ de sélection ou aucune table n'a été spécifiée.
	 */
	public abstract String query();

	@Override
	public String toString() {
		return query();
	}

	/**
	 * Retourne la liste des champs de sélection.
	 * 
	 * @return Liste des champs de sélection.
	 */
	protected List<AliasElement> getSelectRows() {
		return selectFields;
	}

	/**
	 * Vérifie si le flag DISTINCT est présent.
	 * 
	 * @return True si le flag DISTINCT est présent, false sinon.
	 */
	protected boolean isDistinct() {
		return distinctFlag;
	}

	/**
	 * Retourne la liste des elements de la clause FROM : tables et sous-requete.
	 * 
	 * @return Liste des tables.
	 */
	protected List<AliasElement> getFroms() {
		return froms;
	}

	/**
	 * Retourne les critères de sélection.
	 * 
	 * @return Critères de sélection.
	 */
	protected WhereCriterion getWhere() {
		return whereCriterion;
	}

	/**
	 * Retourne la liste des critères de regroupement.
	 * 
	 * @return Liste des critères de regroupement.
	 */
	protected List<String> getGroups() {
		return groups;
	}

	/**
	 * Retourne la liste des critères de tri.
	 * 
	 * @return Liste des critères de tri.
	 */
	protected List<OrderDirective> getOrders() {
		return orders;
	}

	private void removeFieldByAlias(List<AliasElement> fields, String alias) {
		for (int i = 0; i < fields.size(); ++i) {
			if (StringUtils.equals(alias, fields.get(i).getAlias())) {
				fields.remove(i);
				return;
			}
		}
	}

	
	
}
