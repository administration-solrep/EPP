package fr.dila.ss.core.flux;

import java.util.Calendar;

/**
 * Regroupe les fonctions utilitaires pour calculer une date échéance à partir d'une échéance indicative en jour et d'une date de départ.
 *
 * @author ARN
 */
public final class EcheanceCalculateur {

    /**
     *  utility class
     */
    private EcheanceCalculateur() {
        // do nothing
    }

    /**
     * On renvoie l'échéance à partir d'une date de départ et d'une durée en jours en ne comptant pas les jours non ouvrés.
     *
     * @param dateEcheance
     * @param echeanceIndicative
     * @return Calendar
     */
    public static Calendar getEcheanceCompteUniquementJourOuvre(Calendar dateEcheance, int echeanceIndicative) {
        // date de départ : date de publication
        for (int iterateurJour = 0; iterateurJour < echeanceIndicative; iterateurJour++) {
            // dans tous les cas, on ajoute au moins un jour
            dateEcheance.add(Calendar.DAY_OF_YEAR, 1);

            // si on arrive un samedi ou un dimanche on ne compte pas ces jours comme ouvrés
            if (dateEcheance.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                dateEcheance.add(Calendar.DAY_OF_YEAR, 2);
            } else if (dateEcheance.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dateEcheance.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return dateEcheance;
    }
}
