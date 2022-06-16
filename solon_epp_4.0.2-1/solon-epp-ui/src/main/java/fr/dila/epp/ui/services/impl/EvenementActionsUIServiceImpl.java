package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.ACCEPTER;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_MESSAGE;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.PIECE_JOINTE_LIST;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.STContextDataKey.TRANSMETTRE_PAR_MEL_FORM;
import static java.util.Optional.ofNullable;

import com.google.common.collect.ImmutableMap;
import fr.dila.epp.ui.services.EvenementActionsUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementRequest;
import fr.dila.solonepp.api.service.version.AccuserReceptionContext;
import fr.dila.solonepp.api.service.version.AccuserReceptionRequest;
import fr.dila.solonepp.api.service.version.ValiderVersionContext;
import fr.dila.solonepp.api.service.version.ValiderVersionRequest;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.bean.TransmettreParMelForm;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.xsd.solon.epp.EvenementType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

public class EvenementActionsUIServiceImpl implements EvenementActionsUIService {
    private static final String EVENEMENT_ABANDONNER_OK = "evenement.abandonner.ok";
    private static final String EVENEMENT_ACCEPTER_OK = "evenement.accepter.ok";
    private static final String EVENEMENT_ACCUSER_RECEPTION_OK = "evenement.accuser.reception.ok";
    private static final String EVENEMENT_ANNULER_OK = "evenement.annuler.ok";
    private static final String EVENEMENT_EN_COURS_TRAITEMENT_OK = "evenement.transition.encours.ok";
    private static final String EVENEMENT_REJETER_OK = "evenement.rejeter.ok";
    private static final String EVENEMENT_SUPPRIMER_OK = "evenement.supprimer.ok";
    private static final String EVENEMENT_SUPPRIMER_KO = "evenement.supprimer.ko";
    private static final String EVENEMENT_TRAITER_OK = "evenement.transition.traite.ok";
    private static final String EVENEMENT_TRANSFERER_OK = "evenement.transferer.ok";

