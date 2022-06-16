package fr.dila.solonepp.core.operation;

import static fr.dila.st.core.service.STServiceLocator.getCaseManagementDocumentTypeService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.api.Framework;

import com.google.common.collect.Lists;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.MailboxCreator;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.Profile;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.operation.version.STCreateParametresOperation;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.service.STDefaultMailboxCreator;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * Une opération pour injecter des données métiers dans la BDD et le repository,
 * on produit ainsi des communications et autres données sans passer par les
 * Webservices.
 *
 * @author tlombard
 */
@Operation(
    id = DataInjectionOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "",
    description = "Injecte des données dans la base de données"
)
public class DataInjectionOperation {
    private static final String SENAT_USER = "senat";

    private static final String AN_USER = "an";

    private static final String ADMIN_GOUVERNEMENT = "admin-gouvernement";

    private static final String ADMIN_SENAT = "admin-senat";

    private static final String ADMIN_AN = "admin-an";

    private static final String WS_GOUVERNEMENT = "ws-gouvernement";

    private static final String ADMINISTRATEUR_FONCTIONNEL = "Administrateur fonctionnel";

    private static final String UTILISATEUR = "Utilisateur";

    private static final String UTILISATEUR_WS = "UtilisateurWS";

    private static final String PASSWORD = "Solon2NG";

    private static final String TABLE_REFERENCE_READER = "TableReferenceReader";

    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "EPP.Injection.Donnees";

    private InstitutionNode an;
    private InstitutionNode senat;
    private InstitutionNode gouv;

    private PosteNode posteAn;
    private PosteNode posteSenat;
    private PosteNode posteGouv;

    @Context
    protected CoreSession session;

    private MailboxCreator mailboxCreator;

    private OrganigrammeService orgaService;

    private STPostesService posteService;
    private STUserManager userManager;
    private EvenementService eventService;

    private void initServicesAndParams() {
        mailboxCreator = STServiceLocator.getMailboxCreator();
        posteService = STServiceLocator.getSTPostesService();
        userManager = (STUserManager) STServiceLocator.getUserManager();
        orgaService = SolonEppServiceLocator.getOrganigrammeService();
        eventService = SolonEppServiceLocator.getEvenementService();

        initEtatApplicationDoc();
    }

    private void initEtatApplicationDoc() {
        STServiceLocator.getEtatApplicationService().createEtatApplication(session);
    }

    @OperationMethod
    public void run() {
        initServicesAndParams();

        if (checkNotRun() && (Framework.isDevModeSet() || Framework.isTestModeSet())) {
            injectOrganigramme();
            injectUserRelatedData();
            injectMailboxes();
            externalOperation();
        }
    }

    public void run(CoreSession session) {
        this.session = session;
        run();
    }

    private void injectUserRelatedData() {
        injectGroups();
        injectUsers();
    }

    private void externalOperation() {
        callOperation(STCreateParametresOperation.ID);
    }

    private void callOperation(String... operationIds) {
        AutomationService automation = Framework.getService(AutomationService.class);
        Stream
            .of(operationIds)
            .forEach(
                id -> {
                    OperationContext ctx = new OperationContext(session);
                    try {
                        automation.run(ctx, id);
                    } catch (OperationException e) {
                        throw new NuxeoException(e);
                    }
                }
            );
    }

    private void injectGroups() {
        injectOneGroup(
            UTILISATEUR,
            Arrays.asList("EspaceCorbeilleReader", "EspaceRechercheReader", TABLE_REFERENCE_READER)
        );
        injectOneGroup(
            ADMINISTRATEUR_FONCTIONNEL,
            Arrays.asList(
                "UtilisateurDeleter",
                "OrganigrammeReader",
                "BatchSuiviReader",
                "UtilisateurCreator",
                "TableReferenceUpdater",
                "EspaceCorbeilleReader",
                "EspaceRechercheReader",
                "NotificationEmailRecipient",
                "EspaceAdministrationReader",
                "UtilisateurUpdater",
                "UtilisateurReader",
                "OrganigrammeUpdater",
                "AdminFonctionnelEmailReceiver",
                TABLE_REFERENCE_READER,
                "NotificationEmailErrorRecipient"
            )
        );
        injectOneGroup(UTILISATEUR_WS, Arrays.asList(TABLE_REFERENCE_READER, "TableReferenceUpdater"));
    }

