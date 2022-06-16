package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;

/**
 * Représente un critère de sélection, qui peut lui même être une liste de critères de sélection et leur opérateur.
 * 
 * @author fmh
 */
public class WhereCriterion {
	private static final String OPENING_BRACKET = "(";

	private static final String CLOSING_BRACKET = ")";

	private static final String SPACER = " ";

	private static final String DUMMY_FALSE = "0 = 1";

	private QueryOperatorEnum operator;

	private List<Object> criteria;

	private List<Object> params;

	/**
	 * Construit le critère de sélection à partir d'une liste de String ou de WhereCriterion, utilisant l'opérateur
	 * booléen ET.
	 * 
	 */
	public WhereCriterion() {
		this(QueryOperatorEnum.AND);
	}

	/**
	 * Constructeur par copie.
	 * 
	 * @param criterion
	 *            Critère de sélection à copier.
	 */
	public WhereCriterion(WhereCriterion criterion) {
		this.operator = criterion.getOperator();
		this.criteria = new ArrayList<>();

		for (Object whereCriterion : criterion.getCriteria()) {
			if (whereCriterion instanceof WhereCriterion) {
				criteria.add(new WhereCriterion((WhereCriterion) whereCriterion));
			} else if (whereCriterion instanceof String) {
				criteria.add(whereCriterion);
			}
		}

		if (criterion.params != null && !criterion.params.isEmpty()) {
			this.params = new ArrayList<>(criterion.params);
		}
	}

	/**
	 * Construit le critère de sélection à partir d'une liste de String ou de WhereCriterion, utilisant l'opérateur
	 * booléen spécifié.
	 * 
	 * @param operator
	 *            Opérateur séparant les critères listés.
	 */
	public WhereCriterion(QueryOperatorEnum operator) {
		this(operator, new Object[0]);
	}

	/**
	 * Construit le critère de sélection à partir d'une liste de String ou de WhereCriterion, utilisant l'opérateur
	 * booléen ET.
	 * 
	 * @param criteria
	 *            Liste des critères sous forme de chaîne de caractères ou de WhereCriterion.
	 */
	public WhereCriterion(Object... criteria) {
		this(QueryOperatorEnum.AND, criteria);
	}

	/**
	 * Construit le critère de sélection à partir d'une liste de String ou de WhereCriterion, utilisant l'opérateur
	 * booléen spécifié.
	 * 
	 * @param operator
	 *            Opérateur séparant les critères listés.
	 * @param criteria
	 *            Liste des critères sous forme de chaîne de caractères ou de WhereCriterion.
	 */
	public WhereCriterion(QueryOperatorEnum operator, Object... criteria) {
		this.operator = operator;
		this.criteria = new ArrayList<>(Arrays.asList(criteria));
	}

	/**
	 * Ajoute un critère de recherche à la liste des critères.
	 * 
	 * @param criterion
	 *            Critère à ajouter.
	 */
	public void addCriterion(Object criterion) {
		criteria.add(criterion);
	}

	/**
	 * Ajoute un critère IN à partir de la valeur à tester et de la liste des valeurs possibles.
	 * 
	 * @param field
	 *            Valeur à tester.
	 * @param list
	 *            Liste des valeurs possibles.
	 */
	public void addIn(String field, Collection<?> list) {
		if (list == null || list.isEmpty()) {
			criteria.add(DUMMY_FALSE);
			return;
		}

		StringBuilder strBuilder = new StringBuilder(field);
		List<String> marks = new ArrayList<>();

		strBuilder.append(" IN (");
		for (Object elem : list) {
			marks.add("?");
			bind(elem);
		}
		strBuilder.append(StringUtil.join(marks, ", "));
		strBuilder.append(")");

		criteria.add(strBuilder.toString());
	}

	/**
	 * Ajoute un paramètre au critère de sélection.
	 * 
	 * @param param
	 *            Paramètre de la requête.
	 */
	public void bind(Object param) {
		if (params == null) {
			params = new ArrayList<>();
		}
		params.add(param);
	}

	/**
	 * Ajoute une liste de paramètres au critère de sélection.
	 * 
	 * @param param
	 *            Liste de paramètres de la requête.
	 */
	public void bindAll(List<Object> param) {
		if (params == null) {
			params = new ArrayList<>();
		}
		params.addAll(param);
	}

	/**
	 * Retourne la liste des paramètres du critère de sélection (et de ses descendants).
	 * 
	 * @return Liste des paramètres.
	 */
	public List<Object> getParams() {
		List<Object> allParams;
		if (params == null) {
			allParams = new ArrayList<>();
		} else {
			allParams = new ArrayList<>(params);
		}

		for (Object criterion : criteria) {
			if (criterion instanceof WhereCriterion) {
				allParams.addAll(((WhereCriterion) criterion).getParams());
			}
		}
		return allParams;
	}

	/**
	 * Génère le critère de sélection, récursivement.
	 * 
	 * @return null s'il n'y a aucun critère.
	 */
	public String where() {
		if (criteria.isEmpty()) {
			return null;
		}

		StringBuilder where = new StringBuilder();
		String operator = this.operator.getOperator();

		for (Object criterion : criteria) {
			if (criterion instanceof WhereCriterion) {
				appendWhereCriterionChild(where, operator, (WhereCriterion) criterion);
			} else if (criterion instanceof String) {
				appendStringChild(where, operator, (String) criterion);
			}
		}

		return where.toString();
	}

	/**
	 * Retourne le critère de sélection au format chaîne de caractères.
	 */
	@Override
	public String toString() {
		String where = where();

		if (where == null) {
			return "";
		}

		return where;
	}

	protected QueryOperatorEnum getOperator() {
		return operator;
	}

	protected List<Object> getCriteria() {
		return criteria;
	}

	private void appendWhereCriterionChild(StringBuilder builder, String operator, WhereCriterion child) {
		String childStr = child.where();

		if (childStr != null) {
			appendOperator(builder, operator);
			builder.append(OPENING_BRACKET);
			builder.append(childStr);
			builder.append(CLOSING_BRACKET);
		}
	}

	private void appendStringChild(StringBuilder builder, String operator, String child) {
		appendOperator(builder, operator);
		builder.append(child);
	}

	private void appendOperator(StringBuilder builder, String operator) {
		if (builder.length() > 0) {
			builder.append(SPACER);
			builder.append(operator);
			builder.append(SPACER);
		}
	}
}
