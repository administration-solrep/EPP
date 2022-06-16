package fr.dila.ss.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        LockUtils.class,
        SSUIServiceLocator.class,
        Framework.class,
        SSActionsServiceLocator.class,
        STServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SSModeleFdrFicheUIServiceImplTest {
    private static final String IDENTIFIANT = "id";
    private static final String FDR_NAME = "fdrName";
    private static final String CREATOR_NAME = "creatorName";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSModeleFdrFicheUIServiceImpl service;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private OrganigrammeService organigrammeService;

    @Mock
    private DocumentRef docRef;

    @Mock
    private DocumentModel doc;

    @Mock
    private DocumentModel doc2;

    @Mock
    private SSFeuilleRoute modele;

    @Mock
    private OrganigrammeNode node;

    @Mock
    private OrganigrammeNode node2;

    @Mock
    private SSFeuilleRouteUIService feuilleRouteUIService;

    @Mock
    private FdrTableDTO tableDto;

    @Mock
    private ModeleFdrForm modeleForm;

    @Mock
    private ModeleFeuilleRouteActionService modeleAction;

    @Mock
    private ModeleFeuilleRouteActionService repModeleAction;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(LockUtils.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        service = new SSModeleFdrFicheUIServiceImpl();

        when(context.getSession()).thenReturn(session);
        when(SSUIServiceLocator.getSSFeuilleRouteUIService()).thenReturn(feuilleRouteUIService);
        when(SSActionsServiceLocator.getModeleFeuilleRouteActionService()).thenReturn(modeleAction);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        doCallRealMethod().when(context).putInContextData(STContextDataKey.ID, "id");
        when(modele.getDocument()).thenReturn(doc);
        when(session.getDocument(any())).thenReturn(doc);
        when(session.getPrincipal()).thenReturn(principal);
    }

    @Test
    public void testGetModeleFdrForm() throws Exception {
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(modele);
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn("id");
        doCallRealMethod().when(context).setCurrentDocument(doc);
        when(doc.getRef()).thenReturn(docRef);
        when(LockUtils.isLocked(session, docRef)).thenReturn(true);
        when(modele.getTitle()).thenReturn("title");
        when(modele.getMinistere()).thenReturn("min");
        when(modele.isFeuilleRouteDefaut()).thenReturn(false);
        when(modele.getDescription()).thenReturn("description");
        when(modele.isValidated()).thenReturn(true);
        when(organigrammeService.getOrganigrammeNodeById("min", OrganigrammeType.MINISTERE)).thenReturn(node);
        when(organigrammeService.getOrganigrammeNodeById("idDirection", OrganigrammeType.DIRECTION)).thenReturn(node2);
        when(node.getLabel()).thenReturn("label node");
        when(node2.getLabel()).thenReturn("intitule direction");
        when(doc.getCurrentLifeCycleState()).thenReturn("validated");
        when(LockUtils.isLockedByCurrentUser(session, docRef)).thenReturn(true);

        when(context.getCurrentDocument()).thenReturn(doc);
        when(doc.getId()).thenReturn("id");
        when(feuilleRouteUIService.getFeuilleRouteDTO(context)).thenReturn(tableDto);
        when(Framework.expandVars(anyString())).thenReturn("link");

        ModeleFdrForm form = new ModeleFdrForm();
        service.getModeleFdrForm(context, form);

        assertNotNull(form);
        assertEquals("id", form.getId());
        assertEquals("title", form.getIntitule());
        assertEquals("min", form.getIdMinistere());
        assertEquals("label node", form.getLibelleMinistere());
        assertEquals(false, form.getModeleParDefaut());
        assertEquals("description", form.getDescription());
        assertEquals(StatutModeleFDR.VALIDE.name(), form.getEtat());
        assertEquals(true, form.getIsLock());
        assertEquals(true, form.getIsLockByCurrentUser());
        assertNotNull(form.getFdrDto());
        assertEquals(tableDto, form.getFdrDto().getTable());
    }

    @Test
    public void testUpdateModeleSuccess() {
        ArrayList<String> index = new ArrayList<>();
        index.add("index1");
        index.add("index2");

        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(modele);
        when(modeleAction.canUserModifyRoute(context)).thenReturn(true);
        when(doc.getType()).thenReturn(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);

        SolonAlertManager messageQueue = new SolonAlertManager();
        when(context.getMessageQueue()).thenReturn(messageQueue);
        doCallRealMethod().when(context).setCurrentDocument(any(DocumentModel.class));
        doCallRealMethod().when(context).getCurrentDocument();

        service.updateModele(context, modeleForm);

        verify(modeleAction).updateDocument(context);
        assertNotNull(context.getMessageQueue());
        assertEquals(
            ResourceHelper.getString("admin.modele.message.success.saveForm"),
            context.getMessageQueue().getSuccessQueue().get(0).getAlertMessage().get(0)
        );
        assertEquals(doc, context.getCurrentDocument());
    }

    @Test
    public void testUpdateModeleNotAllow() {
        ArrayList<String> index = new ArrayList<>();
        index.add("index1");
        index.add("index2");

        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(modele);
        when(modeleAction.canUserModifyRoute(context)).thenReturn(false);

        SolonAlertManager messageQueue = new SolonAlertManager();
        when(context.getMessageQueue()).thenReturn(messageQueue);

        service.updateModele(context, modeleForm);
        assertNotNull(context.getMessageQueue());
        assertEquals(
            ResourceHelper.getString("admin.modele.message.error.right"),
            context.getMessageQueue().getErrorQueue().get(0).getAlertMessage().get(0)
        );
        verify(context, never()).setCurrentDocument(any(DocumentModel.class));
        verify(modeleAction, never()).updateDocument(context);
    }

    @Test
    public void testCreateModeleSuccess() {
        ArrayList<String> index = new ArrayList<>();
        index.add("index1");
        index.add("index2");

        when(modeleAction.initFeuilleRoute(session, modeleForm, CREATOR_NAME)).thenReturn(doc);
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(modele);
        when(modeleAction.canUserCreateRoute(context)).thenReturn(true);
        when(principal.getName()).thenReturn(CREATOR_NAME);
        when(modeleForm.getIntitule()).thenReturn(FDR_NAME);
        when(modeleForm.getIdMinistere()).thenReturn("idMin");
        when(modeleForm.getModeleParDefaut()).thenReturn(false);
        when(modeleForm.getDescription()).thenReturn("description");
        when(modeleForm.getIdDirection()).thenReturn("idDirection");
        when(modeleForm.getLibelleDirection()).thenReturn("libelle direction");
        when(doc.getType()).thenReturn(SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);

        doCallRealMethod().when(context).setCurrentDocument(doc);
        when(modeleAction.createDocument(context)).thenReturn(doc2);
        when(doc2.getId()).thenReturn(IDENTIFIANT);
        SolonAlertManager messageQueue = new SolonAlertManager();
        when(context.getMessageQueue()).thenReturn(messageQueue);
        doCallRealMethod().when(modeleForm).setId(anyString());
        doCallRealMethod().when(modeleForm).getId();

        ModeleFdrForm modele = service.createModele(context, modeleForm);

        assertNotNull(modele);
        assertEquals(IDENTIFIANT, modele.getId());
        verify(modeleAction).initFeuilleRoute(session, modeleForm, CREATOR_NAME);
        verify(modeleAction).createDocument(context);
        assertNotNull(context.getMessageQueue());
        assertEquals(
            "Le modèle a bien été créé",
            context.getMessageQueue().getSuccessQueue().get(0).getAlertMessage().get(0)
        );
    }

    @Test
    public void testCreateModeleNotAllow() {
        ArrayList<String> index = new ArrayList<>();
        index.add("index1");
        index.add("index2");

        when(repModeleAction.initFeuilleRoute(session, modeleForm, CREATOR_NAME)).thenReturn(doc);
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(modele);
        when(repModeleAction.canUserCreateRoute(context)).thenReturn(false);

        SolonAlertManager messageQueue = new SolonAlertManager();
        when(context.getMessageQueue()).thenReturn(messageQueue);

        ModeleFdrForm modele = service.createModele(context, modeleForm);

        assertNotNull(modele);
        assertNotNull(context.getMessageQueue());
        assertEquals(0, context.getMessageQueue().getSuccessQueue().size());
        verify(context, never()).setCurrentDocument(any(DocumentModel.class));
        verify(modeleAction, never()).updateDocument(context);
    }
}
