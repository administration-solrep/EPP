package fr.dila.st.api.dao.criteria;

/**
 * Critère de recherche des documents Nuxeo.
 * 
 * @author jtremeaux
 */
public class BaseCriteria {
	/**
	 * État en cours du cycle de vie du document.
	 */
	private String	currentLifeCycleState;

	/**
	 * Default constructor
	 */
	public BaseCriteria() {
		// do nothing
	}

	/**
	 * Getter de currentLifeCycleState.
	 * 
	 * @return currentLifeCycleState
	 */
	public String getCurrentLifeCycleState() {
		return currentLifeCycleState;
	}

	/**
	 * Setter de currentLifeCycleState.
	 * 
	 * @param currentLifeCycleState
	 *            currentLifeCycleState
	 */
	public void setCurrentLifeCycleState(String currentLifeCycleState) {
		this.currentLifeCycleState = currentLifeCycleState;
	}
}
