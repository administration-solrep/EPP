package fr.dila.solonepp.core.assembler.ws;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import java.util.Collections;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.jeton.JetonDoc;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.api.service.rechercherMessage.RechercherMessageDTO;
import fr.dila.solonepp.core.assembler.ws.evenement.BaseAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.EtatDossier;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NiveauLecture;
import fr.sword.xsd.solon.epp.NiveauLectureCode;

/**
 * Assembleur des données des objets Message WS <-> Nuxeo.
 * 
 * @author jtremeaux
 */
public class MessageAssembler {
    /**
     * Session.
     */
    protected CoreSession session;

    /**
     * Constructeur de MessageAssembler.
     * 
     * @param session Session
     */
    public MessageAssembler(final CoreSession session) {
        this.session = session;
    }

    public void mapEvenement(final DocumentModel evenementDoc, final Message message) throws ClientException {
        final Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        message.setIdEvenement(evenement.getTitle());
        message.setTypeEvenement(EvenementTypeAssembler.assembleEvenementTypeToXsd(evenement.getTypeEvenement()));
        if (evenement.getEmetteur() != null) {
            message.setEmetteurEvenement(Institution.valueOf(evenement.getEmetteur()));
        }
        if (StringUtils.isNotBlank(evenement.getDestinataire())) {
            message.setDestinataireEvenement(Institution.valueOf(evenement.getDestinataire()));
        }
        final List<String> destinataireCopie = evenement.getDestinataireCopie();
        if (destinataireCopie != null && !destinataireCopie.isEmpty()) {
            for (final String dest : destinataireCopie) {
                message.getCopieEvenement().add(Institution.valueOf(dest));
            }
        }

        if (evenement.isEtatAnnule()) {
            message.setEtatEvenement(EtatEvenement.ANNULE);
        } else if (evenement.isEtatAttenteValidation()) {
            message.setEtatEvenement(EtatEvenement.EN_ATTENTE_DE_VALIDATION);
        } else if (evenement.isEtatBrouillon()) {
            message.setEtatEvenement(EtatEvenement.BROUILLON);
        } else if (evenement.isEtatInstance()) {
            message.setEtatEvenement(EtatEvenement.EN_INSTANCE);
        } else if (evenement.isEtatPublie()) {
            message.setEtatEvenement(EtatEvenement.PUBLIE);
        } else {
            throw new ClientException("Etat de la communication inconnu: " + evenementDoc.getCurrentLifeCycleState());
        }
    }

    public void mapDossier(final DocumentModel dossierDoc, final Message message) {
        final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
        message.setIdDossier(dossier.getTitle());
        if (dossier.isAlerte()) {
            message.setEtatDossier(EtatDossier.ALERTE);
        } else {
            message.setEtatDossier(EtatDossier.EN_INSTANCE);
        }
        final Calendar dateDepotTexte = dossier.getDateDepotTexte();
        if (dateDepotTexte != null) {
            final Depot depot = new Depot();
            message.setNumeroDepot(depot);
            depot.setDate(DateUtil.calendarToXMLGregorianCalendar(dateDepotTexte));
            depot.setNumero(dossier.getNumeroDepotTexte());
        }
    }

    public void mapVersion(final DocumentModel versionDoc, final Message message) throws ClientException {
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

        final Version version = versionDoc.getAdapter(Version.class);
        final Calendar horodatage = version.getHorodatage();
        if (horodatage != null) {
            message.setDateEvenement(DateUtil.calendarToXMLGregorianCalendar(horodatage));
        }
        if (principal.isInstitutionSenat()) {
            message.setIdSenat(version.getSenat());
        }
        message.setObjet(version.getObjet());
        message.setPresencePieceJointe(version.isPieceJointePresente());

        message.setNiveauLecture(BaseAssembler.assembleNiveauLectureVersionToXsd(version));
    }

