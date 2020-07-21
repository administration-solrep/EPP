package fr.dila.st.api.administration;

import fr.dila.st.api.domain.STDomainObject;

/**
 * Interface Adapter Document Etat Application
 * 
 * @author Fabio Esposito
 * 
 */
public interface EtatApplication extends STDomainObject {

	boolean getRestrictionAcces();

	void setRestrictionAcces(boolean restrictionAcces);

	String getDescriptionRestriction();

	void setDescriptionRestriction(String descriptionRestriction);

	void setMessage(String value);

	String getMessage();

	void setAffichage(Boolean value);

	Boolean getAffichage();
}
