package fr.dila.st.core.requeteur;

import java.util.Comparator;

import fr.dila.st.api.requeteur.RequeteurWidgetDescription;

public class CategoryWidgetDescriptionComparator implements Comparator<RequeteurWidgetDescription> {

	/**
	 * Default constructor
	 */
	public CategoryWidgetDescriptionComparator() {
		// do nothing
	}

	@Override
	public int compare(RequeteurWidgetDescription desc1, RequeteurWidgetDescription desc2) {
		final int descComp = desc1.getCategory().compareTo(desc2.getCategory());
		if (descComp != 0) {
			return descComp;
		}
		return desc1.getName().compareTo(desc2.getName());
	}

}
