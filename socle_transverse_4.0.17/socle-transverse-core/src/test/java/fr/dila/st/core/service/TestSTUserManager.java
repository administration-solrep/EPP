package fr.dila.st.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.test.STFeature;
import fr.dila.st.core.user.STPasswordHelper;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.commons.codec.Charsets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.usermanager.UserManager.MatchType;
import org.nuxeo.ecm.platform.usermanager.UserManagerDescriptor;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STFeature.class, SolonMockitoFeature.class })
public class TestSTUserManager {
    private static final String USER_DIRECTORY = "userDirectory";
    private static final String GROUP_DIRECTORY = "groupDirectory";
    private static final Pattern USER_PASSWORD_PATTERN = Pattern.compile(
        "^(?!.*\\s)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~`!@#$%^&*()_+={}\\[\\]|:;\"'<>,.?/-]).{8,}$"
    );

    @Inject
    STUserManagerImpl userManager;

    UserManagerDescriptor userManagerDescriptor;

    @Mock
    @RuntimeService
    private STPersistanceService persistanceService;

    @Mock
    @RuntimeService
    private STProfilUtilisateurService profilUtilisateurService;

    @Mock
    @RuntimeService
    private STMailService stMailService;

    @Before
    public void setUp() {
        userManagerDescriptor = new UserManagerDescriptor();
        userManagerDescriptor.userSearchFields = new HashMap<>();
        userManagerDescriptor.userSearchFields.put("username", MatchType.SUBSTRING);
        userManagerDescriptor.userSearchFields.put("firstName", MatchType.SUBSTRING);
        userManagerDescriptor.userSearchFields.put("lastName", MatchType.SUBSTRING);
        userManagerDescriptor.groupSearchFields.put("groupname", MatchType.SUBSTRING);
        userManagerDescriptor.userDirectoryName = USER_DIRECTORY;
        userManagerDescriptor.groupDirectoryName = GROUP_DIRECTORY;
        userManagerDescriptor.groupMembersField = "members";
        userManagerDescriptor.userPasswordPattern = USER_PASSWORD_PATTERN;

        userManager.setConfiguration(userManagerDescriptor);
    }

    /*
     * Test qui a pour objectif de vérifier qu'on trouve bien des utilisateurs
     * lorsqu'on lance une recherche avec un critère vide complet de l'utilisateur
     */
    @Test
    public void testGetUserSuggestions() {
        String inputTest = "";
        List<DocumentModel> docModelsRetournes = userManager.searchUsers(inputTest);
        assertNotNull(docModelsRetournes);
        assertEquals(5, docModelsRetournes.size());
    }

    @Test
    public void testGetGroupsSuggestions() {
        List<DocumentModel> docModelsRetournes = userManager.searchGroups(new QueryBuilder(), null);
        assertNotNull(docModelsRetournes);
        assertEquals(3, docModelsRetournes.size());
    }

    @Test
    public void testGetUsersFromGroups() {
        NuxeoGroup group1 = userManager.getGroup("group_1");

        assertNotNull(group1);
        assertNotNull(group1.getMemberUsers());
        assertEquals(1, group1.getMemberUsers().size());
    }

    @Test
    public void testUserCreation() {
        when(persistanceService.checkPasswordHistory(anyString(), anyString())).thenReturn(false);
        doNothing().when(stMailService).sendMailUserPasswordCreation(any(CoreSession.class), anyString(), anyString());
        doNothing().when(profilUtilisateurService).changeDatePassword(any(CoreSession.class), anyString());

        DocumentModel newUserDoc = userManager.getBareUserModel();
        newUserDoc.setPropertyValue("username", "totoSolon");
        newUserDoc.setPropertyValue("password", "password123");
        newUserDoc.setPropertyValue("firstName", "toto");
        newUserDoc.setPropertyValue("lastName", "Solon");

        userManager.createUser(newUserDoc);
        DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
        try (Session userDir = directoryService.getDirectory(USER_DIRECTORY).getSession()) {
            userDir.setReadAllColumns(true);
            DocumentModel userCreated = userDir.getEntry("totoSolon");
            assertNotNull(userCreated);

            STUser user = userCreated.getAdapter(STUser.class);
            assertNotNull(user.getSalt());
            assertNotNull(user.getPassword());
            assertNotEquals("password123", user.getPassword());
            assertTrue(user.getPassword().startsWith("{SSHA}"));
        }
    }

