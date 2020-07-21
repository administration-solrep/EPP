package fr.dila.st.core.query.ufnxql;

public final class SQL {

	public static final String	SELECT			= "SELECT";
	public static final String	FROM			= "FROM";
	public static final String	WHERE			= "WHERE";
	public static final String	ORDERBY			= "ORDER BY";
	public static final String	GROUPBY			= "GROUP BY";

	public static final String	AND				= "AND";
	public static final String	OR				= "OR";
	public static final String	AS				= "AS";
	public static final String	LIKE			= "LIKE";
	public static final String	IN				= "IN";
	public static final String	NOT				= "NOT";

	public static final String	EXISTS			= "EXISTS";

	public static final String	STARTWITH		= "START WITH";
	public static final String	CONNECTBYPRIOR	= "CONNECT BY PRIOR";

	public static final String	REC_EXPR		= " " + STARTWITH + " %s " + IN + " ( %s ) " + CONNECTBYPRIOR
														+ " %s = %s";

	/**
	 * Generate recursive expression
	 * 
	 * exemple : START WITH <id> IN (<un ensemble d'id>) CONNECT BY PRIOR id = parentId;
	 * 
	 * genRecExpr(<un ensemble d'id>, <id>, <parentId>)
	 * 
	 * @param clause
	 *            ensemble d'id ou query
	 * @param exprChild
	 * @param exprParent
	 * @return
	 */
	public static String genRecExpr(String clause, String exprChild, String exprParent) {
		return String.format(REC_EXPR, exprChild, clause, exprChild, exprParent);
	}

	/**
	 * return [NOT] EXISTS( ... )
	 * 
	 * @param content
	 *            contenu du exists
	 * @param exist
	 *            si il est faux ajout du NOT devant le exist
	 * @return
	 */
	public static String existPart(String content, boolean exist) {
		StringBuilder strBuilder = new StringBuilder();
		if (!exist) {
			strBuilder.append(NOT).append(" ");
		}
		strBuilder.append(EXISTS).append("(").append(content).append(")");
		return strBuilder.toString();
	}

	/**
	 * utility class
	 */
	private SQL() {
		// do nothing
	}
}
