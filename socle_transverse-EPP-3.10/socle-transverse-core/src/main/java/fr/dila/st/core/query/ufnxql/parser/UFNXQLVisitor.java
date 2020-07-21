package fr.dila.st.core.query.ufnxql.parser;

import org.nuxeo.ecm.core.query.sql.model.IVisitor;

/**
 * Etends IVisitor pour le support des noeuds - SParamLiteral : represente une reference à un paramètre ('?' dans la
 * requete) - SGropuByClause : Group By
 * 
 * @author spesnel
 * 
 */
public interface UFNXQLVisitor extends IVisitor {

	void visitSParamLiteral();

	void visitSGroupByClause(SGroupByClause clause);

}
