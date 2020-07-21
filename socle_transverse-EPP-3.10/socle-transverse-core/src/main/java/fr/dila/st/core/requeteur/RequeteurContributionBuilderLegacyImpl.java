package fr.dila.st.core.requeteur;

import fr.dila.st.api.requeteur.RequeteurWidgetDescription;

/**
 * Une implémentation qui garde le comportement du constructeur de contribution originel. Le nom d'un widget est
 * également le nom du search field. Un widget avec le même nom et des catégories différentes est considéré comme le
 * même widget.
 * 
 * @author jgomez
 * 
 */
public class RequeteurContributionBuilderLegacyImpl extends RequeteurContributionBuilderImpl {

	public RequeteurContributionBuilderLegacyImpl() {
		super();
	}

	public RequeteurContributionBuilderLegacyImpl(String contribName, String componentName, String layoutName,
			Boolean showCategories) {
		super(contribName, componentName, layoutName, showCategories);
	}

	public RequeteurContributionBuilderLegacyImpl(String contribName, String componentName, String layoutName) {
		super(contribName, componentName, layoutName);
	}

	public RequeteurContributionBuilderLegacyImpl(String contribName, String componentName, String layoutName,
			String fileName) {
		super(contribName, componentName, layoutName, fileName);
	}

	@Override
	public String getUsedWidgetName(RequeteurWidgetDescription description) {
		return description.getWidgetName();
	}
}
