package fr.dila.solonepg.rest.client;

import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;

import fr.sword.wsdl.solon.eurlex.EURLexWebService;
import fr.sword.wsdl.solon.eurlex.EURLexWebServiceProvider;
import fr.sword.wsdl.solon.eurlex.SearchRequest;
import fr.sword.wsdl.solon.eurlex.SearchResults;

public class WSEurlexCaller {

	private static final Logger	LOGGER		= Logger.getLogger(WSEurlexCaller.class);
	private static final String	eurlexWSDL	= "/xsd/solon/eurlex/eurlex-ws.wsdl";

	public WSEurlexCaller() {
		super();
	}

	public SearchResults rechercherExistenceTranspositionDirective(SearchRequest request, String login, String mdp,
			String url) throws Exception {
		URL baseUrl = WSEurlexCaller.class.getResource(eurlexWSDL);
		EURLexWebService ws = new EURLexWebService(baseUrl, new QName("http://eur-lex.europa.eu/search",
				"EURLexWebService"));
		EURLexWebServiceProvider port = ws.getEURLexWebServicePort();
		Map<String, Object> requestT = ((BindingProvider) port).getRequestContext();
		requestT.put(BindingProvider.USERNAME_PROPERTY, login);
		requestT.put(BindingProvider.PASSWORD_PROPERTY, mdp);
		requestT.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		LOGGER.info("Début d'appel au Web Service Eurlex");
		fr.sword.wsdl.solon.eurlex.SearchResults resultsWsdl;
		resultsWsdl = port.doQuery(request);
		LOGGER.info("Fin d'appel au Web Service Eurlex");
		// --- check response
		if (resultsWsdl == null) {
			// un résultat vide est normalement un objet non null avec 0 résultat
			// on traite le cas limite où le résultat serait null
			throw new WSEurlexException("null response to rechercherExistenceTranspositionDirective");
		} else {
			return resultsWsdl;
		}
	}
}
