package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

/**
 * Enumération listant les opérateurs utilisables dans les requêtes construites
 * à partir des QueryBuilder.
 * 
 * @author fmh
 */
public enum QueryOperatorEnum {
	AND("AND"), OR("OR");

	private String operator;

	QueryOperatorEnum(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
}
