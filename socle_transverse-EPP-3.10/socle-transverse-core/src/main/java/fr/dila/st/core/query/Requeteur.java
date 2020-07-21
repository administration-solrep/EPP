package fr.dila.st.core.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.query.QueryParseException;

import fr.dila.st.core.query.translation.QueryTranslator;
import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.util.StringUtil;

/**
 * Le requêteur permet d'effectuer les opérations principales du requêteur. Permet de supprimer des lignes, de remonter
 * et de descendre une ligne spécifique.
 * 
 * @author jgomez
 * 
 */

public class Requeteur {

	private static final Log			LOGGER			= LogFactory.getLog(Requeteur.class);
	private static final String			WHERE			= "WHERE";
	private static final String			LIKE			= "LIKE";
	private static final String			NOT_LIKE		= "NOT LIKE";
	private static final String			CONTAINS		= "CONTAINS";
	private static final String			NOT_CONTAINS	= "NOT CONTAINS";
	private final QueryTranslator		translator;
	private String						query;
	private List<TranslatedStatement>	statements;

	public Requeteur() {
		this.translator = new QueryTranslator();
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Met à jour la traduction de la requête : donne une liste d'objets directement utilisable par le bean d'affichage.
	 * 
	 */
	public void updateTranslation() {
		try {
			if (StringUtil.containsNone(query, WHERE)
					|| (StringUtil.contains(query, WHERE) && StringUtil.isNotBlank(getWherePart()))) {
				this.statements = translator.translateUFNXL(this.query);
				translationOfContains(false);
			} else {
				this.statements = new ArrayList<TranslatedStatement>();
			}
		} catch (QueryParseException e) {
			LOGGER.warn(e, e);
			this.statements = new ArrayList<TranslatedStatement>();
		}
	}

	/**
	 * Méthode pour l'affichage du mot "contient" plutôt que "comme" et affichage de la valeur sans le ' et les % Il
	 * faut remettre en état la requête lorsque qu'on la reconstruit depuis la traduction
	 * 
	 * @param rebuild
	 */
	private void translationOfContains(boolean rebuild) {
		if (rebuild) {
			for (TranslatedStatement ts : this.statements) {
				String value = ts.getValue();
				String condOp = ts.getConditionalOperator();
				if (condOp.equals(CONTAINS)) {
					ts.setConditionalOperator(LIKE);
					value = "\'%" + value + "%\'";
				} else if (condOp.equals(NOT_CONTAINS)) {
					ts.setConditionalOperator(NOT_LIKE);
					value = "\'%" + value + "%\'";
				}
				ts.setValue(value);
			}
		} else {
			for (TranslatedStatement ts : this.statements) {
				String value = ts.getValue();
				String condOp = ts.getConditionalOperator();
				if (value.startsWith("\'%") && value.endsWith("%\'")) {
					if (condOp.equals(LIKE)) {
						ts.setConditionalOperator(CONTAINS);
						// On vérifie qu'il y a des données à récupérer, sinon caractère spécial pour "all" 
						value = value.length() > 5 ? value.substring(2, value.length() - 2):"%";
					} else if (condOp.equals(NOT_LIKE)) {
						ts.setConditionalOperator(NOT_CONTAINS);
						value = value.length() > 5 ? value.substring(2, value.length() - 2):"%";
					}
					ts.setValue(value);
				}
			}
		}
	}

	public List<TranslatedStatement> getStatements() {
		return this.statements;
	}

	/**
	 * Ajoute une ligne du requêteur et recalcule la requête.
	 * 
	 * @param index
	 */
	public void add(int index, TranslatedStatement statement) {
		this.statements.add(index, statement);
		this.buildFromTranslation();
	}

	/**
	 * Enlève une ligne du requêteur et recalcule la requête.
	 * 
	 * @param index
	 */
	public TranslatedStatement remove(int index) {
		if (this.statements.size() == 0) {
			return null;
		}
		TranslatedStatement removedStatement = this.statements.remove(index);
		// Si on enlève la dernière instruction, on enlève l'opérateur logique
		// qui séparait la dernière et l'avant-dernière intruction.
		if (index == this.statements.size()) {
			int beforeLastIndex = index - 1;
			if (beforeLastIndex >= 0) {
				TranslatedStatement statement = this.statements.get(beforeLastIndex);
				statement.setLogicalOperator(StringUtils.EMPTY);
			}
		}
		this.buildFromTranslation();
		return removedStatement;
	}

	/**
	 * Reconstruit la requête technique de la requête à partir de la liste des lignes du requêteur. Attention ! Cette
	 * méthode doit être appeler avant la modification de la liste des clauses.
	 * 
	 * @param index
	 * 
	 */
	public void buildFromTranslation() {
		translationOfContains(true);
		if (StringUtils.isEmpty(this.query)) {
			LOGGER.warn("La requête est vide, ne fait rien");
			return;
		}
		String selectClause = getSelectClause();
		List<String> translatedStrParts = new ArrayList<String>();
		for (TranslatedStatement st : this.statements) {
			translatedStrParts.add(st.toString(true));
		}
		String rebuildQuery = StringUtils.join(translatedStrParts, " ");
		this.query = selectClause + " " + rebuildQuery;

	}

	/**
	 * Retourne la clause Select de la requête.
	 * 
	 * @return
	 */
	public String getSelectClause() {
		return this.query.split(WHERE)[0] + WHERE;
	}

	/**
	 * Retourne la partie de la requête qui correspond à la clause WHERE.
	 * 
	 * @return
	 */
	public String getWherePart() {
		String[] selectClause = this.query.split(WHERE);
		if (selectClause.length < 2) {
			return StringUtils.EMPTY;
		}
		return selectClause[1];
	}

	/**
	 * Remonte une ligne du requêteur et recalcule la requête. Si c'est pas possible, ne fait rien
	 * 
	 * @param index
	 */
	public void up(int index) {
		if (index <= 0) {
			return;
		}
		swapStatement(index, index - 1);
	}

	protected void swapStatement(Integer firstIndex, Integer lastIndex) {
		if (lastIndex > this.statements.size()) {
			return;
		}
		if (firstIndex > lastIndex) {
			int tmp = lastIndex;
			lastIndex = firstIndex;
			firstIndex = tmp;
		}
		// On garde l'opérateur logique en mémoire, car il peut être supprimé par la méthode remove.
		TranslatedStatement firstStatement = this.statements.get(firstIndex);
		TranslatedStatement lastStatement = this.statements.get(lastIndex);
		String keepedLogicaloperator = new String(firstStatement.getLogicalOperator());
		this.remove(firstIndex);
		this.add(lastIndex, firstStatement);
		firstStatement.setLogicalOperator(lastStatement.getLogicalOperator());
		lastStatement.setLogicalOperator(keepedLogicaloperator);
		this.buildFromTranslation();
	}

	/**
	 * Remonte redescends une ligne du requêteur. Si c'est pas possible, ne fait rien
	 * 
	 * @param index
	 */
	public void down(int index) {
		if (index + 1 >= this.statements.size()) {
			return;
		}
		swapStatement(index, index + 1);
	}
}
