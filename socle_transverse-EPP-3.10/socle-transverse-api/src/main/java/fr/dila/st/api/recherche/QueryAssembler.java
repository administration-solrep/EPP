package fr.dila.st.api.recherche;

/**
 * Une interface pour décrire les assembleurs de requête qui transforment une clause Where en une requête valide
 * 
 * @author jgomez
 * 
 */
public interface QueryAssembler {

	/**
	 * Met la clause WHERE.
	 */
	void setWhereClause(String clause);

	/**
	 * Retourne la requête complête.
	 * 
	 * @return la requête
	 */
	String getFullQuery();

	/**
	 * Retourne vrai si l'assembleur est le défaut du projet
	 * 
	 * @return vrai si défaut
	 */
	Boolean getIsDefault();

	/**
     */
	void setIsDefault(Boolean isDefault);
}
