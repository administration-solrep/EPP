package fr.dila.ss.api.feuilleroute;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des documents de type étape de feuille de route
 *
 * @author fesposito
 * @author jtremeaux
 */
public interface SSRouteStep extends FeuilleRouteStep {
    /**
     * Retourne l'UUID de la feuille de route (champ dénormalisé).
     *
     * @return UUID de la feuille de route
     */
    String getDocumentRouteId();

    /**
     * Renseigne l'UUID de la feuille de route (champ dénormalisé).
     *
     * @param documentRouteId
     *            UUID de la feuille de route
     */
    void setDocumentRouteId(String documentRouteId);

    /**
     * @return the validationStatus
     */
    String getValidationStatus();

    /**
     * @param validationStatus
     *            the validationStatus to set
     */
    void setValidationStatus(String validationStatus);

    /**
     * @return the alreadyDuplicated
     */
    boolean isAlreadyDuplicated();

    /**
     * @param alreadyDuplicated
     *            the alreadyDuplicated to set
     */
    void setAlreadyDuplicated(boolean alreadyDuplicated);

    /**
     * @return the automaticValidated
     */
    boolean isAutomaticValidated();

    /**
     * @param automaticValidated
     *            the automaticValidated to set
     */
    void setAutomaticValidated(boolean automaticValidated);

    /**
     * @return the isMailSend
     */
    boolean isMailSend();

    /**
     * @param isMailSend
     *            the isMailSend to set
     */
    void setMailSend(boolean isMailSend);

    /**
     * @return the type
     */
    String getType();

    /**
     * @param type
     *            the type to set
     */
    void setType(String type);

    /**
     * @return the distributionMailboxId
     */
    String getDistributionMailboxId();

    /**
     * @param distributionMailboxId
     *            the distributionMailboxId to set
     */
    void setDistributionMailboxId(String distributionMailboxId);

    /**
     * @return the dueDate (échéance)
     */
    Calendar getDueDate();

    /**
     * @param dueDate
     *            the dueDate to set
     */
    void setDueDate(Calendar dueDate);

    /**
     * @return the dealine (échéance indicative)
     */
    Long getDeadLine();

    /**
     * @param deadline
     *            the deadline to set
     */
    void setDeadLine(Long deadline);

    /**
     * @return the automaticValidation
     */
    boolean isAutomaticValidation();

    /**
     * @param automaticValidation
     *            the automaticValidation to set
     */
    void setAutomaticValidation(boolean automaticValidation);

    /**
     * @return Vrai si l'étape est définie obligatoire par le SGG ou par le ministère
     */
    boolean isObligatoire();

    /**
     * Retourne vrai si l'étape est définie obligatoire par le SGG.
     *
     * @return Vrai si l'étape est définie obligatoire par le SGG
     */
    boolean isObligatoireSGG();

    /**
     * Renseigne vrai si l'étape est définie obligatoire par le SGG.
     *
     * @param obligatoireSGG
     *            Vrai si l'étape est définie obligatoire par le SGG
     */
    void setObligatoireSGG(boolean obligatoireSGG);

    /**
     * Retourne vrai si l'étape est définie obligatoire par le ministère.
     *
     * @return Vrai si l'étape est définie obligatoire par le ministère
     */
    boolean isObligatoireMinistere();

    /**
     * Renseigne vrai si l'étape est définie obligatoire par le ministère.
     *
     * @param obligatoireMinistere
     *            Vrai si l'étape est définie obligatoire par le ministère
     */
    void setObligatoireMinistere(boolean obligatoireMinistere);

    /**
     * Date de début de l'étape, correspondont à la date d'arrivée dans la mailbox de l'utilisateur.
     *
     * @return dateDebutEtape
     */
    Calendar getDateDebutEtape();

    /**
     * Date de début de l'étape, correspondont à la date d'arrivée dans la mailbox de l'utilisateur.
     *
     * @param dateDebutEtape
     */
    void setDateDebutEtape(Calendar dateDebutEtape);

