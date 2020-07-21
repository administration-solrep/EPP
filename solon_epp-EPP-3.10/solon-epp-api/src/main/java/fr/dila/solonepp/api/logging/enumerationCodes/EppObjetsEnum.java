package fr.dila.solonepp.api.logging.enumerationCodes;

import fr.dila.st.api.logging.enumerationCodes.STCodes;

/**
 * Énumération de l'objet des actions <br />
 * Décompte sur 3 chiffres, le premier (3) indique qu'il s'agit d'un objet de l'EPP <br />
 * <br />
 * 301 : Institution <br />
 * 302 : EPP <br />
 * 303 : Adresse <br />
 * 304 : Version <br />
 * 305 : WSEPP<br />
 * 306 : WSEvenement<br/>
 * 307 : Evenement delegate<br/>
 * 308 : Search <br />
 * 309 : Message <br />
 * 310 : Période <br />
 * 311 : Organisme <br />
 * 312 : Membre <br />
 * 313 : Circonscription <br />
 */
public enum EppObjetsEnum implements STCodes {
    /**
     * 301 : Institution
     */
    INSTITUTION(301, "Institution"),
    /**
     * 302 : EPP
     */
    EPP(302, "EPP"),
    /**
     * 303 : Adresse
     */
    ADDRESS(303, "Adresse"),
    /**
     * 304 : Version
     */
    VERSION(304, "Version"),
    /**
     * 305 : WSEPP
     */
    WSEPP(305, "WSEPP"),
    /**
     * 306 : WSEvenement
     */
    WS_EVENEMENT(306, "WSEvenement"),
    /**
     * 307 : Evenement delegate
     */
    EVENEMENT_DELEGATE(307, "Evenement delegate"),
    /**
     * 308 : search
     */
    SEARCH(308, "Search"),
    /**
     * 309 : Message
     */
    MESSAGE(309, "Message"),
    /**
     * 310 : Periode 
     */
    PERIODE_TDR(310, "Période"),
    /**
     * 311 : Organisme
     */
    ORGANISME_TDR(311, "Organisme"),
    /**
     * 312 : Membre
     */
    MEMBRE_TDR(312, "Membre"),
    /**
     * 313 : Circonscription
     */
    CIRCONSCRIPTION_TDR(313, "Circonscription"), 
    /**
     * 314 : decsriptor
     */
    DESCRIPTOR(314, "Descriptor"),
    /**
     * 315 : ws epg
     */
    WS_EPG(315, "WS Epg");
    
    

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    EppObjetsEnum(int codeNumber, String codeText) {
        this.codeNumber = codeNumber;
        this.codeText = codeText;
    }

    @Override
    public int getCodeNumber() {
        return this.codeNumber;
    }

    @Override
    public String getCodeText() {
        return this.codeText;
    }

    @Override
    public String getCodeNumberStr() {
        return String.valueOf(this.codeNumber);
    }

}