    private DocumentModel injectOneGroup(String groupname, List<String> functions) {
        DocumentModel groupModel = userManager.getBareGroupModel();
        Profile profile = groupModel.getAdapter(Profile.class);
        groupModel.setPropertyValue("groupname", groupname);
        profile.setBaseFunctionList(functions);
        return userManager.createGroup(groupModel);
    }

    private void injectUsers() {
        injectOneUser(ADMIN_AN, PASSWORD, Arrays.asList(posteAn.getId()), Arrays.asList(ADMINISTRATEUR_FONCTIONNEL));
        injectOneUser(
            ADMIN_SENAT,
            PASSWORD,
            Arrays.asList(posteSenat.getId()),
            Arrays.asList(ADMINISTRATEUR_FONCTIONNEL)
        );
        injectOneUser(
            ADMIN_GOUVERNEMENT,
            PASSWORD,
            Arrays.asList(posteGouv.getId()),
            Arrays.asList(ADMINISTRATEUR_FONCTIONNEL)
        );
        injectOneUser(AN_USER, PASSWORD, Arrays.asList(posteAn.getId()), Arrays.asList(UTILISATEUR));
        injectOneUser(SENAT_USER, PASSWORD, Arrays.asList(posteSenat.getId()), Arrays.asList(UTILISATEUR));
        injectOneUser(WS_GOUVERNEMENT, PASSWORD, Arrays.asList(posteGouv.getId()), Arrays.asList(UTILISATEUR_WS));
    }

    private void injectOneUser(String username, String password, List<String> postes, List<String> groups) {
        DocumentModel user = userManager.getBareUserModel();
        STUser stuser = user.getAdapter(STUser.class);
        stuser.setUsername(username);
        stuser.setLastName(username);
        stuser.setEmail(username + "@solon2ng-epp.com");
        stuser.setPassword(password);
        stuser.setPostes(postes);
        stuser.setGroups(groups);
        stuser.setDateDebut(Calendar.getInstance());

        userManager.createUser(user);

        // rewrite password a second time
        DocumentModel userup = userManager.getUserModel(username);
        STUser stuserup = userup.getAdapter(STUser.class);
        stuserup.setPassword(password);
        stuserup.setPwdReset(false);
        userManager.updateUser(userup);
    }

    /**
     * Vérification : l'opération ne doit jamais avoir été exécutée. En
     * particulier aucun gouvernement ne doit avoir été injecté.
     *
     * @return true si on ne trouve pas de gouvernement.
     */
    private boolean checkNotRun() {
        return orgaService.getAllInstitutions().isEmpty();
    }

    private void injectOrganigramme() {
        createInstitution();
        createPostes();
    }

    private void createPostes() {
        posteAn = createOnePoste(an.getId(), "Poste Technique AN");
        posteSenat = createOnePoste(senat.getId(), "Poste Technique SENAT");
        posteGouv = createOnePoste(gouv.getId(), "Poste Technique Gouvernement");
    }

    private PosteNode createOnePoste(String institId, String label) {
        PosteNode posteNode = posteService.getBarePosteModel();

        // ajout du parent
        posteNode.setParentInstitIds(Lists.newArrayList(institId));
        // Création
        posteNode.setChargeMissionSGG(false);
        posteNode.setConseillerPM(false);
        posteNode.setPosteBdc(true);
        posteNode.setPosteWs(false);
        posteNode.setDateDebut(Calendar.getInstance());
        posteNode.setLabel(label);
        posteNode = (PosteNode) orgaService.createNode(posteNode);

        return posteNode;
    }

    private void createInstitution() {
        an = new InstitutionNodeImpl();
        an.setId("ASSEMBLEE_NATIONALE");
        an.setLabel("Assemblée nationnale");
        an = (InstitutionNode) orgaService.createNode(an);

        senat = new InstitutionNodeImpl();
        senat.setId("SENAT");
        senat.setLabel("Sénat");
        senat = (InstitutionNode) orgaService.createNode(senat);

        gouv = new InstitutionNodeImpl();
        gouv.setId("GOUVERNEMENT");
        gouv.setLabel("Gouvernement");
        gouv = (InstitutionNode) orgaService.createNode(gouv);

        DocumentModel mailboxRoot = STServiceLocator.getMailboxService().getMailboxRoot(session);
        SolonEppServiceLocator.getMailboxInstitutionService().createAllMailboxInstitution(session, mailboxRoot);
    }

