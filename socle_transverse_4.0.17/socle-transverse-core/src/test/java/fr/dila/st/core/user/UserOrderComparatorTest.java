package fr.dila.st.core.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.user.STUser;
import fr.dila.st.api.util.XPathUtils;
import java.util.Calendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;

@RunWith(MockitoJUnitRunner.class)
public class UserOrderComparatorTest {
    private UserOrderComparator userOrderComparator;

    @Mock
    private DocumentModel userDoc1;

    @Mock
    private DocumentModel userDoc2;

    @Test
    public void compareUsersWithLastnameAscending() {
        String xPath = XPathUtils.xPath(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_LAST_NAME);

        STUser user1 = new STUserImpl(userDoc1);
        when(userDoc1.getPropertyValue(xPath)).thenReturn("Zaza");

        STUser user2 = new STUserImpl(userDoc2);
        when(userDoc2.getPropertyValue(xPath)).thenReturn("Baba");

        SortInfo sortInfo = new SortInfo(STSchemaConstant.USER_LAST_NAME, true);
        userOrderComparator = new UserOrderComparator(sortInfo);

        assertThat(userOrderComparator.compare(user1, user2)).isGreaterThanOrEqualTo(1);
        assertThat(userOrderComparator.compare(user2, user1)).isLessThanOrEqualTo(1);
    }

    @Test
    public void compareUsersWithFirstnameDescending() {
        String xPath = XPathUtils.xPath(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_FIRST_NAME);

        STUser user1 = new STUserImpl(userDoc1);
        when(userDoc1.getPropertyValue(xPath)).thenReturn("Jacques");

        STUser user2 = new STUserImpl(userDoc2);
        when(userDoc2.getPropertyValue(xPath)).thenReturn("Jean");

        SortInfo sortInfo = new SortInfo(STSchemaConstant.USER_FIRST_NAME, false);
        userOrderComparator = new UserOrderComparator(sortInfo);

        assertThat(userOrderComparator.compare(user1, user2)).isGreaterThanOrEqualTo(1);
        assertThat(userOrderComparator.compare(user2, user1)).isLessThanOrEqualTo(-1);
    }

    @Test
    public void compareUsersWithDateDebutAscending() {
        String xPath = XPathUtils.xPath(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_DATE_DEBUT);

        STUser user1 = new STUserImpl(userDoc1);
        Calendar dateDebutUser1 = Calendar.getInstance();
        dateDebutUser1.set(2021, Calendar.APRIL, 18);
        when(userDoc1.getPropertyValue(xPath)).thenReturn(dateDebutUser1);

        STUser user2 = new STUserImpl(userDoc2);
        Calendar dateDebutUser2 = Calendar.getInstance();
        dateDebutUser2.set(2020, Calendar.APRIL, 18);
        when(userDoc2.getPropertyValue(xPath)).thenReturn(dateDebutUser2);

        SortInfo sortInfo = new SortInfo(STSchemaConstant.USER_DATE_DEBUT, true);
        userOrderComparator = new UserOrderComparator(sortInfo);

        assertThat(userOrderComparator.compare(user1, user2)).isEqualTo(1);
        assertThat(userOrderComparator.compare(user2, user1)).isEqualTo(-1);
    }

    @Test
    public void compareUsersWithDateDebutDescending() {
        String xPath = XPathUtils.xPath(STSchemaConstant.USER_SCHEMA, STSchemaConstant.USER_DATE_DEBUT);

        STUser user1 = new STUserImpl(userDoc1);
        Calendar dateDebutUser1 = Calendar.getInstance();
        dateDebutUser1.set(2021, Calendar.APRIL, 18);
        when(userDoc1.getPropertyValue(xPath)).thenReturn(dateDebutUser1);

        STUser user2 = new STUserImpl(userDoc2);
        Calendar dateDebutUser2 = Calendar.getInstance();
        dateDebutUser2.set(2020, Calendar.APRIL, 18);
        when(userDoc2.getPropertyValue(xPath)).thenReturn(dateDebutUser2);

        SortInfo sortInfo = new SortInfo(STSchemaConstant.USER_DATE_DEBUT, false);
        userOrderComparator = new UserOrderComparator(sortInfo);

        assertThat(userOrderComparator.compare(user1, user2)).isEqualTo(-1);
        assertThat(userOrderComparator.compare(user2, user1)).isEqualTo(1);
    }
}
