package fr.dila.st.core.jeton;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.jeton.JetonDoc;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation de la liaison Jeton / document.
 * 
 * @author sly
 * @author jtremeaux
 */
public class JetonDocImpl implements JetonDoc {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1064860775690094150L;

	/**
	 * Modèle de document.
	 */
	protected DocumentModel		doc;

	/**
	 * Constructeur de JetonDocImpl.
	 * 
	 * @param doc
	 *            Modèle de document
	 */
	public JetonDocImpl(DocumentModel doc) {
		this.doc = doc;
	}

	@Override
	public DocumentModel getDocument() {
		return doc;
	}

	@Override
	public String getIdDoc() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_ID_DOC);
	}

	@Override
	public void setIdDoc(String idDoc) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA, STSchemaConstant.JETON_DOCUMENT_ID_DOC,
				idDoc);
	}

	@Override
	public Long getNumeroJeton() {
		return PropertyUtil.getLongProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_ID_JETON_DOC);
	}

	@Override
	public void setNumeroJeton(Long numeroJeton) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_ID_JETON_DOC, numeroJeton);
	}

	@Override
	public String getIdOwner() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_ID_OWNER);
	}

	@Override
	public void setIdOwner(String type) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA, STSchemaConstant.JETON_DOCUMENT_ID_OWNER,
				type);
	}

	@Override
	public String getTypeWebservice() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_WEBSERVICE);
	}

	@Override
	public void setTypeWebservice(String type) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_WEBSERVICE, type);
	}

	@Override
	public void saveDocument(CoreSession session) throws ClientException {
		doc = session.saveDocument(doc);
	}

	@Override
	public void setCreated(Calendar created) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOC_CREATED_PROPERTY, created);
	}

	@Override
	public Calendar getCreated() {
		return PropertyUtil.getCalendarProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOC_CREATED_PROPERTY);
	}

	@Override
	public String getTypeModification() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_TYPE_MODIFICATION);
	}

	@Override
	public void setTypeModification(String typeModification) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_TYPE_MODIFICATION, typeModification);
	}

	@Override
	public List<String> getIdsComplementaires() {
		return PropertyUtil.getStringListProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_IDS_COMPLEMENTAIRES);
	}

	@Override
	public void setIdsComplementaires(List<String> idsComplementaires) {
		PropertyUtil.setProperty(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA,
				STSchemaConstant.JETON_DOCUMENT_IDS_COMPLEMENTAIRES, idsComplementaires);
	}

}
