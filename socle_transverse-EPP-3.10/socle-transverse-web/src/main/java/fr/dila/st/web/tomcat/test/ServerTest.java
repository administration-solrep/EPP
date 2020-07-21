package fr.dila.st.web.tomcat.test;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface de base pour les tests serveur.
 * 
 * @author bgamard
 */
public interface ServerTest {

	/**
	 * Retourne le nom du test.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Lance le test.
	 * 
	 * @param session
	 * @return
	 */
	boolean runTest(CoreSession session);

	/**
	 * Retourne la durée du test en millisecondes
	 * 
	 * @return
	 */
	long getElapsedTime();
}
