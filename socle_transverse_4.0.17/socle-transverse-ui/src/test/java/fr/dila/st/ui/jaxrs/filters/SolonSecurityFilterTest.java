package fr.dila.st.ui.jaxrs.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.spi.container.ContainerRequest;
import fr.dila.st.core.exception.STValidationException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class SolonSecurityFilterTest {
    private static final String ERROR_MESSAGE = "Contenu invalide";

    private SolonSecurityFilter solonSecurityFilter;

    @Mock
    private ContainerRequest request;

    @Before
    public void setUp() throws Exception {
        solonSecurityFilter = new SolonSecurityFilter();

        initMocks(this);

        URI requestUri = new URI(
            "http://localhost/reponses/classement/liste?origine=AN&cle=Tous&cleParent=accidents%20du%20travail%20et%20maladies%20professionnelles"
        );
        when(request.getQueryParameters()).thenReturn(UriComponent.decodeQuery(requestUri, true));

        when(request.getCookieNameValueMap()).thenReturn(new MultivaluedMapImpl());

        when(request.getFormParameters()).thenReturn(new Form());

        Map<String, Object> properties = new HashMap<>();
        properties.put("com.sun.jersey.api.representation.form", new Form());
        when(request.getProperties()).thenReturn(properties);
    }

    @Test
    public void testFilterRequestHeadersWithInvalidEncodedUrl() {
        MultivaluedMap<String, String> requestHeaders = new InBoundHeaders();
        requestHeaders.add(
            "dtSa",
            "true%7CC%7C-1%7CSENAT%2019384%7C-%7C1619700395317%7C300348208_882%7Chttps%3A%2F%2Freponses-preprod-2ng.ader.gouv.fr%2Freponses%2Fclassement%2Fliste%3FisTableChangeEvent%3Dtrue%26dateSort%3Ddesc%26dateSortOrder%3D1%26origine%3DSENAT%26cle%3DD_25C3_25A9fense%26cleParent%3DTh_25C3_25A8me%26size%3D10%26page%3D3%7CPlan%20de%20classement%20-%20R%C3%A9ponses%7C1619700355179%7C%7C"
        );

        when(request.getRequestHeaders()).thenReturn(requestHeaders);

        Throwable throwable = catchThrowable(() -> solonSecurityFilter.filter(request));

        assertThat(throwable).isExactlyInstanceOf(STValidationException.class).hasMessage(ERROR_MESSAGE);
    }

    @Test
    public void testFilterRequestHeadersWithValidEncodedUrl() {
        MultivaluedMap<String, String> requestHeaders = new InBoundHeaders();
        requestHeaders.add(
            "dtSa",
            "true%7CC%7C-1%7CSENAT%2019384%7C-%7C1619700395317%7C300348208_882%7Chttps%3A%2F%2Freponses-preprod-2ng.ader.gouv.fr%2Freponses%2Fclassement%2Fliste%3FisTableChangeEvent%3Dtrue%26dateSort%3Ddesc%26dateSortOrder%3D1%26cle%3DD_25C3_25A9fense%26cleParent%3DTh_25C3_25A8me%26size%3D10%26page%3D3%7CPlan%20de%20classement%20-%20R%C3%A9ponses%7C1619700355179%7C%7C"
        );

        when(request.getRequestHeaders()).thenReturn(requestHeaders);

        ContainerRequest result = solonSecurityFilter.filter(request);

        assertThat(result).isEqualTo(request);
    }

    @Test
    public void testFilterRequestHeadersWithUnencodedUrl() {
        MultivaluedMap<String, String> requestHeaders = new InBoundHeaders();
        requestHeaders.add(
            "dtSa",
            "true|C|-1|SENAT 19384|-|1619700395317|300348208_882|https://reponses-preprod-2ng.ader.gouv.fr/reponses/classement/liste?isTableChangeEvent=true&dateSort=desc&dateSortOrder=1&origine=SENAT&cle=D_25C3_25A9fense&cleParent=Th_25C3_25A8me&size=10&page=3|Plan de classement - Réponses|1619700355179||"
        );

        when(request.getRequestHeaders()).thenReturn(requestHeaders);

        ContainerRequest result = solonSecurityFilter.filter(request);

        assertThat(result).isEqualTo(request);
    }

    @Test
    public void testFilterRequestHeadersWithInvalidEncodedValue() {
        MultivaluedMap<String, String> requestHeaders = new InBoundHeaders();
        requestHeaders.add("dtSa", "%26or");

        when(request.getRequestHeaders()).thenReturn(requestHeaders);

        Throwable throwable = catchThrowable(() -> solonSecurityFilter.filter(request));

        assertThat(throwable).isExactlyInstanceOf(STValidationException.class).hasMessage(ERROR_MESSAGE);
    }

    @Test
    public void testFilterRequestHeadersWithInvalidFormValue() {
        when(request.getQueryParameters()).thenReturn(new MultivaluedMapImpl());
        when(request.getRequestHeaders()).thenReturn(new MultivaluedMapImpl());

        Form formProperties = new Form();
        formProperties.add("expediteur", "ne-pas-repondre-dev1@reponses2ng.com");
        formProperties.add("objet", "object mail test");
        formProperties.add("message", "<p>&amp;alpha</p>");

        Map<String, Object> properties = ImmutableMap.of("com.sun.jersey.api.representation.form", formProperties);

        when(request.getProperties()).thenReturn(properties);

        Throwable throwable = catchThrowable(() -> solonSecurityFilter.filter(request));

        assertThat(throwable).isExactlyInstanceOf(STValidationException.class).hasMessage(ERROR_MESSAGE);
    }
}
