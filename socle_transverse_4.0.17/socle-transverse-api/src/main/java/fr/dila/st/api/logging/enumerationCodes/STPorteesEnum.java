package fr.dila.st.api.logging.enumerationCodes;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Énumération de portée des actions <br>
 * Décompte sur 3 chiffres, le premier (0) indique qu'il s'agit d'un objet du ST <br>
 * 000 : défaut <br>
 * 001 : Portée fonctionnnelle <br>
 * 002 : Portée technique <br>
 *
 *
 */
public enum STPorteesEnum implements STCodes {
    /**
     * 000
     */
    DEFAULT(0, "Portée défaut"),
    /**
     * 001 : fonctionnelle
     */
    FONCTIONNELLE(1, "Portée fonctionnelle"),
    /**
     * 002 : technique
     */
    TECHNIQUE(2, "Portée technique");

    /* **** (Ne pas oublier de tenir à jour la documentation en lien avec le code) **** */

    private int codeNumber;
    private String codeText;

    STPorteesEnum(int codeNumber, String codeText) {
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
        NumberFormat formatter = new DecimalFormat("000");
        return formatter.format(this.codeNumber);
    }
}