    public void mapMessage(final DocumentModel messageDoc, final Message message) throws ClientException {
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

        final fr.dila.solonepp.api.domain.message.Message messageAdapter = messageDoc.getAdapter(fr.dila.solonepp.api.domain.message.Message.class);

        if (messageAdapter.isTypeEmetteur()) {
            if (messageAdapter.isEtatNonTraite()) {
                message.setEtatMessage(EtatMessage.EN_COURS_REDACTION);
            } else if (messageAdapter.isEtatTraite()) {
                if (!messageAdapter.isArNecessaire()) {
                    message.setEtatMessage(EtatMessage.EMIS);
                } else {
                    if (messageAdapter.getArNonDonneCount() > 0) {
                        message.setEtatMessage(EtatMessage.EN_ATTENTE_AR);
                    } else {
                        message.setEtatMessage(EtatMessage.AR_RECU);
                    }
                }
            } else {
                throw new ClientException("Etat du message inconnu: " + messageDoc.getCurrentLifeCycleState());
            }
        } else if (messageAdapter.isTypeDestinataire() || messageAdapter.isTypeCopie()) {
            if (messageAdapter.isEtatNonTraite()) {
                message.setEtatMessage(EtatMessage.NON_TRAITE);
            } else if (messageAdapter.isEtatEnCours()) {
                message.setEtatMessage(EtatMessage.EN_COURS_TRAITEMENT);
            } else if (messageAdapter.isEtatTraite()) {
                message.setEtatMessage(EtatMessage.TRAITE);
            } else {
                throw new ClientException("Etat du message inconnu: " + messageDoc.getCurrentLifeCycleState());
            }
        } else {
            throw new ClientException("Type du message inconnu: " + messageAdapter.getMessageType());
        }

        if (principal.isInstitutionAn()) {
            // remap de visa que pour AN ie SOLEX
            final List<String> listVisa = messageAdapter.getVisaInternes();
            if (listVisa != null) {
                message.getInterne().addAll(listVisa);
            }
        }
    }

    /**
     * Assemble les données des messages objet métier -> WS.
     * 
     * @param dossierDoc Document version à assembler
     * @param evenementDoc Document évenement à assembler
     * @param versionDoc Document version à assembler
     * @param messageDoc Document message à assembler
     * @return Message assemblé
     * @throws ClientException
     */
    public Message assembleMessageToWs(final RechercherMessageDTO rechercheMessage) throws ClientException {
        final EppPrincipal principal = (EppPrincipal) session.getPrincipal();

        final Message message = new Message();
        message.setIdEvenement(rechercheMessage.getIdEvenement());
        message.setTypeEvenement(EvenementTypeAssembler.assembleEvenementTypeToXsd(rechercheMessage.getTypeEvenement()));
		if (rechercheMessage.getTypeEvenement().equals("EVT45")) {
			final SolonEppVocabularyService eppVocabularyService = SolonEppServiceLocator
					.getSolonEppVocabularyService();
			message.setRubrique(eppVocabularyService.getLabelFromId(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY,
					rechercheMessage.getRubrique(), STVocabularyConstants.COLUMN_LABEL));
			message.setCommentaire(rechercheMessage.getCommentaire());
		}
        if (rechercheMessage.getEmetteur() != null) {
            message.setEmetteurEvenement(Institution.valueOf(rechercheMessage.getEmetteur()));
        }
        if (StringUtils.isNotBlank(rechercheMessage.getDestinataire())) {
            message.setDestinataireEvenement(Institution.valueOf(rechercheMessage.getDestinataire()));
        }

        if (StringUtils.isNotBlank(rechercheMessage.getDestinataireCopie())) {
            final List<String> destinataireCopie = new ArrayList<String>();
            Collections.addAll(destinataireCopie, rechercheMessage.getDestinataireCopie().split(","));
            if (destinataireCopie != null && !destinataireCopie.isEmpty()) {
                for (final String dest : destinataireCopie) {
                    message.getCopieEvenement().add(Institution.valueOf(dest));
                }
            }
        }

        // Etat événement
        if (SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE.equals(rechercheMessage.getEtatEvenement())) {
            message.setEtatEvenement(EtatEvenement.ANNULE);
        } else if (SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE.equals(rechercheMessage.getEtatEvenement())) {
            message.setEtatEvenement(EtatEvenement.EN_ATTENTE_DE_VALIDATION);
        } else if (SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE.equals(rechercheMessage.getEtatEvenement())) {
            message.setEtatEvenement(EtatEvenement.BROUILLON);
        } else if (SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE.equals(rechercheMessage.getEtatEvenement())) {
            message.setEtatEvenement(EtatEvenement.EN_INSTANCE);
        } else if (SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE.equals(rechercheMessage.getEtatEvenement())) {
            message.setEtatEvenement(EtatEvenement.PUBLIE);
        } else {
            throw new ClientException("Etat de la communication inconnu: " + rechercheMessage.getEtatEvenement());
        }

        // Renseigne les données du dossier
        message.setIdDossier(rechercheMessage.getIdDossier());
        if (rechercheMessage.getAlerteDossier() > 0) {
            message.setEtatDossier(EtatDossier.ALERTE);
        } else {
            message.setEtatDossier(EtatDossier.EN_INSTANCE);
        }
        final Calendar dateDepotTexte = rechercheMessage.getDateDepotTexte();
        if (dateDepotTexte != null) {
            final Depot depot = new Depot();
            message.setNumeroDepot(depot);
            depot.setDate(DateUtil.calendarToXMLGregorianCalendar(dateDepotTexte));
            depot.setNumero(rechercheMessage.getNumeroDepotTexte());
        }

        // Renseigne les données de la version
        final Calendar horodatage = rechercheMessage.getHorodatage();
        if (horodatage != null) {
            message.setDateEvenement(DateUtil.calendarToXMLGregorianCalendar(horodatage));
        }
        if (principal.isInstitutionSenat()) {
            message.setIdSenat(rechercheMessage.getIdSenat());
        }
        message.setObjet(rechercheMessage.getObjet());
        message.setPresencePieceJointe(rechercheMessage.isPieceJointePresente());

        message.setNiveauLecture(assembleNiveauLectureVersionToXsd(rechercheMessage.getNiveauLectureNumero(), rechercheMessage.getNiveauLecture()));

        // Renseigne les données du message
        final DocumentModel messageDoc = rechercheMessage.getMessageDoc();
        if (messageDoc != null) {
            final fr.dila.solonepp.api.domain.message.Message messageAdapter = messageDoc
                    .getAdapter(fr.dila.solonepp.api.domain.message.Message.class);

            if (messageAdapter.isTypeEmetteur()) {
                if (messageAdapter.isEtatNonTraite()) {
                    message.setEtatMessage(EtatMessage.EN_COURS_REDACTION);
                } else if (messageAdapter.isEtatTraite()) {
                    if (!messageAdapter.isArNecessaire()) {
                        message.setEtatMessage(EtatMessage.EMIS);
                    } else {
                        if (messageAdapter.getArNonDonneCount() > 0) {
                            message.setEtatMessage(EtatMessage.EN_ATTENTE_AR);
                        } else {
                            message.setEtatMessage(EtatMessage.AR_RECU);
                        }
                    }
                } else {
                    throw new ClientException("Etat du message inconnu: " + messageDoc.getCurrentLifeCycleState());
                }
            } else if (messageAdapter.isTypeDestinataire() || messageAdapter.isTypeCopie()) {
                if (messageAdapter.isEtatNonTraite()) {
                    message.setEtatMessage(EtatMessage.NON_TRAITE);
                } else if (messageAdapter.isEtatEnCours()) {
                    message.setEtatMessage(EtatMessage.EN_COURS_TRAITEMENT);
                } else if (messageAdapter.isEtatTraite()) {
                    message.setEtatMessage(EtatMessage.TRAITE);
                } else {
                    throw new ClientException("Etat du message inconnu: " + messageDoc.getCurrentLifeCycleState());
                }
            } else {
                throw new ClientException("Type du message inconnu: " + messageAdapter.getMessageType());
            }

            if (principal.isInstitutionAn()) {
                // remap de visa que pour AN ie SOLEX
                final List<String> listVisa = messageAdapter.getVisaInternes();
                if (listVisa != null) {
                    message.getInterne().addAll(listVisa);
                }
            }
        }

        return message;
    }

