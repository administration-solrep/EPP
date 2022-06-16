package fr.dila.ss.ui.services.impl;

import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME;
import static fr.dila.st.ui.enums.STContextDataKey.USER_FORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.Collections;
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
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class, SSUserManagerUIServiceImpl.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSUserManagerUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private CoreSession session;

    @Mock
    private SSPrincipal principal;

    @Mock
    private UserManager userManager;

    @Mock
    private STUtilisateursUIService utilisateursUIService;

    private SSUserManagerUIServiceImpl actionService = new SSUserManagerUIServiceImpl();

    private SpecificContext context = new SpecificContext();

    @Before
    public void setUp() {
        context = spy(new SpecificContext());
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);

        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getSTUtilisateursUIService()).thenReturn(utilisateursUIService);

        context.setSession(session);
        when(session.getPrincipal()).thenReturn(principal);
    }

    @Test
    public void testCanDeleteCannotDeleteSelf() {
        // Given
        DocumentModel user = mock(DocumentModel.class);
        context.setCurrentDocument(user);

        when(principal.getName()).thenReturn("admin");
        when(user.getId()).thenReturn("admin");

        // When
        boolean deleteAllowed = actionService.getAllowDeleteUser(context);

        // Then
        assertThat(deleteAllowed).isFalse();
    }

    @Test
    public void testCanDeleteWithFunctionnalAdmin() {
        // Given
        DocumentModel user = mock(DocumentModel.class);
        context.setCurrentDocument(user);

        when(principal.getName()).thenReturn("admin");
        when(principal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)).thenReturn(true);
        when(user.getId()).thenReturn("user");

        // When
        boolean deleteAllowed = actionService.getAllowDeleteUser(context);

        // Then
        assertThat(deleteAllowed).isTrue();
    }

    @Test
    public void testCanDeleteWithMinisterielAdmin() {
        // Given
        DocumentModel user = mock(DocumentModel.class);
        context.setCurrentDocument(user);

        when(principal.getName()).thenReturn("admin");
        when(principal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_DELETER)).thenReturn(true);

        when(user.getId()).thenReturn("user");
        SSPrincipal userPrincipal = mock(SSPrincipal.class);
        when(userManager.getPrincipal("user")).thenReturn(userPrincipal);

        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1"));
        when(userPrincipal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1"));

        // When
        boolean deleteAllowed = actionService.getAllowDeleteUser(context);

        // Then
        assertThat(deleteAllowed).isTrue();
    }

    @Test
    public void testCanDeleteWithMinisterielAdminCannotDeleteFunctionnalAdmin() {
        // Given
        DocumentModel user = mock(DocumentModel.class);
        context.setCurrentDocument(user);

        when(principal.getName()).thenReturn("admin");
        when(principal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_MINISTERE_DELETER)).thenReturn(true);

        when(user.getId()).thenReturn("user");
        SSPrincipal userPrincipal = mock(SSPrincipal.class);
        when(userManager.getPrincipal("user")).thenReturn(userPrincipal);
        when(userPrincipal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)).thenReturn(true);

        // When
        boolean deleteAllowed = actionService.getAllowDeleteUser(context);

        // Then
        assertThat(deleteAllowed).isFalse();
    }

    @Test
    public void testCanEditCanEditSelf() {
        // Given
        DocumentModel user = mock(DocumentModel.class);
        context.setCurrentDocument(user);

        when(principal.getName()).thenReturn("admin");
        when(user.getId()).thenReturn("admin");

        // When
        boolean deleteAllowed = actionService.getCanEditUsers(context, true);

        // Then
        assertThat(deleteAllowed).isTrue();
    }

    @Test
    public void testInSameMinistere() {
        // Given
        SSPrincipal user = mock(SSPrincipal.class);

        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1", "Min2"));
        when(user.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min2"));

        // When
        boolean inSameMinistere = actionService.inSameFonctionalGroup(principal, user);

        // Then
        assertThat(inSameMinistere).isTrue();
    }

    @Test
    public void testNotInSameMinistere() {
        // Given
        SSPrincipal user = mock(SSPrincipal.class);

        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1", "Min2"));
        when(user.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min3"));

        // When
        boolean inSameMinistere = actionService.inSameFonctionalGroup(principal, user);

        // Then
        assertThat(inSameMinistere).isFalse();
    }

    @Test
    public void testUserCanUpdate() {
        initUpdateUserTest();

        actionService.updateUser(context);

        verify(utilisateursUIService).updateDocWithUserForm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testUserCannotUpdateProtectedProps() {
        STUser newUser = initUpdateUserTest();

        when(newUser.getGroups()).thenReturn(Collections.singletonList(ADMIN_FONCTIONNEL_GROUP_NAME));

        Throwable throwable = Assertions.catchThrowable(() -> actionService.updateUser(context));

        // user realizes he can't fraud -> big deception !
        assertThat(throwable).isInstanceOf(STAuthorizationException.class);

        verify(utilisateursUIService, never()).updateDocWithUserForm(Mockito.any(), Mockito.any());
    }

    @Test
    public void testAdministratorCanUpdateProtectedProps() {
        STUser newUser = initUpdateUserTest();

        when(newUser.getGroups()).thenReturn(Collections.singletonList("group2"));

        // user is super admin
        when(principal.isAdministrator()).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> actionService.updateUser(context));

        // permission definitely granted
        assertThat(throwable).isNull();
    }

    @Test
    public void testAdminFoncCanUpdateProtectedProps() {
        STUser newUser = initUpdateUserTest();

        when(newUser.getGroups()).thenReturn(Collections.singletonList("group2"));

        // user is admin fonc
        when(principal.isAdministrator()).thenReturn(false);
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> actionService.updateUser(context));

        // permission granted
        assertThat(throwable).isNull();
    }

    @Test
    public void testAdminMinCanUpdateProtectedPropsForUserInSameMinistere() {
        STUser newUser = initUpdateUserTest();

        when(newUser.getGroups()).thenReturn(Collections.singletonList("group2"));

        SSPrincipal userPrincipal = mock(SSPrincipal.class);
        when(userManager.getPrincipal(newUser.getUsername())).thenReturn(userPrincipal);

        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1"));
        when(userPrincipal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1"));

        // user is admin min and selected user is in same ministere
        when(principal.isAdministrator()).thenReturn(false);
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(false);
        when(principal.isMemberOf(ADMIN_MINISTERIEL_GROUP_NAME)).thenReturn(true);

        actionService.updateUser(context);
    }

    @Test
    public void testAdminMinCannotUpdateProtectedPropsForUserInDifferentMinistere() {
        STUser newUser = initUpdateUserTest();

        when(newUser.getGroups()).thenReturn(Collections.singletonList("group2"));

        // user is admin min but selected user is not in same ministere
        when(principal.isAdministrator()).thenReturn(false);
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(false);
        when(principal.isMemberOf(ADMIN_MINISTERIEL_GROUP_NAME)).thenReturn(true);

        SSPrincipal userPrincipal = mock(SSPrincipal.class);
        when(userManager.getPrincipal(newUser.getUsername())).thenReturn(userPrincipal);

        when(principal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min1"));
        when(userPrincipal.getMinistereIdSet()).thenReturn(ImmutableSet.of("Min2"));

        Throwable throwable = Assertions.catchThrowable(() -> actionService.updateUser(context));

        // permission denied
        assertThat(throwable).isInstanceOf(STAuthorizationException.class);

        verify(utilisateursUIService, never()).updateDocWithUserForm(Mockito.any(), Mockito.any());
    }

    private STUser initUpdateUserTest() {
        final String username = "toto";
        // mock user
        DocumentModel newUserDoc = mock(DocumentModel.class);
        STUser newUser = mock(STUser.class);
        when(newUserDoc.getAdapter(STUser.class)).thenReturn(newUser);
        when(newUser.getUsername()).thenReturn(username);

        when(context.getCurrentDocument()).thenReturn(newUserDoc);

        // protected props are not modified -> permission granted
        when(newUser.isTemporary()).thenReturn(true);
        when(newUser.getGroups()).thenReturn(Collections.singletonList("group1"));
        when(newUser.getPostes()).thenReturn(Collections.singletonList("poste1"));
        Calendar now = Calendar.getInstance();
        when(newUser.getDateFin()).thenReturn(now);

        UserForm form = new UserForm();
        form.setUtilisateur(username);
        form.setOccasionnel(true);
        form.setProfils(Lists.newArrayList("group1"));
        form.setPostes(Lists.newArrayList("poste1"));
        form.setDateFin(SolonDateConverter.DATE_SLASH.format(now));
        when(context.getFromContextData(USER_FORM)).thenReturn(form);

        // user is not admin
        when(principal.isAdministrator()).thenReturn(false);
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(false);
        when(principal.isMemberOf(ADMIN_MINISTERIEL_GROUP_NAME)).thenReturn(false);

        return newUser;
    }
}