    private static final Map<String, String> ALERTE_FOR_PROCEDURES = ImmutableMap
        .<String, String>builder()
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_PROCEDURE_LEGISLATIVE_VALUE,
            EvenementType.ALERTE_01.value()
        )
        .put(STVocabularyConstants.CATEGORIE_EVENEMENT_PROCEDURE_CENSURE_VALUE, EvenementType.ALERTE_02.value())
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_ORGANISATION_SESSION_EXTRAORDINAIRE_VALUE,
            EvenementType.ALERTE_03.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_CONSULTATION_ASSEMBLEE_PROJET_NOMINATION_VALUE,
            EvenementType.ALERTE_04.value()
        )
        .put(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_CONVOCATION_CONGRES_VALUE, EvenementType.ALERTE_05.value())
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_PROLONGATION_INTERVENTION_EXTERIEURE_VALUE,
            EvenementType.ALERTE_06.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_RESOLUTION_ARTICLE_34_1_VALUE,
            EvenementType.ALERTE_07.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEPOT_RAPPORT_PARLEMENT_VALUE,
            EvenementType.ALERTE_08.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_INSERTION_INFORMATION_PARLEMENTAIRE_JO_VALUE,
            EvenementType.ALERTE_09.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_ORGANISME_EXTRA_PARLEMENTAIRE_VALUE,
            EvenementType.ALERTE_10.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_DE_MISE_EN_OEUVRE_ARTICLE_28_3C_VALUE,
            EvenementType.ALERTE_13.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DECLARATION_DE_POLITIQUE_GENERALE_VALUE,
            EvenementType.ALERTE_11.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DECLARATION_SUR_UN_SUJET_DETERMINE_50_1C_VALUE,
            EvenementType.ALERTE_12.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_AUDITION_PERSONNES_EMPLOIS_ENVISAGEE_VALUE,
            EvenementType.ALERTE_14.value()
        )
        .put(
            SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_AUTRES_DOCUMENTS_TRANSMIS_AUX_ASSEMBLEES_VALUE,
            EvenementType.ALERTE_15.value()
        )
        .build();

    private static final STLogger LOGGER = STLogFactory.getLog(EvenementActionsUIServiceImpl.class);

    @Override
    public void supprimerEvenement(SpecificContext context) {
        try {
            SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

            Version currentVersion = context.getFromContextData(CURRENT_VERSION);
            if (currentVersion != null) {
                SolonEppServiceLocator
                    .getVersionService()
                    .supprimerVersionBrouillon(
                        context.getSession(),
                        context.getCurrentDocument(),
                        currentVersion.getDocument()
                    );
                context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_SUPPRIMER_OK));
            } else {
                context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(EVENEMENT_SUPPRIMER_KO));
            }
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_COMM_TEC, "Echec de suppression", e);
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(EVENEMENT_SUPPRIMER_KO));
            context.getMessageQueue().addErrorToQueue(e.getMessage());
        }
    }

    @Override
    public void annulerEvenement(SpecificContext context) {
        DocumentModel evenementDoc = getEvenementDocFromMessageId(context);
        AnnulerEvenementContext annulerEvenementContext = new AnnulerEvenementContext();
        AnnulerEvenementRequest annulerEvenementRequest = annulerEvenementContext.getAnnulerEvenementRequest();
        annulerEvenementContext.setAnnulerEvenementRequest(annulerEvenementRequest);

        annulerEvenementRequest.setEvenementId(evenementDoc.getTitle());

        SolonEppServiceLocator.getEvenementService().annulerEvenement(context.getSession(), annulerEvenementContext);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_ANNULER_OK));
    }

    @Override
    public void traiterEvenement(SpecificContext context) {
        DocumentModel evenementDoc = getEvenementDocFromMessageId(context);
        SolonEppServiceLocator
            .getMessageService()
            .followTransitionTraite(context.getSession(), evenementDoc.getTitle());

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_TRAITER_OK));
    }

    @Override
    public void enCoursTraitementEvenement(SpecificContext context) {
        DocumentModel evenementDoc = getEvenementDocFromMessageId(context);
        SolonEppServiceLocator
            .getMessageService()
            .followTransitionEnCours(context.getSession(), evenementDoc.getTitle());

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_EN_COURS_TRAITEMENT_OK));
    }

    @Override
    public void accuserReceptionVersion(SpecificContext context) {
        SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

        AccuserReceptionContext accuserReceptionContext = new AccuserReceptionContext();
        AccuserReceptionRequest accuserReceptionRequest = new AccuserReceptionRequest();
        accuserReceptionContext.setAccuserReceptionRequest(accuserReceptionRequest);

        Version version = context.getFromContextData(CURRENT_VERSION);
        accuserReceptionRequest.setEvenementId(context.getCurrentDocument().getTitle());
        accuserReceptionRequest.setNumeroVersion(version.getNumeroVersion());

        SolonEppServiceLocator
            .getVersionService()
            .accuserReceptionDestinataire(context.getSession(), accuserReceptionContext);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_ACCUSER_RECEPTION_OK));
    }

    @Override
    public void validerVersion(SpecificContext context) {
        SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

        ValiderVersionContext validerVersionContext = new ValiderVersionContext();
        ValiderVersionRequest validerVersionRequest = new ValiderVersionRequest();
        validerVersionContext.setValiderVersionRequest(validerVersionRequest);

        Evenement evenement = context.getCurrentDocument().getAdapter(Evenement.class);
        boolean accepter = BooleanUtils.toBoolean((Boolean) context.getFromContextData(ACCEPTER));
        validerVersionRequest.setEvenementId(evenement.getTitle());
        validerVersionRequest.setAccepter(accepter);

        Message message = context.getFromContextData(CURRENT_MESSAGE);
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(message.getMessageType())) {
            SolonEppServiceLocator
                .getVersionService()
                .validerVersionDestinataire(context.getSession(), validerVersionContext);
        }

        context
            .getMessageQueue()
            .addSuccessToQueue(ResourceHelper.getString(accepter ? EVENEMENT_ACCEPTER_OK : EVENEMENT_REJETER_OK));
    }

    @Override
    public void abandonnerVersion(SpecificContext context) {
        ValiderVersionContext validerVersionContext = new ValiderVersionContext();
        ValiderVersionRequest validerVersionRequest = new ValiderVersionRequest();
        validerVersionContext.setValiderVersionRequest(validerVersionRequest);

        validerVersionRequest.setEvenementId(getEvenementDocFromMessageId(context).getTitle());

        SolonEppServiceLocator.getVersionService().validerVersionEmetteur(context.getSession(), validerVersionContext);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_ABANDONNER_OK));
    }

    @Override
    public String getTypeAlerteSuccessive(SpecificContext context) {
        Evenement evenementPrecedent = getEvenementDocFromMessageId(context).getAdapter(Evenement.class);
        EvenementTypeDescriptor evtParentTypeDescriptor = SolonEppServiceLocator
            .getEvenementTypeService()
            .getEvenementType(evenementPrecedent.getTypeEvenement());
        return ALERTE_FOR_PROCEDURES.get(evtParentTypeDescriptor.getProcedure());
    }

    @Override
    public TransmettreParMelForm getTransmettreParMelForm(SpecificContext context) {
        String idMessage = context.getFromContextData(ID);
        String typeEvenement = getEvenementDocFromMessageId(context).getAdapter(Evenement.class).getTypeEvenement();

        return new TransmettreParMelForm(
            idMessage,
            ofNullable(SolonEppServiceLocator.getEvenementTypeService())
                .map(service -> service.getEvenementType(typeEvenement))
                .map(EvenementTypeDescriptor::getLabel)
                .orElse(typeEvenement)
        );
    }

    @Override
    public void transmettreParMelEnvoyer(SpecificContext context) {
        TransmettreParMelForm transmettreParMelForm = context.getFromContextData(TRANSMETTRE_PAR_MEL_FORM);
        context.putInContextData(ID, transmettreParMelForm.getIdMessage());
        SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

        CoreSession session = context.getSession();
        EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();
        Version currentVersion = context.getFromContextData(CURRENT_VERSION);
        DocumentModel dossierDoc = session.getDocument(context.getCurrentDocument().getParentRef());
        List<PieceJointe> pjList = context.getFromContextData(PIECE_JOINTE_LIST);
        List<DocumentModel> pjDocList = pjList.stream().map(PieceJointe::getDocument).collect(Collectors.toList());

        SolonEppServiceLocator
            .getEvenementService()
            .envoyerMel(
                session,
                eppPrincipal.getName(),
                transmettreParMelForm.getObjet(),
                transmettreParMelForm.getMessage(),
                transmettreParMelForm.getFullDestinataires(),
                eppPrincipal.getEmail(),
                context.getCurrentDocument(),
                currentVersion.getDocument(),
                dossierDoc,
                pjDocList
            );

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_TRANSFERER_OK));
    }

    /**
     * Retourne le document événement à partir du message
     *
     * ID id du message
     *
     * @param context
     * @return
     */
    private DocumentModel getEvenementDocFromMessageId(SpecificContext context) {
        String id = context.getFromContextData(ID);
        DocumentModel messageDoc = context.getSession().getDocument(new IdRef(id));
        Message message = messageDoc.getAdapter(Message.class);
        return context.getSession().getDocument(new IdRef(message.getCaseDocumentId()));
    }
}