    /**
     * Assemble les données des messages objet métier -> WS à partir d'un jeton / notification.
     * 
     * @param jetonDocModel Document notification à assembler
     * @return Message assemblé
     * @throws ClientException
     */
    public Message assembleMessageJetonDocToWs(final DocumentModel jetonDocModel) throws ClientException {
        final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);
        final Message message = new Message();
        message.setIdEvenement(jetonDoc.getEvenementId());
        message.setTypeEvenement(EvenementTypeAssembler.assembleEvenementTypeToXsd(jetonDoc.getEvenementType()));
        if (jetonDoc.getEvenementEmetteur() != null) {
            message.setEmetteurEvenement(Institution.valueOf(jetonDoc.getEvenementEmetteur()));
        }
        if (jetonDoc.getEvenementDestinataire() != null) {
            message.setDestinataireEvenement(Institution.valueOf(jetonDoc.getEvenementDestinataire()));
        }
        final List<String> destinataireCopie = jetonDoc.getEvenementDestinataireCopie();
        if (destinataireCopie != null && !destinataireCopie.isEmpty()) {
            for (final String dest : destinataireCopie) {
                message.getCopieEvenement().add(Institution.valueOf(dest));
            }
        }

        if (SolonEppLifecycleConstant.EVENEMENT_ANNULE_STATE.equals(jetonDoc.getEvenementLifeCycleState())) {
            message.setEtatEvenement(EtatEvenement.ANNULE);
        } else if (SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE.equals(jetonDoc.getEvenementLifeCycleState())) {
            message.setEtatEvenement(EtatEvenement.EN_ATTENTE_DE_VALIDATION);
        } else if (SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE.equals(jetonDoc.getEvenementLifeCycleState())) {
            message.setEtatEvenement(EtatEvenement.BROUILLON);
        } else if (SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE.equals(jetonDoc.getEvenementLifeCycleState())) {
            message.setEtatEvenement(EtatEvenement.EN_INSTANCE);
        } else if (SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE.equals(jetonDoc.getEvenementLifeCycleState())) {
            message.setEtatEvenement(EtatEvenement.PUBLIE);
        } else {
            throw new ClientException("Etat de la communication inconnu: " + jetonDoc.getEvenementLifeCycleState());
        }

