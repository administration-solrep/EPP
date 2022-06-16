package fr.dila.solonepp.rest.management;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.dao.criteria.OrderByCriteria;
import fr.dila.solonepp.api.dao.criteria.PieceJointeCriteria;
import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleService;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementAssemblerService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.VersionCreationService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;
import fr.dila.solonepp.api.service.evenement.MajInterneContext;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageContext;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageDTO;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageRequest;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageResponse;
import fr.dila.solonepp.api.service.version.AccuserReceptionContext;
import fr.dila.solonepp.api.service.version.CreerVersionContext;
import fr.dila.solonepp.api.service.version.ValiderVersionContext;
import fr.dila.solonepp.api.service.version.VersionContainer;
import fr.dila.solonepp.core.assembler.ws.EvenementAssembler;
import fr.dila.solonepp.core.assembler.ws.EvenementTypeAssembler;
import fr.dila.solonepp.core.assembler.ws.MessageAssembler;
import fr.dila.solonepp.core.assembler.ws.NiveauLectureCodeAssembler;
import fr.dila.solonepp.core.assembler.ws.PieceJointeAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.parser.XsdToUfnxqlParser;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.dao.pagination.PageInfo;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.DateUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.AccuserReceptionRequest;
import fr.sword.xsd.solon.epp.AccuserReceptionResponse;
import fr.sword.xsd.solon.epp.AnnulerEvenementRequest;
import fr.sword.xsd.solon.epp.AnnulerEvenementResponse;
import fr.sword.xsd.solon.epp.AttributionCommission;
import fr.sword.xsd.solon.epp.ChercherEvenementRequest;
import fr.sword.xsd.solon.epp.ChercherEvenementResponse;
import fr.sword.xsd.solon.epp.ChercherPieceJointeRequest;
import fr.sword.xsd.solon.epp.ChercherPieceJointeResponse;
import fr.sword.xsd.solon.epp.CreationType;
import fr.sword.xsd.solon.epp.CreerVersionDeltaRequest;
import fr.sword.xsd.solon.epp.CreerVersionDeltaResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.EnvoyerMelRequest;
import fr.sword.xsd.solon.epp.EnvoyerMelResponse;
import fr.sword.xsd.solon.epp.EppBaseEvenement;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EppEvtDelta;
import fr.sword.xsd.solon.epp.EtatDossier;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.InitialiserEvenementRequest;
import fr.sword.xsd.solon.epp.InitialiserEvenementResponse;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.MajInterneRequest;
import fr.sword.xsd.solon.epp.MajInterneResponse;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.MotifIrrecevabilite;
import fr.sword.xsd.solon.epp.NatureLoi;
import fr.sword.xsd.solon.epp.NatureRapport;
import fr.sword.xsd.solon.epp.NiveauLecture;
import fr.sword.xsd.solon.epp.OrderBy;
import fr.sword.xsd.solon.epp.Pagination;
import fr.sword.xsd.solon.epp.PieceJointe;
import fr.sword.xsd.solon.epp.QueryRechercheEvenement;
import fr.sword.xsd.solon.epp.RapportParlement;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.ResultatCMP;
import fr.sword.xsd.solon.epp.SensAvis;
import fr.sword.xsd.solon.epp.SortAdoption;
import fr.sword.xsd.solon.epp.SupprimerVersionRequest;
import fr.sword.xsd.solon.epp.SupprimerVersionResponse;
import fr.sword.xsd.solon.epp.TypeActe;
import fr.sword.xsd.solon.epp.TypeLoi;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;
import fr.sword.xsd.solon.epp.Version;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Permet de gerer toutes les operations sur le webservice WSEvenement.
 *
 * @author sly
 */
