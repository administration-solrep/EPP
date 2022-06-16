package fr.dila.st.ui.services.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Date;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

@RunWith(MockitoJUnitRunner.class)
public class STOrganigrammeServiceImplTest {
    @Spy
    STOrganigrammeUIServiceImpl<OrganigrammeNode> stOrganigrammeUIServiceImpl = new STOrganigrammeUIServiceMock();

    SpecificContext context;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    private OrganigrammeNode organigrammeNode;

    @Mock
    private CoreSession session;

    @Mock
    private SolonAlertManager messageQueue;

    @Mock
    private Consumer<OrganigrammeNode> updater;

    @Before
    public void before() throws Exception {
        context = new SpecificContext();
        context.setSession(session);
        context.setMessageQueue(messageQueue);

        NuxeoPrincipal principal = mock(NuxeoPrincipal.class);
        when(principal.getName()).thenReturn("user");
        when(session.getPrincipal()).thenReturn(principal);

        Mockito.doReturn(organigrammeService).when(stOrganigrammeUIServiceImpl).getOrganigrammeService();
    }

    public static class STOrganigrammeUIServiceMock extends STOrganigrammeUIServiceImpl<OrganigrammeNode> {}

    @Test
    public void testOrganigrammeNode() {
        // Given
        when(organigrammeService.lockOrganigrammeNode(session, organigrammeNode)).thenReturn(true);

        // When
        stOrganigrammeUIServiceImpl.lockOrganigrammeNode(context, organigrammeNode);

        // Then
        verify(messageQueue).addSuccessToQueue("organigramme.success.node.locked");
    }

    @Test
    public void testLockOrganigrammeNodeLockedByOtherUser() {
        // Given
        when(organigrammeNode.getLockUserName()).thenReturn("user1");
        when(organigrammeNode.getLockDate()).thenReturn(new Date());

        when(organigrammeService.lockOrganigrammeNode(session, organigrammeNode)).thenReturn(false);

        // When
        stOrganigrammeUIServiceImpl.lockOrganigrammeNode(context, organigrammeNode);

        // Then
        verify(messageQueue).addErrorToQueue(Mockito.contains("user1"));
    }

    @Test
    public void testPerformNodeUpdate() {
        // Given
        when(organigrammeNode.getLockUserName()).thenReturn("user");
        String successKey = "successKey";

        // When
        stOrganigrammeUIServiceImpl.performNodeUpdate(context, organigrammeNode, updater, successKey);

        // Then
        verify(updater).accept(organigrammeNode);
        verify(organigrammeService).unlockOrganigrammeNode(organigrammeNode);
        verify(messageQueue).addSuccessToQueue(successKey);
        verify(messageQueue).addSuccessToQueue("organigramme.success.node.unlocked");
    }

    @Test
    public void testPerformNodeUpdateOnUnlockedNode() {
        // Given
        String successKey = "successKey";

        // When
        stOrganigrammeUIServiceImpl.performNodeUpdate(context, organigrammeNode, updater, successKey);

        // Then
        verify(messageQueue).addErrorToQueue("organigramme.error.node.noLock");
    }

    @Test
    public void testPerformNodeUpdateOnLockedByOtherUser() {
        // Given
        when(organigrammeNode.getLockUserName()).thenReturn("user");
        String successKey = "successKey";
        when(organigrammeNode.getLockUserName()).thenReturn("user1");
        when(organigrammeNode.getLockDate()).thenReturn(new Date());

        // When
        stOrganigrammeUIServiceImpl.performNodeUpdate(context, organigrammeNode, updater, successKey);

        // Then
        verify(messageQueue).addErrorToQueue(Mockito.contains("user1"));
    }
}
