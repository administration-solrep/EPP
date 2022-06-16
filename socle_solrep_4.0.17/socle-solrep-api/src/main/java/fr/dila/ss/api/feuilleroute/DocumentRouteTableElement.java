package fr.dila.ss.api.feuilleroute;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteFolderElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTable;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * Surcharge de DocumentRouteTableElement pour pouvoir l'utiliser dans des sélections multiples.
 *
 * @author jtremeaux
 */
public class DocumentRouteTableElement extends RouteTableElement {

    /**
     * Constructeur de DocumentRouteTableElement.
     *
     * @param element
     * @param table
     * @param depth
     * @param parent
     * @param isFirstChild
     */
    public DocumentRouteTableElement(
        FeuilleRouteElement element,
        RouteTable table,
        int depth,
        RouteFolderElement parent,
        boolean isFirstChild
    ) {
        super(element, table, depth, parent, isFirstChild);
    }

    /**
     * Retourne la référence du document de l'étape de feuille de route.
     *
     * @return Référence du document de l'étape de feuille de route
     */
    public DocumentRef getRef() {
        return getElement().getDocument().getRef();
    }

    /**
     * Retourne l'objet métier étape de feuille de route.
     *
     * @return Objet métier étape de feuille de route
     */
    public SSRouteStep getRouteStep() {
        return getElement().getDocument().getAdapter(SSRouteStep.class);
    }
}
