package fr.dila.epp.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;

public interface EvenementUIService {
    /**
     * Renseigne l'événement, message et version courants dans le context
     *
     * ID id du message
     *
     * @param context
     */
    void consulterEvenement(SpecificContext context);

    /**
     * Renseigne l'événement, et la version dans le context
     *
     * TYPE_EVENEMENT type de l'événement à créer
     * ID id du message précédent (cas d'une communication successive)
     *
     * @param context
     */
    void creerEvenement(SpecificContext context);

    /**
     * Crée le nouvel événement
     *
     * TYPE_EVENEMENT type de l'événement à créer
     * ID id du message précédent (cas d'une communication successive)
     * COMMUNICATION_METADONNEES_MAP map contenant les données du formulaire
     * PUBLIER si vrai publie la communication
     *
     * @param context
     * @return
     */
    String saveCreerEvenement(SpecificContext context);

    /**
     * Renseigne l'événement, message, version et le mode de création dans le context
     *
     * ID id du message
     *
     * @param context
     */
    void modifierEvenement(SpecificContext context);

    /**
     * Sauvegarde de l'événement et la version
     *
     * ID id du message
     * COMMUNICATION_METADONNEES_MAP map contenant les données du formulaire
     * PUBLIER si vrai publie la communication
     *
     * @param context
     */
    void saveModifierEvenement(SpecificContext context);

    /**
     * Création de la version complétée ou rectifiée de l'événement
     *
     * ID id du message
     * COMMUNICATION_METADONNEES_MAP map contenant les données du formulaire
     * PUBLIER si vrai publie la communication
     * MODE_CREATION completer ou rectifier
     *
     * @param context
     */
    void saveCompleterRectifierEvenement(SpecificContext context);

    /**
     * Publier directement un événement à l'état brouillon (sans modifier de données)
     *
     * ID id du message
     *
     * @param context
     */
    void publierEvenement(SpecificContext context);

    String checkLockMessage(SpecificContext context);

    void forceUnlockCurrentMessage(SpecificContext context);

    boolean unlockCurrentMessage(SpecificContext context);

    String getEtatMessage(SpecificContext context);

    String getEtatEvenement(SpecificContext context);
}
