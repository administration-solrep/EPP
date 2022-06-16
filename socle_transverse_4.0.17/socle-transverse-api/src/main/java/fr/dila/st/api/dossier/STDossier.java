package fr.dila.st.api.dossier;

import fr.dila.cm.cases.HasParticipants;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des dossiers commune au socle transverse.
 *
 * @author jtremeaux
 */
public interface STDossier extends HasParticipants, Serializable {
    enum DossierState {
        init("dossiers-crees"),
        running("dossiers-running"),
        done("dossiers-done");

        private final String key;
        private final String title;

        DossierState(String key) {
            this.key = key;
            this.title = key.replace("-", ".") + ".title";
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }
    }

    enum DossierTransition {
        toRunning,
        toDone,
        backToRunning
    }

    /**
     * Gets the document model describing the envelope.
     */
    DocumentModel getDocument();

    /**
     * Persists the envelope.
     */
    DocumentModel save(CoreSession session);

    /**
     * Retourne l'identifiant technique de la dernière feuille de route lancée sur ce dossier.
     *
     * @return Identifiant technique de la dernière feuille de route lancée sur ce dossier
     */
    String getLastDocumentRoute();

    /**
     * Renseigne l'identifiant technique de la dernière feuille de route lancée sur ce dossier.
     *
     * @param routeDocId
     *            Identifiant technique de la dernière feuille de route lancée sur ce dossier
     */
    void setLastDocumentRoute(String routeDocId);

    /**
     * Retourne vrai si le dossier est à l'état initial.
     *
     * @return Vrai si le dossier est à l'état initial
     */
    boolean isInit();

    /**
     * Retourne vrai si le dossier est à l'état en cours.
     *
     * @return Vrai si le dossier est à l'état en cours
     */
    boolean isRunning();

    /**
     * Retourne vrai si le dossier est à l'état terminé.
     *
     * @return Vrai si le dossier est à l'état terminé
     */
    boolean isDone();

    /**
     * Retourne si un dossier a une feuille de route associé
     */
    Boolean hasFeuilleRoute();
}
