package fr.dila.st.web.pdf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.platform.contentview.jsf.ContentView;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageSelections;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;

import fr.dila.ecm.platform.routing.api.DocumentRouteTableElement;
import fr.dila.ecm.platform.routing.web.pdf.RouteToPdfActionsBean;

/**
 * WebBean de gestion de l'affichage et de l'impression en pdf.
 * 
 * @author arolin
 */
@Name("pdfPrintActions")
@Scope(ScopeType.EVENT)
@Install(precedence = Install.FRAMEWORK)
public class PdfPrintActionsBean extends RouteToPdfActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long				serialVersionUID	= 1L;

	@In(create = true, required = true)
	protected transient ContentViewActions	contentViewActions;

	/**
	 * Affiche la liste des résultats dans un pdf en récupérant tout les résultats et pas uniquement les résultats
	 * affichés.
	 * 
	 * @param view_name
	 * @throws Exception
	 */
	public void doRenderViewList(String view_name) throws Exception {
		boolean changeSizePage = false;
		Long pageSize = null;

		ContentView contentView = contentViewActions.getCurrentContentView();
		if (contentView != null && contentView.getCurrentPageProvider() != null) {
			PageProvider<?> provider = contentView.getCurrentPageProvider();
			PageSelections<?> pageSelection = provider.getCurrentSelectPage();
			if (!provider.hasError() && pageSelection != null && pageSelection.getEntries() != null
					&& pageSelection.getEntries().size() > 0) {

				if (provider instanceof CoreQueryDocumentPageProvider) {
					changeSizePage = true;
					// récupération de tous les résultats
					CoreQueryDocumentPageProvider coreProvider = (CoreQueryDocumentPageProvider) provider;
					pageSize = coreProvider.getPageSize();
					coreProvider.setPageSize(provider.getResultsCount());
					coreProvider.setSelectedEntries(coreProvider.setCurrentPage(1));
				}

			}
		}

		super.doRenderView(view_name);

		if (changeSizePage) {
			// recharge les anciennes entrées
			PageProvider<?> provider = contentView.getCurrentPageProvider();
			CoreQueryDocumentPageProvider coreProvider = (CoreQueryDocumentPageProvider) provider;
			coreProvider.setPageSize(pageSize);
			coreProvider.refresh();
			coreProvider.getCurrentPage();
		}
	}

	@Override
	public void doRenderView(String view_name) throws Exception {
		super.doRenderView(view_name);
	}

	/**
	 * Définit le nb de colonne occupée par la première colonne pour l'affichage de la feuille de route.
	 * 
	 * @return le nb de colonne occupée par la première colonne.
	 */
	public int getNbAdditionnalColumn(DocumentRouteTableElement docRouteTableElement) {
		int nbAdditionnalColumn = 0;
		if (docRouteTableElement != null) {
			nbAdditionnalColumn = docRouteTableElement.getRouteMaxDepth();
		}
		return nbAdditionnalColumn;
	}

	/**
	 * retourne le nombre total de colonnes affichées dans le tableau
	 * 
	 * @param layoutColumn
	 * @param documentRouteTableElementObject
	 * @return le nombre total de colonnes affichées dans le tableau
	 */
	public int getTotalNbColumn(int layoutColumn, DocumentRouteTableElement docRouteTableElement) {
		return layoutColumn + getNbAdditionnalColumn(docRouteTableElement) - 1;
	}

	/**
	 * Définit la largeur de chacune des colonnes de la table sous forme de String
	 * 
	 * @param nbTotalColumn
	 * @param nbAditionnalColumn
	 * @return la largeur de chacune des colonnes de la table sous forme de String
	 */
	public String getTableWidths(int nbTotalColumn, int nbAditionnalColumn) {
		List<String> tableColumnsWidthList = new ArrayList<String>();
		// largeur des colonnes pour l'affichage des branhces
		if (nbAditionnalColumn > 0) {
			for (int i = 0; i < nbAditionnalColumn; i++) {
				tableColumnsWidthList.add("0.5");
			}
		}
		// largeur des colonnes affichant des données
		if (nbTotalColumn - nbAditionnalColumn > 0) {
			for (int i = 0; i < nbTotalColumn - nbAditionnalColumn; i++) {
				tableColumnsWidthList.add("1");
			}
		}
		// récupération de la liste
		String tableWidths = org.nuxeo.common.utils.StringUtils.join(tableColumnsWidthList, " ");
		return tableWidths;
	}

	/**
	 * Définit la largeur de chacune des colonnes de la table sous forme de String
	 * 
	 * @param nbTotalColumn
	 * @param nbAditionnalColumn
	 * @return la largeur de chacune des colonnes de la table sous forme de String
	 */
	public float[] getTableWidthsInArrayInt(int nbTotalColumn, int nbAditionnalColumn) {
		if (nbTotalColumn < 1) {
			return null;
		}

		float[] tableColumnsWidth = new float[nbTotalColumn];
		int iterateur = 0;
		// largeur des colonnes pour l'affichage des branhces
		if (nbAditionnalColumn > 0) {
			for (int i = 0; i < nbAditionnalColumn; i++) {
				tableColumnsWidth[iterateur] = 0.5f;
				iterateur = iterateur + 1;
			}
		}
		// largeur des colonnes affichant des données
		if (nbTotalColumn - nbAditionnalColumn > 0) {
			for (int i = 0; i < nbTotalColumn - nbAditionnalColumn; i++) {
				tableColumnsWidth[iterateur] = 1f;
				iterateur = iterateur + 1;
			}
		}
		return tableColumnsWidth;
	}

}
