package fr.dila.st.web.tomcat.test;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Test de l'accès la base de données.
 * 
 * @author bgamard
 */
public class BddServerTest implements ServerTest {

	long	time	= 0;

	@Override
	public boolean runTest(CoreSession session) {
		time = Calendar.getInstance().getTimeInMillis();
		try {
			session.query("SELECT * FROM Parametre");
		} catch (Exception e) {
			return false;
		} finally {
			time = Calendar.getInstance().getTimeInMillis() - time;
		}

		return true;
	}

	@Override
	public String getName() {
		return "Base de données";
	}

	@Override
	public long getElapsedTime() {
		return time;
	}
}
