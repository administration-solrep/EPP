package fr.dila.ss.core.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class UsersSimpleConfigTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private DocumentModel item;

    @Mock
    private CoreSession session;

    @Mock
    private STUser user;

    @Mock
    private STUserService userService;

    @Before
    public void setUp() {
        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTUserService()).thenReturn(userService);
    }

    @Test
    public void getDataCells() {
        when(item.getAdapter(STUser.class)).thenReturn(user);

        Calendar dateDebut = new GregorianCalendar(2021, Calendar.AUGUST, 5);
        when(user.getDateDebut()).thenReturn(dateDebut);

        String userId = "user-id";
        when(item.getId()).thenReturn(userId);

        String ministeres = "ministeres";
        when(userService.getUserMinisteres(userId)).thenReturn(ministeres);

        String directions = "directions";
        when(userService.getAllDirectionsRattachement(userId)).thenReturn(directions);

        String postes = "postes";
        when(userService.getUserPostes(userId)).thenReturn(postes);

        String username = "username";
        when(user.getUsername()).thenReturn(username);

        String lastName = "nom";
        when(user.getLastName()).thenReturn(lastName);

        String firstName = "prenom";
        when(user.getFirstName()).thenReturn(firstName);

        String email = "email@test.com";
        when(user.getEmail()).thenReturn(email);

        String telephone = "06 06 06 06 06";
        when(user.getTelephoneNumber()).thenReturn(telephone);

        UsersSimpleConfig usersSimpleConfig = new UsersSimpleConfig(ImmutableList.of(item));

        assertThat(usersSimpleConfig.getDataCells(session, item))
            .hasSize(usersSimpleConfig.getSheetName().getHeadersSize())
            .containsExactly(
                username,
                lastName,
                firstName,
                email,
                telephone,
                "05/08/2021",
                ministeres,
                directions,
                postes
            );
    }
}
