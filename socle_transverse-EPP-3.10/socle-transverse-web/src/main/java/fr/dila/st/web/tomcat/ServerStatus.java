package fr.dila.st.web.tomcat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.web.tomcat.test.BddServerTest;
import fr.dila.st.web.tomcat.test.LdapServerTest;
import fr.dila.st.web.tomcat.test.ServerTest;

/**
 * Classe utilisée pour vérifier le statut du serveur.
 */
public class ServerStatus {

	private static final Log	LOGGER	= LogFactory.getLog(ServerStatus.class);
	/**
	 * Session vers la BDD.
	 */
	protected CoreSession		session;

	protected List<ServerTest>	tests;

	/**
	 * Constructeur.
	 */
	public ServerStatus() {
		try {
			Framework.login();
			RepositoryManager mgr = Framework.getService(RepositoryManager.class);
			Repository repository = null;
			if (mgr != null) {
				repository = mgr.getDefaultRepository();
			}
			if (repository != null) {
				this.session = repository.open();
			}
		} catch (Exception e1) {
			LOGGER.warn("Erreur ServerStatus initialisation", e1);
		}

		tests = new ArrayList<ServerTest>();
		tests.add(new BddServerTest());
		tests.add(new LdapServerTest());
		// tests.add(new ClusterServerTest());
	}

	@Override
	public void finalize() {
		Repository.close(session);
	}

	/**
	 * Retourne les tests.
	 * 
	 * @return
	 */
	public List<ServerTest> getTests() {
		return tests;
	}

	/**
	 * Retourne la session.
	 * 
	 * @return
	 */
	public CoreSession getSession() {
		return session;
	}

	/**
	 * Vérifie l'état de tous les tests.
	 * 
	 * @return True si tout va bien
	 */
	public boolean getServerStatus() {
		for (ServerTest test : tests) {
			if (!test.runTest(session)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retourne les informations sur la table CLUSTER_INVALS.
	 * 
	 * @return
	 */
	public List<String[]> getClusterInvals() {
		List<String[]> out = new ArrayList<String[]>();
		IterableQueryResult res = null;
		// Format pour les dates
		SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		SimpleDateFormat sdfOut = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss");
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { "dc:title", FlexibleQueryMaker.COL_COUNT,
					FlexibleQueryMaker.COL_ID },
					"select cluster_nodes.nodeid, count(cluster_invals.nodeid) nb, cluster_nodes.created "
							+ "from cluster_nodes "
							+ "left join cluster_invals on cluster_invals.nodeid = cluster_nodes.nodeid "
							+ "group by cluster_nodes.nodeid, cluster_nodes.created", new Object[] {});
			Iterator<Map<String, Serializable>> resIterator = res.iterator();
			while (resIterator.hasNext()) {
				Map<String, Serializable> row = resIterator.next();
				String nodeid = (String) row.get("dc:title");
				String count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).toString();
				// Gestion de la date
				String created = (String) row.get(FlexibleQueryMaker.COL_ID);
				created = sdfOut.format(sdfIn.parse(created));
				out.add(new String[] { nodeid, count, created });
			}
		} catch (Exception e) {
			LOGGER.warn("Erreur de récupération des informations cluster_invals", e);
		} finally {
			if (res != null) {
				res.close();
			}
		}

		return out;
	}

	/**
	 * Retourne l'identifiant de l'instance.
	 * 
	 * @return
	 */
	public String getNxNodeId() {
		// TODO Il faut utiliser la connexion dans ClusterNodeHandler pour avoir le bon NX_NODEID()
		// IterableQueryResult res = null;
		// try {
		// res = QueryUtils.doSqlQuery(session, new String[] { "dc:title" }, "select NX_NODEID() from dual", new
		// Object[] {});
		// Iterator<Map<String, Serializable>> it = res.iterator();
		// if (it.hasNext()) {
		// Map<String, Serializable> row = it.next();
		// String nodeid = (String) row.get("dc:title");
		// return nodeid;
		// }
		// } catch (Exception e) {
		// // NOP
		// } finally {
		// if (res != null) {
		// res.close();
		// }
		// }
		return "";
	}
}
