package fr.dila.st.api.decorator;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des Bean Seam qui permettent de décorer des données.
 *
 * @author jtremeaux.
 */
public interface IDecoratorBean extends Serializable {
    /**
     * Décore un document afin de personaliser les styles d'un content template. Retourne null si le document ne peut
     * pas être décoré, dans ce cas le template devra appliquer une classe par défaut.
     *
     * @param doc
     *            Document à décorer
     * @return Classe CSS ou ensemble de classes CSS séparées par des espaces (ex. "class1 class2").
     */
    String decorate(DocumentModel doc);
}
