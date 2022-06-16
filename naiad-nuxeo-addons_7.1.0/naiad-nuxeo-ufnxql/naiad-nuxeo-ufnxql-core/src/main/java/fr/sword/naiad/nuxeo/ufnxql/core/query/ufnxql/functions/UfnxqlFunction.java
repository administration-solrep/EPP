package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.nuxeo.ecm.core.query.sql.model.Expression;
import org.nuxeo.ecm.core.query.sql.model.Function;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.QueryBuilder;

/**
 * Interface pour gérer des fonctions dans le langage UFNXQL
 *
 */
public interface UfnxqlFunction {

	/**
	 * @return le nom de la fonction
	 */
	String getName();
	
	/**
	 * 
	 * @return l'expression que doit respecter l'appel de la fonction
	 */
	String getExpression();
	
	/**
	 * test si l'expression utilisant la fonction est confirme a ce qui est attendu
	 * Appeler lors de la visite de l'expression (avant de la visite du noeud function)
	 * 
	 * si faux, une exception est levée dont le message inclura la valeur de getExpression
	 * @param queryBuilder
	 * @param expr
	 * @return si l'expression est conforme
	 */
	boolean checkExpression(QueryBuilder queryBuilder, Expression expr);
	
	/**
	 * Appeler lors de la visite sur la fonction.
	 * @param queryBuilder
	 * @param func
	 */
	void visitFunction(QueryBuilder queryBuilder, Function func);
}
