package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static fr.dila.st.ui.th.model.SpecificContext.MESSAGE_QUEUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.PasswordService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { STServiceLocator.class, STUIServiceLocator.class, STUserManagerActionsDTO.class, UserSessionHelper.class }
)
@PowerMockIgnore("javax.management.*")
public class STUserManagerUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STUserManagerUIServiceImpl service;

    @Mock
    private UserManager um;

    @Mock
    private PasswordService passwdService;

    @Mock
    private DocumentModel userModel;

    private SpecificContext context;

    @Mock
    private SolonAlertManager alertManager;

    @Before
    public void before() throws Exception {
        service = spy(new STUserManagerUIServiceImpl());
        context = spy(new SpecificContext());

        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(um);

        mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getPasswordService()).thenReturn(passwdService);

        mockStatic(UserSessionHelper.class);

        doReturn(alertManager).when(context).getMessageQueue();
    }

    @Test
    public void testInitUserContext() {
        final String userId = "user";
        context.putInContextData(USER_ID, userId);
        when(um.getUserModel(userId)).thenReturn(userModel);

        doReturn(true).when(service).getAllowCreateUser(context);
        doReturn(true).when(service).getAllowEditUser(context);
        doReturn(true).when(service).getAllowDeleteUser(context);

        service.initUserContext(context);
        DocumentModel doc = context.getCurrentDocument();
        assertThat(doc).isNotNull();
        assertThat(doc).isEqualTo(userModel);

        STUserManagerActionsDTO userActions = context.getFromContextData(STContextDataKey.USER_ACTIONS);
        assertThat(userActions).isNotNull();
        assertThat(userActions.getIsCreateUserAllowed()).isTrue();
        assertThat(userActions.getIsEditUserAllowed()).isTrue();
        assertThat(userActions.getIsDeleteUserAllowed()).isTrue();
    }

    @Test
    public void testUpdatePassword() {
        doReturn(mock(Action.class)).when(context).getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE);
        final String newPasswd = "Solon2@AG";
        doReturn(newPasswd).when(context).getFromContextData(STContextDataKey.NEW_USER_PASSWORD);

        service.updatePassword(context);

        verify(passwdService).updatePassword(context, newPasswd);
        verify(alertManager).addSuccessToQueue(anyString());

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, MESSAGE_QUEUE, alertManager);
    }

    @Test
    public void testUpdatePasswordWithNoRight() {
        // Given
        doReturn(null).when(context).getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> service.updatePassword(context));

        // Then
        assertThat(throwable).isInstanceOf(STAuthorizationException.class);
    }
}
