package fr.dila.st.core.jeton;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.jeton.LockJetonMaitre;
import fr.dila.st.core.util.PropertyUtil;

public class LockJetonMaitreImpl implements LockJetonMaitre {

	/**
     * 
     */
	private static final long	serialVersionUID	= 3009955889619921722L;
	private DocumentModel		doc;

	public LockJetonMaitreImpl(DocumentModel document) {
		this.doc = document;
	}

	@Override
	public DocumentModel getDocument() {
		return doc;
	}

	@Override
	public String getIdProprietaire() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_ID_PROPRIETAIRE);
	}

	@Override
	public void setIdProprietaire(String id) {
		PropertyUtil.setProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_ID_PROPRIETAIRE, id);
	}

	@Override
	public Long getNumeroJetonMaitre() {
		return PropertyUtil.getLongProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_NUMERO);
	}

	@Override
	public void setNumeroJetonMaitre(Long id) {
		PropertyUtil.setProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_NUMERO, id);
	}

	@Override
	public String getTypeWebservice() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_WEBSERVICE);
	}

	@Override
	public void setTypeWebservice(String type) {
		PropertyUtil.setProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_WEBSERVICE, type);
	}

	@Override
	public void saveDocument(CoreSession session) throws ClientException {
		doc = session.saveDocument(doc);
	}

	@Override
	public String getIdJetonMaitre() {
		return PropertyUtil.getStringProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA,
				STSchemaConstant.LOCK_JETON_MAITRE_ID);
	}

	@Override
	public void setIdJetonMaitre(String id) {
		PropertyUtil.setProperty(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA, STSchemaConstant.LOCK_JETON_MAITRE_ID,
				id);
	}

}
