package fr.dila.ss.core.feuilleroute;

import static fr.dila.ss.api.constant.SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE;

import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter.FeuilleRouteImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation d'une feuille de route du socle transverse.
 *
 * @author jtremeaux
 */
public abstract class SSFeuilleRouteImpl extends FeuilleRouteImpl implements SSFeuilleRoute {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructeur de STFeuilleRouteImpl.
     *
     * @param doc
     *            doc
     * @param runner
     *            runner
     */
    public SSFeuilleRouteImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    // *************************************************************
    // Propriétés du document.
    // *************************************************************
    @Override
    public String getTitle() {
        return DublincoreSchemaUtils.getTitle(document);
    }

    @Override
    public void setTitle(String title) {
        DublincoreSchemaUtils.setTitle(document, title);
    }

    @Override
    public void setCreator(String creator) {
        DublincoreSchemaUtils.setCreator(document, creator);
    }

    // *************************************************************
    // Propriétés de la feuille de route (socle SOLREP).
    // *************************************************************
    @Override
    public boolean isFeuilleRouteDefaut() {
        return PropertyUtil.getBooleanProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_DEFAUT_PROPERTY
        );
    }

    @Override
    public void setFeuilleRouteDefaut(boolean defaultQuestion) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_DEFAUT_PROPERTY,
            defaultQuestion
        );
    }

    @Override
    public String getMinistere() {
        return PropertyUtil.getStringProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY
        );
    }

    @Override
    public void setMinistere(String ministere) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY,
            ministere
        );
    }

    @Override
    public boolean isDemandeValidation() {
        return PropertyUtil.getBooleanProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY
        );
    }

    @Override
    public void setDemandeValidation(boolean demandeValidation) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_PROPERTY,
            demandeValidation
        );
    }

    @Override
    public String getTypeCreation() {
        return PropertyUtil.getStringProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_TYPE_CREATION_PROPERTY
        );
    }

    @Override
    public void setTypeCreation(String typeCreation) {
        PropertyUtil.setProperty(
            document,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA,
            SSFeuilleRouteConstant.FEUILLE_ROUTE_TYPE_CREATION_PROPERTY,
            typeCreation
        );
    }

    // *************************************************************
    // Propriétés calculées.
    // *************************************************************
    @Override
    public boolean isModel() {
        return (
            FEUILLE_ROUTE_DOCUMENT_TYPE.equals(document.getType()) &&
            (checkLifeCycleState(ElementLifeCycleState.draft) || checkLifeCycleState(ElementLifeCycleState.validated))
        );
    }
}
