package fr.dila.st.core.administration;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.STDomainObjectImpl;

/**
 * Adapter Document Etat Application
 * 
 * @author Fabio Esposito
 * 
 */
public class EtatApplicationImpl extends STDomainObjectImpl implements EtatApplication {

	/**
	 * Serial version UID.
	 */
	private static final long	serialVersionUID	= 3432049854761778547L;

	public EtatApplicationImpl(DocumentModel doc) {
		super(doc);
	}

	@Override
	public boolean getRestrictionAcces() {
		return getBooleanProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA,
				STSchemaConstant.ETAT_APPLICATION_RESTRICTION_ACCES_PROPERTY);
	}

	@Override
	public void setRestrictionAcces(boolean restrictionAcces) {
		setProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA,
				STSchemaConstant.ETAT_APPLICATION_RESTRICTION_ACCES_PROPERTY, restrictionAcces);
	}

	@Override
	public String getDescriptionRestriction() {
		return getStringProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA,
				STSchemaConstant.ETAT_APPLICATION_DESCRIPTION_RESTRICTION_PROPERTY);
	}

	@Override
	public void setDescriptionRestriction(String descriptionRestriction) {
		setProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA,
				STSchemaConstant.ETAT_APPLICATION_DESCRIPTION_RESTRICTION_PROPERTY, descriptionRestriction);
	}

	@Override
	public Boolean getAffichage() {
		return getBooleanProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA,
				STSchemaConstant.BANNIERE_AFFICHAGE_PROPERTY);

	}

	@Override
	public void setAffichage(Boolean value) {
		setProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA, STSchemaConstant.BANNIERE_AFFICHAGE_PROPERTY, value);
	}

	@Override
	public String getMessage() {
		return getStringProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA, STSchemaConstant.BANNIERE_MESSAGE_PROPERTY);
	}

	@Override
	public void setMessage(String value) {
		setProperty(STSchemaConstant.ETAT_APPLICATION_SCHEMA, STSchemaConstant.BANNIERE_MESSAGE_PROPERTY, value);
	}

}
