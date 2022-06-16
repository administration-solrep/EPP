package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.epp.ui.services.actions.impl.CorbeilleActionServiceImpl.EXTEND_MESSAGE;
import static fr.dila.epp.ui.services.actions.impl.CorbeilleActionServiceImpl.MESSAGE_CRITERIA_FROM_RECHERCHE;
import static fr.dila.solonepp.api.institution.InstitutionsEnum.ASSEMBLEE_NATIONALE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SolonEppServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class CorbeilleActionServiceImplTest {
    CorbeilleActionService service;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    CoreSession session;

    @Mock
    EppPrincipal principal;

    @Mock
    STParametreService parametreService;

    @Mock
    MailboxInstitutionService mailboxInstitutionService;

    private static final String MAILBOX_AN = "mailboxAN";
    private static final String CORBEILLE_AN = "corbeilleAN";
    private static final String QUERY =
        "SELECT m.ecm:uuid as id  FROM Message AS m , Evenement AS e , Version AS v  " +
        "WHERE  m.cslk:caseDocumentId = e.ecm:uuid AND  m.cslk:activeVersionId = v.ecm:uuid AND  ((m.cslk:dateTraitement >= ?   " +
        "AND m.cslk:etatMessage IN ('AR_RECU', 'EMIS', 'TRAITE'))  OR m.cslk:etatMessage IN ('EN_ATTENTE_AR', 'EN_COURS_REDACTION', " +
        "'EN_COURS_TRAITEMENT', 'NON_TRAITE'))  AND  m.ecm:parentId = ? ";
    private static final String QUERY_WITH_CORBEILLE_ID =
        "SELECT m.ecm:uuid as id  FROM Message AS m , Evenement AS e , Version AS v  " +
        "WHERE  m.cslk:caseDocumentId = e.ecm:uuid AND  m.cslk:activeVersionId = v.ecm:uuid AND  m.cslk:corbeilleList = ?  AND  " +
        "((m.cslk:dateTraitement >= ?   AND m.cslk:etatMessage IN ('AR_RECU', 'EMIS', 'TRAITE'))  OR m.cslk:etatMessage IN " +
        "('EN_ATTENTE_AR', 'EN_COURS_REDACTION', 'EN_COURS_TRAITEMENT', 'NON_TRAITE'))  AND  m.ecm:parentId = ? ";
    private static final String QUERY_WITH_MESSAGE_CRITERIA =
        "SELECT m.ecm:uuid as id  FROM Message AS m , Dossier AS d , " +
        "Evenement AS e , Version AS v , PieceJointe AS p , PieceJointeFichier AS f  WHERE  e.ecm:parentId = d.ecm:uuid AND  " +
        "m.cslk:caseDocumentId = e.ecm:uuid AND  m.cslk:activeVersionId = v.ecm:uuid AND  p.ecm:parentId = v.ecm:uuid AND  " +
        "p.pj:pieceJointeFichierList = f.ecm:uuid AND  d.dos:alerteCount > ?  AND  m.cslk:corbeilleList = ?  AND  " +
        "m.cslk:arNecessaire ?  AND  m.cslk:arNonDonneCount = ?  AND  v.ver:objet LIKE ?  AND  f.ecm:fulltext = '*.txt'";

    @Before
    public void before() {
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppServiceLocator.getMailboxInstitutionService()).thenReturn(mailboxInstitutionService);
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTParametreService()).thenReturn(parametreService);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getInstitutionId()).thenReturn(ASSEMBLEE_NATIONALE.name());
        DocumentModel mailboxANDoc = mock(DocumentModel.class);
        when(mailboxANDoc.getId()).thenReturn(MAILBOX_AN);
        when(mailboxInstitutionService.getMailboxInstitution(session, ASSEMBLEE_NATIONALE.name()))
            .thenReturn(mailboxANDoc);

        service = new CorbeilleActionServiceImpl();
    }

    @Test
    public void testGetMessageListQueryString() {
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        String query = service.getMessageListQueryString(context);
        assertEquals(QUERY, query);

        context.putInContextData(ID, CORBEILLE_AN);
        query = service.getMessageListQueryString(context);
        assertEquals(QUERY_WITH_CORBEILLE_ID, query);

        context.putInContextData(EXTEND_MESSAGE, true);
        query = service.getMessageListQueryString(context);
        assertEquals(QUERY_WITH_CORBEILLE_ID, query);

        context.putInContextData(MESSAGE_CRITERIA_FROM_RECHERCHE, buildMessageCriteria());
        query = service.getMessageListQueryString(context);
        assertEquals(QUERY_WITH_MESSAGE_CRITERIA, query);
    }

    @Test
    public void testGetMessageListQueryParameter() {
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        List<Object> queryParams = service.getMessageListQueryParameter(context);
        assertEquals(2, queryParams.size());
        assertTrue(queryParams.get(0) instanceof Calendar);
        // Difference between today and date in parameters is greater than 9 days (10 days is the default value)
        assertTrue(
            Calendar.getInstance().getTimeInMillis() -
            ((Calendar) queryParams.get(0)).getTimeInMillis() >
            9 *
            24 *
            60 *
            60 *
            1000
        );
        assertEquals(MAILBOX_AN, queryParams.get(1));

        context.putInContextData(ID, CORBEILLE_AN);
        queryParams = service.getMessageListQueryParameter(context);
        assertEquals(3, queryParams.size());
        assertEquals(CORBEILLE_AN, queryParams.get(0));
        assertTrue(queryParams.get(1) instanceof Calendar);
        assertEquals(MAILBOX_AN, queryParams.get(2));

        context.putInContextData(EXTEND_MESSAGE, true);
        queryParams = service.getMessageListQueryParameter(context);
        assertEquals(3, queryParams.size());
        assertEquals(CORBEILLE_AN, queryParams.get(0));
        assertTrue(queryParams.get(1) instanceof Calendar);
        // Difference between today and date in parameters is greater than 19 days (20 days is the value when extend message is true)
        assertTrue(
            Calendar.getInstance().getTimeInMillis() -
            ((Calendar) queryParams.get(1)).getTimeInMillis() >
            19 *
            24 *
            60 *
            60 *
            1000
        );
        assertEquals(MAILBOX_AN, queryParams.get(2));

        context.putInContextData(MESSAGE_CRITERIA_FROM_RECHERCHE, buildMessageCriteria());
        queryParams = service.getMessageListQueryParameter(context);
        assertEquals(5, queryParams.size());
        assertEquals(0L, queryParams.get(0));
        assertEquals(CORBEILLE_AN, queryParams.get(1));
        assertEquals(0, queryParams.get(2));
        assertEquals(0L, queryParams.get(3));
        assertEquals("%Accord%", queryParams.get(4));
    }

    private MessageCriteria buildMessageCriteria() {
        MessageCriteria criteria = new MessageCriteria();
        criteria.setArDonne(true);
        criteria.setArNecessaire(false);
        criteria.setDossierAlerte(true);
        criteria.setPieceJointeFichierFulltext("*.txt");
        criteria.setVersionObjetLike("Accord");
        return criteria;
    }
}
