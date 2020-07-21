package fr.dila.st.core.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.domain.STDomainObject;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Interface de base des objets métiers du socle transverse qui encapsulent un DocumentModel.
 * 
 * @author antoine Rolin
 * @author jtremeaux
 */
public class STDomainObjectImpl implements STDomainObject {

	private static final long	serialVersionUID	= -7821610638706381718L;

	protected DocumentModel		document;

	/**
	 * Constructeur de STDomainObjectImpl.
	 * 
	 * @param doc
	 *            Document
	 */
	public STDomainObjectImpl(DocumentModel doc) {
		this.document = doc;
	}

	@Override
	public DocumentModel getDocument() {
		return document;
	}

	@Override
	public DocumentModel save(CoreSession session) throws ClientException {
		document = session.saveDocument(document);
		return document;
	}

	// *************************************************************
	// Propriétés du schéma Dublin Core.
	// *************************************************************
	@Override
	public String getTitle() {
		return DublincoreSchemaUtils.getTitle(document);
	}

	@Override
	public void setTitle(String title) {
		DublincoreSchemaUtils.setTitle(document, title);
	}

	@Override
	public String getCreator() {
		return DublincoreSchemaUtils.getCreator(document);
	}

	@Override
	public void setCreator(String creator) {
		DublincoreSchemaUtils.setCreator(document, creator);
	}

	@Override
	public String getLastContributor() {
		return DublincoreSchemaUtils.getLastContributor(document);
	}

	@Override
	public void setLastContributor(String lastContributor) {
		DublincoreSchemaUtils.setLastContributor(document, lastContributor);
	}

	@Override
	public Calendar getModifiedDate() {
		return DublincoreSchemaUtils.getModifiedDate(document);
	}

	@Override
	public void setModifiedDate(Calendar modifiedDate) {
		DublincoreSchemaUtils.setModifiedDate(document, modifiedDate);
	}

	@Override
	public boolean isDeleted() throws ClientException {
		return STLifeCycleConstant.DELETED_STATE.equals(document.getCurrentLifeCycleState());
	}

	// /////////////////
	// getter/setter of documentModel Property
	// use propertyUtil
	// ////////////////

	protected List<Map<String, Serializable>> getCollectionFileProperty(String schema, String value) {
		return PropertyUtil.getMapStringSerializableListProperty(document, schema, value);
	}

	protected Object getProperty(String schema, String value) {
		return PropertyUtil.getProperty(document, schema, value);
	}

	protected Long getLongProperty(String schema, String value) {
		return PropertyUtil.getLongProperty(document, schema, value);
	}

	protected Calendar getDateProperty(String schema, String value) {
		return PropertyUtil.getCalendarProperty(document, schema, value);
	}

	protected String getStringProperty(String schema, String value) {
		return PropertyUtil.getStringProperty(document, schema, value);
	}

	protected List<String> getListStringProperty(String schema, String value) {
		return PropertyUtil.getStringListProperty(document, schema, value);
	}

	protected Boolean getBooleanProperty(String schema, String value) {
		return PropertyUtil.getBooleanProperty(document, schema, value);
	}

	protected Blob getBlobProperty(String schema, String value) {
		return PropertyUtil.getBlobProperty(document, schema, value);
	}

	public void setProperty(String schema, String property, Object value) {
		PropertyUtil.setProperty(document, schema, property, value);
	}

	@Override
	public String getId() {
		return document.getId();
	}
}