    @Test
    public void testUserUpdate() {
        DocumentModel newUserDoc = userManager.getBareUserModel();
        newUserDoc.setPropertyValue("username", "monSolon");
        newUserDoc.setPropertyValue("password", "password123");
        newUserDoc.setPropertyValue("firstName", "mon");
        newUserDoc.setPropertyValue("lastName", "Solon");

        userManager.createUser(newUserDoc);
        DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
        try (Session userDir = directoryService.getDirectory(USER_DIRECTORY).getSession()) {
            userDir.setReadAllColumns(true);
            DocumentModel userCreated = userDir.getEntry("monSolon");
            userCreated.setPropertyValue("password", "password456");

            // On vérifie que la mise à jour de mot de passe le hashe correctement
            userManager.updateUser(userCreated);
            DocumentModel userUpdated = userDir.getEntry("monSolon");
            STUser user = userUpdated.getAdapter(STUser.class);
            assertNotNull(user);
            assertNotEquals("password456", userUpdated.getProperties("password"));
            byte[] salt = user.getSalt().getBytes(Charsets.UTF_8);
            String hashedPassword = user.getPassword();
            assertTrue(STPasswordHelper.verifyPassword("password456", salt, hashedPassword));
            assertFalse(STPasswordHelper.verifyPassword("password123", salt, hashedPassword));

            // On vérifie qu'une mise à jour de champ normal ne met pas à jour le mot de passe
            user.setDateFin(Calendar.getInstance());
            userManager.updateUser(user.getDocument());

            DocumentModel userUpdated2 = userDir.getEntry("monSolon");
            STUser user2 = userUpdated2.getAdapter(STUser.class);
            assertEquals(new String(salt), user2.getSalt());
            assertEquals(hashedPassword, user2.getPassword());

            user.setPwdReset(true);
            userManager.updateUser(user.getDocument(), (DocumentModel) null);

            userUpdated2 = userDir.getEntry("monSolon");
            user2 = userUpdated2.getAdapter(STUser.class);
            assertEquals(hashedPassword, user2.getPassword());
            hashedPassword = "";
            assertTrue(user2.isPwdReset());
        }
    }

    @Test
    public void testUserAuthent() {
        Calendar lastMonth = DateUtil.removeMonthsToNow(1);

        DocumentModel newUserDoc = userManager.getBareUserModel();
        byte[] salt = STPasswordHelper.generateSalt();
        newUserDoc.setPropertyValue("username", "titiSolon");
        newUserDoc.setPropertyValue("salt", new String(salt));
        newUserDoc.setPropertyValue("password", STPasswordHelper.hashPassword("password123", salt));
        newUserDoc.setPropertyValue("firstName", "titi");
        newUserDoc.setPropertyValue("lastName", "Solon");

        userManager.createUser(newUserDoc, null);

        DocumentModel newUserDoc2 = userManager.getBareUserModel();
        byte[] salt2 = STPasswordHelper.generateSalt();
        newUserDoc2.setPropertyValue("username", "oldSolon");
        newUserDoc2.setPropertyValue("salt", new String(salt2));
        newUserDoc2.setPropertyValue("password", STPasswordHelper.hashPassword("password123", salt2));
        newUserDoc2.setPropertyValue("firstName", "old");
        newUserDoc2.setPropertyValue("lastName", "Solon");
        newUserDoc2.setPropertyValue("dateFin", lastMonth);

        userManager.createUser(newUserDoc2, null);

        assertNotEquals(
            newUserDoc.getProperty("password").getValue().toString(),
            newUserDoc2.getProperty("password").getValue().toString()
        );

        assertFalse(userManager.checkUsernamePassword("titiSolon", null));
        assertFalse(userManager.checkUsernamePassword("titiSolon", "Password123"));
        assertFalse(userManager.checkUsernamePassword("oldSolon", "password123"));
        assertFalse(userManager.checkUsernamePassword("titaSolon", "password123"));
        assertTrue(userManager.checkUsernamePassword("titiSolon", "password123"));
    }

    @Test
    public void validatePassword() {
        List<String> values = ImmutableList.of("[-%^A54a", "Az1]By9<", "~`!@#$%^&*()_-+={}[]|:;\"'<>,.?/Aa1");

        assertThat(values).allSatisfy(password -> assertThat(userManager.validatePassword(password)).isTrue());
    }

    @Test
    public void validatePasswordShouldFailWithBadPassword() {
        List<String> values = ImmutableList.of(
            "[-)^A5a",
            "[-)^ A5a",
            "aaaaaaaa",
            "AAAAAAAA",
            "11111111",
            "@@@@@@@@",
            "aaaaAAAA",
            "aaaa1111",
            "aaaa@@@@",
            "AAAA1111",
            "AAAA@@@@",
            "1111@@@@",
            "aaaAAA98",
            "aaaAAA@@@",
            "999aaa@_{",
            "#'[999AAA"
        );

        assertThat(values).allSatisfy(badPassword -> assertThat(userManager.validatePassword(badPassword)).isFalse());
    }
}
