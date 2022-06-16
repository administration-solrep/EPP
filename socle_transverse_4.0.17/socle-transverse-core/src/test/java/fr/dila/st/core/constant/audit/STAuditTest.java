package fr.dila.st.core.constant.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_COMMENT;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.audit.STAuditBackend;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.test.STAuditFeature;
import fr.dila.st.core.test.STFeature;
import fr.sword.naiad.nuxeo.commons.core.schema.UserPropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.service.AuditBackend;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STAuditFeature.class, STFeature.class, SolonMockitoFeature.class })
public class STAuditTest {
    @Inject
    protected CoreSession session;

    @Inject
    protected AuditBackend auditBackend;

    @Inject
    protected UserManager um;

    @Mock
    @RuntimeService
    protected STPostesService mockPostesService;

    private static final String TOTO = "toto";

    private static final String USER_DIRECTORY = "userDirectory";

    @Before
    public void setUp() {
        DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
        try (Session userDir = directoryService.getDirectory(USER_DIRECTORY).getSession()) {
            DocumentModel toto = um.getBareUserModel();
            UserPropertyUtil.setUserName(toto, TOTO);
            UserPropertyUtil.setFirstName(toto, "Toto");
            UserPropertyUtil.setLastName(toto, "Maestro");
            userDir.createEntry(toto);
        }
    }

    @Test
    public void testServices() {
        assertNotNull(auditBackend);
        assertNotNull(um);
        assertNotNull(um.getPrincipal(TOTO));
    }

    @Test
    public void testAuditBackend() {
        Mockito.when(mockPostesService.getAllPosteIdsForUser(TOTO)).thenReturn(Lists.newArrayList());
        assertThat(auditBackend instanceof STAuditBackend).isTrue();

        DocumentModel doc = session.createDocumentModel("File");
        session.createDocument(doc);
        EventContext evtCtx = new DocumentEventContext(session, um.getPrincipal(TOTO), doc);
        evtCtx.setProperty(LOG_COMMENT, StringUtils.repeat("*", 2048));

        LogEntry entry = auditBackend.buildEntryFromEvent(evtCtx.newEvent("event"));

        assertThat(entry.getPrincipalName()).isEqualTo("Toto Maestro");
        assertThat(entry.getComment()).hasSize(STAuditBackend.MAX_SIZE_COMMENT - 1);
    }
}
