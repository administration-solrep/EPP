package fr.dila.st.core.requeteur;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.Profile;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.test.STCommonFeature;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STCommonFeature.class, SolonMockitoFeature.class })
@Deploy("fr.dila.st.core.test:OSGI-INF/test-core-type-contrib.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-default-user-directory.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-config-contrib.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/cmf/cm-document-type-contrib.xml")
public class RequeteurRechercheUtilisateursIT {
    @Inject
    private UserManager userManager;

    @Inject
    private DirectoryService dirService;

    @Mock
    @RuntimeService
    private STPostesService postesService;

    @Mock
    private PosteNode poste50000656;

    @Mock
    private PosteNode poste50002249;

    private static final String POSTE_50000656 = "50000656";
    private static final String POSTE_50002249 = "50002249";

    @Before
    public void setUp() throws ParseException {
        createTestGroups();
        createTestUsers();
        assertThat(userManager.getUserIds()).isNotEmpty();

        STUser toto = userManager.getUserModel("toto").getAdapter(STUser.class);
        STUser titi = userManager.getUserModel("titi").getAdapter(STUser.class);

        Mockito.when(postesService.getUserFromPoste(POSTE_50000656)).thenReturn(Arrays.asList(toto, titi));

        Mockito.when(postesService.getUserFromPoste(POSTE_50002249)).thenReturn(Arrays.asList(toto));
    }

    @Test
    public void testSearchUsersByStringParams() {
        try (Session session = dirService.open(STConstant.ORGANIGRAMME_USER_DIR)) {
            RequeteurRechercheUtilisateurs ur = new RequeteurRechercheUtilisateurs.Builder()
                .firstName("Toto")
                .lastName("Lavignasse")
                .build();
            List<DocumentModel> users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
            STUser stuser = users.get(0).getAdapter(STUser.class);
            assertThat(stuser.getUsername()).isEqualTo("toto");
        }
    }

    @Test
    public void testSearchUsersByDateParams() throws ParseException {
        try (Session session = dirService.open(STConstant.ORGANIGRAMME_USER_DIR)) {
            // dateDebut_min <= 10/11/2020
            RequeteurRechercheUtilisateurs ur = new RequeteurRechercheUtilisateurs.Builder()
                .dateDebutMax(DateUtil.localDateToDate(LocalDate.of(2020, 11, 10)))
                .build();
            List<DocumentModel> users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(2);

            // dateDebut_min <= 30/09/2020
            ur =
                new RequeteurRechercheUtilisateurs.Builder()
                    .dateDebutMax(DateUtil.localDateToDate(LocalDate.of(2020, 9, 30)))
                    .build();
            users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
            STUser stuser = users.get(0).getAdapter(STUser.class);
            assertThat(stuser.getUsername()).isEqualTo("titi");

            // dateDebut_min <= 05/10/2020 and dateFin_max >= 06/10/2020
            ur =
                new RequeteurRechercheUtilisateurs.Builder()
                    .dateDebutMax(DateUtil.localDateToDate(LocalDate.of(2020, 10, 05)))
                    .dateFin(DateUtil.localDateToDate(LocalDate.of(2020, 10, 06)))
                    .build();
            users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
            stuser = users.get(0).getAdapter(STUser.class);
            assertThat(stuser.getUsername()).isEqualTo("toto");
        }
    }

    @Test
    public void testSearchUsersDeleted() throws ParseException {
        userManager.deleteUser("titi");
        try (Session session = dirService.open(STConstant.ORGANIGRAMME_USER_DIR)) {
            RequeteurRechercheUtilisateurs ur = new RequeteurRechercheUtilisateurs.Builder()
                .telephoneNumber("0670707070")
                .build();
            List<DocumentModel> users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
        }
    }

