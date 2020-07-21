package fr.dila.st.core.domain.user;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de l'objet métier délégation.
 * 
 * @author jtremeaux
 */
public class DelegationImpl implements Delegation {
	protected DocumentModel	document;

	/**
	 * Constructeur de DelegationImpl.
	 * 
	 * @param document
	 *            Document
	 */
	public DelegationImpl(DocumentModel document) {
		this.document = document;
	}

	@Override
	public DocumentModel getDocument() {
		return document;
	}

	@Override
	public void setDocument(DocumentModel document) {
		this.document = document;
	}

	@Override
	public Calendar getDateDebut() {
		return PropertyUtil.getCalendarProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME);
	}

	@Override
	public void setDateDebut(Calendar dateDebut) {
		PropertyUtil.setProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME, dateDebut);
	}

	@Override
	public Calendar getDateFin() {
		return PropertyUtil.getCalendarProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DATE_FIN_PROPERTY_NAME);
	}

	@Override
	public void setDateFin(Calendar dateFin) {
		PropertyUtil.setProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DATE_FIN_PROPERTY_NAME, dateFin);
	}

	@Override
	public String getSourceId() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME);
	}

	@Override
	public void setSourceId(String sourceId) {
		PropertyUtil.setProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_SOURCE_ID_PROPERTY_NAME, sourceId);
	}

	@Override
	public String getDestinataireId() {
		return PropertyUtil.getStringProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME);
	}

	@Override
	public void setDestinataireId(String destinataireId) {
		PropertyUtil.setProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME, destinataireId);
	}

	@Override
	public List<String> getProfilListId() {
		return PropertyUtil.getStringListProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_PROFIL_LIST_PROPERTY_NAME);
	}

	@Override
	public void setProfilListId(List<String> profilListId) {
		PropertyUtil.setProperty(document, STSchemaConstant.DELEGATION_SCHEMA,
				STSchemaConstant.DELEGATION_PROFIL_LIST_PROPERTY_NAME, profilListId);
	}
}
