package fr.dila.st.web.widget;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

/**
 * WebBean qui permet de filtrer les documents via un autre bean (configurable dans le Layout Manager). Le filtre doit
 * être un bean Seam qui implémente l'interface Filter.
 * 
 * @author jtremeaux
 */
@Name("filteredDirectoryWidget")
@Scope(ScopeType.EVENT)
public class FilteredDirectoryWidgetBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Détermine si une tâche de distribution est filtrée.
	 * 
	 * @param doc
	 *            Document à filtrer
	 * @return Element filtré
	 */
	public boolean accept(DocumentModel doc, String filterBean) {
		Filter filter = (Filter) Component.getInstance(filterBean, true);

		return filter.accept(doc);
	}
}
