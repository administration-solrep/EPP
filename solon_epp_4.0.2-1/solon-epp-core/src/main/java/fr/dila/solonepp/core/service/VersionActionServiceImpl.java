package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.VersionActionService;
import fr.dila.solonepp.api.service.VersionService;
import fr.sword.xsd.solon.epp.EtatMessage;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du service permettant de gérer les actions possibles sur les versions.
 *
 * @author jtremeaux
 */
public class VersionActionServiceImpl implements VersionActionService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public List<String> findActionPossible(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        String messageType,
        String etatMessage,
        boolean isForUi
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        Version version = versionDoc.getAdapter(Version.class);
        String suffixSecondaire = isForUi ? "_SECONDAIRE" : "";
        String suffixTertiaire = isForUi ? "_TERTIAIRE" : "";

        // Recherche la liste des versions visibles de l'événement
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<DocumentModel> versionDocList = versionService.findVersionVisible(session, evenementDoc, messageType);

        List<String> actionPossibleList = new ArrayList<String>();
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(messageType)) {
            if (version.isEtatBrouillon()) {
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_MODIFIER);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PUBLIER);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_SUPPRIMER);
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
            } else if (version.isEtatPublie()) {
                addActionCreerAlerte(actionPossibleList, evenement, version, messageType);
                addActionLeverAlerte(actionPossibleList, evenement, version, messageType);
                addActionCreerEvenement(actionPossibleList, evenement, version, messageType);
                if (!evenementTypeService.isEvenementTypeAlerte(evenement.getTypeEvenement())) {
                    addActionCompleter(actionPossibleList, evenement, version, messageType);
                    addActionRectifier(session, actionPossibleList, evenement, version, messageType);
                    addActionAnnuler(actionPossibleList, evenement, version, messageType);
                    addActionVisualiserVersion(actionPossibleList, versionDocList);
                    actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
                }
            } else if (version.isEtatAttenteValidation()) {
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_ALERTE);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ABANDONNER);
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixSecondaire);
            } else if (version.isEtatAbandonne()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatRejete()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatObsolete()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            }
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(messageType)) {
            if (version.isEtatBrouillon()) {
                // Version non visible
            } else if (version.isEtatPublie()) {
                addActionCreerAlerte(actionPossibleList, evenement, version, messageType);
                addActionLeverAlerte(actionPossibleList, evenement, version, messageType);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT);

                if (!evenementTypeService.isEvenementTypeAlerte(evenement.getTypeEvenement())) {
                    if (EtatMessage.NON_TRAITE.value().equals(etatMessage)) {
                        actionPossibleList.add(
                            SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_EN_COURS_DE_TRAITEMENT + suffixSecondaire
                        );
                        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_TRAITE);
                    } else if (EtatMessage.EN_COURS_TRAITEMENT.value().equals(etatMessage)) {
                        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_TRAITE);
                    }
                    addActionVisualiserVersion(actionPossibleList, versionDocList);
                    actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
                    addActionAccuserReception(actionPossibleList, evenement, version, messageType);
                }
            } else if (version.isEtatAttenteValidation()) {
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_ALERTE);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ACCEPTER);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_REJETER);
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ACCUSER_RECEPTION);
            } else if (version.isEtatAbandonne()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatRejete()) {
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ACCUSER_RECEPTION);
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatObsolete()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ACCUSER_RECEPTION);
            }
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(messageType)) {
            if (version.isEtatBrouillon()) {
                // Version non visible
            } else if (version.isEtatPublie()) {
                addActionCreerAlerte(actionPossibleList, evenement, version, messageType);
                addActionLeverAlerte(actionPossibleList, evenement, version, messageType);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT);

                if (!evenementTypeService.isEvenementTypeAlerte(evenement.getTypeEvenement())) {
                    if (EtatMessage.NON_TRAITE.value().equals(etatMessage)) {
                        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_EN_COURS_DE_TRAITEMENT);
                        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_TRAITE);
                    } else if (EtatMessage.EN_COURS_TRAITEMENT.value().equals(etatMessage)) {
                        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_PASSER_MESSAGE_TRAITE);
                    }
                    addActionVisualiserVersion(actionPossibleList, versionDocList);
                    actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL + suffixTertiaire);
                }
            } else if (version.isEtatAttenteValidation()) {
                // Version non visible
            } else if (version.isEtatAbandonne()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatRejete()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            } else if (version.isEtatObsolete()) {
                addActionVisualiserVersion(actionPossibleList, versionDocList);
                actionPossibleList.add(SolonEppConstant.VERSION_ACTION_TRANSMETTRE_MEL);
            }
        }

        return actionPossibleList;
    }

    /**
     * Ajoute l'action CREER_EVENEMENT à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionCreerEvenement(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        if (evenement.isEtatAnnule()) {
            return;
        }
        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT);
    }

    /**
     * Ajoute l'action VISUALISER_VERSION à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param versionDocList Liste des versions visible par l'utilisateur
     */
    protected void addActionVisualiserVersion(List<String> actionPossibleList, List<DocumentModel> versionDocList) {
        if (versionDocList.size() < 2) {
            return;
        }
        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_VISUALISER_VERSION);
    }

    /**
     * Ajoute l'action CREER_ALERTE à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionCreerAlerte(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        // Si la branche est en alerte (posée ou levée), interdiction de créer une nouvelle alerte
        String brancheAlerte = evenement.getBrancheAlerte();
        if (StringUtils.isNotBlank(brancheAlerte)) {
            return;
        }

        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_CREER_ALERTE);
    }

    /**
     * Ajoute l'action LEVER_ALERTE à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionLeverAlerte(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        // La branche doit être en alerte (posée) afin de lever l'alerte
        String brancheAlerte = evenement.getBrancheAlerte();
        if (!SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_POSEE_VALUE.equals(brancheAlerte)) {
            return;
        }

        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_LEVER_ALERTE);
    }

    /**
     * Ajoute l'action COMPLETER à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionCompleter(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        if (evenement.isEtatAnnule()) {
            return;
        }
        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_COMPLETER);
    }

    /**
     * Ajoute l'action RECTIFIER à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionRectifier(
        CoreSession session,
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        if (evenement.isEtatAnnule()) {
            return;
        }
        if ("EVT45".equals(evenement.getTypeEvenement())) {
            try {
                DossierService dossierService = SolonEppServiceLocator.getDossierService();
                DocumentModel dossierDoc = dossierService.getDossier(session, evenement.getDossier());
                Dossier dossier = dossierDoc.getAdapter(Dossier.class);
                if (
                    dossierService.getEvenementTypeDossierList(session, dossierDoc).contains("EVT48") ||
                    dossier.getDatePublication() != null
                ) {
                    return;
                }
            } catch (Exception e) {
                // On affiche le bouton rectifier
            }
        }
        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_RECTIFIER);
    }

    /**
     * Ajoute l'action ANNULER à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionAnnuler(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        if (evenement.isEtatAnnule() || "EVT45".equals(evenement.getTypeEvenement())) {
            return;
        }
        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ANNULER);
    }

    /**
     * Ajoute l'action ACCUSER_RECEPTION à la liste des actions possibles.
     *
     * @param actionPossibleList Liste des actions possibles (modifiée par effet de bord)
     * @param evenement Evenement
     * @param version Version
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    protected void addActionAccuserReception(
        List<String> actionPossibleList,
        Evenement evenement,
        Version version,
        String messageType
    ) {
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        boolean arNecessaire = evenementTypeService.isDemandeAr(evenement.getTypeEvenement());
        if (!arNecessaire || version.getDateAr() != null) {
            return;
        }

        actionPossibleList.add(SolonEppConstant.VERSION_ACTION_ACCUSER_RECEPTION);
    }
}