    @Test
    public void testSearchUsersByPoste() {
        try (Session session = dirService.open(STConstant.ORGANIGRAMME_USER_DIR)) {
            RequeteurRechercheUtilisateurs ur = new RequeteurRechercheUtilisateurs.Builder()
                .postes(asList(POSTE_50000656))
                .build();
            List<DocumentModel> users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(2);

            ur = new RequeteurRechercheUtilisateurs.Builder().postes(asList(POSTE_50002249)).build();
            users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
            STUser stuser = users.get(0).getAdapter(STUser.class);
            assertThat(stuser.getUsername()).isEqualTo("toto");
        }
    }

    @Test
    public void testSearchUsersByGroup() {
        try (Session session = dirService.open(STConstant.ORGANIGRAMME_USER_DIR)) {
            RequeteurRechercheUtilisateurs ur = new RequeteurRechercheUtilisateurs.Builder()
                .groups(asList("Administrateur ministériel"))
                .build();
            List<DocumentModel> users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(2);

            ur = new RequeteurRechercheUtilisateurs.Builder().groups(asList("Administrateur Reader")).build();
            users = ur.execute();
            assertThat(users).isNotNull();
            assertThat(users.size()).isEqualTo(1);
            STUser stuser = users.get(0).getAdapter(STUser.class);
            assertThat(stuser.getUsername()).isEqualTo("toto");
        }
    }

    private void createTestGroups() throws ParseException {
        createGroup("Administrateur ministériel", Arrays.asList("OrganigrammeReader", "OrganigrammeMinistereUpdater"));
        createGroup("Administrateur Reader", Arrays.asList("OrganigrammeReader"));
    }

    private void createGroup(String groupname, List<String> functions) {
        DocumentModel groupModel = userManager.getBareGroupModel();
        Profile profile = groupModel.getAdapter(Profile.class);
        groupModel.setPropertyValue("groupname", groupname);
        profile.setBaseFunctionList(functions);
        userManager.createGroup(groupModel);
    }

    private void createTestUsers() throws ParseException {
        Calendar calDebut = DateUtil.toCalendarFromNotNullDate(SolonDateConverter.DATE_DASH.parseToDate("05-10-2020"));
        Calendar calFin = DateUtil.toCalendarFromNotNullDate(SolonDateConverter.DATE_DASH.parseToDate("10-10-2020"));
        createUser(
            "toto",
            "Toto",
            "Lavignasse",
            "0670707070",
            Arrays.asList(POSTE_50002249, POSTE_50000656),
            Arrays.asList("Administrateur ministériel", "Administrateur Reader"),
            calDebut,
            calFin,
            "Rue",
            "75001",
            "Bose"
        );

        calDebut.setTime(SolonDateConverter.DATE_DASH.parseToDate("05-09-2020"));
        calFin.setTime(SolonDateConverter.DATE_DASH.parseToDate("09-09-2020"));
        createUser(
            "titi",
            "Titi",
            "Madelaine",
            "0670707070",
            Arrays.asList(POSTE_50000656),
            Arrays.asList("Administrateur ministériel"),
            calDebut,
            calFin,
            "Rue",
            "75001",
            "Bose"
        );
    }

    private void createUser(
        String username,
        String firstName,
        String lastName,
        String telephoneNumber,
        List<String> postes,
        List<String> groups,
        Calendar dateDebut,
        Calendar dateFin,
        String postalAddress,
        String postalCode,
        String locality
    ) {
        DocumentModel user = userManager.getBareUserModel();
        STUser stuser = user.getAdapter(STUser.class);
        stuser.setUsername(username);
        stuser.setFirstName(firstName);
        stuser.setLastName(lastName);
        stuser.setTelephoneNumber(telephoneNumber);
        stuser.setEmail(username + "@idl-demo.com");
        stuser.setPostes(postes);
        stuser.setGroups(groups);
        stuser.setDateDebut(dateDebut);
        stuser.setDateFin(dateFin);
        stuser.setPostalAddress(postalAddress);
        stuser.setPostalCode(postalCode);
        stuser.setLocality(locality);

        userManager.createUser(user);
    }
}