    /**
     * Date de début de l'étape, correspondont à la date d'arrivée dans la mailbox de l'utilisateur.
     *
     * @return dateFinEtape
     */
    Calendar getDateFinEtape();

    /**
     * Date de début de l'étape, correspondont à la date d'arrivée dans la mailbox de l'utilisateur.
     */
    void setDateFinEtape(Calendar dateFinEtape);

    /**
     * Retourne le libellé des ministères après validation de l'étape (dénormalisation).
     *
     * @return Libellé des ministères après validation de l'étape (dénormalisation)
     */
    String getMinistereLabel();

    /**
     * Renseigne le libellé des ministères après validation de l'étape (dénormalisation).
     *
     * @param ministereLabel
     *            Libellé des ministères après validation de l'étape (dénormalisation)
     */
    void setMinistereLabel(String ministereLabel);

    /**
     * Retourne l'Id des ministères après validation de l'étape (dénormalisation).
     *
     * @return Identifiant des ministères après validation de l'étape (dénormalisation)
     */
    String getMinistereId();

    /**
     * Renseigne l'identifiant des ministères après validation de l'étape (dénormalisation).
     *
     * @param ministereId
     *            identifiant des ministères après validation de l'étape (dénormalisation)
     */
    void setMinistereId(String ministereId);

    /**
     * Retourne le libellé du poste de distribution après validation de l'étape (dénormalisation).
     *
     * @return Libellé du poste de distribution après validation de l'étape (dénormalisation)
     */
    String getPosteLabel();

    /**
     * Renseigne le libellé du poste de distribution après validation de l'étape (dénormalisation).
     *
     * @param posteLabel
     *            Libellé du poste de distribution après validation de l'étape (dénormalisation)
     */
    void setPosteLabel(String posteLabel);

    /**
     * Retourne le libellé de la direction après validation de l'étape (dénormalisation).
     *
     * @return
     */
    String getDirectionLabel();

    /**
     * Renseigne le libellé de la direction après validation de l'étape (dénormalisation).
     *
     * @param directionLabel
     */
    void setDirectionLabel(String directionLabel);

    /**
     * Retourne l'identifiant de la direction après validation de l'étape (dénormalisation).
     *
     * @return
     */
    String getDirectionId();

    /**
     * Renseigne l'identifiant de la direction après validation de l'étape (dénormalisation).
     *
     * @param directionId
     */
    void setDirectionId(String directionId);

    /**
     * Retourne le nom de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     *
     * @return Nom de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     */
    String getValidationUserLabel();

    /**
     * Renseigne le nom de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     *
     * @param validationUserLabel
     *            Nom de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     */
    void setValidationUserLabel(String validationUserLabel);

    /**
     * Retourne l'identifiant de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     *
     * @return Identifiant de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     */
    String getValidationUserId();

    /**
     * Renseigne l'identifiant de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     *
     * @param validationUserId
     *            Nom de l'utilisateur qui a validé l'étape après validation de l'étape (dénormalisation).
     */
    void setValidationUserId(String validationUserId);

    void setCreator(String creator);

    /**
     * renseigne le nombre de commentaire sur l'étape
     *
     * @param numberOfComments
     */
    void setNumberOfComments(int numberOfComments);

    /**
     * Récupère le nombre de commentaire sur l'étape
     *
     * @return
     */
    int getNumberOfComments();

    boolean isEtapeAvenir();

    boolean isValideManuellement();

    boolean isInvalide();

    boolean isValidationAuto();

    boolean isNonConcerne();

    /**
     * Retourne l'état du cycle de vie.
     *
     * @return
     */
    String getCurrentLifeCycleState();

    static SSRouteStep adapt(DocumentModel doc) {
        return doc.getAdapter(SSRouteStep.class);
    }
}
