package fr.dila.solonepp.rest.api;

import javax.xml.bind.JAXBException;

import fr.dila.st.rest.client.HttpTransactionException;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.NotifierEvenementRequest;
import fr.sword.xsd.solon.epp.NotifierEvenementResponse;
import fr.sword.xsd.solon.epp.NotifierTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.NotifierTableDeReferenceResponse;

public interface WSNotification {

	static final String	SERVICE_NAME					= "WSnotification";

	static final String	METHOD_NAME_TEST				= "test";
	static final String	METHOD_NAME_VERSION				= "version";

	static final String	METHOD_NAME_NOTIFIER_EVENEMENT	= "notifierEvenement";

	static final String	METHOD_NAME_NOTIFIER_TDR		= "notifierTableDeReference";

	String test() throws Exception;

	VersionResponse version() throws Exception;

	NotifierEvenementResponse notifierEvenement(NotifierEvenementRequest request) throws Exception;

	NotifierTableDeReferenceResponse notifierTableDeReference(NotifierTableDeReferenceRequest request)
			throws JAXBException, HttpTransactionException;

}
