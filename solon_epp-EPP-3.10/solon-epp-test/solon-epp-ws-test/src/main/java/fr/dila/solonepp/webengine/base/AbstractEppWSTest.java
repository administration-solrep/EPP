package fr.dila.solonepp.webengine.base;

import fr.sword.naiad.commons.webtest.logger.FunctionalLogger;
import fr.sword.naiad.nuxeo.commons.webtest.helper.AbstractNuxeoWebTest;

public abstract class AbstractEppWSTest extends AbstractNuxeoWebTest {

	@Override
	protected FunctionalLogger getFlog() {
		return (FunctionalLogger) super.getFlog();
	}
	

}