public class EvenementDelegate {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EvenementDelegate.class);

    private static final String ERROR_CANT_GET_INFO_COM =
        "Impossible de récupérer les informations de la communication.";

    private static final String ERROR_CANT_GET_INFO_EVT =
        "Impossible de récupérer les informations de création d'évènement.";

    private static final String ERROR_VERSION_NOT_FOUND = "Version non trouvée";

    private static final String ERROR_CANT_GET_INFO_REPONSE = "Impossible de récupérer les informations de la reponse.";

    private static final String ERROR_EVT_NOT_FOUND = "Evenement non trouvé : ";
    /**
     * Session.
     */
    protected CoreSession session;

    /**
     * Constructeur de EvenementDelegate.
     *
     * @param session
     */
    public EvenementDelegate(final CoreSession session) {
        this.session = session;
    }

    /**
     * Méthode couteau suisse qui crée / modifie / publie / complète / rectifie une version d'un événement, crée / annule / modifie un événement, crée / supprime un dossier.
     *
     * @param request Requête
     * @return Réponse
     */
    public CreerVersionResponse creerVersion(final CreerVersionRequest request) {
        final CreerVersionResponse response = new CreerVersionResponse();
        final CreationType creationType = request.getModeCreation();
        final EppEvtContainer eppEvtContainer = request.getEvenement();

        final CreerVersionContext creerVersionContext = new CreerVersionContext();
        final fr.dila.solonepp.api.service.version.CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();

        // Assemble les données de l'événement et de la version
        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        evenementAssembler.assembleXsdToEvenement(eppEvtContainer, creerVersionRequest);

        // Renseigne le type de création
        switch (creationType) {
            case CREER_BROUILLON:
                creerVersionContext.setPublie(false);
                break;
            case PUBLIER:
                creerVersionContext.setPublie(true);
                break;
            default:
            // L'état de publication est non applicable aux autres modes de création
        }

        // Crée / modifie la version
        final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
        switch (creationType) {
            case CREER_BROUILLON:
            case PUBLIER:
                versionCreationService.createVersion(session, creerVersionContext);
                break;
            case COMPLETER_BROUILLON:
                versionCreationService.completerBrouillon(session, creerVersionContext);
                break;
            case COMPLETER_PUBLIER:
                versionCreationService.completerPublier(session, creerVersionContext);
                break;
            case RECTIFIER_BROUILLON:
                versionCreationService.rectifierBrouillon(session, creerVersionContext);
                break;
            case RECTIFIER_PUBLIER:
                versionCreationService.rectifierPublier(session, creerVersionContext);
                break;
            default:
                throw new NuxeoException("Mode de création inconnu: " + creationType);
        }

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.version.CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();
        if (creerVersionResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_EVT);
        }

        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            creerVersionResponse.getDossierDoc(),
            creerVersionResponse.getEvenementDoc(),
            creerVersionResponse.getVersionDoc(),
            creerVersionResponse.getPieceJointeDocList(),
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);

        return response;
    }

    /**
     * Publie une nouvelle version (complétée ou rectifiée) en mode delta.
     *
     * @param request Requete
     * @return Réponse
     */
    public CreerVersionDeltaResponse creerVersionDelta(final CreerVersionDeltaRequest request) {
        final CreerVersionDeltaResponse response = new CreerVersionDeltaResponse();

        // Recherche l'événement et le dossier
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final EppEvtDelta eppEvtDelta = request.getEvenement();
        final String evenementId = eppEvtDelta.getIdEvenement();
        final DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException(ERROR_EVT_NOT_FOUND + evenementId);
        }
        final DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());

        // Charge la version publiée de l'événement à modifier
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel versionPublieeDoc = versionService.getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (versionPublieeDoc == null) {
            throw new NuxeoException("Version publiée non trouvée pour la communication : " + evenementId);
        }

        final CreerVersionContext creerVersionContext = new CreerVersionContext();
        final fr.dila.solonepp.api.service.version.CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        creerVersionRequest.setDossierDoc(dossierDoc);
        creerVersionRequest.setEvenementDoc(evenementDoc);
        creerVersionRequest.setVersionDoc(versionPublieeDoc);

        // Charge les pièces jointes de la version publiée
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pieceJointeVersionPublieeDocList = pieceJointeService.getVersionPieceJointeListWithFichier(
            session,
            versionPublieeDoc
        );
        creerVersionRequest.getPieceJointeDocList().addAll(pieceJointeVersionPublieeDocList);

        // Assemble les données de la version publiée
        EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        evenementAssembler.assembleXsdToEvenementDelta(
            eppEvtDelta,
            evenementDoc,
            versionPublieeDoc,
            creerVersionRequest.getPieceJointeDocList()
        );

        // Charge la version brouillon de l'événement à modifier (peut être nulle)
        final DocumentModel versionBrouillonDoc = versionService.getVersionBrouillonUlterieure(
            session,
            versionPublieeDoc
        );
        if (versionBrouillonDoc != null) {
            final VersionContainer versionContainer = new VersionContainer();
            versionContainer.setVersionDoc(versionBrouillonDoc);
            creerVersionRequest.setSecondaryVersionContainer(versionContainer);

            // Charge les pièces jointes de la version brouillon
            final List<DocumentModel> pjVersionBrouillonDocList = pieceJointeService.getVersionPieceJointeListWithFichier(
                session,
                versionBrouillonDoc
            );
            versionContainer.getPieceJointeDocList().addAll(pjVersionBrouillonDocList);

            // Assemble les données de la version publiée
            evenementAssembler = new EvenementAssembler(session);
            evenementAssembler.assembleXsdToEvenementDelta(
                eppEvtDelta,
                evenementDoc,
                versionBrouillonDoc,
                versionContainer.getPieceJointeDocList()
            );
        }

        // Crée / modifie la version
        final CreationType creationType = request.getModeCreation();
        final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
        switch (creationType) {
            case COMPLETER_PUBLIER:
                versionCreationService.completerPublierDelta(session, creerVersionContext);
                break;
            case RECTIFIER_PUBLIER:
                versionCreationService.rectifierPublierDelta(session, creerVersionContext);
                break;
            default:
                throw new NuxeoException("Mode de création interdit en mode delta : " + creationType);
        }

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.version.CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();
        if (creerVersionResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_EVT);
        }

        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            creerVersionResponse.getDossierDoc(),
            creerVersionResponse.getEvenementDoc(),
            creerVersionResponse.getVersionDoc(),
            creerVersionResponse.getPieceJointeDocList(),
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);

        return response;
    }

    /**
     * Accuse la réception d'un message.
     *
     * @param request Requête
     * @return Réponse
     */
    public AccuserReceptionResponse accuserReception(final AccuserReceptionRequest request) {
        final AccuserReceptionResponse response = new AccuserReceptionResponse();

        final EvtId evtId = request.getIdEvenement();
        final fr.sword.xsd.solon.epp.Version version = evtId.getVersion();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final AccuserReceptionContext accuserReceptionContext = new AccuserReceptionContext();
        final fr.dila.solonepp.api.service.version.AccuserReceptionRequest accuserReceptionRequest = accuserReceptionContext.getAccuserReceptionRequest();
        accuserReceptionRequest.setEvenementId(evtId.getId());
        if (version != null) {
            final NumeroVersion numeroVersion = new NumeroVersion(
                (long) version.getMajeur(),
                (long) version.getMineur()
            );
            accuserReceptionRequest.setNumeroVersion(numeroVersion);
        }
        versionService.accuserReceptionDestinataire(session, accuserReceptionContext);

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.version.AccuserReceptionResponse creerVersionResponse = accuserReceptionContext.getAccuserReceptionResponse();
        if (creerVersionResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_COM);
        }

        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            creerVersionResponse.getDossierDoc(),
            creerVersionResponse.getEvenementDoc(),
            creerVersionResponse.getVersionDoc(),
            null,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Accepte / rejete la rectification d'un événement (pour le destinataire). Abandonne la rectification d'un événement (pour l'émetteur).
     *
     * @param request Requête
     * @return Réponse
     */
    public ValiderVersionResponse validerVersion(final ValiderVersionRequest request) {
        final ValiderVersionResponse response = new ValiderVersionResponse();

        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final ValiderVersionContext validerVersionContext = new ValiderVersionContext();
        final fr.dila.solonepp.api.service.version.ValiderVersionRequest validerVersionRequest = validerVersionContext.getValiderVersionRequest();
        validerVersionRequest.setEvenementId(request.getIdEvenement());
        String messageType = null;
        switch (request.getModeValidation()) {
            case ACCEPTER:
                validerVersionRequest.setAccepter(true);
                versionService.validerVersionDestinataire(session, validerVersionContext);
                messageType = SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE;
                break;
            case REJETER:
                validerVersionRequest.setAccepter(false);
                versionService.validerVersionDestinataire(session, validerVersionContext);
                messageType = SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE;
                break;
            case ABANDONNER:
                validerVersionRequest.setAccepter(false);
                versionService.validerVersionEmetteur(session, validerVersionContext);
                messageType = SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE;
                break;
            default:
                throw new NuxeoException("Mode de validation inconnu: " + request.getModeValidation());
        }

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.version.ValiderVersionResponse creerVersionResponse = validerVersionContext.getValiderVersionResponse();
        if (creerVersionResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_COM);
        }

        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            creerVersionResponse.getDossierDoc(),
            creerVersionResponse.getEvenementDoc(),
            creerVersionResponse.getVersionDoc(),
            null,
            messageType
        );

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Recherche d'événements à partir de leur identifiants techniques ou via un jeton.
     *
     * @param request Requête
     * @return Réponse
     * @throws NuxeoException
     * @throws IOException
     */
    public ChercherEvenementResponse chercherEvenement(final ChercherEvenementRequest request) throws IOException {
        final ChercherEvenementResponse response = new ChercherEvenementResponse();

        // Récupération des services utiles
        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();

        response.setStatut(TraitementStatut.OK);
        response.setDernierRenvoi(true);
        response.setJetonEvenementSuivant("");

        final boolean contenuPj = request.isContenuPJ();

        // Construit la liste des événements (+ éventuellement version spécifique) à rechercher
        List<EvtId> evtIdList = request.getIdEvenement();
        List<DocumentModel> evtDocModelList = new ArrayList<>();
        if (StringUtils.isNotBlank(request.getJetonEvenement())) {
            final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
            final String institutionId = principal.getInstitutionId();
            final Long numeroJeton = Long.valueOf(request.getJetonEvenement());

            // Recherche l'ensemble des notifications à partir du jeton
            final JetonServiceDto result = jetonService.getDocuments(
                session,
                institutionId,
                numeroJeton,
                SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE
            );

            // Si le jeton maitre est introuvable, on retourne le dernier jeton maitre
            if (result == null) {
                long numeroJetonSuivant =
                    jetonService
                        .getNumeroJetonMaxForWS(
                            session,
                            institutionId,
                            SolonEppSchemaConstant.JETON_DOC_TYPE_WEBSERVICE_EVENEMENT_VALUE
                        )
                        .longValue() +
                    1;
                response.setJetonEvenementSuivant(Long.toString(numeroJetonSuivant));
                return response;
            }

            evtDocModelList = result.getDocumentList();

            final List<EppEvtContainer> eppEvtContainerList = response.getEvenement();
            final List<String> eppEvtIdList = new ArrayList<>();

            if (evtDocModelList.isEmpty() && !result.getJetonDocDocList().isEmpty()) {
                response.setMessageErreur(result.getMessageErreur());
            } else {
                final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
                for (final DocumentModel evenementDoc : evtDocModelList) {
                    if (eppEvtIdList.contains(evenementDoc.getTitle())) {
                        continue;
                    }

                    final DocumentModel messageDoc = messageService.getMessageByEvenementId(
                        session,
                        evenementDoc.getTitle()
                    );
                    if (messageDoc == null) {
                        throw new NuxeoException("Communication non trouvé: " + evenementDoc.getTitle());
                    }
                    final fr.dila.solonepp.api.domain.message.Message message = messageDoc.getAdapter(
                        fr.dila.solonepp.api.domain.message.Message.class
                    );

                    // récupère la version active
                    DocumentModel versionDoc = null;
                    versionDoc = versionService.getVersionActive(session, evenementDoc, message.getMessageType());
                    if (versionDoc == null) {
                        throw new NuxeoException(
                            "Version active non trouvée pour la communication : " + evenementDoc.getTitle()
                        );
                    }

                    // Récupère le dossier
                    final DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());

                    // Récupère les pièces jointes
                    final List<DocumentModel> pieceJointeDocList = pieceJointeService.getVersionPieceJointeListWithFichier(
                        session,
                        versionDoc
                    );

                    final EppEvtContainer eppEvtContainer = evenementAssembler.assembleEvenementToXsd(
                        dossierDoc,
                        evenementDoc,
                        versionDoc,
                        pieceJointeDocList,
                        message.getMessageType(),
                        contenuPj
                    );
                    eppEvtContainerList.add(eppEvtContainer);
                    eppEvtIdList.add(evenementDoc.getTitle());
                }
            }

            if (result.getNextJetonNumber() != null) {
                final String jetonSortant = result.getNextJetonNumber().toString();
                response.setJetonEvenementSuivant(jetonSortant);
            }

            if (result.isLastSending() != null) {
                response.setDernierRenvoi(result.isLastSending());
            }
        } else {
            evtIdList = request.getIdEvenement();
            final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
            final List<EppEvtContainer> eppEvtContainerList = response.getEvenement();
            for (final EvtId evtId : evtIdList) {
                final DocumentModel evenementDoc = evenementService.getEvenement(session, evtId.getId());
                evtDocModelList.add(evenementDoc);

                final DocumentModel messageDoc = messageService.getMessageByEvenementId(session, evtId.getId());
                if (messageDoc == null) {
                    throw new NuxeoException(ERROR_EVT_NOT_FOUND + evtId.getId());
                }
                final fr.dila.solonepp.api.domain.message.Message message = messageDoc.getAdapter(
                    fr.dila.solonepp.api.domain.message.Message.class
                );

                // Si la version n'est pas spécifiée, récupère la version active
                DocumentModel versionDoc = null;
                final fr.sword.xsd.solon.epp.Version version = evtId.getVersion();
                if (version == null) {
                    versionDoc = versionService.getVersionActive(session, evenementDoc, message.getMessageType());
                    if (versionDoc == null) {
                        throw new NuxeoException("Version active non trouvée pour la communication : " + evtId.getId());
                    }
                } else {
                    final NumeroVersion numeroVersion = new NumeroVersion(
                        (long) version.getMajeur(),
                        (long) version.getMineur()
                    );
                    versionDoc =
                        versionService.getVersionVisible(
                            session,
                            evenementDoc,
                            message.getMessageType(),
                            numeroVersion
                        );
                    if (versionDoc == null) {
                        throw new NuxeoException(
                            "Version spécifique non trouvée pour la communication : " +
                            evtId.getId() +
                            " et le numéro de version: " +
                            numeroVersion
                        );
                    }
                }
                // Récupère le dossier
                final DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());

                // Récupère les pièces jointes
                final List<DocumentModel> pieceJointeDocList = pieceJointeService.getVersionPieceJointeListWithFichier(
                    session,
                    versionDoc
                );

                final EppEvtContainer eppEvtContainer = evenementAssembler.assembleEvenementToXsd(
                    dossierDoc,
                    evenementDoc,
                    versionDoc,
                    pieceJointeDocList,
                    message.getMessageType(),
                    contenuPj
                );
                eppEvtContainerList.add(eppEvtContainer);
            }
        }

        return response;
    }

    /**
     * Annule un événement (si l'événement est publié), ou crée une nouvelle version correspondant à la demande d'annulation (si l'événement est en instance).
     *
     * @param request Requête
     * @return Réponse
     */
    public AnnulerEvenementResponse annulerEvenement(final AnnulerEvenementRequest request) {
        final AnnulerEvenementResponse response = new AnnulerEvenementResponse();

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final AnnulerEvenementContext annulerEvenementContext = new AnnulerEvenementContext();
        final fr.dila.solonepp.api.service.evenement.AnnulerEvenementRequest annulerEvenementRequest = annulerEvenementContext.getAnnulerEvenementRequest();
        annulerEvenementRequest.setEvenementId(request.getIdEvenement());
        evenementService.annulerEvenement(session, annulerEvenementContext);

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.evenement.AnnulerEvenementResponse creerVersionResponse = annulerEvenementContext.getAnnulerEvenementResponse();
        if (creerVersionResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_COM);
        }

        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            creerVersionResponse.getDossierDoc(),
            creerVersionResponse.getEvenementDoc(),
            creerVersionResponse.getVersionDoc(),
            null,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Supprime une version d'un événement.
     *
     * @param request Requête
     * @return Réponse
     */
    public SupprimerVersionResponse supprimerVersion(final SupprimerVersionRequest request) {
        final SupprimerVersionResponse response = new SupprimerVersionResponse();

        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final EvtId evtId = request.getIdEvenement();
        final fr.sword.xsd.solon.epp.Version version = evtId.getVersion();
        final long majorVersion = version.getMajeur();
        final long minorVersion = version.getMineur();
        versionService.supprimerVersionBrouillon(session, evtId.getId(), majorVersion, minorVersion);

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Recherche un message dans la Mailbox de l'utilisateur.
     *
     * @param request Requête
     * @return Réponse
     */
    public RechercherEvenementResponse rechercherMessage(final RechercherEvenementRequest request) {
        final RechercherEvenementResponse response = new RechercherEvenementResponse();

        final RechercherMessageContext rechercherMessageContext = new RechercherMessageContext();
        final RechercherMessageRequest rechercherMessageRequest = rechercherMessageContext.getRechercherMessageRequest();

        final CritereRechercheEvenement critereRechercheEvenement = request.getParCritere();
        final QueryRechercheEvenement queryRechercheEvenement = request.getParRequeteXsd();
        final String requete = request.getParRequete();

        if (critereRechercheEvenement != null) {
            final MessageCriteria messageCriteria = buildFromCritereRechercheEvenement(critereRechercheEvenement);
            rechercherMessageRequest.setMessageCriteria(messageCriteria);
        } else if (StringUtils.isNotBlank(requete)) {
            // Recherche "documentaire"
            rechercherMessageRequest.setQueryString(requete);
        } else if (queryRechercheEvenement != null) {
            // Recherche "xsd"
            final XsdToUfnxqlParser xsdToUfnxqlParser = new XsdToUfnxqlParser();
            final List<Object> params = queryRechercheEvenement.getParametres();

            final EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();
            final String ufnxqlQuery = xsdToUfnxqlParser.parse(
                queryRechercheEvenement.getWhereClause(),
                params,
                session,
                eppPrincipal.getInstitutionId(),
                false
            );

            rechercherMessageRequest.setParametrizedQuery(ufnxqlQuery);
            rechercherMessageRequest.setParamList(params);
        } else {
            throw new NuxeoException("Recherche simple ou recherche documentaire ou recherche xsd doit être renseigné");
        }

        // Renseigne les informations de pagination
        final Pagination pagination = request.getPagination();

        if (pagination != null) {
            final PageInfo pageInfo = new PageInfo();
            pageInfo.setPageSize(pagination.getNombreElementParPage());
            pageInfo.setPageNumber(pagination.getNumeroPage());
            rechercherMessageRequest.setPageInfo(pageInfo);
        }

        // Effectue la recherche des messages
        final CorbeilleService corbeilleService = SolonEppServiceLocator.getCorbeilleService();
        corbeilleService.findMessage(session, rechercherMessageContext);

        final RechercherMessageResponse rechercherMessageResponse = rechercherMessageContext.getRechercherMessageResponse();
        final List<RechercherMessageDTO> rechercherMessageElementList = rechercherMessageResponse.getMessagetList();
        if (rechercherMessageElementList != null) {
            final MessageAssembler messageAssembler = new MessageAssembler(session);
            for (final RechercherMessageDTO rechercherMessageElement : rechercherMessageElementList) {
                // Renseigne les données de l'événement
                final Message message = messageAssembler.assembleMessageToWs(rechercherMessageElement);
                response.getMessage().add(message);
            }
        }

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Renseignes les critères de recherche simple
     *
     * @param critereRechercheEvenement
     * @return {@link MessageCriteria}
     */
    private MessageCriteria buildFromCritereRechercheEvenement(
        final CritereRechercheEvenement critereRechercheEvenement
    ) {
        final MessageCriteria messageCriteria = new MessageCriteria();

        messageCriteria.setCheckReadPermission(true);
        final EtatMessage etatMessage = critereRechercheEvenement.getEtatMessage();
        if (etatMessage != null) {
            messageCriteria.setEtatMessage(etatMessage.value());
        }
        final EtatDossier etatDossier = critereRechercheEvenement.getEtatDossier();
        if (etatDossier != null) {
            messageCriteria.setDossierAlerte(etatDossier == EtatDossier.ALERTE);
        }
        messageCriteria.setEvenementId(critereRechercheEvenement.getIdEvenement());
        final EtatEvenement etatEvenement = critereRechercheEvenement.getEtatEvenement();
        if (etatEvenement != null) {
            switch (etatEvenement) {
                case ANNULE:
                    messageCriteria.setEvenementCurrentLifeCycleState(SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE);
                    break;
                case EN_ATTENTE_DE_VALIDATION:
                    messageCriteria.setEvenementCurrentLifeCycleState(
                        SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE
                    );
                    break;
                case BROUILLON:
                    messageCriteria.setEvenementCurrentLifeCycleState(
                        SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE
                    );
                    break;
                case EN_INSTANCE:
                    messageCriteria.setEvenementCurrentLifeCycleState(
                        SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE
                    );
                    break;
                case PUBLIE:
                    messageCriteria.setEvenementCurrentLifeCycleState(SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE);
                    break;
            }
        }

        messageCriteria.setCorbeille(critereRechercheEvenement.getIdCorbeille());
        messageCriteria.setDossierId(critereRechercheEvenement.getIdDossier());
        if (critereRechercheEvenement.getTypeEvenement() != null) {
            final String evenementType = EvenementTypeAssembler.assembleXsdToEvenementType(
                critereRechercheEvenement.getTypeEvenement()
            );
            messageCriteria.setEvenementType(evenementType);
        }
        final Institution emetteur = critereRechercheEvenement.getEmetteur();
        if (emetteur != null) {
            messageCriteria.setEvenementEmetteur(emetteur.name());
        }
        final Institution destinataire = critereRechercheEvenement.getDestinataire();
        if (destinataire != null) {
            messageCriteria.setEvenementDestinataire(critereRechercheEvenement.getDestinataire().name());
        }
        final Institution copie = critereRechercheEvenement.getCopie();
        if (copie != null) {
            messageCriteria.setEvenementDestinataireCopie(copie.name());
        }
        final String idSenat = critereRechercheEvenement.getIdSenat();
        if (idSenat != null) {
            final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

            if (principal.isInstitutionSenat()) {
                messageCriteria.setVersionSenat(idSenat);
            }
        }
        messageCriteria.setVersionObjetLike(critereRechercheEvenement.getObjet());
        if (critereRechercheEvenement.getDateEvenementMin() != null) {
            final Calendar versionHorodatageMin = critereRechercheEvenement.getDateEvenementMin().toGregorianCalendar();
            final Calendar calMin = DateUtil.toCalendarFromNotNullDate(versionHorodatageMin.getTime());
            DateUtil.setDateToBeginingOfDay(calMin);
            messageCriteria.setVersionHorodatageMin(calMin);
        }
        if (critereRechercheEvenement.getDateEvenementMax() != null) {
            final Calendar versionHorodatageMax = critereRechercheEvenement.getDateEvenementMax().toGregorianCalendar();
            final Calendar calMax = DateUtil.toCalendarFromNotNullDate(versionHorodatageMax.getTime());
            DateUtil.setDateToEndOfDay(calMax);
            messageCriteria.setVersionHorodatageMax(calMax);
        }
        final NiveauLecture niveauLecture = critereRechercheEvenement.getNiveauLecture();
        if (niveauLecture != null) {
            if (niveauLecture.getNiveau() != null) {
                messageCriteria.setVersionNiveauLectureNumero((long) niveauLecture.getNiveau());
            }
            if (niveauLecture.getCode() != null) {
                messageCriteria.setVersionNiveauLecture(
                    NiveauLectureCodeAssembler.assembleXsdToNiveauLectureCode(niveauLecture.getCode())
                );
            }
        }
        messageCriteria.setVersionPieceJointePresente(critereRechercheEvenement.isPresencePieceJointe());
        final Depot depot = critereRechercheEvenement.getNumeroDepot();
        if (depot != null) {
            messageCriteria.setDossierNumeroDepotTexte(depot.getNumero());
        }

        // exclusion
        if (critereRechercheEvenement.getAncienneteMessageExclus() != null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, -critereRechercheEvenement.getAncienneteMessageExclus());
            messageCriteria.setDateTraitementMin(cal);
        }
        final List<EtatMessage> etatMessageList = critereRechercheEvenement.getEtatMessageExclus();
        final List<String> etatMessageStringList = new ArrayList<>();
        if (etatMessageList != null && !etatMessageList.isEmpty()) {
            for (final EtatMessage etat : etatMessageList) {
                etatMessageStringList.add(etat.value().toString());
            }
        }
        messageCriteria.setEtatMessageExclus(etatMessageStringList);

        // order by
        if (critereRechercheEvenement.getOrderBy() != null) {
            final List<OrderBy> listOrderBy = critereRechercheEvenement.getOrderBy();

            Collections.sort(
                listOrderBy,
                new Comparator<OrderBy>() {

                    @Override
                    public int compare(final OrderBy o1, final OrderBy o2) {
                        if (o1.getPosition() == null) {
                            return 0;
                        } else {
                            return o1.getPosition().compareTo(o2.getPosition());
                        }
                    }
                }
            );

            final List<OrderByCriteria> orderCriteriaList = new ArrayList<>();
            for (final OrderBy orderBy : listOrderBy) {
                final OrderByCriteria orderCriteria = new OrderByCriteria();

                if (orderBy.getField() == null) {
                    throw new EppNuxeoException("OrderBy doit contenir un champs valide");
                }
                orderCriteria.setField(OrderByCriteria.OrderField.fromValue(orderBy.getField().toString()));
                orderCriteria.setOrder(orderBy.getOrder().toString());
                orderCriteriaList.add(orderCriteria);
            }

            messageCriteria.setOrderByList(orderCriteriaList);
        }

        return messageCriteria;
    }

    /**
     * Initialisation d'un evenement a partir d'un autre evenement
     *
     * @param request
     * @return
     */
    public InitialiserEvenementResponse initialiserEvenement(final InitialiserEvenementRequest request) {
        final InitialiserEvenementResponse response = new InitialiserEvenementResponse();

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final InitialiserEvenementContext initialiserEvenementContext = new InitialiserEvenementContext();
        final fr.dila.solonepp.api.service.evenement.InitialiserEvenementRequest initialiserEvenementRequest = initialiserEvenementContext.getInitialiserEvenementRequest();
        initialiserEvenementRequest.setIdEvenementPrecedent(request.getIdEvenementPrecedent());

        final String evenementType = EvenementTypeAssembler.assembleXsdToEvenementType(request.getTypeEvenement());
        initialiserEvenementRequest.setTypeEvenement(evenementType);

        evenementService.initialiserEvenement(session, initialiserEvenementContext);

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.evenement.InitialiserEvenementResponse initialiserEvenementResponse = initialiserEvenementContext.getInitialiserEvenementResponse();
        if (initialiserEvenementResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_REPONSE);
        }

        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            initialiserEvenementResponse.getDossierDoc(),
            initialiserEvenementResponse.getEvenementDoc(),
            initialiserEvenementResponse.getVersionDoc(),
            null,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        if (request.isAllMeta() == null || request.isAllMeta()) {
            fillEmptyField(eppEvtContainerResponse, evenementType);
        }

        response.setEvenement(eppEvtContainerResponse);

        response.setStatut(TraitementStatut.OK);
        return response;
    }

    /**
     * Remplit les champs de l'événement pour initialiser événement
     *
     * @param evtContainer
     * @param evenementType
     */
    protected void fillEmptyField(final EppEvtContainer evtContainer, final String evenementType) {
        final EvenementAssemblerService assemblerService = ServiceUtil.getService(EvenementAssemblerService.class);
        final Assembler assembler = assemblerService.getAssemblerInstanceFor(
            EvenementType.fromValue(evenementType),
            evtContainer,
            session,
            null
        );
        final EppBaseEvenement evt = assembler.getEppBaseEvenement();
        fillEmptyField(evt, evenementType);
    }

    /**
     * Remplit les champs de l'événement pour initialiser événement
     *
     * @param evtBaseEvt
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void fillEmptyField(final EppBaseEvenement evtBaseEvt, final String evenementType) {
        final EvenementTypeService evtTypeService = SolonEppServiceLocator.getEvenementTypeService();

        final List<Field> fieldList = new ArrayList<>();
        Field[] fields = evtBaseEvt.getClass().getDeclaredFields();
        Collections.addAll(fieldList, fields);
        fields = evtBaseEvt.getClass().getSuperclass().getDeclaredFields();
        Collections.addAll(fieldList, fields);

        for (final Field field : fieldList) {
            try {
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(String.class)) {
                    if (
                        field.get(evtBaseEvt) == null &&
                        !field.getName().equals(SolonEppSchemaConstant.VERSION_URL_DOSSIER_AN_PROPERTY)
                    ) {
                        field.set(evtBaseEvt, "");
                    }
                } else if (field.getType().isAssignableFrom(PieceJointe.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, new PieceJointe());
                    }
                } else if (field.getType().isAssignableFrom(List.class)) {
                    final Type genericFieldType = field.getGenericType();

                    List list = (List) field.get(evtBaseEvt);
                    if ((list == null || list.isEmpty()) && !field.getName().equals("metadonneeModifiee")) {
                        if (genericFieldType instanceof ParameterizedType) {
                            final ParameterizedType aType = (ParameterizedType) genericFieldType;
                            final Type fieldArgType = aType.getActualTypeArguments()[0];
                            Object obj = null;
                            boolean isCopieNeeded = true;
                            if (((Class) fieldArgType).isAssignableFrom(Institution.class)) {
                                obj = Institution.ASSEMBLEE_NATIONALE;
                                isCopieNeeded =
                                    field.getName().equals("copie") &&
                                    evtTypeService.isDestinataireCopieAutorise(
                                        evenementType,
                                        Institution.ASSEMBLEE_NATIONALE.toString()
                                    );
                            } else if (((Class) fieldArgType).isAssignableFrom(Mandat.class)) {
                                obj = new Mandat();
                            } else if (((Class) fieldArgType).isAssignableFrom(XMLGregorianCalendar.class)) {
                                try {
                                    obj = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                                } catch (final DatatypeConfigurationException e) {
                                    LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e);
                                }
                            } else if (!((Class) fieldArgType).isEnum()) {
                                obj = ((Class) fieldArgType).newInstance();
                            } else if (((Class) fieldArgType).isEnum()) {
                                field.set(evtBaseEvt, getDefaultValueEnum((Class) fieldArgType));
                            }

                            if (list == null && (isCopieNeeded || !field.getName().equals("copie"))) {
                                list = new ArrayList();
                                field.set(evtBaseEvt, list);
                            }
                            if (list != null && (isCopieNeeded || !field.getName().equals("copie"))) {
                                list.add(obj);
                            }
                        }
                    }
                } else if (field.getType().isAssignableFrom(Institution.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, Institution.ASSEMBLEE_NATIONALE);
                    }
                } else if (field.getType().isAssignableFrom(Mandat.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, new Mandat());
                    }
                } else if (field.getType().isAssignableFrom(XMLGregorianCalendar.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        try {
                            final XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                            field.set(evtBaseEvt, xmlCal);
                        } catch (final DatatypeConfigurationException e) {
                            LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e);
                        }
                    }
                } else if (field.getType().isAssignableFrom(Integer.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, new Integer(0));
                    }
                } else if (field.getType().isAssignableFrom(Long.class)) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, new Long(0));
                    }
                } else if (!field.getType().isEnum()) {
                    if (field.get(evtBaseEvt) == null) {
                        final Class fieldArgType = field.getType();
                        final Object o = fieldArgType.newInstance();
                        field.set(evtBaseEvt, o);
                    }
                } else if (field.getType().isEnum()) {
                    if (field.get(evtBaseEvt) == null) {
                        field.set(evtBaseEvt, getDefaultValueEnum(field.getType()));
                    }
                }
            } catch (final IllegalArgumentException e) {
                LOGGER.error(session, EppLogEnumImpl.FAIL_FILL_EVNT_DELEGATE_FIELD_TEC, e);
            } catch (final IllegalAccessException e) {
                LOGGER.error(session, EppLogEnumImpl.FAIL_FILL_EVNT_DELEGATE_FIELD_TEC, e);
            } catch (final InstantiationException e) {
                LOGGER.error(session, EppLogEnumImpl.FAIL_FILL_EVNT_DELEGATE_FIELD_TEC, e);
            } catch (NuxeoException e) {
                LOGGER.error(session, EppLogEnumImpl.FAIL_FILL_EVNT_DELEGATE_FIELD_TEC, e);
            }
        }
    }

    /**
     * Retourne une valeur par défaut pour les enums des événements
     *
     * @param enumType
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object getDefaultValueEnum(final Class enumType) {
        Object o = null;

        if (enumType.isAssignableFrom(NatureLoi.class)) {
            o = NatureLoi.PROJET;
        } else if (enumType.isAssignableFrom(TypeLoi.class)) {
            o = TypeLoi.LOI;
        } else if (enumType.isAssignableFrom(NatureRapport.class)) {
            o = NatureRapport.RAPPORT;
        } else if (enumType.isAssignableFrom(RapportParlement.class)) {
            o = RapportParlement.RAPPORT_UNIQUE;
        } else if (enumType.isAssignableFrom(AttributionCommission.class)) {
            o = AttributionCommission.AU_FOND;
        } else if (enumType.isAssignableFrom(SensAvis.class)) {
            o = SensAvis.FAVORABLE;
        } else if (enumType.isAssignableFrom(MotifIrrecevabilite.class)) {
            o = MotifIrrecevabilite.AUTRES;
        } else if (enumType.isAssignableFrom(ResultatCMP.class)) {
            o = ResultatCMP.ACCORD;
        } else if (enumType.isAssignableFrom(SortAdoption.class)) {
            o = SortAdoption.ADOPTE;
        } else if (enumType.isAssignableFrom(TypeActe.class)) {
            o = TypeActe.LOI;
        }

        return o;
    }

    /**
     * Mise a jour du visa interne EPP
     *
     * @param request
     * @return
     */
    public MajInterneResponse majInterne(final MajInterneRequest request) {
        final MajInterneResponse response = new MajInterneResponse();

        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        final MajInterneContext majVisaInterneContext = new MajInterneContext();
        final fr.dila.solonepp.api.service.evenement.MajInterneRequest majVisaInterneRequest = majVisaInterneContext.getMajInterneRequest();
        majVisaInterneRequest.setIdEvenement(request.getIdEvenement());
        majVisaInterneRequest.setInterne(request.getInterne());

        messageService.majInterne(session, majVisaInterneContext);

        // Renseigne la réponse
        final fr.dila.solonepp.api.service.evenement.MajInterneResponse majVisaInterneResponse = majVisaInterneContext.getMajInterneResponse();

        if (majVisaInterneResponse == null) {
            throw new NuxeoException(ERROR_CANT_GET_INFO_REPONSE);
        }

        response.setStatut(TraitementStatut.OK);

        return response;
    }

    /**
     * Retourne les pièces jointes
     *
     * @param request
     * @return
     */
    public ChercherPieceJointeResponse chercherPieceJointe(final ChercherPieceJointeRequest request) {
        final ChercherPieceJointeResponse response = new ChercherPieceJointeResponse();

        final EvtId evtId = checkEvtId(request.getIdEvt());
        final Version version = evtId.getVersion();

        response.setIdDossier(checkIdDossier(request.getIdDossier()));
        response.setIdEvt(evtId);
        response.setStatut(TraitementStatut.OK);

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        final MessageService messageService = SolonEppServiceLocator.getMessageService();

        final DocumentModel messageDoc = messageService.getMessageByEvenementId(session, evtId.getId());
        if (messageDoc == null) {
            throw new NuxeoException(ERROR_EVT_NOT_FOUND + evtId.getId());
        }
        final fr.dila.solonepp.api.domain.message.Message message = messageDoc.getAdapter(
            fr.dila.solonepp.api.domain.message.Message.class
        );

        final DocumentModel evenementDoc = evenementService.getEvenement(session, evtId.getId());
        DocumentModel versionDoc = null;
        if (version == null) {
            versionDoc = versionService.getVersionActive(session, evenementDoc, message.getMessageType());
        } else {
            final NumeroVersion numeroVersion = new NumeroVersion(
                (long) version.getMajeur(),
                (long) version.getMineur()
            );
            versionDoc =
                versionService.getVersionVisible(session, evenementDoc, message.getMessageType(), numeroVersion);
        }

        if (versionDoc == null) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(ERROR_VERSION_NOT_FOUND);
            return response;
        }

        final PieceJointeCriteria criteria = new PieceJointeCriteria();
        criteria.setIdVersion(versionDoc.getId());
        criteria.setTypePieceJointe(request.getTypePieceJointe().toString());

        final List<DocumentModel> pjDocList = pieceJointeService.findPieceJointeByCriteria(session, criteria);

        if (pjDocList == null || pjDocList.isEmpty()) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur("Pièce jointe non trouvée");
            return response;
        }
        for (final DocumentModel pjDoc : pjDocList) {
            try {
                final PieceJointe pieceJointe = PieceJointeAssembler.toPieceJointeXsd(
                    session,
                    pjDoc,
                    request.getNomFichier()
                );
                if (pieceJointe.getFichier().size() > 0) {
                    response.setPieceJointe(pieceJointe);
                }
            } catch (final IOException e) {
                response.setStatut(TraitementStatut.KO);
                response.setMessageErreur(e.getMessage());
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_PIECE_JOINTE_TEC, pjDoc);
            }
        }

        return response;
    }

    public EnvoyerMelResponse envoyerMel(final EnvoyerMelRequest request) {
        final EnvoyerMelResponse response = new EnvoyerMelResponse();

        final String contenuMel = request.getContenuMel();
        final String objetMel = request.getObjetMel();
        final String destinataireMel = request.getDestinataireMel();
        String copieMel = request.getCopieMel();
        final EvtId evtId = request.getIdEvenement();
        final Version version = evtId.getVersion();

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

        // Si le mail en copie est vide, on prend celui de l'utilisateur connecté
        if (StringUtils.isBlank(copieMel) && !StringUtils.isBlank(principal.getEmail())) {
            copieMel = principal.getEmail();
        }

        final DocumentModel messageDoc = messageService.getMessageByEvenementId(session, evtId.getId());
        if (messageDoc == null) {
            throw new NuxeoException(ERROR_EVT_NOT_FOUND + evtId.getId());
        }
        final fr.dila.solonepp.api.domain.message.Message message = messageDoc.getAdapter(
            fr.dila.solonepp.api.domain.message.Message.class
        );

        final DocumentModel evenementDoc = evenementService.getEvenement(session, evtId.getId());
        final DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());

        DocumentModel versionDoc = null;
        if (version != null) {
            final NumeroVersion numeroVersion = new NumeroVersion(
                (long) version.getMajeur(),
                (long) version.getMineur()
            );
            versionDoc =
                versionService.getVersionVisible(session, evenementDoc, message.getMessageType(), numeroVersion);
        } else {
            versionDoc = versionService.getVersionActive(session, evenementDoc, message.getMessageType());
        }

        if (versionDoc == null) {
            response.setStatut(TraitementStatut.KO);
            response.setMessageErreur(ERROR_VERSION_NOT_FOUND);
            return response;
        }

        final PieceJointeCriteria criteria = new PieceJointeCriteria();
        criteria.setIdVersion(versionDoc.getId());
        final List<DocumentModel> pjDocList = pieceJointeService.findPieceJointeByCriteria(session, criteria);

        final EvenementAssembler evenementAssembler = new EvenementAssembler(session);
        final EppEvtContainer eppEvtContainerResponse = evenementAssembler.assembleEvenementToXsd(
            dossierDoc,
            evenementDoc,
            versionDoc,
            pjDocList,
            message.getMessageType()
        );

        response.setEvenement(eppEvtContainerResponse);
        response.setStatut(TraitementStatut.OK);

        evenementService.envoyerMel(
            session,
            principal.getName(),
            objetMel,
            contenuMel,
            destinataireMel,
            copieMel,
            evenementDoc,
            versionDoc,
            dossierDoc,
            pjDocList,
            false
        );

        return response;
    }

    private EvtId checkEvtId(EvtId evtId) {
        if (evtId == null) {
            throw new NuxeoException("L'evtId est null");
        }
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        if (evtId != null && evtId.getId() != null && !evenementService.isIdEvenementValid(evtId.getId())) {
            throw new NuxeoException(String.format("Le format de l'evtId [%s] est invalide", evtId.getId()));
        }
        return evtId;
    }

    private String checkIdDossier(String idDossier) {
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        if (idDossier != null && !dossierService.isIdDossierValid(idDossier)) {
            throw new NuxeoException(String.format("Le format de l'idDossier [%s] est invalide", idDossier));
        }
        return idDossier;
    }
}
