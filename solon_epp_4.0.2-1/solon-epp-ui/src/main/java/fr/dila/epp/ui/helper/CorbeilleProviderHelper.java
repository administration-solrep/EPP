package fr.dila.epp.ui.helper;

import static java.util.Optional.ofNullable;

import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.enumeration.EtatMessageEnum;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.enums.EtatEvenementEPPEnum;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Helper pour les providers des listes de messages
 *
 * @author fskaff
 *
 */
public class CorbeilleProviderHelper {

    private CorbeilleProviderHelper() {
        // Default constructor
    }

    public static void buildDTOFromMessage(
        EppMessageDTO messageDto,
        Message message,
        DocumentModel messageDoc,
        CoreSession session
    ) {
        messageDto.setId(messageDoc.getId());
        messageDto.setIdDossier(message.getIdDossier());
        messageDto.setDate(SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(message.getDate()));
        messageDto.setEtatMessage(getEtatMessage(message, session).toString());

        // Renseigne les infos sur le locks
        final Lock lockDetail = STServiceLocator.getSTLockService().getLockDetails(session, messageDoc);
        if (lockDetail != null) {
            final String locker = lockDetail.getOwner();
            final String lockTime = SolonDateConverter.DATE_SLASH.format(lockDetail.getCreated());

            messageDto.setLocker(locker);
            messageDto.setLockTime(lockTime);
        }
    }

    public static void buildDTOFromVersion(EppMessageDTO messageDto, Version version) {
        messageDto.setObjetDossier(version.getObjet());
        String niveauLectureNumero = ofNullable(version.getNiveauLectureNumero()).map(l -> l.toString()).orElse("");
        String niveauLecture = ofNullable(version.getNiveauLecture())
            .map(
                n ->
                    STServiceLocator
                        .getVocabularyService()
                        .getEntryLabel(SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY, n)
            )
            .orElse("");
        messageDto.setLecture(
            String.format(
                "%s%s",
                StringUtils.isNotBlank(niveauLectureNumero) ? niveauLectureNumero + " - " : "",
                niveauLecture
            )
        );
        messageDto.setVersion(version.getNumeroVersion().toString());
        messageDto.setPieceJointe(version.isPieceJointePresente());
        messageDto.setModeCreationVersion(version.getModeCreation());
    }

    public static void buildDTOFromEvenement(EppMessageDTO messageDto, Evenement evenement) {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        messageDto.setEmetteur(InstitutionsEnum.getLabelFromInstitutionKey(evenement.getEmetteur()));
        messageDto.setDestinataire(InstitutionsEnum.getLabelFromInstitutionKey(evenement.getDestinataire()));
        messageDto.setCopie(
            String.join(
                ", ",
                evenement
                    .getDestinataireCopie()
                    .stream()
                    .map(key -> InstitutionsEnum.getLabelFromInstitutionKey(key))
                    .collect(Collectors.toList())
            )
        );
        final EvenementTypeDescriptor evtType = evenementTypeService.getEvenementType(evenement.getTypeEvenement());
        if (evtType != null) {
            messageDto.setCommunication(evtType.getLabel());
            messageDto.setEnAlerte(evenementTypeService.isEvenementTypeAlerte(evtType.getName()));
        }
        messageDto.setEtatEvenement(getEtatEvenement(evenement, messageDto.getModeCreationVersion()).toString());
    }

    public static EtatMessageEnum getEtatMessage(final Message message, final CoreSession session) {
        EtatMessageEnum etatMessage = null;
        if (message.isTypeEmetteur()) {
            if (message.isEtatNonTraite()) {
                etatMessage = EtatMessageEnum.EN_COURS_REDACTION;
            } else if (message.isEtatTraite()) {
                if (!message.isArNecessaire()) {
                    etatMessage = EtatMessageEnum.EMIS;
                } else {
                    if (message.getArNonDonneCount() > 0) {
                        etatMessage = EtatMessageEnum.EN_ATTENTE_AR;
                    } else {
                        final VersionService versionService = SolonEppServiceLocator.getVersionService();
                        final DocumentModel versionDoc = versionService.getLastVersion(
                            session,
                            message.getIdEvenement()
                        );
                        final Version version = versionDoc.getAdapter(Version.class);

                        if (version.isEtatRejete()) {
                            etatMessage = EtatMessageEnum.AR_RECU_VERSION_REJETE;
                        } else {
                            etatMessage = EtatMessageEnum.AR_RECU;
                        }
                    }
                }
            }
        } else if (message.isTypeDestinataire() || message.isTypeCopie()) {
            if (message.isEtatNonTraite()) {
                etatMessage = EtatMessageEnum.NON_TRAITE;
            } else if (message.isEtatEnCours()) {
                etatMessage = EtatMessageEnum.EN_COURS_TRAITEMENT;
            } else if (message.isEtatTraite()) {
                etatMessage = EtatMessageEnum.TRAITE;
            }
        }
        if (etatMessage == null) {
            throw new NuxeoException("Type du message inconnu: " + message.getMessageType());
        }
        return etatMessage;
    }

    public static EtatEvenementEPPEnum getEtatEvenement(final Evenement evenement, final String modeCreationVersion) {
        EtatEvenementEPPEnum etatEvenement = null;
        if (evenement.isEtatAnnule()) {
            etatEvenement = EtatEvenementEPPEnum.ANNULER;
        } else if (
            evenement.isEtatAttenteValidation() &&
            !SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(modeCreationVersion)
        ) {
            etatEvenement = EtatEvenementEPPEnum.EN_ATTENTE_VALIDATION;
        } else if (
            evenement.isEtatAttenteValidation() &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(modeCreationVersion)
        ) {
            etatEvenement = EtatEvenementEPPEnum.EN_ATTENTE_VALIDATION_ANNULATION;
        } else if (evenement.isEtatPublie()) {
            etatEvenement = EtatEvenementEPPEnum.PUBLIE;
        } else if (evenement.isEtatBrouillon()) {
            etatEvenement = EtatEvenementEPPEnum.BROUILLON;
        } else if (evenement.isEtatInstance()) {
            etatEvenement = EtatEvenementEPPEnum.EN_INSTANCE;
        }
        if (etatEvenement == null) {
            throw new NuxeoException("Type de l'evenement " + evenement.getIdEvenement() + " inconnu");
        }
        return etatEvenement;
    }
}
