package fr.dila.st.core.service;

import static fr.dila.st.core.service.STUserServiceImpl.PASSWORD_MIN_LENGTH;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.platform.ui.web.auth.service.BruteforceSecurityService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class, STServiceLocator.class, SessionUtil.class })
@PowerMockIgnore("javax.management.*")
public class STUserServiceImplTest {
    private static final String USERID = "userId";
    private static final String USERNAME = "username";
    private static final String FULLNAME = "Prénom Nom";
    private static final String FULLNAME_WITH_USERNAME = "Prénom Nom (username)";
    private static final String START_HASHED_PASSWORD = "{SSHA}";
    private static final String SALT = "EqbfhyYaCAdahimjSfnxuA==";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STUserServiceImpl service;

    @Mock
    protected DocumentModel userDoc;

    @Mock
    protected STUser user;

    @Mock
    protected CloseableCoreSession session;

    @Mock
    private UserManager userManager;

    @Mock
    private STPersistanceService persistanceService;

    @Mock
    private STProfilUtilisateurService profilUtilisateurService;

    @Mock
    private BruteforceSecurityService bruteforceSecurityService;

    @Before
    public void setUp() {
        service = new STUserServiceImpl();

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(STServiceLocator.getSTPersistanceService()).thenReturn(persistanceService);
        when(STServiceLocator.getSTProfilUtilisateurService()).thenReturn(profilUtilisateurService);
        when(STServiceLocator.getBruteforceSecurityService()).thenReturn(bruteforceSecurityService);

        PowerMockito.mockStatic(Framework.class);
        when(Framework.doPrivileged(Matchers.<Supplier<STUser>>any())).thenReturn(user);

        PowerMockito.mockStatic(SessionUtil.class);
        when(SessionUtil.openSession()).thenReturn(session);

        when(user.getDocument()).thenReturn(userDoc);
        when(user.getUsername()).thenReturn(USERNAME);

        doNothing().when(userManager).updateUser(userDoc);
    }

    @Test
    public void generateAndSaveNewUserPassword() {
        when(userManager.validatePassword(anyString())).thenReturn(true);
        when(userManager.checkUsernamePassword(eq(USERID), anyString())).thenReturn(false);
        when(persistanceService.checkPasswordHistory(anyString(), eq(USERID))).thenReturn(false);

        String newValue = service.generateAndSaveNewUserPassword(USERID);

        assertThat(newValue.length()).isEqualTo(PASSWORD_MIN_LENGTH);

        verify(userManager).validatePassword(newValue);
        verify(userManager).checkUsernamePassword(USERID, newValue);
        verify(persistanceService).checkPasswordHistory(newValue, USERID);
        verify(user).setSalt(anyString());
        verify(user).setPassword(startsWith(START_HASHED_PASSWORD));
        verify(user).setPwdReset(true);
        verify(profilUtilisateurService).changeDatePassword(session, USERNAME);
        PowerMockito.verifyStatic();
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verify(bruteforceSecurityService).deleteLoginInfos(USERNAME);
        verifyNoMoreInteractions(persistanceService);
        verifyNoMoreInteractions(bruteforceSecurityService);
    }

