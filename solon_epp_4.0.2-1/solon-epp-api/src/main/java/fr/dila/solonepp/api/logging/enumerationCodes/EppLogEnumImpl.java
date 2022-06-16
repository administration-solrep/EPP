package fr.dila.solonepp.api.logging.enumerationCodes;

import fr.dila.st.api.logging.STLogEnum;
import fr.dila.st.api.logging.enumerationCodes.STCodes;
import fr.dila.st.api.logging.enumerationCodes.STObjetsEnum;
import fr.dila.st.api.logging.enumerationCodes.STPorteesEnum;
import fr.dila.st.api.logging.enumerationCodes.STTypesEnum;

/**
 * Enumération des logs info codifiés de l'EPP Code : 000_000_000 <br />
 */
public enum EppLogEnumImpl implements STLogEnum {
    //************************** CREATION ***********************************************
    /**
     * Création d'un message : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#MESSAGE}
     */
    CREATE_MESSAGE_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.MESSAGE, "Création d'un message"),
    /**
     * Création d'une période : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#PERIODE_TDR}
     */
    CREATE_PERIODE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.PERIODE_TDR,
        "Création d'une période"
    ),
    /**
     * Création d'un organisme : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#ORGANISME_TDR}
     */
    CREATE_ORGANISME_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.ORGANISME_TDR,
        "Création d'un organisme"
    ),
    /**
     * Création d'un membre : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#MEMBRE_TDR}
     */
    CREATE_MEMBRE_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.MEMBRE_TDR,
        "Création d'un membre (tdr)"
    ),
    /**
     * Création d'une circonscription : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#CIRCONSCRIPTION_TDR}
     */
    CREATE_CIRCONSCRIPTION_TEC(
        STTypesEnum.CREATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.CIRCONSCRIPTION_TDR,
        "Création d'une circonscription"
    ),

    //************************** MISE À JOUR ***********************************************
    /**
     * Mise à jour d'une période : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#PERIODE_TDR}
     */
    UPDATE_PERIODE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.PERIODE_TDR,
        "Mise à jour d'une période"
    ),
    /**
     * Mise à jour d'un organisme : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#ORGANISME_TDR}
     */
    UPDATE_ORGANISME_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.ORGANISME_TDR,
        "Mise à jour d'un organisme"
    ),
    /**
     * Mise à jour d'un membre : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#MEMBRE_TDR}
     */
    UPDATE_MEMBRE_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.MEMBRE_TDR,
        "Mise à jour d'un membre (tdr)"
    ),
    /**
     * Mise à jour d'une circonscription : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#CIRCONSCRIPTION_TDR}
     */
    UPDATE_CIRCONSCRIPTION_TEC(
        STTypesEnum.UPDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.CIRCONSCRIPTION_TDR,
        "Mise à jour d'une circonscription"
    ),

    //************************** SUPPRESSION ***********************************************
    /**
     * Suppression d'un message : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#MESSAGE}
     */
    DEL_MESSAGE_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.MESSAGE, "Suppression d'un message"),

    //************************** ECHEC D'ACTIONS*****************************************
    /**
     * Aucune institution trouvee. : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#INSTITUTION}
     */
    FAIL_GET_INSTITUTION(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.INSTITUTION,
        "Aucune institution trouvee."
    ),

    /**
     * Erreur d'ajout d'une institution. : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#INSTITUTION}
     */
    FAIL_ADD_INSTITUTION(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.INSTITUTION,
        "Erreur lors de l'ajout d'une institution"
    ),

    /**
     * Erreur de suppression d'une institution. : {@link STTypesEnum#FAIL_DEL}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#INSTITUTION}
     */
    FAIL_REMOVE_INSTITUTION(
        STTypesEnum.FAIL_DEL,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.INSTITUTION,
        "Erreur lors de la suppression d'une institution"
    ),

    /**
     * FAIL ASSOCIATE INSTITUTION GROUPS TO CONNECTED USER. : {@link STTypesEnum#FAIL_ASSOCIATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#INSTITUTION}
     */
    FAIL_ASSOCIATE_INSTITUTION_GROUPS_TO_CONNECTED_USER_TEC(
        STTypesEnum.FAIL_ASSOCIATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.INSTITUTION,
        "Impossible d'associer les groupes institutions à l'utilisateur connecté."
    ),

    /**
     * FAIL ASSOCIATE EPP GROUPS TO CONNECTED USER. : {@link STTypesEnum#FAIL_ASSOCIATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#EPP}
     */
    FAIL_ASSOCIATE_EPP_GROUPS_TO_CONNECTED_USER_TEC(
        STTypesEnum.FAIL_ASSOCIATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.EPP,
        "Impossible d'associer les groupes de SOLON EPP à l'utilisateur connecté."
    ),

    /**
     * Erreur lors de l'ajout de l'adresse. : {@link STTypesEnum#FAIL_ADD}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#ADDRESS}
     */
    FAIL_ADD_ADDRESS_TEC(
        STTypesEnum.FAIL_ADD,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.ADDRESS,
        "Erreur lors de l'ajout de l'adresse."
    ),

    /**
     * Notification de transition à l'état \"en cours\" de la communication. : {@link STTypesEnum#NOTIFICATION}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#COMMUNICATION}
     */
    NOTIFICATION_TRANSITION_COMMUNICTION_TEC(
        STTypesEnum.NOTIFICATION,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Notification de transition à l'état \"en cours\" de la communication."
    ),

    /**
     * Passage de l'état \"en cours\" ou \"traité\" à \"en cours\". : {@link STTypesEnum#CHANGE_STATE}_{@link STPorteesEnum#TECHNIQUE}_{@link STObjetsEnum#COMMUNICATION}
     */
    CHANGE_MESSAGE_STATE_PROGRESS_OR_SOLVED_TO_IN_PROGRESS_TEC(
        STTypesEnum.CHANGE_STATE,
        STPorteesEnum.TECHNIQUE,
        STObjetsEnum.COMMUNICATION,
        "Passage de l'état \"en cours\" ou \"traité\" à \"en cours\""
    ),

    /**
     * Création de la version : {@link STTypesEnum#CREATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    CREATE_VERSION_TEC(STTypesEnum.CREATE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.VERSION, "Création de la version"),

    /**
     * Mise à jour de la version : {@link STTypesEnum#UPDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    UPDATE_VERSION_TEC(STTypesEnum.UPDATE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.VERSION, "Mise à jour de la version"),

    /**
     * Publication de la version : {@link STTypesEnum#PUBLICATION}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    PUBLISH_VERSION_TEC(
        STTypesEnum.PUBLICATION,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.VERSION,
        "Publication de la version"
    ),

    /**
     * Report de la version : {@link STTypesEnum#POSTPONE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    POSTPONE_VERSION_TEC(STTypesEnum.POSTPONE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.VERSION, "Report de la version"),

    /**
     * Suppression de la dernière version de la communication. : {@link STTypesEnum#DELETE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    REMOVE_VERSION_TEC(STTypesEnum.DELETE, STPorteesEnum.TECHNIQUE, EppObjetsEnum.VERSION, "Suppression de la version"),

    /**
     * Accusé de réception de la version de la communication. : {@link STTypesEnum#ACKNOWLEDGMENT}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    ACKNOWLEDGMENT_VERSION_TEC(
        STTypesEnum.ACKNOWLEDGMENT,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.VERSION,
        "Accusé de réception de la version de la communication"
    ),

    /**
     * Echec de l'accusé de réception : {@link STTypesEnum#ACKNOWLEDGMENT}_{@link STPorteesEnum#FONCTIONNELLE}_{@link EppObjetsEnum#VERSION}
     */
    FAIL_ACKNOWLEDGMENT_VERSION_FONC(
        STTypesEnum.ACKNOWLEDGMENT,
        STPorteesEnum.FONCTIONNELLE,
        EppObjetsEnum.VERSION,
        "Echec de l'accusé de réception"
    ),

    /**
     * Validation de la version. : {@link STTypesEnum#VALIDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    VALIDATE_VERSION_TEC(
        STTypesEnum.VALIDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.VERSION,
        "Validation de la version"
    ),

    /**
     * Invalidation de la version. : {@link STTypesEnum#INVALIDATE}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    INVALIDATE_VERSION_TEC(
        STTypesEnum.INVALIDATE,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.VERSION,
        "Invalidation de la version"
    ),

    /**
     * Service web de l'EPP. : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#WSEPP}
     */
    GET_WSEPP_TEC(STTypesEnum.GET, STPorteesEnum.TECHNIQUE, EppObjetsEnum.WSEPP, "Service web de l'EPP."),

    /**
     * Service web des événements. : {@link STTypesEnum#GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#WS_EVENEMENT}
     */
    GET_WSEVENEMENT_TEC(
        STTypesEnum.GET,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.WS_EVENEMENT,
        "Service web des événements."
    ),

    /**
     * Erreur lors de la récupération d'une version : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#VERSION}
     */
    FAIL_GET_VERSION_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.VERSION,
        "Erreur lors de la récupération d'une version."
    ),

    /**
     * Erreur lors de la récupération d'une version : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_{@link EppObjetsEnum#VERSION}
     */
    FAIL_GET_VERSION_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        EppObjetsEnum.VERSION,
        "Erreur lors de la récupération d'une version (fonc)"
    ),

    /**
     * Erreur lors du renseignement d'un champ vide : {@link STTypesEnum#FAIL_FILL}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#EVENEMENT_DELEGATE}
     */
    FAIL_FILL_EVNT_DELEGATE_FIELD_TEC(
        STTypesEnum.FAIL_FILL,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.EVENEMENT_DELEGATE,
        "Erreur lors du renseignement d'un champ vide"
    ),

    /**
     * Erreur de navigation vers la recherche : {@link STTypesEnum#FAIL_NAVIGATE}_{@link STPorteesEnum#FONCTIONNELLE}_{@link EppObjetsEnum#SEARCH}
     */
    FAIL_NAVIGATE_SEARCH_FONC(
        STTypesEnum.FAIL_NAVIGATE,
        STPorteesEnum.FONCTIONNELLE,
        EppObjetsEnum.SEARCH,
        "Erreur de navigation vers la recherche"
    ),

    /**
     * Erreur de récupération de message : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_{@link EppObjetsEnum#MESSAGE}
     */
    FAIL_GET_MESSAGE_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        EppObjetsEnum.MESSAGE,
        "Erreur de récupération de message (fonc)"
    ),
    /**
     * Erreur de récupération de message : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#MESSAGE}
     */
    FAIL_GET_MESSAGE_TEC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.TECHNIQUE,
        EppObjetsEnum.MESSAGE,
        "Erreur de récupération de message"
    ),
    /**
     * Erreur de récupération du descriptor : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#FONCTIONNELLE}_{@link EppObjetsEnum#DESCRIPTOR}
     */
    FAIL_GET_DESCRIPTOR_FONC(
        STTypesEnum.FAIL_GET,
        STPorteesEnum.FONCTIONNELLE,
        EppObjetsEnum.DESCRIPTOR,
        "Erreur de récupération du descriptor"
    ),
    /**
     * Echec d'appel du ws epg : {@link STTypesEnum#FAIL_GET}_{@link STPorteesEnum#TECHNIQUE}_{@link EppObjetsEnum#WS_EPG}
     */
    FAIL_GET_WS_EPG_TEC(STTypesEnum.FAIL_GET, STPorteesEnum.TECHNIQUE, EppObjetsEnum.WS_EPG, "Échec d'appel du ws epg");

    private STCodes type;
    private STCodes portee;
    private STCodes objet;
    private String text;

    EppLogEnumImpl(STCodes type, STCodes portee, STCodes objet, String text) {
        this.type = type;
        this.portee = portee;
        this.objet = objet;
        this.text = text;
    }

    @Override
    public String getCode() {
        StringBuilder code = new StringBuilder();
        code
            .append(type.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(portee.getCodeNumberStr())
            .append(SEPARATOR_CODE)
            .append(objet.getCodeNumberStr());
        return code.toString();
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        StringBuilder loggable = new StringBuilder();
        loggable.append("[CODE:").append(getCode()).append("] ").append(this.text);
        return loggable.toString();
    }
}
