package fr.dila.ss.core.birt;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.dila.solon.birt.common.SerializationUtils;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.birt.BirtException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.nuxeo.runtime.api.Framework;

public class BirtServerCommand {
    private static final STLogger LOGGER = STLogFactory.getLog(BirtServerCommand.class);

    private static final String BIRT_APP_URL_SERVER_PROPERTY = "solon.birt.app.server.url";
    private static final String BIRT_APP_URL_SERVER_DEF_VALUE = "http://localhost:8080/birt/v1/api/report";

    public String call(SolonBirtParameters solonBirtParameters) throws BirtException {
        String serializedParam;
        try {
            serializedParam = SerializationUtils.serialize(solonBirtParameters);
        } catch (JsonProcessingException e1) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e1);
            throw new BirtException(e1);
        }

        String birtApiURL = Framework.getProperty(BIRT_APP_URL_SERVER_PROPERTY, BIRT_APP_URL_SERVER_DEF_VALUE);
        HttpPost request = new HttpPost(birtApiURL);
        request.setEntity(new StringEntity(serializedParam, ContentType.APPLICATION_JSON));

        String response;

        try (
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse httpResponse = client.execute(request)
        ) {
            response = EntityUtils.toString(httpResponse.getEntity());
            int status = httpResponse.getStatusLine().getStatusCode();

            if (status != HttpStatus.SC_OK) {
                throw new BirtException(String.format("Status : %s - message: %s", status, response));
            }
        } catch (IOException e1) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e1);
            throw new BirtException(e1);
        }

        return response;
    }
}
