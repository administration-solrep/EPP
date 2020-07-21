package fr.dila.solonepp.rest.client;

import javax.xml.bind.JAXBException;

import fr.dila.solonepp.rest.api.WSNotification;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.dila.st.rest.client.HttpTransactionException;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.NotifierEvenementRequest;
import fr.sword.xsd.solon.epp.NotifierEvenementResponse;
import fr.sword.xsd.solon.epp.NotifierTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.NotifierTableDeReferenceResponse;

public class WSNotificationProxy extends AbstractWsProxy implements WSNotification {

	public WSNotificationProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_NAME_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_NAME_VERSION, VersionResponse.class);
	}

	@Override
	public NotifierEvenementResponse notifierEvenement(NotifierEvenementRequest request) throws Exception {
		return doPost(METHOD_NAME_NOTIFIER_EVENEMENT, request, NotifierEvenementResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSNotification.SERVICE_NAME;
	}

	@Override
	public NotifierTableDeReferenceResponse notifierTableDeReference(NotifierTableDeReferenceRequest request)
			throws JAXBException, HttpTransactionException {
		return doPost(METHOD_NAME_NOTIFIER_TDR, request, NotifierTableDeReferenceResponse.class);
	}
}
