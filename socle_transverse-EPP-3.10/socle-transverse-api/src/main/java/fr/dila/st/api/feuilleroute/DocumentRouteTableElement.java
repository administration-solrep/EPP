package fr.dila.st.api.feuilleroute;

import org.nuxeo.ecm.core.api.DocumentRef;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.RouteFolderElement;
import fr.dila.ecm.platform.routing.api.RouteTable;

/**
 * Surcharge de DocumentRouteTableElement pour pouvoir l'utiliser dans des sélections multiples.
 * 
 * @author jtremeaux
 */
public class DocumentRouteTableElement extends fr.dila.ecm.platform.routing.api.DocumentRouteTableElement {

	/**
	 * Constructeur de DocumentRouteTableElement.
	 * 
	 * @param element
	 * @param table
	 * @param depth
	 * @param parent
	 * @param isFirstChild
	 */
	public DocumentRouteTableElement(DocumentRouteElement element, RouteTable table, int depth,
			RouteFolderElement parent, boolean isFirstChild) {
		super(element, table, depth, parent, isFirstChild);
	}

	/**
	 * Retourne la référence du document de l'étape de feuille de route.
	 * 
	 * @return Référence du document de l'étape de feuille de route
	 */
	public DocumentRef getRef() {
		return element.getDocument().getRef();
	}

	/**
	 * Retourne l'objet métier étape de feuille de route.
	 * 
	 * @return Objet métier étape de feuille de route
	 */
	public STRouteStep getRouteStep() {
		return element.getDocument().getAdapter(STRouteStep.class);
	}
}