    @Test
    public void generateAndSaveNewUserPasswordWithExistingUserPassword() {
        String currentValue = "mxXL[_84";
        String currentSalt = SALT;

        when(userManager.validatePassword(anyString())).thenReturn(true);
        when(userManager.checkUsernamePassword(eq(USERID), anyString())).thenReturn(false);
        when(persistanceService.checkPasswordHistory(anyString(), eq(USERID))).thenReturn(false);
        when(user.getPassword()).thenReturn(currentValue);
        when(user.getSalt()).thenReturn(currentSalt);

        String newPassword = service.generateAndSaveNewUserPassword(USERID);

        assertThat(newPassword).isNotEqualTo(currentValue);
        assertThat(newPassword.length()).isEqualTo(PASSWORD_MIN_LENGTH);

        verify(userManager).validatePassword(newPassword);
        verify(userManager).checkUsernamePassword(USERID, newPassword);
        verify(persistanceService).checkPasswordHistory(newPassword, USERID);
        verify(persistanceService).saveCurrentPassword(currentValue, USERNAME, currentSalt.getBytes());
        verify(user).setSalt(not(eq(currentSalt)));
        verify(user).setPassword(startsWith(START_HASHED_PASSWORD));
        verify(user).setPwdReset(true);
        verify(profilUtilisateurService).changeDatePassword(session, USERNAME);
        PowerMockito.verifyStatic();
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verify(bruteforceSecurityService).deleteLoginInfos(USERNAME);
        verifyNoMoreInteractions(persistanceService);
        verifyNoMoreInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPassword() {
        String newPassword = "NewPassword";
        String currentPassword = "CurrentPassword";
        String currentSalt = SALT;

        when(userManager.validatePassword(newPassword)).thenReturn(true);
        when(userManager.checkUsernamePassword(USERID, newPassword)).thenReturn(false);
        when(persistanceService.checkPasswordHistory(newPassword, USERID)).thenReturn(false);
        when(user.getPassword()).thenReturn(currentPassword);
        when(user.getSalt()).thenReturn(currentSalt);

        String updateValue = service.saveNewUserPassword(USERID, newPassword);

        assertThat(updateValue).isEqualTo(newPassword);

        verify(userManager).validatePassword(updateValue);
        verify(userManager).checkUsernamePassword(USERID, updateValue);
        verify(persistanceService).checkPasswordHistory(updateValue, USERID);
        verify(persistanceService).saveCurrentPassword(currentPassword, USERNAME, currentSalt.getBytes());
        verify(user).setSalt(not(eq(currentSalt)));
        verify(user).setPassword(startsWith(START_HASHED_PASSWORD));
        verify(user).setPwdReset(false);
        verify(profilUtilisateurService).changeDatePassword(session, USERNAME);
        PowerMockito.verifyStatic();
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verify(bruteforceSecurityService).deleteLoginInfos(USERNAME);
        verifyNoMoreInteractions(persistanceService);
        verifyNoMoreInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPasswordShouldFailWithBlankPassword() {
        Stream.of(null, EMPTY, "   ").forEach(this::assertBlankPassword);
    }

    private void assertBlankPassword(String blankPassword) {
        Throwable throwable = catchThrowable(() -> service.saveNewUserPassword(USERID, blankPassword));
        assertThat(throwable)
            .isInstanceOf(STValidationException.class)
            .hasMessage(ResourceHelper.getString("form.validation.password.notblank"));

        verify(userManager, never()).validatePassword(blankPassword);
        verify(userManager, never()).checkUsernamePassword(USERID, blankPassword);
        verify(user, never()).setSalt(anyString());
        verify(user, never()).setPassword(anyString());
        verify(user, never()).setPwdReset(anyBoolean());
        verify(userManager, never()).updateUser(userDoc);
        PowerMockito.verifyStatic(never());
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verifyZeroInteractions(persistanceService);
        verifyZeroInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPasswordShouldFailWithInvalidPassword() {
        String invalidPassword = "invalidPassword";

        when(userManager.validatePassword(invalidPassword)).thenReturn(false);

        Throwable throwable = catchThrowable(() -> service.saveNewUserPassword(USERID, invalidPassword));
        assertThat(throwable)
            .isInstanceOf(STValidationException.class)
            .hasMessage(ResourceHelper.getString("form.validation.password.invalid"));

        verify(userManager).validatePassword(invalidPassword);
        verify(userManager, never()).checkUsernamePassword(USERID, invalidPassword);
        verify(user, never()).setSalt(anyString());
        verify(user, never()).setPassword(anyString());
        verify(user, never()).setPwdReset(anyBoolean());
        PowerMockito.verifyStatic(never());
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verifyZeroInteractions(persistanceService);
        verifyZeroInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPasswordShouldFailWithActualPassword() {
        String actualPassword = "actualPassword";

        when(userManager.validatePassword(actualPassword)).thenReturn(true);
        when(userManager.checkUsernamePassword(USERID, actualPassword)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> service.saveNewUserPassword(USERID, actualPassword));
        assertThat(throwable)
            .isInstanceOf(STValidationException.class)
            .hasMessage(ResourceHelper.getString("form.validation.password.notcurrent"));

        verify(userManager).validatePassword(actualPassword);
        verify(userManager).checkUsernamePassword(USERID, actualPassword);
        verify(user, never()).setSalt(anyString());
        verify(user, never()).setPassword(anyString());
        verify(user, never()).setPwdReset(anyBoolean());
        PowerMockito.verifyStatic(never());
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verifyZeroInteractions(persistanceService);
        verifyZeroInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPasswordShouldFailWithHistoricalPassword() {
        String historicalPassword = "historicalPassword";

        when(userManager.validatePassword(historicalPassword)).thenReturn(true);
        when(userManager.checkUsernamePassword(USERID, historicalPassword)).thenReturn(false);
        when(persistanceService.checkPasswordHistory(historicalPassword, USERID)).thenReturn(true);

        Throwable throwable = catchThrowable(() -> service.saveNewUserPassword(USERID, historicalPassword));
        assertThat(throwable)
            .isInstanceOf(STValidationException.class)
            .hasMessage(ResourceHelper.getString("form.validation.password.nothistorical"));

        verify(userManager).validatePassword(historicalPassword);
        verify(userManager).checkUsernamePassword(USERID, historicalPassword);
        verify(persistanceService).checkPasswordHistory(historicalPassword, USERID);
        verify(user, never()).setSalt(anyString());
        verify(user, never()).setPassword(anyString());
        verify(user, never()).setPwdReset(anyBoolean());
        PowerMockito.verifyStatic(never());
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verifyZeroInteractions(bruteforceSecurityService);
    }

    @Test
    public void saveNewUserPasswordShouldFailWithPasswordContainingUsername() {
        String valueWithUsername = USERID + "Password";

        when(userManager.validatePassword(valueWithUsername)).thenReturn(true);
        when(userManager.checkUsernamePassword(USERID, valueWithUsername)).thenReturn(false);
        when(persistanceService.checkPasswordHistory(valueWithUsername, USERID)).thenReturn(false);

        Throwable throwable = catchThrowable(() -> service.saveNewUserPassword(USERID, valueWithUsername));
        assertThat(throwable)
            .isInstanceOf(STValidationException.class)
            .hasMessage(ResourceHelper.getString("form.validation.password.notusername"));

        verify(userManager).validatePassword(valueWithUsername);
        verify(userManager).checkUsernamePassword(USERID, valueWithUsername);
        verify(persistanceService).checkPasswordHistory(valueWithUsername, USERID);
        verify(user, never()).setSalt(anyString());
        verify(user, never()).setPassword(anyString());
        verify(user, never()).setPwdReset(anyBoolean());
        PowerMockito.verifyStatic(never());
        Framework.doPrivileged(Matchers.<Supplier<DocumentModel>>any());
        verifyZeroInteractions(bruteforceSecurityService);
    }

    @Test
    public void getUserFullName() {
        when(userManager.getUserModel(USERID)).thenReturn(userDoc);
        when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        when(user.getFullName()).thenReturn(FULLNAME);

        String result = service.getUserFullName(USERID);

        assertThat(result).isEqualTo(FULLNAME);
    }

    @Test
    public void getUserFullNameShouldReturnUsernameWithoutUserDoc() {
        when(userManager.getUserModel(USERID)).thenReturn(null);

        String result = service.getUserFullName(USERID);

        assertThat(result).isEqualTo(USERID);
    }

    @Test
    public void getUserFullNameShouldReturnUsernameWithException() {
        when(userManager.getUserModel(USERID)).thenThrow(new DirectoryException("message d'erreur"));

        String result = service.getUserFullName(USERID);

        assertThat(result).isEqualTo(USERID);
    }

    @Test
    public void getUserFullNameWithUsername() {
        when(userManager.getUserModel(USERID)).thenReturn(userDoc);
        when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        when(user.getFullNameWithUsername()).thenReturn(FULLNAME_WITH_USERNAME);

        String result = service.getUserFullNameWithUsername(USERID);

        assertThat(result).isEqualTo(FULLNAME_WITH_USERNAME);
    }

    @Test
    public void getLegacyUserFullName() {
        when(userManager.getUserModel(USERID)).thenReturn(userDoc);
        when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");

        String result = service.getLegacyUserFullName(USERID);

        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    public void getLegacyUserFullNameWithNoFirstName() {
        when(userManager.getUserModel(USERID)).thenReturn(userDoc);
        when(userDoc.getAdapter(STUser.class)).thenReturn(user);
        when(user.getFirstName()).thenReturn(null);
        when(user.getLastName()).thenReturn("Doe");

        String result = service.getLegacyUserFullName(USERID);

        assertThat(result).isEqualTo(" Doe");
    }

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.{12}$)(?=(?:.*?[A-Z]){3})(?=(?:.*?[a-z]){3})(?=(?:.*?[0-9]){3})(?=(?:.*?[$?!:;()\\[\\]{}+-=_\\/|*,.]){3}).*$"
    );

    @Test
    public void getRandomSecurePassword() {
        List<String> values = IntStream
            .rangeClosed(1, 100)
            .mapToObj(i -> STUserServiceImpl.getRandomSecurePassword())
            .collect(Collectors.toList());

        assertThat(values)
            .doesNotHaveDuplicates()
            .allSatisfy(password -> assertThat(password).matches(PASSWORD_PATTERN));
    }

    @Test
    public void testIsMigratedUser() {
        when(user.getPassword()).thenReturn(null);
        when(user.getSalt()).thenReturn(null);
        assertThat(service.isMigratedUser(USERNAME)).isTrue();

        when(user.getPassword()).thenReturn(null);
        when(user.getSalt()).thenReturn("notnull");
        assertThat(service.isMigratedUser(USERNAME)).isFalse();

        when(user.getPassword()).thenReturn("notnull");
        when(user.getSalt()).thenReturn(null);
        assertThat(service.isMigratedUser(USERNAME)).isFalse();

        when(user.getPassword()).thenReturn("notnull");
        when(user.getSalt()).thenReturn("notnull");
        assertThat(service.isMigratedUser(USERNAME)).isFalse();
    }
}
