package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STConstant.CASE_MANAGEMENT_PATH;
import static fr.dila.st.core.service.EtatApplicationServiceImpl.ERROR_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STPathConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.test.STCommonFeature;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.transaction.TransactionHelper;

@RunWith(FeaturesRunner.class)
@Features(STCommonFeature.class)
public class EtatApplicationServiceIT {
    @Inject
    private CoreSession session;

    @Inject
    private EtatApplicationService service;

    @Before
    public void setUp() {
        service.resetCache();

        DocumentModel doc = session.createDocumentModel(
            CASE_MANAGEMENT_PATH,
            getAdminWorkspaceRootId(),
            "WorkspaceRoot"
        );
        session.createDocument(doc);

        doc = session.createDocumentModel(doc.getPathAsString(), "admin", "AdminWorkspace");
        session.createDocument(doc);
    }

    @Test
    public void testService() {
        assertThat(service).isNotNull();
    }

    @Test
    public void getAtatApplicationShouldThrowExc() {
        assertThatThrownBy(() -> service.getEtatApplicationDocument(session)).hasMessage(ERROR_MSG);
    }

    @Test
    public void testCreateEtatApplication() {
        service.createEtatApplication(session);
        session.save();

        EtatApplication etatApp = service.getEtatApplicationDocument(session);
        assertThat(etatApp.getDocument().getPathAsString()).startsWith(getEtatApplicationParentPath());
    }

    @Test
    public void testRestrictAccesTechnique() {
        service.createEtatApplication(session);
        session.save();
        TransactionHelper.commitOrRollbackTransaction();
        TransactionHelper.startTransaction();

        service.isApplicationTechnicallyRestricted();
        assertThat(service.isApplicationTechnicallyRestricted()).isFalse();

        service.restrictTechnicalAccess(session);
        assertThat(service.isApplicationTechnicallyRestricted()).isTrue();
    }

    protected String getAdminWorkspaceRootId() {
        return "workspaces";
    }

    protected String getEtatApplicationParentPath() {
        return STPathConstant.ADMIN_WORKSPACE_PATH;
    }
}
