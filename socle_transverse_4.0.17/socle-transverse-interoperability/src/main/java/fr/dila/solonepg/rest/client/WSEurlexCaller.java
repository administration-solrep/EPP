package fr.dila.solonepg.rest.client;

import fr.sword.wsdl.solon.eurlex.EURLexWebService;
import fr.sword.wsdl.solon.eurlex.EURLexWebServiceProvider;
import fr.sword.wsdl.solon.eurlex.SearchRequest;
import fr.sword.wsdl.solon.eurlex.SearchResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class WSEurlexCaller {

    private static final Logger LOGGER = LogManager.getLogger(WSEurlexCaller.class);
    private static final String EURLEX_WSDL = "/xsd/solon/eurlex/eurlex-ws.wsdl";

    public WSEurlexCaller() {
        super();
    }

    public SearchResults rechercherExistenceTranspositionDirective(SearchRequest request, String login, String mdp,
                                                                   String url) throws Exception {
        URL baseUrl = WSEurlexCaller.class.getResource(EURLEX_WSDL);
        EURLexWebService ws = new EURLexWebService(baseUrl, new QName("http://eur-lex.europa.eu/search",
                "EURLexWebService"));
        EURLexWebServiceProvider port = ws.getEURLexWebServicePort();
        Map<String, Object> requestT = ((BindingProvider) port).getRequestContext();
        requestT.put(BindingProvider.USERNAME_PROPERTY, login);
        requestT.put(BindingProvider.PASSWORD_PROPERTY, mdp);
        requestT.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);

        // Use WSSE auth
        BindingProvider bp = (BindingProvider) port;
        List<Handler> handlerChain = new ArrayList<>();
        handlerChain.add(new WSSEHandler(login, mdp));
        Binding binding = bp.getBinding();
        binding.setHandlerChain(handlerChain);

        LOGGER.info("Début d'appel au Web Service Eurlex");
        return Optional.ofNullable(port.doQuery(request))
                .map(resultsWsdl -> {
                    LOGGER.info("Fin d'appel au Web Service Eurlex");
                    return resultsWsdl;
                })
                .orElseThrow(() -> new WSEurlexException("null response to rechercherExistenceTranspositionDirective"));
    }

    public class WSSEHandler implements SOAPHandler<SOAPMessageContext> {
        private final String password;
        private final String login;

        public WSSEHandler(String login, String mdp) {
            this.login = login;
            this.password = mdp;
        }

        @Override
        public Set<QName> getHeaders() {
            return new HashSet<>();
        }

        @Override
        public boolean handleMessage(SOAPMessageContext smc) {
            Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outboundProperty) {
                try {
                    SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
                    SOAPHeader header = envelope.getHeader();
                    SOAPElement security = header.addChildElement("Security", "wsse",
                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                    SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
                    SOAPElement username = usernameToken.addChildElement("Username", "wsse");
                    username.addTextNode(login);
                    SOAPElement pass = usernameToken.addChildElement("Password", "wsse");
                    pass.addTextNode(password);
                } catch (Exception e) {
                    LOGGER.error(e);
                    return false;

                }
            }
            return true;
        }

        @Override
        public boolean handleFault(SOAPMessageContext soapMessageContext) {
            return false;
        }

        @Override
        public void close(MessageContext messageContext) {

        }
    }
}
