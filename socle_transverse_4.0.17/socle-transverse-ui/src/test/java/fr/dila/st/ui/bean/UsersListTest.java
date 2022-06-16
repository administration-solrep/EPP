package fr.dila.st.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.bean.UserForm;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class UsersListTest {

    @Test
    public void testConstructor() {
        STUsersList lstUsers = new STUsersList();
        assertThat(lstUsers).isNotNull();
        assertThat(lstUsers.getListe()).isNotNull().isEmpty();
        assertThat(lstUsers.getLstLettres()).isNotNull().isEmpty();
        assertThat(lstUsers.getListeColonnes()).isNotNull().hasSize(6);
    }

    @Test
    public void testSetters() {
        STUsersList lstUsers = new STUsersList();
        assertThat(lstUsers).isNotNull();
        assertThat(lstUsers.getListe()).isNotNull().isEmpty();
        assertThat(lstUsers.getLstLettres()).isNotNull().isEmpty();
        assertThat(lstUsers.getListeColonnes()).isNotNull().hasSize(6);

        List<UserForm> lstUsersDTO = Lists.newArrayList(
            new UserForm("Michu", "Toto", "tmichu", "tmichu@test.fr", SolonDateConverter.DATE_SLASH.format(new Date()))
        );
        lstUsers.setListe(lstUsersDTO);
        assertThat(lstUsers.getListe()).isEqualTo(lstUsersDTO);

        List<String> lstLettres = Lists.newArrayList("a", "b", "z");
        lstUsers.setLstLettres(lstLettres);
        assertThat(lstUsers.getLstLettres()).isEqualTo(lstLettres);
    }
}
