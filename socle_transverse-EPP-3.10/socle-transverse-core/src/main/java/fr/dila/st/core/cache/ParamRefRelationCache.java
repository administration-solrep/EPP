package fr.dila.st.core.cache;

import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;

import fr.dila.st.core.query.QueryUtils;

/**
 * Recupere une ref à partir d'une requete UFNXQL paramétrée ou depuis un cache. Garde la ref en l'associant à la valeur
 * du parametre présent dans la requete
 * 
 * Le document est recupere en prenant une requete de base contenant un parametre, et en l'executant dans
 * CoreSession.query. Si la requete de l'id a deja ete faite avec ce parametre, l'id est deja présent dans le cache.
 * celui ci est retourné.
 * 
 * exemple : baseQuery : 'select p.from Parametre as p where p.ecm:name = ?'
 * 
 * Objetctif : eviter la requete systematique à la base pour recupérer la valeur d'un id qui ne change jamais.
 * 
 * T : type du parametre passé à la requete UFNXQL
 * 
 * @author spesnel
 * 
 */
public class ParamRefRelationCache<T> {

	private final String				queryUFNXQL;
	private final Map<T, DocumentRef>	idmap;

	public ParamRefRelationCache(String queryUFNXQL) {
		this.queryUFNXQL = queryUFNXQL;
		this.idmap = new HashMap<T, DocumentRef>();
	}

	/**
	 * Recupere l'id d'un document sans test des ACLS
	 * 
	 * @param session
	 * @param queryParamValue
	 * @return
	 * @throws ClientException
	 */
	public DocumentRef retrieveDocumentRef(CoreSession session, T queryParamValue) throws ClientException {
		DocumentRef aref = idmap.get(queryParamValue);
		if (aref == null) {
			Object[] params = new Object[] { queryParamValue };
			DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, queryUFNXQL, params);
			if (refs.length > 1) {
				throw new ClientException("Plus d'un document est retrourné pour [" + queryUFNXQL + "]["
						+ queryParamValue + "]");
			} else if (refs.length == 1) {
				aref = refs[0];
				idmap.put(queryParamValue, aref);
			}
		}
		return aref;
	}

	/**
	 * recupere le document associé à l'id associé à la valeur du parametre de la requete. Attention session.getDocument
	 * teste les acls
	 * 
	 * @param session
	 * @param queryParamValue
	 * @return
	 * @throws ClientException
	 */
	public DocumentModel retrieveDocument(CoreSession session, T queryParamValue) throws ClientException {
		DocumentRef ref = retrieveDocumentRef(session, queryParamValue);
		if (ref != null) {
			return session.getDocument(ref);
		}
		return null;
	}

}
