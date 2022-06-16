package fr.dila.solonepp.core.service;

import static fr.dila.st.api.constant.STParametreConstant.ADRESSE_URL_APPLICATION;
import static fr.dila.st.api.constant.STPathConstant.PATH_SEP;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static org.apache.commons.lang3.StringUtils.defaultString;

import fr.dila.solonepp.api.constant.SolonEppBaseFunctionConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.dao.criteria.JetonDocCriteria;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.jeton.JetonDoc;
import fr.dila.solonepp.api.domain.mailbox.Mailbox;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.core.assembler.ws.MessageAssembler;
import fr.dila.solonepp.core.dao.JetonDocDao;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.solonepp.rest.api.WSNotification;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.dao.pagination.PageInfo;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.rest.client.HttpTransactionException;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.client.WSProxyFactoryException;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.NotificationEvenementType;
import fr.sword.xsd.solon.epp.NotifierEvenementRequest;
import fr.sword.xsd.solon.epp.NotifierEvenementRequest.Notifications;
import fr.sword.xsd.solon.epp.NotifierEvenementResponse;
import fr.sword.xsd.solon.epp.NotifierTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ObjetType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.xml.bind.JAXBException;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.common.Environment;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Implémentation du service permettant de gérer les jetons.
 *
 * @author jtremeaux
 */