        // Renseigne les données du dossier
        message.setIdDossier(jetonDoc.getDossierId());
        if (jetonDoc.getDossierAlerteCount() > 0) {
            message.setEtatDossier(EtatDossier.ALERTE);
        } else {
            message.setEtatDossier(EtatDossier.EN_INSTANCE);
        }

        // Renseigne les données de la version
        final Calendar horodatage = jetonDoc.getVersionHorodatage();
        if (horodatage != null) {
            message.setDateEvenement(DateUtil.calendarToXMLGregorianCalendar(horodatage));
        }
        message.setIdSenat(jetonDoc.getVersionSenat());
        message.setObjet(jetonDoc.getVersionObjet());
        message.setPresencePieceJointe(jetonDoc.isVersionPresencePieceJointe());
        // TODO ajouter date / n° dépot ? présent dans le WS mais pas dans les speces notif
        // Calendar dateDepotTexte = jetonDoc.getVersionDateDepotTexte();
        // if (dateDepotTexte != null) {
        // Depot depot = new Depot();
        // message.setNumeroDepot(depot);
        // depot.setDate(DateUtil.calendarToXMLGregorianCalendar(dateDepotTexte));
        // depot.setNumero(jetonDoc.getVersionNumeroDepotTexte());
        // }

        // Renseigne les données du message
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(jetonDoc.getMessageType())) {
            if (SolonEppLifecycleConstant.MESSAGE_NON_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                message.setEtatMessage(EtatMessage.EN_COURS_REDACTION);
            } else if (SolonEppLifecycleConstant.MESSAGE_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                if (!jetonDoc.isMessageArNecessaire()) {
                    message.setEtatMessage(EtatMessage.EMIS);
                } else {
                    if (jetonDoc.getMessageArNonDonneCount() > 0) {
                        message.setEtatMessage(EtatMessage.EN_ATTENTE_AR);
                    } else {
                        message.setEtatMessage(EtatMessage.AR_RECU);
                    }
                }
            } else {
                throw new ClientException("Etat du message inconnu: " + jetonDoc.getMessageLifeCycleState());
            }
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(jetonDoc.getMessageType())
                || SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(jetonDoc.getMessageType())) {
            if (SolonEppLifecycleConstant.MESSAGE_NON_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                message.setEtatMessage(EtatMessage.NON_TRAITE);
            } else if (SolonEppLifecycleConstant.MESSAGE_EN_COURS_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                message.setEtatMessage(EtatMessage.EN_COURS_TRAITEMENT);
            } else if (SolonEppLifecycleConstant.MESSAGE_TRAITE_STATE.equals(jetonDoc.getMessageLifeCycleState())) {
                message.setEtatMessage(EtatMessage.TRAITE);
            } else {
                throw new ClientException("Etat du message inconnu: " + jetonDoc.getMessageLifeCycleState());
            }
        } else {
            throw new ClientException("Type du message inconnu: " + jetonDoc.getMessageType());
        }

        return message;
    }

    /**
     * Assemble les données du niveau de lecture objet métier -> XSD.
     * 
     * @param niveauLectureNumero numero niveau lecture de la version à assembler
     * @param niveauLecture code niveau lecture de la version à assembler
     * @return Niveau de lecture assemblé
     * @throws ClientException
     */
    protected NiveauLecture assembleNiveauLectureVersionToXsd(final Long niveauLectureNumero, final String niveauLectureCodeStr)
            throws ClientException {
        final NiveauLecture niveauLecture = new NiveauLecture();
        if (niveauLectureNumero != null) {
            niveauLecture.setNiveau(niveauLectureNumero.intValue());
        }
        if (niveauLectureCodeStr != null) {
            final NiveauLectureCode niveauLectureCode = NiveauLectureCodeAssembler.assembleNiveauLectureCodeToXsd(niveauLectureCodeStr);
            niveauLecture.setCode(niveauLectureCode);

            if (!BaseAssembler.hasNumeroLecture(niveauLectureCodeStr)) {
                niveauLecture.setNiveau(null);
            }
        }
        return niveauLecture;
    }
}
