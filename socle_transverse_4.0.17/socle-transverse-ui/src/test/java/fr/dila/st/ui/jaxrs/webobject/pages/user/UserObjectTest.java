package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.PasswordService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ UserSessionHelper.class, STServiceLocator.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class UserObjectTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private UserObject controller;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private UserManager userManager;

    @Mock
    private STUserManagerUIService userManangerActionService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private DocumentModel user;

    @Mock
    private SolonAlertManager messageQueue;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);

        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getPasswordService()).thenReturn(passwordService);

        when(STUIServiceLocator.getSTUserManagerUIService()).thenReturn(userManangerActionService);

        when(context.getMessageQueue()).thenReturn(messageQueue);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getSession()).thenReturn(session);

        when(session.getPrincipal()).thenReturn(principal);
        when(userManager.getUserModel("user")).thenReturn(user);

        PowerMockito.mockStatic(UserSessionHelper.class);

        controller = new UserObject();

        Whitebox.setInternalState(controller, "context", context);
    }

    @Test
    public void testUpdatePassword() {
        // Given
        when(context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE)).thenReturn(new Action());
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);
        when(principal.getName()).thenReturn("user");

        // When
        Response response = controller.updatePassword("Solon!2NG");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());

        verify(context).putInContextData(STContextDataKey.USER_ID, "user");
        verify(userManangerActionService).initUserContext(context);
        verify(context).putInContextData(STContextDataKey.NEW_USER_PASSWORD, "Solon!2NG");
        verify(userManangerActionService).updatePassword(context);
    }

    @Test
    public void testUpdatePasswordWithInvalidPassword() {
        // Given
        when(context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE)).thenReturn(new Action());
        doThrow(new STValidationException("")).when(userManangerActionService).updatePassword(context);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> controller.updatePassword("pass"));

        // Then
        assertThat(throwable).isInstanceOf(STValidationException.class);
        Mockito.verify(messageQueue, never()).addSuccessToQueue(Mockito.anyString());
        PowerMockito.verifyStatic(never());
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, messageQueue);
    }

    @Test
    public void testUpdatePasswordForUser() {
        // Given
        when(context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE)).thenReturn(new Action());
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);

        // When
        Response response = controller.updatePasswordForUser("Solon!2NG", "user");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());

        verify(context).putInContextData(STContextDataKey.USER_ID, "user");
        verify(userManangerActionService).initUserContext(context);
        verify(context).putInContextData(STContextDataKey.NEW_USER_PASSWORD, "Solon!2NG");
        verify(userManangerActionService).updatePassword(context);
    }

    @Test
    public void testUpdatePasswordForUserWithInvalidPassword() {
        // Given
        when(context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE)).thenReturn(new Action());
        doThrow(new STValidationException("")).when(userManangerActionService).updatePassword(context);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> controller.updatePasswordForUser("pass", "user"));

        // Then
        assertThat(throwable).isInstanceOf(STValidationException.class);
    }

    @Test
    public void testUpdatePasswordForUserWithEmptyUser() {
        // Given
        when(context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE)).thenReturn(new Action());

        // When
        Throwable throwable = Assertions.catchThrowable(() -> controller.updatePasswordForUser("user", ""));

        // Then
        assertThat(throwable).isInstanceOf(STValidationException.class);
    }
}
