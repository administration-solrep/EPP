package fr.dila.solonepp.core.assembler.ws;

import fr.sword.xsd.solon.epp.EvenementType;

/**
 * Assembleur des types d'événements objet métier <-> Web service.
 *
 * @author jtremeaux
 */
public class EvenementTypeAssembler {

    /**
     * Assemble l'objet web service -> métier.
     *
     * @param evenementType Objet web service
     * @return Objet métier
     */
    public static String assembleXsdToEvenementType(final EvenementType evenementType) {
        return evenementType.value();
    }

    /**
     * Assemble l'objet métier -> web service.
     *
     * @param evenementType Objet métier
     * @return Objet web service
     */
    public static EvenementType assembleEvenementTypeToXsd(final String evenementType) {
        return EvenementType.fromValue(evenementType);
    }
}