    private void injectMailboxes() {
        createPersonalMailbox(ADMIN_AN);
        createPersonalMailbox(ADMIN_SENAT);
        createPersonalMailbox(ADMIN_GOUVERNEMENT);
        createPersonalMailbox(AN_USER);
        createPersonalMailbox(SENAT_USER);
        createPersonalMailbox(WS_GOUVERNEMENT);
        session.save();
    }

    private void createPersonalMailbox(String user) {
        // Create the personal mailbox for the user
        final String mailboxType = getCaseManagementDocumentTypeService().getMailboxType();
        DocumentModel mailboxModel = session.createDocumentModel(mailboxType);
        Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

        // Set mailbox properties
        mailbox.setId(mailboxCreator.getPersonalMailboxId(user));
        mailbox.setTitle(user);
        mailbox.setOwner(user);
        mailbox.setType(MailboxConstants.type.personal);

        DocumentModelList res = session.query(
            String.format("SELECT * from %s", MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE)
        );
        if (res == null || res.isEmpty()) {
            throw new NuxeoException("Cannot find any mailbox folder");
        }

        mailboxModel.setPathInfo(
            res.get(0).getPathAsString(),
            STDefaultMailboxCreator.generateMailboxName(mailbox.getTitle())
        );
        session.createDocument(mailboxModel);
        session.save();
    }

    /**
     * Injection de dossiers ne fonctionne pas pour l'instant car seul un vrai utilisateur peut injecter les dossiers
     *
     * @throws Exception
     */
    public void injectDossiers() {
        DocumentModel dossDoc = createDossier("Loi travail", "EINX1426821L");
        injectCommunication(dossDoc, null, an.getId(), "de l'égalité des chances", EvenementType.EVT_02, ADMIN_AN);
        DocumentModel dossDoc2 = createDossier("Session extraordinaire", "HRUX2020633D");
        injectCommunication(
            dossDoc2,
            null,
            senat.getId(),
            "Demande de délai de livraison SOLON",
            EvenementType.GENERIQUE_03,
            ADMIN_SENAT
        );
    }

    private DocumentModel injectCommunication(
        DocumentModel dossierDoc,
        String evtParent,
        String institEmetteur,
        String titre,
        EvenementType type,
        String userName
    ) {
        DocumentModel evtDoc = eventService.createBareEvenement(session);
        Evenement evt = evtDoc.getAdapter(Evenement.class);

        evt.setEvenementParent(evtParent);

        evt.setEmetteur(institEmetteur);
        if (institEmetteur.equals(an.getId())) {
            evt.setDestinataire(gouv.getId());
            evt.setDestinataireCopie(Lists.newArrayList(senat.getId()));
        } else if (institEmetteur.equals(senat.getId())) {
            evt.setDestinataire(gouv.getId());
            evt.setDestinataireCopie(Lists.newArrayList(an.getId()));
        } else {
            evt.setDestinataire(an.getId());
            evt.setDestinataireCopie(Lists.newArrayList(senat.getId()));
        }
        evt.setTitle(titre);
        evt.setTypeEvenement(type.value());

        DocumentModel event = null;

        try (CloseableCoreSession userSession = CoreInstance.openCoreSession(session.getRepositoryName(), userName)) {
            event = eventService.creerEvenement(userSession, evt.getDocument(), dossierDoc, true);
        }
        return event;
    }

    private DocumentModel createDossier(String titre, String nor) {
        DocumentModel dossierDoc = SolonEppServiceLocator.getDossierService().createBareDossier(session);
        Dossier dos = dossierDoc.getAdapter(Dossier.class);
        dos.setTitle(titre);
        dos.setAuteur("J. Doe");
        dos.setNatureLoi("LOI");
        dos.setNiveauLecture("1");
        dos.setNor(nor);

        return SolonEppServiceLocator.getDossierService().createDossier(session, dos.getDocument());
    }
}
