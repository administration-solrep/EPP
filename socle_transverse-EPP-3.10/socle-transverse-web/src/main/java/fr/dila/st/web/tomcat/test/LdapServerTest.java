package fr.dila.st.web.tomcat.test;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Test du serveur LDAP.
 * 
 * @author bgamard
 */
public class LdapServerTest implements ServerTest {

	long	time	= 0;

	@Override
	public boolean runTest(CoreSession session) {
		time = Calendar.getInstance().getTimeInMillis();
		try {
			STServiceLocator.getProfileService().getBaseFunctionFromProfil(
					STConstant.PROFILE_ADMINISTRATEUR_FONCTIONNEL);
		} catch (Exception e) {
			return false;
		} finally {
			time = Calendar.getInstance().getTimeInMillis() - time;
		}

		return true;
	}

	@Override
	public String getName() {
		return "LDAP";
	}

	@Override
	public long getElapsedTime() {
		return time;
	}
}
