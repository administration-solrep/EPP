package fr.dila.st.core.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.STUser;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;

@RunWith(MockitoJUnitRunner.class)
public class STUserTest {
    private STUser user;

    @Mock
    private DocumentModel userDoc;

    @Before
    public void before() {
        user = new STUserImpl(userDoc);
    }

    @Test
    public void isActive() {
        Calendar debutPast = Calendar.getInstance();
        debutPast.add(Calendar.YEAR, -2);
        Calendar finFuture = Calendar.getInstance();
        finFuture.add(Calendar.YEAR, 2);
        Calendar debutFuture = Calendar.getInstance();
        debutFuture.add(Calendar.YEAR, 1);
        Calendar finPast = Calendar.getInstance();
        finPast.add(Calendar.YEAR, -1);

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateDebut")).thenReturn(debutPast);
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateFin")).thenReturn(finFuture);
        assertThat(user.isActive()).isTrue();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateFin")).thenReturn(finPast);
        assertThat(user.isActive()).isFalse();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateDebut")).thenReturn(debutFuture);
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateFin")).thenReturn(finFuture);
        assertThat(user.isActive()).isFalse();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateDebut")).thenReturn(null);
        assertThat(user.isActive()).isFalse();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateDebut")).thenReturn(debutPast);
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "dateFin")).thenReturn(null);
        assertThat(user.isActive()).isTrue();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, "deleted")).thenReturn("true");
        assertThat(user.isActive()).isFalse();
    }

    @Test
    public void getFullNameOrEmpty() {
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME))
            .thenReturn("Philippe");
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME)).thenReturn("Dupont");
        assertThat(user.getFullNameOrEmpty()).isEqualTo("Philippe Dupont");

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME))
            .thenReturn("   Philippe   ");
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME))
            .thenReturn("    Dupont ");
        assertThat(user.getFullNameOrEmpty()).isEqualTo("Philippe Dupont");

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME)).thenReturn(null);
        assertThat(user.getFullNameOrEmpty()).isEqualTo("Philippe");

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME)).thenReturn(null);
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME)).thenReturn("Dupont");
        assertThat(user.getFullNameOrEmpty()).isEqualTo("Dupont");

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME)).thenReturn(" ");
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME)).thenReturn(" ");
        assertThat(user.getFullNameOrEmpty()).isEmpty();

        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME)).thenReturn(null);
        when(userDoc.getProperty(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME)).thenReturn(null);
        assertThat(user.getFullNameOrEmpty()).isEmpty();
    }
}
