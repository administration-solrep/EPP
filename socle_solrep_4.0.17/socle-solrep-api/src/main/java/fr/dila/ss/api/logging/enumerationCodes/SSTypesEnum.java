package fr.dila.ss.api.logging.enumerationCodes;

import fr.dila.st.api.logging.enumerationCodes.STCodes;

/**
 * Énumération de l'objet des actions <br />
 * Décompte sur 3 chiffres, le premier (1) indique qu'il s'agit d'un objet du SS <br />
 * 100 : défaut <br />
 * 101 : Echec de réattribution
 */
public enum SSTypesEnum implements STCodes {
    /**
     * 400 défaut
     */
    DEFAULT(300, "Types SOLREP défaut"),
    /**
     * 401 : Echec de signature
     */
    FAIL_REATTR(301, "Echec de réattribution");

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    SSTypesEnum(int codeNumber, String codeText) {
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
        return String.valueOf(codeNumber);
    }
}
