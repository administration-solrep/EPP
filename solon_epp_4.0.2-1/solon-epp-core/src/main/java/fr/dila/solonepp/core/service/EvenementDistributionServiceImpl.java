package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.mailbox.Mailbox;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementDistributionService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Implémentation du service de distribution des événements.
 *
 * @author jtremeaux
 */
public class EvenementDistributionServiceImpl implements EvenementDistributionService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 7107339212741482302L;
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EvenementDistributionServiceImpl.class);

    /**
     * Crée et sauvegarde un nouveau CaseLink.
     *
     * @param session Session
     * @param mailboxDoc Document de la mailbox dans laquelle créer le message
     * @param evenementDoc Document de l'événement à distribuer
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @return Document CaseLink nouvellement créé
     */
    protected DocumentModel createCaseLink(
        CoreSession session,
        DocumentModel mailboxDoc,
        DocumentModel evenementDoc,
        String messageType
    ) {
        LOGGER.info(
            session,
            STLogEnumImpl.DISTRIBUTION_COMM_IN_MAIL_BOX_TEC,
            "communication = " + evenementDoc.getTitle() + ", mailbox = " + mailboxDoc.getTitle()
        );

        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        // Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
        // eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE, evenementDoc);
        // eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
        // fireEvent(session, messageDoc, eventProperties, EventNames.beforeDraftCreated.name());

        final VersionService versionService = SolonEppServiceLocator.getVersionService();

        // Crée le message
        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        DocumentModel messageDoc = messageService.createBareMessage(session, evenementDoc, mailboxDoc);

        Message message = messageDoc.getAdapter(Message.class);
        message.setMessageType(messageType);
        message.setTitle(evenementDoc.getTitle());
        message.setCaseDocumentId(evenementDoc.getId());
        message.setSenderMailboxId(mailboxDoc.getId());
        message.setDate(Calendar.getInstance());
        message.setSender(mailboxDoc.getId());
        message.setIdEvenement(evenementDoc.getTitle());

        DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
        message.setIdDossier(dossierDoc.getTitle());

        // Renseigne les données pour l'émetteur
        boolean isEmetteur = SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(messageType);
        if (isEmetteur) {
            final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
            boolean arNecessaire = evenementTypeService.isDemandeAr(evenement.getTypeEvenement());
            message.setArNecessaire(arNecessaire);
            message.setArNonDonneCount(0);
        } else {
            message.setArNecessaire(false);
            message.setArNonDonneCount(0);
        }

        // Renseigne la version active pour le destinataire du message
        DocumentModel activeVersionDoc = versionService.getVersionActive(session, evenementDoc, messageType);
        if (activeVersionDoc != null) {
            message.setActiveVersionId(activeVersionDoc.getId());
        }

        // Renseigne la liste des corbeilles de distribution du message
        final CorbeilleTypeService corbeilleTypeService = SolonEppServiceLocator.getCorbeilleTypeService();
        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();

        final Mailbox destinationMailbox = mailboxDoc.getAdapter(Mailbox.class);
        final String institution = mailboxInstitutionService.getInstitutionIdFromMailboxId(destinationMailbox.getId());

        List<String> corbeilleList = corbeilleTypeService.findCorbeilleDistribution(
            institution,
            messageType,
            evenement.getTypeEvenement()
        );
        message.setCorbeilleList(corbeilleList);

        // Sauvegarde le message
        session.createDocument(messageDoc);
        session.save();

        // eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_DRAFT, draft);
        // fireEvent(session, messageDoc, eventProperties, EventNames.afterDraftCreated.name());

        return messageDoc;
    }

    @Override
    public void sendMessageEmetteur(CoreSession session, DocumentModel evenementDoc) {
        LOGGER.info(session, EppLogEnumImpl.CREATE_MESSAGE_TEC, "communication " + evenementDoc.getTitle());
        // Récupère la Mailbox institution de l'émetteur
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        String institutionId = principal.getInstitutionId();

        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
        DocumentModel mailboxDoc = mailboxInstitutionService.getMailboxInstitution(session, institutionId);

        // Crée le message et envoie le message pour l'émetteur
        createCaseLink(session, mailboxDoc, evenementDoc, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);
    }

    @Override
    public void sendMessageDestinataire(CoreSession session, final DocumentModel evenementDoc) {
        LOGGER.info(session, EppLogEnumImpl.CREATE_MESSAGE_TEC, "communication " + evenementDoc.getTitle());

        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                // Récupère la Mailbox institution du destinataire
                String destinataireInstitutionId = evenement.getDestinataire();

                if (StringUtils.isBlank(destinataireInstitutionId)) {
                    throw new NuxeoException("Le destinataire du message doit être renseigné");
                }

                DocumentModel destinataireMailboxDoc = mailboxInstitutionService.getMailboxInstitution(
                    session,
                    destinataireInstitutionId
                );

                // Crée le message et envoie le message au destinataire
                createCaseLink(
                    session,
                    destinataireMailboxDoc,
                    evenementDoc,
                    SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
                );
            }
        }
        .runUnrestricted();
    }

    @Override
    public void sendMessageDestinataireCopie(CoreSession session, final DocumentModel evenementDoc) {
        LOGGER.info(session, EppLogEnumImpl.CREATE_MESSAGE_TEC, "communication " + evenementDoc.getTitle());
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                // Récupère les Mailbox institution des destinataires en copie
                List<String> destinataireInstitutionIdList = evenement.getDestinataireCopie();

                if (destinataireInstitutionIdList == null) {
                    return;
                }

                for (String destinataireInstitutionId : destinataireInstitutionIdList) {
                    DocumentModel destinataireMailboxDoc = mailboxInstitutionService.getMailboxInstitution(
                        session,
                        destinataireInstitutionId
                    );

                    // Crée le message et envoie le message aux destinataires
                    createCaseLink(
                        session,
                        destinataireMailboxDoc,
                        evenementDoc,
                        SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
                    );
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void accuserReceptionMessageEmetteur(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final boolean validationManuelle
    ) {
        LOGGER.info(
            session,
            STLogEnumImpl.ACKNOWLEDGMENT_MESSAGE_FROM_COMM_TRANSMITTER_TEC,
            "communication " + evenementDoc.getTitle()
        );

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                // Récupère le message de l'émetteur
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setCaseDocumentId(evenementDoc.getId());
                messageCriteria.setMessageType(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocs = new ArrayList<DocumentModel>();
                if ("EVT45".equals(evenementDoc.getAdapter(Evenement.class).getTypeEvenement())) {
                    List<DocumentModel> msgDocs = messageDao.getMultipleResults();
                    if (msgDocs == null) {
                        throw new NuxeoException(
                            "Messages de l'émetteur non trouvés pour la communication " + evenementDoc.getTitle()
                        );
                    }
                    messageDocs = msgDocs;
                } else {
                    DocumentModel msgDoc = messageDao.getSingleResult();
                    if (msgDoc == null) {
                        throw new NuxeoException(
                            "Message de l'émetteur non trouvé pour la communication " + evenementDoc.getTitle()
                        );
                    }
                    messageDocs.add(msgDoc);
                }

                // Vérifie si les versions antérieures ont un accusé de réception
                for (DocumentModel messageDoc : messageDocs) {
                    Message message = messageDoc.getAdapter(Message.class);
                    if (message.isArNecessaire()) {
                        /*long arNonDonneCount = message.getArNonDonneCount() - 1;*/
                        long arNonDonneCount = 0;
                        //TCH: mantis 0051657 : pas de necessite pour ce code
                        /*if (validationManuelle && arNonDonneCount >= 1) {
                          throw new NuxeoException("Les versions antérieures de la communication doivent toutes avoir un accusé de réception");
                        }*/

                        // Accuse réception d'une version pour ce message
                        /*if (arNonDonneCount < 0) {
                            arNonDonneCount = 0;
                                }*/
                        message.setArNonDonneCount(arNonDonneCount);
                        message.setDateTraitement(Calendar.getInstance());
                        session.saveDocument(messageDoc);
                    }
                }
            }
        }
        .runUnrestricted();

        session.save();
    }

    @Override
    public void updateMessageAfterCreerBrouillon(
        CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final Version version = versionDoc.getAdapter(Version.class);

                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                // Récupère l'événement
                final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
                String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
                DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
                if (currentEvenementDoc == null) {
                    throw new NuxeoException("Communication non trouvé: " + evenementId);
                }
                Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);

                // Récupère le dossier
                final DossierService dossierService = SolonEppServiceLocator.getDossierService();
                final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());
                    // MAJ de la date du message
                    message.setDate(version.getHorodatage());

                    if (!message.isEtatNonTraite()) {
                        messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_BACK_TO_NON_TRAITE_TRANSITION);
                    }

                    // EVT45 : conserver l'accusé de réception de la version précédente
                    if (
                        "EVT45".equals(evenementDoc.getAdapter(Evenement.class).getTypeEvenement()) &&
                        version.getDateAr() != null
                    ) {
                        messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_TRAITE_TRANSITION);
                        message.setArNonDonneCount(0);
                    }

                    session.saveDocument(messageDoc);

                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = null;
                    //final String modeCreation = version.getModeCreation();
                    if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(
                            version.getModeCreation()
                        )
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_BROUILLON_INITIALE_VALUE;
                        jetonService.createNotificationEvenement(
                            session,
                            dossierDoc,
                            evenementDoc,
                            versionDoc,
                            messageDoc,
                            notificationType
                        );
                    }
                    //                    else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_COMPLETION_VALUE.equals(version.getModeCreation())) {
                    //                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_COMPLEMENT_VALUE;
                    //                    } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_RECTIFICATION_VALUE.equals(version.getModeCreation())) {
                    //                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_RECTIFICATION_VALUE;
                    //                    } else {
                    //                        throw new NuxeoException("Mode de création inconnu: " + modeCreation);
                    //                    }

                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterPublier(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        final Version version = versionDoc.getAdapter(Version.class);

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());
                    // MAJ de la date du message
                    message.setDate(version.getHorodatage());

                    if (message.isTypeEmetteur()) {
                        // Incrémente le compteur du nombre d'AR du destinataire attendus
                        if (message.isArNecessaire()) {
                            long arNonDonneCount = message.getArNonDonneCount() + 1;
                            message.setArNonDonneCount(arNonDonneCount);
                        } else {
                            message.setDateTraitement(Calendar.getInstance());
                        }
                    }

                    // EVT45 : conserver l'accusé de réception de la version précédente
                    if (
                        "EVT45".equals(evenementDoc.getAdapter(Evenement.class).getTypeEvenement()) &&
                        version.getDateAr() != null
                    ) {
                        message.setArNonDonneCount(0);
                    }

                    session.saveDocument(messageDoc);

                    if (message.isTypeEmetteur()) {
                        // Transitionne le message de l'émetteur à l'état traité
                        if (message.isEtatNonTraite()) {
                            messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_TRAITE_TRANSITION);
                        }
                    } else if (message.isTypeDestinataire() || message.isTypeCopie()) {
                        // Repasse le message du destinataire et de la copie à non traité
                        if (message.isEtatEnCours() || message.isEtatTraite()) {
                            messageDoc.followTransition(
                                SolonEppLifecycleConstant.MESSAGE_BACK_TO_NON_TRAITE_TRANSITION
                            );
                        }
                    }

                    // Crée une notification pour l'événement publié
                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = null;
                    final String modeCreation = version.getModeCreation();
                    if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_INIT_VALUE.equals(version.getModeCreation())
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_INITIALE_VALUE;
                    } else if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_COMPLETION_VALUE.equals(
                            version.getModeCreation()
                        )
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_COMPLEMENT_VALUE;
                    } else if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_RECTIFICATION_VALUE.equals(
                            version.getModeCreation()
                        )
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_RECTIFICATION_VALUE;
                    } else {
                        throw new NuxeoException("Mode de création inconnu: " + modeCreation);
                    }
                    jetonService.createNotificationEvenement(
                        session,
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        messageDoc,
                        notificationType
                    );
                }
            }
        }
        .runUnrestricted();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        if (evenement.getEvenementParent() != null) {
            MessageService messageService = SolonEppServiceLocator.getMessageService();
            messageService.followTransitionTraite(session, evenement.getEvenementParent());
        }
    }

    @Override
    public void updateMessageAfterDemanderValidation(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        final Version version = versionDoc.getAdapter(Version.class);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                List<String> messageTypeIn = new ArrayList<String>();
                messageTypeIn.add(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);
                messageTypeIn.add(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE);
                messageCriteria.setMessageTypeIn(messageTypeIn);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version en attente de validation
                    message.setActiveVersionId(versionDoc.getId());

                    if (message.isTypeEmetteur()) {
                        // Incrémente le compteur du nombre d'AR du destinataire attendus
                        if (message.isArNecessaire()) {
                            long arNonDonneCount = message.getArNonDonneCount() + 1;
                            message.setArNonDonneCount(arNonDonneCount);
                        }
                    }

                    // Renseigne la date d'enrigistrement de la version
                    message.setDate(version.getHorodatage());
                    session.saveDocument(messageDoc);

                    if (message.isTypeEmetteur()) {
                        // Transitionne le message de l'émetteur à l'état traité
                        if (message.isEtatNonTraite()) {
                            messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_TRAITE_TRANSITION);
                        }
                    } else if (message.isTypeDestinataire()) {
                        // Repasse le message du destinataire à non traité
                        if (message.isEtatEnCours() || message.isEtatTraite()) {
                            messageDoc.followTransition(
                                SolonEppLifecycleConstant.MESSAGE_BACK_TO_NON_TRAITE_TRANSITION
                            );
                        }
                    }

                    // Crée une notification pour la demande d'annulation / de rectification
                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = null;
                    final String modeCreation = version.getModeCreation();
                    if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                            version.getModeCreation()
                        ) ||
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DELTA_DEMANDE_RECTIFICATION_VALUE.equals(
                            version.getModeCreation()
                        ) ||
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                            version.getModeCreation()
                        )
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_DEMANDER_VALIDATION_VALUE;
                    } else {
                        throw new NuxeoException("Mode de création inconnu: " + modeCreation);
                    }
                    jetonService.createNotificationEvenement(
                        session,
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        messageDoc,
                        notificationType
                    );
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterValider(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final String messageType
    ) {
        final Version version = versionDoc.getAdapter(Version.class);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(messageType);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());

                    message.setDateTraitement(Calendar.getInstance());
                    // Renseigne la date d'enrigistrement de la version
                    message.setDate(version.getHorodatage());
                    session.saveDocument(messageDoc);

                    // Crée une notification pour l'annulation / rectification de la version
                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = null;
                    final String modeCreation = version.getModeCreation();
                    if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_VALIDATION_RECTIFICATION_VALUE.equals(
                            modeCreation
                        ) ||
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_VALIDATION_ANNULATION_VALUE.equals(
                            modeCreation
                        ) ||
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                            modeCreation
                        ) ||
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(modeCreation)
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_ACCEPTER_VALUE;
                    } else if (
                        SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_ANNULATION_VALUE.equals(modeCreation)
                    ) {
                        notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_PUBLIER_ANNULATION_VALUE;
                    } else {
                        throw new NuxeoException("Mode de création inconnu: " + modeCreation);
                    }
                    jetonService.createNotificationEvenement(
                        session,
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        messageDoc,
                        notificationType
                    );
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterRejeter(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final String messageType
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(messageType);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());

                    message.setDateTraitement(Calendar.getInstance());

                    Version version = versionDoc.getAdapter(Version.class);

                    // Renseigne la date d'enrigistrement de la version
                    message.setDate(version.getHorodatage());
                    session.saveDocument(messageDoc);

                    // Crée une notification pour le rejet / de l'abandon de la version
                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_REJETER_VALUE;
                    jetonService.createNotificationEvenement(
                        session,
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        messageDoc,
                        notificationType
                    );
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterAbandonner(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final String messageType
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(messageType);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());

                    message.setDateTraitement(Calendar.getInstance());

                    session.saveDocument(messageDoc);

                    // Crée une notification pour le rejet / de l'abandon de la version
                    final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                    String notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_ABANDONNER_VALUE;
                    jetonService.createNotificationEvenement(
                        session,
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        messageDoc,
                        notificationType
                    );
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageMajVisaInterne(
        CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final DocumentModel messageDoc
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                Message message = messageDoc.getAdapter(Message.class);

                // Renseigne l'identifiant de la version publiée
                message.setActiveVersionId(versionDoc.getId());

                session.saveDocument(messageDoc);

                // Crée une notification pour le rejet / de l'abandon de la version
                final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                String notificationType = SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_ABANDONNER_VALUE;
                jetonService.createNotificationEvenement(
                    session,
                    dossierDoc,
                    evenementDoc,
                    versionDoc,
                    messageDoc,
                    notificationType
                );
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterEnregister(
        CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);
                    Version version = versionDoc.getAdapter(Version.class);

                    // Renseigne la date d'enrigistrement de la version
                    message.setDate(version.getHorodatage());

                    session.saveDocument(messageDoc);
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public void updateMessageAfterSupprimerBrouillon(
        CoreSession session,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc
    ) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final Version version = versionDoc.getAdapter(Version.class);

                MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setEvenementId(evenementDoc.getTitle());
                messageCriteria.setMessageType(SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);

                MessageDao messageDao = new MessageDao(session, messageCriteria);
                List<DocumentModel> messageDocList = messageDao.list();

                for (DocumentModel messageDoc : messageDocList) {
                    Message message = messageDoc.getAdapter(Message.class);

                    // Renseigne l'identifiant de la version publiée
                    message.setActiveVersionId(versionDoc.getId());

                    // MAJ de la date du message
                    message.setDate(version.getHorodatage());

                    if (!message.isEtatTraite()) {
                        messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_TRAITE_TRANSITION);
                    }

                    session.saveDocument(messageDoc);
                }
            }
        }
        .runUnrestricted();
    }
}
