package fr.dila.epp.ui.services;

import fr.dila.st.ui.th.bean.TransmettreParMelForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface EvenementActionsUIService {
    /**
     * Supprimer l'événement
     *
     * ID id du message
     *
     * @param context
     */
    void supprimerEvenement(SpecificContext context);

    /**
     * Annuler l'événement
     *
     * ID id du message
     *
     * @param context
     */
    void annulerEvenement(SpecificContext context);

    /**
     * Suivre la transition traiter sur l'événement
     *
     * ID id du message
     *
     * @param context
     */
    void traiterEvenement(SpecificContext context);

    /**
     * Suivre la transition en cours de traitement sur l'événement
     *
     * ID id du message
     *
     * @param context
     */
    void enCoursTraitementEvenement(SpecificContext context);

    /**
     * Accuser réception de la version
     *
     * ID id du message
     *
     * @param context
     */
    void accuserReceptionVersion(SpecificContext context);

    /**
     * Accepter ou refuser la version
     *
     * ID id du message
     * ACCEPTER true : accepter, false : rejeter
     *
     * @param context
     */
    void validerVersion(SpecificContext context);

    /**
     * Abandonner la version
     *
     * ID id du message
     *
     * @param context
     */
    void abandonnerVersion(SpecificContext context);

    /**
     * Retourne le type d'alerte successive à l'événement courant
     *
     * ID id du message
     *
     * @param context
     * @return un type d'événement
     */
    String getTypeAlerteSuccessive(SpecificContext context);

    /**
     * Retourne un TransmettreParMelForm avec le type d'événement comme objet
     *
     * ID id du message
     *
     * @param context
     * @return
     */
    TransmettreParMelForm getTransmettreParMelForm(SpecificContext context);

    /**
     * Envoi du message suite à l'action Transmettre par mél
     *
     * TRANSMETTRE_PAR_MEL_FORM
     *
     * @param context
     */
    void transmettreParMelEnvoyer(SpecificContext context);
}
