package fr.dila.ss.core.event.batch;

import static fr.dila.st.api.constant.STConstant.USER_ADMINISTRATOR;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.DEFAULT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHost.DEFAULT_SCHEME_NAME;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.services.CloseSessionsOperation;
import fr.dila.st.core.service.STServiceLocator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.launcher.config.ConfigurationGenerator;
import org.nuxeo.runtime.api.Framework;

/**
 * Récupère les utilisateurs toujours indiqués comme connectés et place le flag à "déconnecté"
 *
 */
public class CloseUsersConnectionsBatchListener extends AbstractBatchEventListener {
    private static final STLogger LOGGER = STLogFactory.getLog(CloseUsersConnectionsBatchListener.class);

    private static final String USERS_GOT = "%d utilisateurs récupérés";

    public CloseUsersConnectionsBatchListener() {
        super(LOGGER, SSEventConstant.BATCH_EVENT_CLOSE_USERS_CONNECTIONS);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        final long startTime = System.currentTimeMillis();
        LOGGER.info(session, SSLogEnumImpl.INIT_B_CLOSE_USERS_CONNEC_TEC);
        List<STUser> users = SSServiceLocator.getSSUserService().getAllUserConnected();
        int nbUsers = users.size();
        LOGGER.info(session, SSLogEnumImpl.GET_IUC_TEC, String.format(USERS_GOT, nbUsers));

        try {
            closeSessionsOnAllNuxeoServers();
        } catch (Exception exc) {
            LOGGER.error(session, SSLogEnumImpl.FAIL_PROCESS_B_CLOSE_USERS_CONNEC_TEC, exc);
            errorCount++;
        }

        SSServiceLocator.getSSUserService().setLogoutTrueForAllUsers();

        final long endTime = System.currentTimeMillis();
        try {
            STServiceLocator
                .getSuiviBatchService()
                .createBatchResultFor(batchLoggerModel, "Utilisateurs mis à jour", nbUsers, endTime - startTime);
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
        }
        LOGGER.info(session, SSLogEnumImpl.END_B_CLOSE_USERS_CONNEC_TEC);
    }

    private void closeSessionsOnAllNuxeoServers() {
        LOGGER.debug(DEFAULT, "Deconnexion des utilisateurs sur tous les serveurs Nuxeo");

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            int port = Integer.parseInt(Framework.getProperty(ConfigurationGenerator.PARAM_HTTP_PORT));
            String contextPath = Framework.getProperty(ConfigurationGenerator.PARAM_CONTEXT_PATH);
            String path = String.format("%s/site/%s/%s", contextPath, "execoperation", CloseSessionsOperation.ID);

            URIBuilder uriBuilder = new URIBuilder().setScheme(DEFAULT_SCHEME_NAME).setPort(port).setPath(path);
            HttpPost post = new HttpPost();
            setAuthHeader(post);

            String nuxeoHosts = Framework.getProperty(STConfigConstants.NUXEO_HOSTS);
            Objects.requireNonNull(nuxeoHosts);

            for (String host : nuxeoHosts.split(",")) {
                uriBuilder.setHost(host);
                // example of url : http://localhost:8180/solon-epg/site/execoperation/Services.CloseSessions
                URI uri = uriBuilder.build();
                LOGGER.debug(DEFAULT, uri.toString());
                post.setURI(uri);
                try (CloseableHttpResponse response = client.execute(post)) {
                    logResponse(response);
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new NuxeoException(e);
        }
    }

    private void setAuthHeader(HttpPost post) {
        String usernameP = Framework.getProperty(STConfigConstants.ADMIN_SYSTEMP);
        Objects.requireNonNull(usernameP);

        post.setHeader(
            AUTHORIZATION,
            "Basic " + Base64.getEncoder().encodeToString((USER_ADMINISTRATOR + ":" + usernameP).getBytes())
        );
        // dereference usernameP
        usernameP = ""; // NOSONAR
    }

    private void logResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        String entity = EntityUtils.toString(response.getEntity());

        if (HttpStatus.SC_OK != statusCode) {
            LOGGER.error(DEFAULT, "" + statusCode);
            LOGGER.error(DEFAULT, entity);
        } else {
            LOGGER.debug(DEFAULT, "" + statusCode);
            LOGGER.debug(DEFAULT, entity);
        }
    }
}
