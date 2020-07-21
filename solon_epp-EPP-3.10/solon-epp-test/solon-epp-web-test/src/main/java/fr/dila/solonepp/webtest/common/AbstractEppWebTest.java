package fr.dila.solonepp.webtest.common;

import fr.dila.solonepp.page.ChangePasswordPage;
import fr.dila.solonepp.page.CorbeillePage;
import fr.dila.solonepp.page.LoginPage;
import fr.sword.naiad.commons.webtest.WebPage;
import fr.sword.naiad.commons.webtest.logger.FunctionalLogger;
import fr.sword.naiad.nuxeo.commons.webtest.helper.AbstractNuxeoWebTest;
import fr.sword.naiad.nuxeo.commons.webtest.helper.UrlHelper;

public class AbstractEppWebTest extends AbstractNuxeoWebTest {

	protected CorbeillePage	corbeillePage;

	protected void doLoginAs (final String username, final String password) {
		System.getProperties().put("APP_URL", "http://idlv-solrep-epp-qa.lyon-dev2.local:8080/solon-epp");
		// System.getProperties().put("APP_URL", "http://localhost:8080/solon-epp");
		if (getDriver() != null) {
			getFlog().action("Ouverture de la page de login");

			WebPage.TIMEOUT_IN_SECONDS = 60;
			final LoginPage loginPage = WebPage.goToPage(getDriver(), getFlog(), UrlHelper.getInstance().getLoginUrl(),
					LoginPage.class);

			corbeillePage = loginPage.submitLogin(username, password, CorbeillePage.class);
			corbeillePage.waitElementVisibilityById("userMetaServicesSearchDiv");

		}
	}

	protected ChangePasswordPage doLoginAsNewUser (final String username, final String password) {
		// System.getProperties().put("APP_URL", "http://idlv-solrep-epp-qa.lyon-dev2.local:8080/solon-epp");
		System.getProperties().put("APP_URL", "http://localhost:8080/solon-epp");
		if (getDriver() != null) {
			getFlog().action("Ouverture de la page de login");
			WebPage.TIMEOUT_IN_SECONDS = 60;
			final LoginPage loginPage = WebPage.goToPage(getDriver(), getFlog(), UrlHelper.getInstance().getLoginUrl(),
					LoginPage.class);

			return loginPage.submitLogin(username, password, ChangePasswordPage.class);
		}
		return null;
	}

	protected void doLogout () {
		if (getDriver() != null) {
			logout();
		}
		corbeillePage = null;
	}

	@Override
	protected FunctionalLogger getFlog () {
		return (FunctionalLogger) super.getFlog();
	}

}
