package fr.dila.ss.api.domain.feuilleroute;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStepsContainer;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;

/**
 * Interface des documents de type conteneur d'étape de feuille de route.
 *
 * @author jtremeaux
 */
public interface StepFolder extends FeuilleRouteStepsContainer {
    /**
     * Retourne le type d'exécution du conteneur (parallèle ou série).
     *
     * @return Type d'exécution
     */
    String getExecution();

    /**
     * Renseigne le type d'exécution du conteneur (parallèle ou série).
     *
     * @param execution Type d'exécution
     */
    void setExecution(FeuilleRouteExecutionType execution);

    /**
     * Retourne vrai si le conteneur est de type série.
     *
     * @return Vrai si le conteneur est de type série
     */
    boolean isSerial();

    /**
     * Retourne vrai si le conteneur est de type parallèle.
     *
     * @return Vrai si le conteneur est de type parallèle
     */
    boolean isParallel();
}
