package fr.dila.ss.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.fdr.ContainerDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.bean.fdr.EtapeDTO;
import fr.dila.ss.ui.bean.fdr.EtatEtapeDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.bean.fdr.NoteEtapeDTO;
import fr.dila.ss.ui.enums.EtapeLifeCycle;
import fr.dila.ss.ui.enums.EtapeValidationStatus;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.RouteStepNoteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.ss.ui.services.comment.SSCommentManagerUIService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.comment.api.Comment;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.comment.api.Comments;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        WebEngine.class,
        Comments.class,
        Framework.class,
        STServiceLocator.class,
        STLogFactory.class,
        SSUIServiceLocator.class,
        SSServiceLocator.class,
        SSActionsServiceLocator.class,
        ServiceUtil.class,
        SSServiceLocator.class,
        FeuilleRouteStepFolderSchemaUtil.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SSFeuilleRouteUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSFeuilleRouteUIServiceImpl service;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel feuilleRouteDoc;

    @Mock
    private DocumentModel routeElementDoc;

    @Mock
    private DocumentModel dossierDoc;

    @Mock
    private SpecificContext context;

    @Mock
    private STLogger logger;

    @Mock
    private DocumentRoutingService documentRoutingService;

    @Mock
    private SSDocumentRoutingActionService documentRoutingActionService;

    @Mock
    private RouteStepNoteActionService routeStepNoteActionService;

    @Mock
    private MailboxPosteService mailboxPosteService;

    @Mock
    private MailboxService mailboxService;

    @Mock
    private VocabularyService vocabularyService;

    @Mock
    private FeuilleRouteElement relatedRouteElement;

    @Mock
    private SSFeuilleRoute feuilleRoute;

    @Mock
    private DossierDistributionService dossierDistributionService;

    @Mock
    private SSCommentManagerUIService commentManagerService;

    @Mock
    private STPostesService postesService;

    @Mock
    private EtapeValidationStatus validationStatusEnum;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);
        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(ServiceUtil.getRequiredService(VocabularyService.class)).thenReturn(vocabularyService);
        when(SSActionsServiceLocator.getDocumentRoutingActionService()).thenReturn(documentRoutingActionService);
        when(SSActionsServiceLocator.getRouteStepNoteActionService()).thenReturn(routeStepNoteActionService);
        when(SSServiceLocator.getDocumentRoutingService()).thenReturn(documentRoutingService);
        when(SSServiceLocator.getMailboxPosteService()).thenReturn(mailboxPosteService);
        when(STServiceLocator.getMailboxService()).thenReturn(mailboxService);
        when(SSServiceLocator.getDossierDistributionService()).thenReturn(dossierDistributionService);
        when(SSUIServiceLocator.getSSCommentManagerUIService()).thenReturn(commentManagerService);
        when(STServiceLocator.getSTPostesService()).thenReturn(postesService);
        service = spy(new SSFeuilleRouteUIServiceImpl());

        PowerMockito.mockStatic(FeuilleRouteStepFolderSchemaUtil.class);
        PowerMockito.mockStatic(Comments.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(STLogFactory.class);

        when(STLogFactory.getLog(SSFeuilleRouteUIServiceImpl.class)).thenReturn(logger);
        when(feuilleRouteDoc.getAdapter(SSFeuilleRoute.class)).thenReturn(feuilleRoute);

        when(Framework.expandVars(any())).thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));

        when(context.getSession()).thenReturn(session);
        when(context.getFromContextData(SSContextDataKey.FEUILLE_ROUTE)).thenReturn(feuilleRouteDoc);
    }

    @Test
    public void testGetFeuilleRouteDTO() {
        when(context.getFromContextData(STContextDataKey.ID)).thenReturn("monId");
        when(session.getDocument(any())).thenReturn(feuilleRouteDoc);
        doReturn(4)
            .when(service)
            .processElementsInFolder(
                eq(context),
                eq(feuilleRouteDoc),
                any(FdrTableDTO.class),
                eq(false),
                eq(0),
                eq(null),
                eq(new ArrayList<>())
            );

        FdrTableDTO dto = service.getFeuilleRouteDTO(context);
        assertNotNull(dto);
        assertEquals(new Integer(4), dto.getTotalNbLevel());
        assertNotNull(dto.getLines());
        verify(service).processElementsInFolder(context, feuilleRouteDoc, dto, false, 0, null, new ArrayList<>());
        verify(context).getFromContextData(STContextDataKey.ID);
        verify(session).getDocument(new IdRef("monId"));
    }

    @Test
    public void testGetEtatEtapeDto() {
        doReturn(validationStatusEnum).when(service).getValidationStatut(anyString(), anyString());
        when(validationStatusEnum.getLabelKey()).thenReturn("labelKey");
        when(validationStatusEnum.getIcon()).thenReturn("icon");

        EtatEtapeDTO dto = service.getEtatEtape("done", "validationKey", "typeEtape");
        assertNotNull(dto);
        assertEquals("labelKey", dto.getLabel());
        assertEquals("icon", dto.getIcon());

        dto = service.getEtatEtape(EtapeLifeCycle.RUNNING.toString(), "", "");
        assertNotNull(dto);
        assertEquals(EtapeLifeCycle.RUNNING.getLabelKey(), dto.getLabel());
        assertEquals(EtapeLifeCycle.RUNNING.getIcon(), dto.getIcon());

        dto = service.getEtatEtape(EtapeLifeCycle.CANCELED.toString(), "", "");
        assertNotNull(dto);
        assertEquals(EtapeLifeCycle.CANCELED.getLabelKey(), dto.getLabel());
        assertEquals(EtapeLifeCycle.CANCELED.getIcon(), dto.getIcon());
    }

    @Test
    public void testGetNotesFromDocEmpty() {
        DocumentModel docParent = mock(DocumentModel.class);
        List<Action> noteActions = new ArrayList<>();
        noteActions.add(new Action());

        when(docParent.getAdapter(CommentableDocument.class)).thenReturn(null);
        List<NoteEtapeDTO> listeNotes = service.getNotesFromDoc(docParent, context);
        assertNotNull(listeNotes);
        assertThat(listeNotes).isEmpty();

        CommentableDocument docAdapter = mock(CommentableDocument.class);
        when(docAdapter.getComments()).thenReturn(new ArrayList<>());
        when(docParent.getType()).thenReturn("RouteStep");
        when(docParent.getAdapter(CommentableDocument.class)).thenReturn(docAdapter);
        listeNotes = service.getNotesFromDoc(docParent, context);
        assertNotNull(listeNotes);
        assertThat(listeNotes).isEmpty();
    }

    @Test
    public void testGetNotesFromDocWithoutReponse() {
        //--------------------Test sans réponses---------------------

        DocumentModel docParent = mock(DocumentModel.class);
        CommentableDocument docAdapter = mock(CommentableDocument.class);
        List<Action> noteActions = new ArrayList<>();
        noteActions.add(new Action());
        List<DocumentModel> fakeList = constructNotesWithoutResponse(docAdapter);
        when(routeStepNoteActionService.getCommentList(context)).thenReturn(fakeList, new ArrayList<>());
        List<NoteEtapeDTO> listeNotes = service.getNotesFromDoc(docParent, context);
        assertNotNull(listeNotes);
        assertEquals(2, listeNotes.size());

        //On vérifie la 2ème note complètement
        assertEquals("auteur2", listeNotes.get(1).getAuteur());
        assertEquals("contenue de la note 2", listeNotes.get(1).getContent());
        assertEquals(Date.from(Instant.parse("2018-10-10T12:00:00.00Z")), listeNotes.get(1).getDate());
        assertNotNull(listeNotes.get(1).getReponses());
        assertThat(listeNotes.get(1).getReponses()).isEmpty();

        //On vérifie la 1ère note de manière rapide
        assertEquals("auteur1", listeNotes.get(0).getAuteur());
        assertEquals(Date.from(Instant.parse("2020-10-10T12:00:00.00Z")), listeNotes.get(0).getDate());
        assertNotNull(listeNotes.get(0).getReponses());
        assertThat(listeNotes.get(0).getReponses()).isEmpty();
    }

    @Test
    public void testGetNotesFromDocWithReponse() {
        //--------------------Test avec réponses---------------------
        DocumentModel docParent = mock(DocumentModel.class);
        CommentableDocument docAdapter = mock(CommentableDocument.class);
        List<Action> noteActions = new ArrayList<>();
        noteActions.add(new Action());

        constructNotesWithResponse(docParent, docAdapter);
        when(docParent.getAdapter(CommentableDocument.class)).thenReturn(docAdapter);
        List<NoteEtapeDTO> listeNotes = service.getNotesFromDoc(docParent, context);
        assertNotNull(listeNotes);
        assertEquals(2, listeNotes.size());

        //On vérifie la 2ème note complètement
        assertEquals("auteur2", listeNotes.get(1).getAuteur());
        assertEquals("contenue de la note 2", listeNotes.get(1).getContent());
        assertEquals(Date.from(Instant.parse("2018-10-10T12:00:00.00Z")), listeNotes.get(1).getDate());
        assertNotNull(listeNotes.get(1).getReponses());
        assertEquals(1, listeNotes.get(1).getReponses().size());
        assertEquals("reponse1", listeNotes.get(1).getReponses().get(0).getAuteur());
        assertEquals("contenue de la reponse 1", listeNotes.get(1).getReponses().get(0).getContent());
        assertEquals(
            Date.from(Instant.parse("2018-10-11T12:00:00.00Z")),
            listeNotes.get(1).getReponses().get(0).getDate()
        );

        //On vérifie la 1ère note de manière rapide
        assertEquals("auteur1", listeNotes.get(0).getAuteur());
        assertEquals(Date.from(Instant.parse("2020-10-10T12:00:00.00Z")), listeNotes.get(0).getDate());
        assertNotNull(listeNotes.get(0).getReponses());
        assertThat(listeNotes.get(0).getReponses()).isEmpty();
    }

    private List<DocumentModel> constructNotesWithoutResponse(CommentableDocument parent) {
        List<DocumentModel> returnList = new ArrayList<>();
        DocumentModel commentDoc = mock(DocumentModel.class);
        Comment comment = mock(Comment.class);
        when(comment.getAuthor()).thenReturn("auteur1");
        when(comment.getCreationDate()).thenReturn(Instant.parse("2020-10-10T12:00:00.00Z"));
        when(comment.getModificationDate()).thenReturn(Instant.parse("2020-10-10T12:00:00.00Z"));
        when(comment.getText()).thenReturn("contenue de la note 1");
        when(commentDoc.getType()).thenReturn("Comment");
        when(commentDoc.getAdapter(CommentableDocument.class)).thenReturn(null);
        when(Comments.newComment(commentDoc)).thenReturn(comment);
        returnList.add(commentDoc);

        DocumentModel commentDoc2 = mock(DocumentModel.class);
        Comment comment2 = mock(Comment.class);
        when(comment2.getAuthor()).thenReturn("auteur2");
        when(comment2.getCreationDate()).thenReturn(Instant.parse("2018-10-10T12:00:00.00Z"));
        when(comment2.getModificationDate()).thenReturn(Instant.parse("2018-10-10T12:00:00.00Z"));
        when(comment2.getText()).thenReturn("contenue de la note 2");
        CommentableDocument adpater2 = mock(CommentableDocument.class);
        when(parent.getComments(commentDoc2)).thenReturn(new ArrayList<>());
        when(commentDoc2.getType()).thenReturn("Comment");
        when(commentDoc2.getAdapter(CommentableDocument.class)).thenReturn(adpater2);
        when(Comments.newComment(commentDoc2)).thenReturn(comment2);
        returnList.add(commentDoc2);
        return returnList;
    }

    @SuppressWarnings("unchecked")
    private void constructNotesWithResponse(DocumentModel parentDoc, CommentableDocument parent) {
        List<DocumentModel> returnList = new ArrayList<>();
        DocumentModel commentDoc = mock(DocumentModel.class);
        Comment comment = mock(Comment.class);
        when(comment.getAuthor()).thenReturn("auteur1");
        when(comment.getCreationDate()).thenReturn(Instant.parse("2020-10-10T12:00:00.00Z"));
        when(comment.getModificationDate()).thenReturn(Instant.parse("2020-10-10T12:00:00.00Z"));
        when(comment.getText()).thenReturn("contenue de la note 1");
        when(commentDoc.getType()).thenReturn("Comment");
        when(commentDoc.getAdapter(CommentableDocument.class)).thenReturn(null);
        when(Comments.newComment(commentDoc)).thenReturn(comment);
        returnList.add(commentDoc);

        DocumentModel commentDoc2 = mock(DocumentModel.class);
        Comment comment2 = mock(Comment.class);
        when(comment2.getAuthor()).thenReturn("auteur2");
        when(comment2.getCreationDate()).thenReturn(Instant.parse("2018-10-10T12:00:00.00Z"));
        when(comment2.getModificationDate()).thenReturn(Instant.parse("2018-10-10T12:00:00.00Z"));
        when(comment2.getText()).thenReturn("contenue de la note 2");
        CommentableDocument adpater2 = mock(CommentableDocument.class);

        //Construction des réponses de la note
        List<DocumentModel> responseList = new ArrayList<>();
        DocumentModel reponseDoc = mock(DocumentModel.class);
        Comment reponse = mock(Comment.class);
        when(reponse.getAuthor()).thenReturn("reponse1");
        when(reponse.getCreationDate()).thenReturn(Instant.parse("2018-10-11T12:00:00.00Z"));
        when(reponse.getModificationDate()).thenReturn(Instant.parse("2018-10-11T12:00:00.00Z"));
        when(reponse.getText()).thenReturn("contenue de la reponse 1");
        CommentableDocument reponseAdapter = mock(CommentableDocument.class);
        when(parent.getComments(reponseDoc)).thenReturn(new ArrayList<>());
        when(reponseDoc.getType()).thenReturn("Comment");
        when(reponseDoc.getAdapter(CommentableDocument.class)).thenReturn(reponseAdapter);
        when(Comments.newComment(reponseDoc)).thenReturn(reponse);
        responseList.add(reponseDoc);

        when(parent.getComments(commentDoc2)).thenReturn(responseList);
        when(commentDoc2.getType()).thenReturn("Comment");
        when(commentDoc2.getAdapter(CommentableDocument.class)).thenReturn(adpater2);
        when(Comments.newComment(commentDoc2)).thenReturn(comment2);
        returnList.add(commentDoc2);

        when(routeStepNoteActionService.getCommentList(context))
            .thenReturn(returnList, new ArrayList<>(), responseList, new ArrayList<>());
    }

    @Test
    public void testGetMailboxTitleFromId() {
        when(mailboxService.getMailboxTitle(session, "idmailbox")).thenReturn("maValeur");

        assertEquals("", service.getMailboxTitleFromId(null, session));
        assertEquals("", service.getMailboxTitleFromId("", session));
        assertEquals("maValeur", service.getMailboxTitleFromId("idmailbox", session));
    }

    @Test(expected = NuxeoException.class)
    public void testGetMailboxTitleFromIdException() {
        when(service.getMailboxTitleFromId(anyString(), any())).thenCallRealMethod();

        MailboxService mailboxService = mock(MailboxService.class);
        when(mailboxService.getMailboxTitle(session, "idmailbox")).thenThrow(new NuxeoException());
        when(STServiceLocator.getMailboxService()).thenReturn(mailboxService);

        service.getMailboxTitleFromId("idmailbox", session);
    }

    @Test
    public void testGetMinisteresEditionFromMailboxId() {
        when(mailboxPosteService.getMinisteresEditionFromMailboxId("monId")).thenReturn("valeur1");
        when(mailboxPosteService.getMinisteresEditionFromMailboxId("monId2")).thenReturn("valeur2");

        assertEquals("valeur1", service.getMinisteresEditionFromMailboxId("monId"));
        assertEquals("valeur2", service.getMinisteresEditionFromMailboxId("monId2"));
    }

    @Test
    public void testCreateStepDTO() {
        ContainerDTO parentFolder = new ContainerDTO();
        parentFolder.setId("parentFolderId");

        List<Action> noteActions = new ArrayList<>();
        noteActions.add(new Action());

        DocumentModel etape = mock(DocumentModel.class);
        constructInitialDoc(etape, "idEtape");

        when(context.getCurrentDocument()).thenReturn(etape);

        SSRouteStep etapeAdapter = mock(SSRouteStep.class);
        when(etape.getAdapter(SSRouteStep.class)).thenReturn(etapeAdapter);
        when(etapeAdapter.isObligatoireMinistere()).thenReturn(false);
        when(etapeAdapter.isObligatoireSGG()).thenReturn(false);

        when(vocabularyService.getEntryLabel("cm_routing_task_type", "action")).thenReturn("vocActionLabel");
        doReturn(new EtatEtapeDTO("etatLabel", "etatIcon"))
            .when(service)
            .getEtatEtape(anyString(), anyString(), anyString());
        when(mailboxPosteService.getMinisteresEditionFromMailboxId("mailBoxId")).thenReturn("ministereFromService");

        when(mailboxService.getMailboxTitle(session, "mailBoxId")).thenReturn("posteFromService");
        when(feuilleRoute.isFeuilleRouteInstance()).thenReturn(true);
        doReturn(new ArrayList<>()).when(service).getNotesFromDoc(etape, context);

        List<String> parentsIds = new ArrayList<>();
        parentsIds.add("parent1");
        parentsIds.add("parent2");

        //--------------------Test de base---------------
        EtapeDTO etapeDto = service.createStepDTO(context, feuilleRouteDoc, 1, true, parentsIds);

        assertNotNull(etapeDto);
        assertEquals("idEtape", etapeDto.getId());
        assertEquals("mailBoxId", etapeDto.getMailBoxId());
        assertEquals(new Integer(1), etapeDto.getDepth());
        assertEquals("non", etapeDto.getObligatoire());
        assertEquals("ministereFromService", etapeDto.getMinistere());
        assertEquals("posteFromService", etapeDto.getPoste());
        assertEquals("ministereFromService <br /> posteFromService", etapeDto.getPosteInMinistere());
        assertTrue(etapeDto.getIsStripped());
        assertNotNull(etapeDto.getEtat());
        assertEquals("etatLabel", etapeDto.getEtat().getLabel());
        assertEquals("etatIcon", etapeDto.getEtat().getIcon());
        assertNotNull(etapeDto.getParentsId());
        assertThat(etapeDto.getParentsId()).isNotEmpty();
        assertEquals("parent2", etapeDto.getParentsId().get(1));
        assertNull(etapeDto.getParent());
        assertNotNull(etapeDto.getActions());
        assertNotNull(etapeDto.getNotes());

        //--------------------Test sans mailboxid + isRequired Sgg---------------
        when(etape.getPropertyValue(SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_XPATH)).thenReturn(null);
        when(etapeAdapter.isObligatoireSGG()).thenReturn(true);

        etapeDto = service.createStepDTO(context, feuilleRouteDoc, 1, false, parentsIds);

        assertNotNull(etapeDto);
        assertEquals("idEtape", etapeDto.getId());
        assertEquals("sgg", etapeDto.getObligatoire());
        assertNull(etapeDto.getMinistere());
        assertNull(etapeDto.getPoste());
        assertEquals("Sans ministère défini <br /> sans poste", etapeDto.getPosteInMinistere());
        assertFalse(etapeDto.getIsStripped());
        assertNotNull(etapeDto.getNotes());
        assertThat(etapeDto.getNotes()).isEmpty();

        //--------------------Test avec mailboxId + isRequired Ministere---------------
        when(
                etape.getPropertyValue(
                    SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
                    ":" +
                    SSFeuilleRouteConstant.ROUTING_TASK_MINISTERE_LABEL_PROPERTY
                )
            )
            .thenReturn("ministereFromDoc");
        when(
                etape.getPropertyValue(
                    SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
                    ":" +
                    SSFeuilleRouteConstant.ROUTING_TASK_POSTE_LABEL_PROPERTY
                )
            )
            .thenReturn("posteFromDoc");
        when(etapeAdapter.isObligatoireMinistere()).thenReturn(true);

        etapeDto = service.createStepDTO(context, feuilleRouteDoc, 1, false, parentsIds);

        assertNotNull(etapeDto);
        assertEquals("idEtape", etapeDto.getId());
        assertEquals("ministere", etapeDto.getObligatoire());
        assertEquals("ministereFromDoc", etapeDto.getMinistere());
        assertEquals("posteFromDoc", etapeDto.getPoste());
        assertEquals("ministereFromDoc <br /> posteFromDoc", etapeDto.getPosteInMinistere());
        assertFalse(etapeDto.getIsStripped());
        assertNotNull(etapeDto.getNotes());
        assertThat(etapeDto.getNotes()).isEmpty();
    }

    private void constructInitialDoc(DocumentModel etape, String id) {
        when(etape.getType()).thenReturn(SSConstant.ROUTE_STEP_DOCUMENT_TYPE);
        when(etape.getId()).thenReturn(id);
        when(etape.getCurrentLifeCycleState()).thenReturn("done");
        when(etape.getPropertyValue(SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_XPATH)).thenReturn("mailBoxId");
        when(etape.getPropertyValue(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_XPATH)).thenReturn(false);
        when(etape.getPropertyValue(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_XPATH)).thenReturn(false);
        when(etape.getPropertyValue(SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_XPATH)).thenReturn(false);
        when(
                etape.getPropertyValue(
                    SSFeuilleRouteConstant.ROUTING_TASK_SCHEMA_PREFIX +
                    ":" +
                    SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY
                )
            )
            .thenReturn("action");

        SSRouteStep etapeAdapter = mock(SSRouteStep.class);
        when(etape.getAdapter(SSRouteStep.class)).thenReturn(etapeAdapter);
    }

    @Test
    public void testProcessElementsInFolder() {
        FdrTableDTO fdrDto = new FdrTableDTO();

        DocumentModelList listChildren = new DocumentModelListImpl(createChildMockList(feuilleRouteDoc));
        doReturn(new EtatEtapeDTO("etatLabel", "etatIcon"))
            .when(service)
            .getEtatEtape(anyString(), anyString(), anyString());
        when(session.getChildren(feuilleRouteDoc.getRef())).thenReturn(listChildren);
        when(feuilleRoute.isFeuilleRouteInstance()).thenReturn(true);

        doCallRealMethod().when(context).getCurrentDocument();
        doCallRealMethod().when(context).setCurrentDocument(any(DocumentModel.class));

        context.setCurrentDocument(feuilleRouteDoc);

        //--------------------Test de base---------------
        fdrDto.setTotalNbLevel(
            service.processElementsInFolder(context, feuilleRouteDoc, fdrDto, true, 0, null, new ArrayList<>())
        );

        assertNotNull(fdrDto);
        assertEquals(new Integer(2), fdrDto.getTotalNbLevel());
        //On doit avoir les étapes donc 5
        assertEquals(5, fdrDto.getLines().size());

        //Vérif étape 1
        assertEquals("idEtape1", fdrDto.getLines().get(0).getId());
        assertNull(fdrDto.getLines().get(0).getParent());

        //vérif étape 1 de serie 1
        assertEquals("etape1serie1", fdrDto.getLines().get(1).getId());
        //Premier enfant de la branche donc connait son parent
        assertNotNull(fdrDto.getLines().get(1).getParent());
        ContainerDTO parentSerie1 = fdrDto.getLines().get(1).getParent();
        assertFalse(parentSerie1.getIsParallel());
        assertEquals(new Integer(2), parentSerie1.getLevel());
        assertEquals(new Integer(1), parentSerie1.getPosition());
        assertEquals(new Integer(1), parentSerie1.getDepth());
        assertEquals(new Integer(3), parentSerie1.getNbChilds());
        assertEquals("serie1Id", parentSerie1.getId());
        //Premier enfant de la branche parrallèle donc doit connaitre son parent
        assertNotNull(parentSerie1.getParent());
        ContainerDTO paralleleBranche = parentSerie1.getParent();
        assertTrue(paralleleBranche.getIsParallel());
        assertEquals(new Integer(1), paralleleBranche.getLevel());
        assertEquals(new Integer(1), paralleleBranche.getPosition());
        assertEquals(new Integer(2), paralleleBranche.getDepth());
        //Vérifie que la branche parrallèle à bien le nombre d'enfant attendu (somme de tous les enfants)
        assertEquals(new Integer(4), paralleleBranche.getNbChilds());
        assertEquals("branch1Id", paralleleBranche.getId());
        assertNull(paralleleBranche.getParent());

        //vérif étape 2 de serie 1
        assertEquals("etape2serie1", fdrDto.getLines().get(2).getId());
        //Deuxieme enfant de la branch donc ne connait pas le parent
        assertNull(fdrDto.getLines().get(2).getParent());

        //vérif étape 1 de serie 2
        assertEquals("etape1serie2", fdrDto.getLines().get(3).getId());
        //Premier enfant de la branche donc connait son parent
        assertNotNull(fdrDto.getLines().get(3).getParent());
        ContainerDTO parentSerie2 = fdrDto.getLines().get(3).getParent();
        assertFalse(parentSerie2.getIsParallel());
        assertEquals(new Integer(2), parentSerie2.getLevel());
        assertEquals(new Integer(2), parentSerie2.getPosition());
        assertEquals(new Integer(1), parentSerie2.getDepth());
        assertEquals(new Integer(1), parentSerie2.getNbChilds());
        //Deuxieme enfant de la branche parrallèle donc ne doit pas connaitre son parent
        assertNull(parentSerie2.getParent());

        //vérif étape 4
        assertEquals("idEtape4", fdrDto.getLines().get(4).getId());
        assertNull(fdrDto.getLines().get(4).getParent());
    }

    @Test
    public void testAddEtapesModele() {
        CreationEtapeDTO dto = new CreationEtapeDTO();
        dto.setIdBranche("branche");
        dto.setTypeAjoutEnum(FeuilleRouteEtapeOrder.BEFORE);

        when(context.getCurrentDocument()).thenReturn(feuilleRouteDoc);
        when(context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO)).thenReturn(dto);
        when(session.getDocument(new IdRef("branche"))).thenReturn(routeElementDoc);
        when(routeElementDoc.getAdapter(FeuilleRouteElement.class)).thenReturn(relatedRouteElement);
        when(relatedRouteElement.getFeuilleRoute(session)).thenReturn(feuilleRoute);
        when(feuilleRoute.getDocument()).thenReturn(feuilleRouteDoc);
        when(context.getAction(SSActionEnum.ADD_STEP_BEFORE)).thenReturn(new Action());
        when(documentRoutingActionService.isModeleFeuilleRoute(context, feuilleRouteDoc)).thenReturn(true);
        when(routeElementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP);
        when(feuilleRoute.isFeuilleRouteInstance()).thenReturn(false);

        service.addEtapes(context);

        verify(documentRoutingActionService).saveRouteElementMass(context);
        verify(context).setCurrentDocument(feuilleRouteDoc);
    }

    @Test
    public void testAddEtapesdossier() {
        CreationEtapeDTO dto = new CreationEtapeDTO();
        dto.setIdBranche("branche");
        dto.setTypeAjoutEnum(FeuilleRouteEtapeOrder.BEFORE);

        when(context.getCurrentDocument()).thenReturn(feuilleRouteDoc);
        when(context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO)).thenReturn(dto);
        when(session.getDocument(new IdRef("branche"))).thenReturn(routeElementDoc);
        when(routeElementDoc.getAdapter(FeuilleRouteElement.class)).thenReturn(relatedRouteElement);
        when(relatedRouteElement.getFeuilleRoute(session)).thenReturn(feuilleRoute);
        when(feuilleRoute.getDocument()).thenReturn(feuilleRouteDoc);
        when(context.getAction(SSActionEnum.ADD_STEP_BEFORE)).thenReturn(new Action());
        when(feuilleRoute.isFeuilleRouteInstance()).thenReturn(true);
        when(documentRoutingActionService.isModeleFeuilleRoute(context, feuilleRouteDoc)).thenReturn(false);
        when(routeElementDoc.getType()).thenReturn(FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP);
        when(feuilleRouteDoc.getId()).thenReturn("idFdr");
        when(dossierDistributionService.getDossierFromDocumentRouteId(session, "idFdr")).thenReturn(dossierDoc);

        service.addEtapes(context);

        verify(documentRoutingActionService).saveRouteElementMass(context);
        verify(context).setCurrentDocument(dossierDoc);
    }

    // -- idEtape1
    // -- branch1Id -- serie1Id -- etape1serie1
    // |||||||||||||||||||||||||||||||| -- Note etape1serie1
    // |||||||||||||||||||||||| -- etape2serie1
    // |||||||||||| -- serie2Id -- etape1serie2
    // -- idEtape4
    // -- branch2Id
    private List<DocumentModel> createChildMockList(DocumentModel fdr) {
        List<DocumentModel> childList = new ArrayList<>();
        List<Action> noteActions = new ArrayList<>();
        noteActions.add(new Action());

        //Creation simple etape
        DocumentModel etape1 = mock(DocumentModel.class);
        constructInitialDoc(etape1, "idEtape1");
        when(etape1.isFolder()).thenReturn(false);
        childList.add(etape1);

        //Creation branche parrallel avec enfants
        DocumentModel branche1 = mock(DocumentModel.class);
        constructInitialDoc(branche1, "branch1Id");
        when(branche1.isFolder()).thenReturn(true);
        when(branche1.getRef()).thenReturn(new IdRef("branch1Id"));
        when(FeuilleRouteStepFolderSchemaUtil.getExecutionType(branche1))
            .thenReturn(FeuilleRouteExecutionType.parallel);

        //Creation branche série
        DocumentModel serie1 = mock(DocumentModel.class);
        constructInitialDoc(serie1, "serie1Id");
        when(serie1.isFolder()).thenReturn(true);
        when(serie1.getRef()).thenReturn(new IdRef("serie1Id"));
        when(FeuilleRouteStepFolderSchemaUtil.getExecutionType(serie1)).thenReturn(FeuilleRouteExecutionType.serial);

        //Creation des 2 étapes de la branche série
        DocumentModel etape1serie1 = mock(DocumentModel.class);
        constructInitialDoc(etape1serie1, "etape1serie1");
        when(etape1serie1.isFolder()).thenReturn(false);
        doReturn(Arrays.asList(new NoteEtapeDTO())).when(service).getNotesFromDoc(etape1serie1, context);

        DocumentModel etape2serie1 = mock(DocumentModel.class);
        constructInitialDoc(etape2serie1, "etape2serie1");
        when(etape2serie1.isFolder()).thenReturn(false);

        //Ajout des étapes en enfant de la branche série 1
        List<DocumentModel> listeChildrenBranchSerie1 = new ArrayList<>();
        listeChildrenBranchSerie1.add(etape1serie1);
        listeChildrenBranchSerie1.add(etape2serie1);
        when(session.getChildren(serie1.getRef())).thenReturn(new DocumentModelListImpl(listeChildrenBranchSerie1));

        //Creation branche série
        DocumentModel serie2 = mock(DocumentModel.class);
        constructInitialDoc(serie2, "serie2Id");
        when(serie2.isFolder()).thenReturn(true);
        when(serie2.getRef()).thenReturn(new IdRef("serie2Id"));
        when(FeuilleRouteStepFolderSchemaUtil.getExecutionType(serie2)).thenReturn(FeuilleRouteExecutionType.serial);

        //Creation de la seule de la branche série
        DocumentModel etape1serie2 = mock(DocumentModel.class);
        constructInitialDoc(etape1serie2, "etape1serie2");
        when(etape1serie2.isFolder()).thenReturn(false);

        //Ajout de l'étape en enfant de la branche série 2
        List<DocumentModel> listeChildrenBranchSerie2 = new ArrayList<>();
        listeChildrenBranchSerie2.add(etape1serie2);
        when(session.getChildren(serie2.getRef())).thenReturn(new DocumentModelListImpl(listeChildrenBranchSerie2));

        //Ajout des branches série en enfant de la branche parrallel 1
        List<DocumentModel> listeChildrenBranch1 = new ArrayList<>();
        listeChildrenBranch1.add(serie1);
        listeChildrenBranch1.add(serie2);
        when(session.getChildren(branche1.getRef())).thenReturn(new DocumentModelListImpl(listeChildrenBranch1));
        childList.add(branche1);

        //Creation simple etape 4
        DocumentModel etape4 = mock(DocumentModel.class);
        constructInitialDoc(etape4, "idEtape4");
        when(etape1.isFolder()).thenReturn(false);
        childList.add(etape4);

        //Branche sans enfant
        DocumentModel branche2 = mock(DocumentModel.class);
        constructInitialDoc(branche2, "branch2Id");
        when(branche2.isFolder()).thenReturn(true);
        when(branche2.getRef()).thenReturn(new IdRef("branch2Id"));
        when(FeuilleRouteStepFolderSchemaUtil.getExecutionType(branche2))
            .thenReturn(FeuilleRouteExecutionType.parallel);
        when(session.getChildren(branche2.getRef())).thenReturn(new DocumentModelListImpl(new ArrayList<>()));
        childList.add(branche2);

        return childList;
    }
}
