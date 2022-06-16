package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.InformationsParlementairesService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.api.service.VersionCreationService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.version.CreerVersionContext;
import fr.dila.solonepp.api.service.version.CreerVersionRequest;
import fr.dila.solonepp.api.service.version.CreerVersionResponse;
import fr.dila.solonepp.api.service.version.VersionContainer;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.solonepp.core.validator.PieceJointeValidator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service permettant de gérer la logique de création des versions d'événements.
 *
 * @author jtremeaux
 */
public class VersionCreationServiceImpl implements VersionCreationService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2131124321316701354L;

    /**
     * LOGGER
     */
    private static final STLogger LOGGER = STLogFactory.getLog(VersionCreationServiceImpl.class);

    @Override
    public void createVersion(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();

        DocumentModel dossierDoc = creerVersionRequest.getDossierDoc();
        if (dossierDoc == null) {
            throw new NuxeoException("Dossier doit être défini");
        }

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel versionDoc = creerVersionRequest.getVersionDoc();
        if (versionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        Version version = versionDoc.getAdapter(Version.class);
        if (StringUtils.isBlank(evenementDoc.getAdapter(Evenement.class).getTitle())) {
            if ("EVT45".equals(evenement.getTypeEvenement())) {
                InformationsParlementairesService infosParService = SolonEppServiceLocator.getInfosParlementaireservice();
                Dossier dossier = dossierDoc.getAdapter(Dossier.class);
                boolean isPublier = creerVersionContext.isPublier();
                setObjetFromRubrique(version);
                // Appel du ws epg créer dossier pour l'evt 45
                try {
                    infosParService.callWsCreerDossierEpg(session, dossier, evenement, version, isPublier);
                } catch (Exception e) {
                    throw new EppNuxeoException(e);
                }
                // Créer l'événement en brouillon comme s'il ne devait pas être publié
                creerVersionContext.setPublie(false);
                createEvenement(session, creerVersionContext);
                // Appel du ws epg modifier dossier pour l'evt 45 s'il est publié directement
                if (isPublier) {
                    creerVersionContext.setPublie(true);
                    version = creerVersionContext.getCreerVersionResponse().getVersionDoc().getAdapter(Version.class);
                    try {
                        extraStepsForPublishEvt45(session, creerVersionRequest, evenement, version);
                        createVersionExistingEvenement(session, creerVersionContext);
                    } catch (Exception e) {
                        creerVersionContext.setPublie(false);
                        LOGGER.error(
                            session,
                            EppLogEnumImpl.FAIL_GET_WS_EPG_TEC,
                            "Échec de l'appel WS Modifier Dossier : création de la communication en brouillon",
                            e
                        );
                    }
                }
            } else {
                // Crée un nouvel événement (et potentiellement un nouveau dossier)
                createEvenement(session, creerVersionContext);
            }
        } else {
            if ("EVT45".equals(evenement.getTypeEvenement())) {
                setObjetFromRubrique(version);
                EvenementValidator evenementValidator = new EvenementValidator(session);
                evenementValidator.validateInstitutionEmettrice(evenementDoc);
                checkChangementEmetteur(session, evenement);
                if (creerVersionContext.isPublier()) {
                    try {
                        extraStepsForPublishEvt45(session, creerVersionRequest, evenement, version);
                    } catch (Exception e) {
                        LOGGER.error(
                            session,
                            EppLogEnumImpl.FAIL_GET_WS_EPG_TEC,
                            "Échec de l'appel WS Modifier Dossier : pas de publication de la communication",
                            e
                        );
                        throw new EppNuxeoException(e);
                    }
                }
            }
            // Crée / modifie une version sur un événement existant
            createVersionExistingEvenement(session, creerVersionContext);
        }
    }

    private void extraStepsForPublishEvt45(
        CoreSession session,
        CreerVersionRequest creerVersionRequest,
        Evenement evenement,
        Version version
    )
        throws Exception {
        final InformationsParlementairesService infosParService = SolonEppServiceLocator.getInfosParlementaireservice();
        checkEvt45(version);
        List<DocumentModel> piecesJointeDocList = creerVersionRequest.getPieceJointeDocList();
        checkPiecesJointeEvt45(evenement, false, piecesJointeDocList);
        DocumentModel pjDoc = creerVersionRequest.getPieceJointeDocList().get(0);
        PieceJointe pj = pjDoc.getAdapter(PieceJointe.class);
        // Appel du ws epg modifier dossier pour l'evt 45 s'il est publié
        infosParService.callWsModifierDossierEpg(session, evenement, version, pj);
    }

    private void checkEvt45(Version version) {
        String rubrique = version.getRubrique();
        if (rubrique == null || rubrique.isEmpty()) {
            throw new EppNuxeoException("La rubrique ne peut pas être vide pour ce type de communication.");
        }
        Calendar dateJo = version.getDateJo();
        if (dateJo == null) {
            throw new EppNuxeoException(
                "La date demandée de publication au JOLD ne peut pas être vide pour ce type de communication."
            );
        }
    }

    private void checkPiecesJointeEvt45(
        Evenement evenement,
        boolean etatBrouillon,
        List<DocumentModel> piecesJointeDocList
    ) {
        if (piecesJointeDocList == null || piecesJointeDocList.isEmpty()) {
            throw new EppNuxeoException("Les pieces jointes ne peuvent pas être vide pour ce type de communication.");
        }

        final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
        pieceJointeValidator.validatePiecesJointes(piecesJointeDocList, etatBrouillon, evenement.getTypeEvenement());
    }

    private void checkChangementEmetteur(CoreSession session, Evenement modifiedEvenement) {
        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = modifiedEvenement.getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        // Vérifie si l'émetteur est changé
        String currentEmetteur = currentEvenement.getEmetteur();
        String modifiedEmetteur = modifiedEvenement.getEmetteur();
        if (!currentEmetteur.equals(modifiedEmetteur)) {
            EppPrincipal principal = (EppPrincipal) session.getPrincipal();
            // Si l'émetteur initial est l'une des institutions principales
            if (
                !InstitutionsEnum.isInstitutionAlwaysAccessible(currentEmetteur) &&
                !principal.getInstitutionIdSet().contains(currentEmetteur)
            ) {
                modifiedEvenement.setEmetteur(currentEmetteur);
                throw new NuxeoException("Seule l'institution émettrice peut modifier cette communication");
            }
        }
    }

    private void setObjetFromRubrique(Version version) {
        SolonEppVocabularyService eppVocabularyService = SolonEppServiceLocator.getSolonEppVocabularyService();
        String rubrique = eppVocabularyService.getLabelFromId(
            SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY,
            version.getRubrique(),
            STVocabularyConstants.COLUMN_LABEL
        );
        version.setObjet(rubrique);
    }

    @Override
    public void completerBrouillon(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            currentEvenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        if (currentVersion.isEtatBrouillon()) {
            // Modifie la version brouillon "pour complétion" actuelle
            DocumentModel createdVersionDoc = versionService.modifierVersionBrouillonPourCompletion(
                session,
                currentVersionDoc,
                newVersionDoc,
                currentEvenementDoc
            );
            creerVersionResponse.setVersionDoc(createdVersionDoc);
        } else if (currentVersion.isEtatPublie() || currentVersion.isEtatAttenteValidation()) {
            // Crée une nouvelle version brouillon "pour complétion"
            DocumentModel createdVersionDoc = versionService.creerVersionBrouillonPourCompletion(
                session,
                currentVersionDoc,
                newVersionDoc,
                currentEvenementDoc
            );
            creerVersionResponse.setVersionDoc(createdVersionDoc);
        } else {
            throw new NuxeoException("Impossible de modifier la version: " + currentVersion.getTitle());
        }

        // Crée, modifie ou supprime les pièces jointes
        final DocumentModel versionCreatedDoc = creerVersionResponse.getVersionDoc();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            true
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
    }

    @Override
    public void completerPublier(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            currentEvenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        // Publie une nouvelle version pour complétion
        final DocumentModel versionCreatedDoc = versionService.publierVersionPourCompletion(
            session,
            currentVersionDoc,
            newVersionDoc,
            dossierDoc,
            currentEvenementDoc
        );
        creerVersionResponse.setVersionDoc(versionCreatedDoc);

        // Crée, modifie ou supprime les pièces jointes
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            true
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
    }

    @Override
    public void completerPublierDelta(CoreSession session, CreerVersionContext creerVersionContext) {
        // Complète la version publiée actuelle et publie
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = session.getDocument(newVersionDoc.getRef());

        // Publie une nouvelle version pour complétion
        final DocumentModel versionCreatedDoc = versionService.publierVersionPourCompletion(
            session,
            currentVersionDoc,
            newVersionDoc,
            dossierDoc,
            currentEvenementDoc
        );
        creerVersionResponse.setVersionDoc(versionCreatedDoc);

        // Crée, modifie ou supprime les pièces jointes
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            true
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);

        // Complète la version brouillon actuelle et publie
        VersionContainer versionContainer = creerVersionRequest.getSecondaryVersionContainer();
        if (versionContainer != null) {
            // Récupère la version modifiée
            DocumentModel newVersionBrouillonDoc = versionContainer.getVersionDoc();
            if (newVersionBrouillonDoc == null) {
                throw new NuxeoException("Version doit être défini");
            }

            // Récupère la version actuelle
            DocumentModel currentVersionBrouillonDoc = session.getDocument(newVersionBrouillonDoc.getRef());

            // Reporte les modifications sur la version brouillon actuelle et renomme la version
            DocumentModel createdVersionBrouillonDoc = versionService.reporterVersionBrouillonPourCompletion(
                session,
                currentVersionBrouillonDoc,
                newVersionBrouillonDoc,
                currentEvenementDoc
            );

            // Crée, modifie ou supprime les pièces jointes
            List<DocumentModel> pieceJointeBrouillonToCreateDocList = versionContainer.getPieceJointeDocList();
            pieceJointeService.updatePieceJointeList(
                session,
                currentEvenementDoc,
                createdVersionBrouillonDoc,
                pieceJointeBrouillonToCreateDocList,
                true
            );
        }
    }

    @Override
    public CreerVersionResponse rectifierBrouillon(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            currentEvenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        if (currentVersion.isEtatBrouillon()) {
            // Modifie la version brouillon "pour rectification" actuelle
            DocumentModel createdVersionDoc = versionService.modifierVersionBrouillonPourRectification(
                session,
                currentVersionDoc,
                newVersionDoc,
                currentEvenementDoc
            );
            creerVersionResponse.setVersionDoc(createdVersionDoc);
        } else if (currentVersion.isEtatPublie() || currentVersion.isEtatAttenteValidation()) {
            // Crée une nouvelle version brouillon "pour rectification"
            DocumentModel createdVersionDoc = versionService.creerVersionBrouillonPourRectification(
                session,
                currentVersionDoc,
                newVersionDoc,
                currentEvenementDoc
            );
            creerVersionResponse.setVersionDoc(createdVersionDoc);
        } else {
            throw new NuxeoException("Impossible de modifier la version: " + currentVersion.getTitle());
        }

        // Crée, modifie ou supprime les pièces jointes
        final DocumentModel versionCreatedDoc = creerVersionResponse.getVersionDoc();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            false
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
        return creerVersionResponse;
    }

    @Override
    public CreerVersionResponse rectifierPublier(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            currentEvenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        // Dans le cas d'une JO-01
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        Version version = newVersionDoc.getAdapter(Version.class);
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);
        if ("EVT45".equals(evenement.getTypeEvenement())) {
            // Vérifie que la version est à l'état brouillon
            if (currentVersion.isEtatBrouillon()) {
                // Vérifie que la version est une version brouillon initiale
                if (
                    !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                        currentVersion.getModeCreation()
                    )
                ) {
                    throw new EppNuxeoException("Seule la version brouillon pour rectification peut être modifiée");
                }
            } else if (!(currentVersion.isEtatPublie() || currentVersion.isEtatAttenteValidation())) {
                throw new EppNuxeoException(
                    "Impossible de publier pour rectification la version: " +
                    currentVersion.getTitle() +
                    " à l'état " +
                    currentVersionDoc.getCurrentLifeCycleState()
                );
            }
            setObjetFromRubrique(version);
            try {
                extraStepsForPublishEvt45(session, creerVersionRequest, evenement, version);
            } catch (Exception e) {
                LOGGER.error(
                    session,
                    EppLogEnumImpl.FAIL_GET_WS_EPG_TEC,
                    "Échec de l'appel WS Modifier Dossier : pas de publication de la communication",
                    e
                );
                throw new EppNuxeoException(e);
            }
        }

        // Publie une nouvelle version pour rectification
        final DocumentModel versionCreatedDoc = versionService.publierVersionPourRectification(
            session,
            currentVersionDoc,
            newVersionDoc,
            dossierDoc,
            currentEvenementDoc,
            false
        );
        creerVersionResponse.setVersionDoc(versionCreatedDoc);

        // Crée, modifie ou supprime les pièces jointes
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            false
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
        return creerVersionResponse;
    }

    @Override
    public void rectifierPublierDelta(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        if (evenementDoc == null) {
            throw new NuxeoException("Evenement doit être défini");
        }

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        if (newVersionDoc == null) {
            throw new NuxeoException("Version doit être défini");
        }

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = evenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        DocumentModel currentVersionDoc = session.getDocument(newVersionDoc.getRef());

        // Publie une nouvelle version pour rectification
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final DocumentModel versionCreatedDoc = versionService.publierVersionPourRectification(
            session,
            currentVersionDoc,
            newVersionDoc,
            dossierDoc,
            currentEvenementDoc,
            true
        );
        creerVersionResponse.setVersionDoc(versionCreatedDoc);

        // Crée, modifie ou supprime les pièces jointes
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            false
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);

        // Complète la version brouillon actuelle et publie
        VersionContainer versionContainer = creerVersionRequest.getSecondaryVersionContainer();
        if (versionContainer != null) {
            // Récupère la version modifiée
            DocumentModel newVersionBrouillonDoc = versionContainer.getVersionDoc();
            if (newVersionBrouillonDoc == null) {
                throw new NuxeoException("Version doit être défini");
            }

            // Récupère la version actuelle
            DocumentModel currentVersionBrouillonDoc = session.getDocument(newVersionBrouillonDoc.getRef());

            // Reporte les modifications sur la version brouillon actuelle et renomme la version
            DocumentModel createdVersionBrouillonDoc = versionService.reporterVersionBrouillonPourRectification(
                session,
                currentVersionBrouillonDoc,
                newVersionBrouillonDoc,
                currentEvenementDoc
            );

            // Crée, modifie ou supprime les pièces jointes
            List<DocumentModel> pieceJointeBrouillonToCreateDocList = versionContainer.getPieceJointeDocList();
            pieceJointeService.updatePieceJointeList(
                session,
                currentEvenementDoc,
                createdVersionBrouillonDoc,
                pieceJointeBrouillonToCreateDocList,
                true
            );
        }
    }

    /**
     * Crée un nouvel événement (et potentiellement un nouveau dossier).
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createEvenement(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        DocumentModel dossierDoc = creerVersionRequest.getDossierDoc();
        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();

        // Renseigne le dossier s'il existe déjà pour l'identifiant fourni
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        dossierDoc = dossierService.getDossier(session, dossierDoc.getTitle());
        if (dossierDoc != null) {
            CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();
            creerVersionResponse.setDossierDoc(dossierDoc);
        }

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        String typeEvenement = evenement.getTypeEvenement();

        if (evenementTypeService.isTypeCreateur(typeEvenement)) {
            createEvenementCreateur(session, creerVersionContext);
        } else {
            createEvenementSuccessif(session, creerVersionContext);
        }
    }

    /**
     * Crée un nouveau dossier.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createEvenementCreateur(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();

        // Crée le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        DocumentModel dossierToCreateDoc = creerVersionRequest.getDossierDoc();
        DocumentModel dossierCreatedDoc = dossierService.createDossier(session, dossierToCreateDoc);
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();
        creerVersionResponse.setDossierDoc(dossierCreatedDoc);

        // Crée l'événement
        createEvenementExistingDossier(session, creerVersionContext);
    }

    /**
     * Crée un événement successif (événement sur un dossier existant et comprenant déjà un événement).
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createEvenementSuccessif(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        DocumentModel dossierDoc = creerVersionRequest.getDossierDoc();
        DocumentModel evenementDoc = creerVersionRequest.getEvenementDoc();
        DocumentModel versionDoc = creerVersionRequest.getVersionDoc();

        // Valide les données de l'événement successif
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateEvenementSuccessif(dossierDoc, evenementDoc, versionDoc);

        // Crée l'événement
        createEvenementExistingDossier(session, creerVersionContext);
    }

    /**
     * Crée un nouvel événement (créateur ou successif), une fois le dossier créé.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createEvenementExistingDossier(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        // Crée l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel dossierDoc = creerVersionResponse.getDossierDoc();
        DocumentModel evenementToCreateDoc = creerVersionRequest.getEvenementDoc();
        boolean publie = creerVersionContext.isPublier();
        DocumentModel evenementCreatedDoc = evenementService.creerEvenement(
            session,
            evenementToCreateDoc,
            dossierDoc,
            publie
        );
        creerVersionResponse.setEvenementDoc(evenementCreatedDoc);

        // Crée la version initiale
        createVersionInitiale(session, creerVersionContext);
    }

    /**
     * Crée la version initiale (brouillon ou publiée) sur un événement existant.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createVersionInitiale(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();

        // Crée la version initiale
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel dossierDoc = creerVersionResponse.getDossierDoc();
        DocumentModel evenementDoc = creerVersionResponse.getEvenementDoc();
        DocumentModel versionToCreateDoc = creerVersionRequest.getVersionDoc();
        DocumentModel versionCreatedDoc = null;
        if (!creerVersionContext.isPublier()) {
            versionCreatedDoc = versionService.creerVersionBrouillonInitiale(session, versionToCreateDoc, evenementDoc);
        } else {
            versionCreatedDoc =
                versionService.publierVersionInitiale(session, versionToCreateDoc, dossierDoc, evenementDoc);
        }
        creerVersionResponse.setVersionDoc(versionCreatedDoc);

        // Crée les pièces jointes
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            evenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            false
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
    }

    /**
     * Crée une version publiée / modifie une version brouillon sur un événement existant.
     *
     * @param session Session
     * @param creerVersionContext Contexte de création de version
     */
    protected void createVersionExistingEvenement(CoreSession session, CreerVersionContext creerVersionContext) {
        CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
        CreerVersionResponse creerVersionResponse = creerVersionContext.getCreerVersionResponse();
        DocumentModel modifiedEvenementDoc = creerVersionRequest.getEvenementDoc();

        // Récupère l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        String evenementId = modifiedEvenementDoc.getAdapter(Evenement.class).getTitle();
        DocumentModel currentEvenementDoc = evenementService.getEvenement(session, evenementId);
        if (currentEvenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }
        Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
        Evenement modifiedEvenement = modifiedEvenementDoc.getAdapter(Evenement.class);
        if (!currentEvenement.getEmetteur().equals(modifiedEvenement.getEmetteur())) {
            currentEvenement.setEmetteur(modifiedEvenement.getEmetteur());
        }
        creerVersionResponse.setEvenementDoc(currentEvenementDoc);

        // Récupère le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        final DocumentModel dossierDoc = dossierService.getDossier(session, currentEvenement.getDossier());
        creerVersionResponse.setDossierDoc(dossierDoc);

        // Récupère la version actuelle
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            currentEvenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        DocumentModel newVersionDoc = creerVersionRequest.getVersionDoc();
        boolean newVersionPublie = creerVersionContext.isPublier();
        if (!newVersionPublie) {
            if (currentVersion.isEtatBrouillon()) {
                // Modifie la version brouillon actuelle
                DocumentModel updatedVersionDoc = versionService.modifierVersionBrouillonInitiale(
                    session,
                    currentVersionDoc,
                    newVersionDoc,
                    currentEvenementDoc
                );
                creerVersionResponse.setVersionDoc(updatedVersionDoc);

                if (currentEvenement.isEtatBrouillon()) {
                    // Met à jour les données de l'événement brouillon
                    DocumentModel updatedEvenementDoc = evenementService.modifierEvenementBrouillon(
                        session,
                        currentEvenementDoc,
                        modifiedEvenementDoc
                    );
                    creerVersionResponse.setEvenementDoc(updatedEvenementDoc);
                }
            } else {
                throw new NuxeoException(
                    "Interdiction de créer une nouvelle version à l'état brouillon de la communication " +
                    modifiedEvenementDoc.getAdapter(Evenement.class).getTitle()
                );
            }
        } else if (currentVersion.isEtatBrouillon()) {
            if (currentEvenement.isEtatBrouillon()) {
                // Met à jour les données de l'événement brouillon
                DocumentModel updatedEvenementDoc = evenementService.modifierEvenementBrouillon(
                    session,
                    currentEvenementDoc,
                    modifiedEvenementDoc
                );

                // Lors de la validation de la première version, publie l'événement
                evenementService.publierEvenement(session, updatedEvenementDoc);
                creerVersionResponse.setEvenementDoc(updatedEvenementDoc);
            }
            // Publie la version brouillon actuelle
            DocumentModel pubishedVersionDoc = versionService.publierVersionBrouillonInitiale(
                session,
                currentVersionDoc,
                newVersionDoc,
                dossierDoc,
                currentEvenementDoc
            );
            creerVersionResponse.setVersionDoc(pubishedVersionDoc);
        } else {
            throw new NuxeoException(
                "Interdiction de créer une nouvelle version à l'état publié de la communication " +
                modifiedEvenementDoc.getAdapter(Evenement.class).getTitle()
            );
        }

        // Crée, modifie ou supprime les pièces jointes
        final DocumentModel versionCreatedDoc = creerVersionResponse.getVersionDoc();
        final PieceJointeService pieceJointeService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pieceJointeToCreateDocList = creerVersionRequest.getPieceJointeDocList();
        List<DocumentModel> pieceJointeCreatedDocList = pieceJointeService.updatePieceJointeList(
            session,
            modifiedEvenementDoc,
            versionCreatedDoc,
            pieceJointeToCreateDocList,
            false
        );
        creerVersionResponse.getPieceJointeDocList().addAll(pieceJointeCreatedDocList);
    }
}
