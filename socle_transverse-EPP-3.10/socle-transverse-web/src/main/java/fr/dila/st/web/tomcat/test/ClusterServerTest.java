package fr.dila.st.web.tomcat.test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * Test l'état du cluster.
 * 
 * @author admin
 * 
 */
public class ClusterServerTest implements ServerTest {

	private static final Log	LOGGER	= LogFactory.getLog(ClusterServerTest.class);
	long						time	= 0;

	@Override
	public boolean runTest(CoreSession session) {
		time = Calendar.getInstance().getTimeInMillis();
		IterableQueryResult res = null;
		try {
			// TODO Fonctionne pas en l'état, il faudrait utiliser la connexion dans ClusterNodeHandler pour avoir le
			// même NX_NODEID()
			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_COUNT },
					"select count(*) from cluster_nodes where nodeid = NX_NODEID()", new Object[] {});
			Iterator<Map<String, Serializable>> resIterator = res.iterator();
			if (resIterator.hasNext()) {
				Map<String, Serializable> row = resIterator.next();
				int count = ((Long) row.get(FlexibleQueryMaker.COL_COUNT)).intValue();
				if (count == 1) {
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.warn("Erreur dans le compte des cluster_nodes", e);
		} finally {
			if (res != null) {
				res.close();
			}
			time = Calendar.getInstance().getTimeInMillis() - time;
		}
		return false;
	}

	@Override
	public String getName() {
		return "Cluster";
	}

	@Override
	public long getElapsedTime() {
		return time;
	}

}
