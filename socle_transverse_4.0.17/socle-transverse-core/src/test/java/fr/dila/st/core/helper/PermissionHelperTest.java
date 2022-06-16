package fr.dila.st.core.helper;

import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME;
import static fr.dila.st.core.util.PermissionHelper.checkAdminFonctionnel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.st.core.exception.STAuthorizationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

@RunWith(MockitoJUnitRunner.class)
public final class PermissionHelperTest {
    private static final String ERROR_MSG = "forbidden action";

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    @Before
    public void setUp() {
        when(session.getPrincipal()).thenReturn(principal);
    }

    @Test
    public void testCheckAdministrator() {
        when(principal.isAdministrator()).thenReturn(true);

        Throwable exc = catchThrowable(() -> checkAdminFonctionnel(session, ERROR_MSG));
        assertThat(exc).isNull();
    }

    @Test
    public void testCheckAdminFonctionnel() {
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);

        Throwable exc = catchThrowable(() -> checkAdminFonctionnel(session, ERROR_MSG));
        assertThat(exc).isNull();
    }

    @Test
    public void testCheckNonAdminFonctionnel() {
        Throwable exc = catchThrowable(() -> checkAdminFonctionnel(session, ERROR_MSG));
        assertThat(exc).isInstanceOf(STAuthorizationException.class);
    }
}
