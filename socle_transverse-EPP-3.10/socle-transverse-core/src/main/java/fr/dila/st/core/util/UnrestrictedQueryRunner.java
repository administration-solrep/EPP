package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Classe utilitaire permettant d'effectuer une recherche de documents en désactivant la gestion des ACL.
 * 
 * @author jtremeaux
 */
public class UnrestrictedQueryRunner extends UnrestrictedSessionRunner {
	/**
	 * Requête à exécuter.
	 */
	private String				queryString;

	/**
	 * Résultats de recherche.
	 */
	private DocumentModelList	resultList;

	/**
	 * Limite le nombre maximum de documents à retourner, 0 pour tous les retourner.
	 */
	private long				limit;

	/**
	 * Décalage du premier résultat.
	 */
	private long				offset;

	/**
	 * Si vrai, retourne un DocumentModelList qui inclut le nombre total de résultats (comme s'il n'y avait pas de
	 * limite ni de décalage).
	 */
	private boolean				countTotal;

	/**
	 * Constructeur de UnrestrictedQueryRunner.
	 * 
	 * @param session
	 *            Session (restreinte)
	 * @param queryString
	 *            Requête à exécuter
	 */
	public UnrestrictedQueryRunner(CoreSession session, String queryString) {
		super(session);

		this.queryString = queryString;
	}

	/**
	 * Constructeur de UnrestrictedQueryRunner.
	 * 
	 * @param session
	 *            Session (restreinte)
	 * @param queryString
	 *            Requête à exécuter
	 * @param limit
	 *            Limite le nombre maximum de documents à retourner, 0 pour tous les retourner
	 * @param offset
	 *            Décalage du premier résultat
	 * @param countTotal
	 *            Si vrai, retourne un DocumentModelList qui inclut le nombre total de résultats (comme s'il n'y avait
	 *            pas de limite ni de décalage)
	 */
	public UnrestrictedQueryRunner(CoreSession session, String queryString, long limit, long offset, boolean countTotal) {
		super(session.getRepositoryName());

		this.queryString = queryString;
		this.limit = limit;
		this.offset = offset;
		this.countTotal = countTotal;
	}

	@Override
	public void run() throws ClientException {
		if (countTotal) {
			resultList = session.query(queryString, null, limit, offset, countTotal);
		} else {
			resultList = session.query(queryString);
		}
	}

	/**
	 * Effectue la recherche et retourne la liste de résultats.
	 * 
	 * @return Liste des résultats
	 * @throws ClientException
	 */
	public DocumentModelList findAll() throws ClientException {
		runUnrestricted();
		return resultList;
	}

	/**
	 * Retourne le premier résultat de la recherche. Retourne null si aucun résultat, et le premier résultat si la
	 * requête en retourne plusieurs.
	 * 
	 * @return Premier résultat de la recherche
	 * @throws ClientException
	 */
	public DocumentModel getFirst() throws ClientException {
		runUnrestricted();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		}
		return null;
	}
}