public class JetonServiceImpl extends fr.dila.st.core.service.JetonServiceImpl implements JetonService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(JetonServiceImpl.class);

    private static final String UNKNOWN_MESSAGE_STATE = "Etat du message inconnu : ";

    private static final String UNKNOWN_NOTIFICATION_TYPE = "Type de notification inconnue : ";

    private static final String UNKNOWN_MESSAGE_TYPE = "Type du message inconnu : ";

    private static final String FAIL_NOTIF_TDR =
        "Notification communication web service : la modification tdr n'a pas pu être notifiée";

    private Long nbError = 0L;

    private String value = "";

    public JetonServiceImpl() {
        super();
        // Default constructor
    }

    @Override
    public void createNotificationObjetRefUpdate(
        final CoreSession session,
        final String objetRefType,
        final DocumentModel objetRefDoc,
        final String objetRefId
    ) {
        final DocumentModel jetonDocModel = createBareJetonDoc(session);
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        jetonDoc.setNotificationType(SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_TYPE_OBJET_REF_UPDATE_VALUE);
        jetonDoc.setObjetRefType(objetRefType);
        jetonDoc.setObjetRefId(objetRefId);

        createJetonDocFromBareJeton(
            session,
            jetonDocModel,
            SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE,
            SolonEppBaseFunctionConstant.NOTIFICATION_TABLE_REF_READER,
            objetRefDoc.getId(),
            null,
            null
        );
    }

    @Override
    public void createNotificationObjetRefReset(final CoreSession session, final String objetRefType) {
        final DocumentModel jetonDocModel = createBareJetonDoc(session);
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        jetonDoc.setNotificationType(SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_TYPE_OBJET_REF_RESET_VALUE);
        jetonDoc.setObjetRefType(objetRefType);

        createJetonDocFromBareJeton(
            session,
            jetonDocModel,
            SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE,
            SolonEppBaseFunctionConstant.NOTIFICATION_TABLE_REF_READER,
            null,
            null,
            null
        );
    }

    @Override
    public void createNotificationEvenement(
        final CoreSession session,
        final DocumentModel dossierDoc,
        final DocumentModel evenementDoc,
        final DocumentModel versionDoc,
        final DocumentModel messageDoc,
        final String notificationType
    ) {
        final DocumentModel jetonDocModel = createBareJetonDoc(session);
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        final Message message = messageDoc.getAdapter(Message.class);
        final DocumentModel destinationMailboxDoc = session.getDocument(new IdRef(message.getSenderMailboxId()));
        final Mailbox destinationMailbox = destinationMailboxDoc.getAdapter(Mailbox.class);
        final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
        final String institution = mailboxInstitutionService.getInstitutionIdFromMailboxId(destinationMailbox.getId());

        jetonDoc.setNotificationType(SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_TYPE_OBJET_REF_RESET_VALUE);

        // Renseigne les informations de l'événement
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        jetonDoc.setEvenementId(evenement.getTitle());
        jetonDoc.setEvenementType(evenement.getTypeEvenement());
        jetonDoc.setEvenementLifeCycleState(evenementDoc.getCurrentLifeCycleState());
        jetonDoc.setEvenementEmetteur(evenement.getEmetteur());
        jetonDoc.setEvenementDestinataire(evenement.getDestinataire());
        final List<String> destinataireCopie = new ArrayList<>();
        if (evenement.getDestinataireCopie() != null) {
            destinataireCopie.addAll(evenement.getDestinataireCopie());
        }
        jetonDoc.setEvenementDestinataireCopie(destinataireCopie);

        // Renseigne les informations de la version
        final Version version = versionDoc.getAdapter(Version.class);
        jetonDoc.setVersionLifeCycleState(versionDoc.getCurrentLifeCycleState());
        jetonDoc.setVersionPresencePieceJointe(version.isPieceJointePresente());
        jetonDoc.setVersionObjet(version.getObjet());
        jetonDoc.setVersionHorodatage(version.getHorodatage());
        jetonDoc.setVersionNiveauLecture(version.getNiveauLecture());
        jetonDoc.setVersionNiveauLectureNumero(version.getNiveauLectureNumero());
        jetonDoc.setVersionMajorVersion(version.getNumeroVersion().getMajorVersion());
        jetonDoc.setVersionMinorVersion(version.getNumeroVersion().getMinorVersion());
        if (InstitutionsEnum.SENAT.name().equals(institution)) {
            jetonDoc.setVersionSenat(version.getSenat());
        }
        jetonDoc.setNotificationType(notificationType);

        // Renseigne les informations du dossier
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        jetonDoc.setDossierId(dossier.getTitle());
        jetonDoc.setDossierAlerteCount(dossier.getAlerteCount());

        // Renseigne les informations du message
        final List<String> messageCorbeilleList = new ArrayList<>();
        if (message.getCorbeilleList() != null) {
            messageCorbeilleList.addAll(message.getCorbeilleList());
        }
        jetonDoc.setMessageCorbeilleList(messageCorbeilleList);
        jetonDoc.setMessageLifeCycleState(messageDoc.getCurrentLifeCycleState());
        jetonDoc.setMessageType(message.getMessageType());
        jetonDoc.setMessageArNecessaire(message.isArNecessaire());
        jetonDoc.setMessageArNonDonneCount(message.getArNonDonneCount());

        createJetonDocFromBareJeton(
            session,
            jetonDocModel,
            SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE,
            institution,
            evenementDoc.getId(),
            null,
            null
        );
    }

    @Override
    public void createNotificationEvenement(
        final CoreSession session,
        final DocumentModel evenementDoc,
        final String notificationType
    ) {
        // Récupère le dossier
        final DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());

        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                // Récupère tous les messages
                final MessageCriteria messageCriteria = new MessageCriteria();
                messageCriteria.setCaseDocumentId(evenementDoc.getId());

                final JetonService jetonService = SolonEppServiceLocator.getJetonService();
                final MessageDao messageDao = new MessageDao(session, messageCriteria);
                final List<DocumentModel> messageDocList = messageDao.list();
                for (final DocumentModel messageDoc : messageDocList) {
                    final Message message = messageDoc.getAdapter(Message.class);

                    // Récupère la version active
                    final DocumentModel versionDoc = session.getDocument(new IdRef(message.getActiveVersionId()));

                    // Crée la notification
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
    public List<DocumentModel> findNotification(
        final CoreSession session,
        final String proprietaireId,
        final String jetonType,
        final long size
    ) {
        final JetonDocCriteria criteria = new JetonDocCriteria();
        criteria.setJetonType(jetonType);
        criteria.setProprietaireId(proprietaireId);
        final PageInfo pageInfo = new PageInfo(size, 0);
        final JetonDocDao jetonDocDao = new JetonDocDao(session, criteria, pageInfo);

        return jetonDocDao.list();
    }

    @Override
    public Map<String, Object> getNotificationTableReferenceParam(
        final CoreSession session,
        final DocumentModel jetonDocModel
    ) {
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("type_objet", jetonDoc.getObjetRefType());
        String idObjet = jetonDoc.getObjetRefId();
        if (StringUtils.isEmpty(idObjet)) {
            idObjet = "INTEGRAL";
        }
        paramMap.put("id_objet", idObjet);

        return paramMap;
    }

    @Override
    public Map<String, Object> getNotificationEvenementParam(
        final CoreSession session,
        final DocumentModel jetonDocModel
    ) {
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("type_message", jetonDoc.getMessageType());
        paramMap.put("etat_version", jetonDoc.getVersionLifeCycleState().toUpperCase());
        paramMap.put("id_evenement", jetonDoc.getEvenementId());
        final String etatEvenementKey = "evenement.etat." + jetonDoc.getEvenementLifeCycleState();
        paramMap.put("etat_evenement", ResourceHelper.getString(etatEvenementKey));
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final EvenementTypeDescriptor evenementTypeDescriptor = evenementTypeService.getEvenementType(
            jetonDoc.getEvenementType()
        );
        if (evenementTypeDescriptor != null) {
            paramMap.put("type_evenement", evenementTypeDescriptor.getLabel());
        }
        if (jetonDoc.getVersionHorodatage() != null) {
            paramMap.put("date_evenement", SolonDateConverter.DATE_SLASH.format(jetonDoc.getVersionHorodatage()));
        }

        paramMap.put("emetteur_evenement", ResourceHelper.getString(jetonDoc.getEvenementEmetteur()));
        paramMap.put("destinataire_evenement", defaultString(getString(jetonDoc.getEvenementDestinataire())));

        final List<String> copieList = new ArrayList<>();
        if (jetonDoc.getEvenementDestinataireCopie() != null) {
            for (final String copie : jetonDoc.getEvenementDestinataireCopie()) {
                copieList.add(ResourceHelper.getString(copie));
            }
        }
        paramMap.put("copie_evenement", StringUtils.join(copieList, ", "));
        final String pieceJointeLabelKey = jetonDoc.isVersionPresencePieceJointe() ? "label.yes" : "label.no";
        paramMap.put("presence_piece_jointe", ResourceHelper.getString(pieceJointeLabelKey));
        paramMap.put("objet", jetonDoc.getVersionObjet());
        final VocabularyService vocabularyService = STServiceLocator.getVocabularyService();
        if (StringUtils.isNotBlank(jetonDoc.getVersionNiveauLecture())) {
            String niveauLectureLabel = "";
            if (jetonDoc.getVersionNiveauLectureNumero() != null) {
                niveauLectureLabel += jetonDoc.getVersionNiveauLectureNumero() + " - ";
            }
            niveauLectureLabel +=
                vocabularyService.getLabelFromId(
                    SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY,
                    jetonDoc.getVersionNiveauLecture(),
                    STVocabularyConstants.COLUMN_LABEL
                );
            paramMap.put("niveau_lecture", niveauLectureLabel);
        }
        paramMap.put("id_dossier", jetonDoc.getDossierId());
        paramMap.put("id_senat", jetonDoc.getVersionSenat());
        final NumeroVersion version = new NumeroVersion(
            jetonDoc.getVersionMajorVersion(),
            jetonDoc.getVersionMinorVersion()
        );
        paramMap.put("numero_version", version.toString());
        String dossierAlerteKey = null;
        if (jetonDoc.getDossierAlerteCount() > 0) {
            dossierAlerteKey = "evenement.etat.alerte";
        } else {
            dossierAlerteKey = "evenement.etat.instance";
        }
        paramMap.put("etat_dossier", ResourceHelper.getString(dossierAlerteKey));
        String messageLifeCycleState = null;
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(jetonDoc.getMessageType())) {
            if (SolonEppLifecycleConstant.MESSAGE_NON_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                messageLifeCycleState = "EN_COURS_REDACTION";
            } else if (SolonEppLifecycleConstant.MESSAGE_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                if (!jetonDoc.isMessageArNecessaire()) {
                    messageLifeCycleState = "EMIS";
                } else {
                    if (jetonDoc.getMessageArNonDonneCount() > 0) {
                        messageLifeCycleState = "EN_ATTENTE_AR";
                    } else {
                        messageLifeCycleState = "AR_RECU";
                    }
                }
            } else {
                throw new NuxeoException(UNKNOWN_MESSAGE_STATE + jetonDoc.getMessageLifeCycleState());
            }
        } else if (
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(jetonDoc.getMessageType()) ||
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(jetonDoc.getMessageType())
        ) {
            if (SolonEppLifecycleConstant.MESSAGE_NON_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                messageLifeCycleState = "NON_TRAITE";
            } else if (SolonEppLifecycleConstant.MESSAGE_EN_COURS_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                messageLifeCycleState = "EN_COURS_TRAITEMENT";
            } else if (SolonEppLifecycleConstant.MESSAGE_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                messageLifeCycleState = "TRAITE";
            } else {
                throw new NuxeoException(UNKNOWN_MESSAGE_STATE + jetonDoc.getMessageLifeCycleState());
            }
        } else {
            throw new NuxeoException(UNKNOWN_MESSAGE_TYPE + jetonDoc.getMessageType());
        }
        paramMap.put("etat_message", ResourceHelper.getString("evenement.etat." + messageLifeCycleState));
        paramMap.put("type_version", jetonDoc.getNotificationType());
        paramMap.put("url_evenement", getLinkHtmlToEvent(session, jetonDoc.getEvenementId()));

        return paramMap;
    }

    @Override
    public void sendNotificationEmail(final CoreSession session, final DocumentModel jetonDocModel) {
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        // Détermine l'objet et le corps du mail
        final String jetonType = jetonDoc.getTypeWebservice();
        final STParametreService parametreService = STServiceLocator.getSTParametreService();
        final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
        final ProfileService profileService = STServiceLocator.getProfileService();
        final STUserService userService = STServiceLocator.getSTUserService();
        String mailObjet = "";
        String mailCorps = "";
        Map<String, Object> paramMap = null;
        List<Address> userEmailList;
        if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE.equals(jetonType)) {
            mailObjet =
                parametreService.getParametreValue(
                    session,
                    SolonEppParametreConstant.MAIL_NOTIFICATION_TABLE_REFERENCE_OBJET
                );
            mailCorps =
                parametreService.getParametreValue(
                    session,
                    SolonEppParametreConstant.MAIL_NOTIFICATION_TABLE_REFERENCE_CORPS
                );
            paramMap = getNotificationTableReferenceParam(session, jetonDocModel);
            List<STUser> userList = profileService.getUsersFromBaseFunction(
                SolonEppBaseFunctionConstant.NOTIFICATION_EMAIL_RECIPIENT
            );
            final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator.getProfilUtilisateurService();
            userList = profilUtilisateurService.filterUserForNotification(session, userList);
            userEmailList = userService.getAddressFromUserList(userList);
        } else if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE.equals(jetonType)) {
            mailObjet =
                parametreService.getParametreValue(
                    session,
                    SolonEppParametreConstant.MAIL_NOTIFICATION_EVENEMENT_OBJET
                );
            mailCorps =
                parametreService.getParametreValue(
                    session,
                    SolonEppParametreConstant.MAIL_NOTIFICATION_EVENEMENT_CORPS
                );
            paramMap = getNotificationEvenementParam(session, jetonDocModel);
            List<STUser> userList = organigrammeService.getUserFromInstitutionAndBaseFunction(
                jetonDoc.getIdOwner(),
                SolonEppBaseFunctionConstant.NOTIFICATION_EMAIL_RECIPIENT
            );
            final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator.getProfilUtilisateurService();
            userList = profilUtilisateurService.filterUserForNotification(session, userList);
            userEmailList = userService.getAddressFromUserList(userList);
        } else {
            throw new NuxeoException(UNKNOWN_NOTIFICATION_TYPE + jetonType);
        }

        // Envoie l'email au destinataire de la notification
        final STMailService mailService = STServiceLocator.getSTMailService();
        mailService.sendTemplateHtmlMail(userEmailList, mailObjet, mailCorps, paramMap);
    }

    @Override
    public void sendNotificationWebService(final CoreSession session, final DocumentModel jetonDocModel) {
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);

        final Notifications notification = new Notifications();
        final String jetonType = jetonDoc.getTypeWebservice();
        final String institutionId = jetonDoc.getIdOwner();

        if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_TABLE_REF_VALUE.equals(jetonType)) {
            final WSProxyFactory wsProxyFactory = getWSProxyFactory(InstitutionsEnum.GOUVERNEMENT.name());
            if (wsProxyFactory == null) {
                LOGGER.info(
                    session,
                    STLogEnumImpl.FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC,
                    "Notification communication web service : aucun poste trouvé pour l'institution GOUVERNEMENT"
                );
            } else {
                WSNotification wsNotification = null;
                try {
                    wsNotification = wsProxyFactory.getService(WSNotification.class, value);
                } catch (final WSProxyFactoryException e) {
                    LOGGER.info(
                        session,
                        STLogEnumImpl.FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC,
                        "Notification communication web service : le service de notification n'a pas pu être retrouvé",
                        e
                    );
                    return;
                }

                final NotifierTableDeReferenceRequest request = new NotifierTableDeReferenceRequest();
                request.setType(ObjetType.valueOf(jetonDoc.getObjetRefType()));
                try {
                    wsNotification.notifierTableDeReference(request);
                } catch (JAXBException | HttpTransactionException e) {
                    LOGGER.info(session, STLogEnumImpl.FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC, FAIL_NOTIF_TDR, e);
                }
            }
        } else if (SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE.equals(jetonType)) {
            final MessageAssembler messageAssembler = new MessageAssembler(session);
            notification.setMessage(messageAssembler.assembleMessageJetonDocToWs(jetonDocModel));
            notification.setTypeNotification(NotificationEvenementType.valueOf(jetonDoc.getNotificationType()));
            final List<String> messageCorbeilleList = jetonDoc.getMessageCorbeilleList();
            if (messageCorbeilleList != null) {
                notification.getIdCorbeille().addAll(messageCorbeilleList);
            }
            final fr.sword.xsd.solon.epp.Version version = new fr.sword.xsd.solon.epp.Version();
            version.setMajeur(jetonDoc.getVersionMajorVersion().intValue());
            version.setMineur(jetonDoc.getVersionMinorVersion().intValue());
            notification.setVersion(version);
            final NotifierEvenementRequest request = new NotifierEvenementRequest();
            request.getNotifications().add(notification);

            final WSProxyFactory wsProxyFactory = getWSProxyFactory(institutionId);
            if (wsProxyFactory == null) {
                nbError++;
                LOGGER.info(
                    session,
                    STLogEnumImpl.FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC,
                    "Notification communication web service : aucun poste trouvé pour l'institution " + institutionId
                );
                return;
            }
            WSNotification wsNotification = null;
            try {
                wsNotification = wsProxyFactory.getService(WSNotification.class, value);
            } catch (final WSProxyFactoryException e) {
                nbError++;
                throw new NuxeoException("Echec d'envoi de la notification", e);
            }

            try {
                callNotification(wsNotification, request);
            } catch (final Exception e) {
                long wsRetryLeft = jetonDoc.getWsRetryLeft();
                if (wsRetryLeft == 0) {
                    wsRetryLeft = 3;
                } else {
                    wsRetryLeft--;
                }
                nbError++;
                LOGGER.info(
                    session,
                    STLogEnumImpl.FAIL_SEND_NOTIFICATION_COMMUNICATION_WS_TEC,
                    "Erreur lors de l'appel au WS notifierEvenement de l'institution " +
                    institutionId +
                    ", " +
                    wsRetryLeft +
                    " essais restants",
                    e
                );

                jetonDoc.setWsRetryLeft(wsRetryLeft);
                session.saveDocument(jetonDocModel);

                if (wsRetryLeft == 0) {
                    // Trop d'essais effectués, abandon de la notification et envoi d'un mail aux administrateurs
                    // fonctionnels
                    final ProfileService profileService = STServiceLocator.getProfileService();
                    final List<STUser> userList = profileService.getUsersFromBaseFunction(
                        SolonEppBaseFunctionConstant.NOTIFICATION_EMAIL_ERROR_RECIPIENT
                    );
                    final STMailService mailService = STServiceLocator.getSTMailService();
                    final STUserService userService = STServiceLocator.getSTUserService();
                    final List<Address> addressList = userService.getAddressFromUserList(userList);

                    final STParametreService parametreService = STServiceLocator.getSTParametreService();
                    final String mailObjet = parametreService.getParametreValue(
                        session,
                        SolonEppParametreConstant.MAIL_NOTIFICATION_ERROR_OBJET
                    );
                    final String mailCorps = parametreService.getParametreValue(
                        session,
                        SolonEppParametreConstant.MAIL_NOTIFICATION_ERROR_CORPS
                    );
                    final Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("institution", institutionId);
                    paramMap.put("evenement", jetonDoc.getEvenementId());
                    paramMap.put("jeton_id", jetonDocModel.getId());
                    mailService.sendTemplateHtmlMail(addressList, mailObjet, mailCorps, paramMap);
                }
                return;
            }

            // Succès : pas de ré-essai supplémentaire
            if (jetonDoc.getWsRetryLeft() > 0) {
                jetonDoc.setWsRetryLeft(0L);
                session.saveDocument(jetonDocModel);
            }
        } else {
            throw new NuxeoException(UNKNOWN_NOTIFICATION_TYPE + jetonType);
        }
    }

    /**
     * Appelle le WS notifierEvenement. Lève toujours une exception si un traitement s'est mal passé (erreur réseau,
     * réponse KO...)
     *
     * @param wsNotification
     *            WS Notification
     * @param request
     *            Requête
     * @return Réponse
     */
    protected NotifierEvenementResponse callNotification(
        final WSNotification wsNotification,
        final NotifierEvenementRequest request
    ) {
        NotifierEvenementResponse response = null;
        try {
            response = wsNotification.notifierEvenement(request);
        } catch (final Exception e) {
            throw new NuxeoException("Erreur lors de l'appel à notifierEvenement", e);
        }

        if (response == null || response.getStatut() == null) {
            throw new NuxeoException("null response to notification");
        }

        final TraitementStatut statut = response.getStatut();

        if (statut != TraitementStatut.OK) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("bad response status ");
            stringBuilder.append(statut);

            try {
                stringBuilder.append("response was \n");
                stringBuilder.append(JaxBHelper.marshallToString(response, NotifierEvenementResponse.class));
            } catch (final JAXBException e) {
                stringBuilder.append("  could not unmarshall response");
            }
            throw new NuxeoException(stringBuilder.toString());
        }

        return response;
    }

    /**
     * Récupère les paramètres (URL, login, clé, alias) du client WS pour une institution donnée. Les paramètres sont
     * stockés dans un noeud Poste de l'organigramme en dessous de l'institution. Le premier noeud trouvé est utilisé.
     *
     * @param institutionId
     *            Identifiant technique de l'institution
     * @return Proxy WS
     */
    protected WSProxyFactory getWSProxyFactory(final String institutionId) {
        final List<PosteNode> posteNodeList = SolonEppServiceLocator
            .getOrganigrammeService()
            .getPosteFromInstitution(institutionId);
        String url = null;
        String username = null;
        String keyAlias = null;

        boolean forGouv = InstitutionsEnum.GOUVERNEMENT.name().equals(institutionId);

        for (final PosteNode posteNode : posteNodeList) {
            if (StringUtils.isNotBlank(posteNode.getWsUrl()) && !posteNode.getDeleted() && posteNode.isActive()) {
                url = posteNode.getWsUrl();
                username = posteNode.getWsUser();
                value = posteNode.getWsPassword();
                keyAlias = posteNode.getWsKeyAlias();
                // Le gouvernement peut avoir plusieurs ws à viser : epg et mgpp
                if (!forGouv || url.contains("/site/solonmgpp")) {
                    break;
                }
            }
        }
        if (url == null) {
            return null;
        }

        return new WSProxyFactory(url, null, username, keyAlias);
    }

    @Override
    public void retryNotification(final CoreSession session, final BatchLoggerModel batchloggerModel, Long nbError) {
        final JetonDocCriteria criteria = new JetonDocCriteria();
        criteria.setRetryGreaterThanZero(true);
        final JetonDocDao jetonDocDao = new JetonDocDao(session, criteria, null);
        this.nbError = nbError;
        for (final DocumentModel jetonDocModel : jetonDocDao.list()) {
            final long startTime = Calendar.getInstance().getTimeInMillis();
            sendNotificationWebService(session, jetonDocModel);
            final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);
            final long endTime = Calendar.getInstance().getTimeInMillis();
            try {
                STServiceLocator
                    .getSuiviBatchService()
                    .createBatchResultFor(
                        batchloggerModel,
                        "Appel au WS notifierEvenement de l'institution : " + jetonDoc.getIdOwner(),
                        endTime - startTime
                    );
            } catch (Exception exc) {
                LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
            }
        }
    }

    /**
     * Lien vers la recherche de l'événement
     *
     * @param session
     * @param eventId
     * @return
     */
    public String getLinkHtmlToEvent(final CoreSession session, final String eventId) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final ConfigService configService = STServiceLocator.getConfigService();
        String urlApp = paramService.getParametreValue(session, ADRESSE_URL_APPLICATION);
        if (StringUtils.isBlank(urlApp)) {
            urlApp = configService.getValue(STConfigConstants.SERVER_URL);
        }
        urlApp = StringUtils.removeEnd(urlApp, PATH_SEP);
        String contextPath = configService.getValue(Environment.NUXEO_CONTEXT_PATH);
        urlApp = StringUtils.appendIfMissing(urlApp, contextPath);
        return urlApp + "/searchEvent.faces?evenementId=" + eventId;
    }

    @Override
    public Long getCountJetonsCorbeilleSince(final CoreSession session, final String corbeille, final Calendar date) {
        final JetonDocCriteria criteria = new JetonDocCriteria();
        criteria.setCorbeille(corbeille);
        criteria.setDateMax(date);
        final JetonDocDao jetonDocDao = new JetonDocDao(session, criteria, null);
        return jetonDocDao.count();
    }

    @Override
    public Long getCountJetonsEvenementSince(final CoreSession session, final String evenementId, final Calendar date) {
        final JetonDocCriteria criteria = new JetonDocCriteria();
        criteria.setEvenementId(evenementId);
        criteria.setDateMax(date);
        final JetonDocDao jetonDocDao = new JetonDocDao(session, criteria, null);

        return jetonDocDao.count();
    }
}
