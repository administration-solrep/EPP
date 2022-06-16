package fr.dila.solonepp.core.domain.tablereference;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.tablereference.Acteur;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation de l'objet métier acteur.
 *
 * @author jtremeaux
 */
public class ActeurImpl implements Acteur {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Modèle de document.
     */
    protected DocumentModel document;

    /**
     * Constructeur de ActeurImpl.
     *
     * @param document Modèle de document
     */
    public ActeurImpl(DocumentModel document) {
        this.document = document;
    }

    @Override
    public String getIdentifiant() {
        return PropertyUtil.getStringProperty(
            document,
            SolonEppSchemaConstant.ACTEUR_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY
        );
    }

    @Override
    public void setIdentifiant(String id) {
        PropertyUtil.setProperty(
            document,
            SolonEppSchemaConstant.ACTEUR_SCHEMA,
            SolonEppSchemaConstant.TABLE_REFERENCE_IDENTIFIANT_PROPERTY,
            id
        );
    }
}
