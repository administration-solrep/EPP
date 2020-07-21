package fr.dila.st.api.parametre;

import org.nuxeo.ecm.core.api.DocumentModel;

public interface STParametre {

	DocumentModel getDocument();

	void setDocument(DocumentModel document);

	/**
	 * @return the unit
	 */
	String getUnit();

	/**
	 * @param unit
	 *            the unit to set
	 */
	void setUnit(String unit);

	/**
	 * @return the value
	 */
	String getValue();

	/**
	 * @param value
	 *            the value to set
	 */
	void setValue(String value);

	/**
	 * @return the description
	 */
	String getDescription();

	/**
	 * @param description
	 *            the description to set
	 */
	void setDescription(String description);
}
