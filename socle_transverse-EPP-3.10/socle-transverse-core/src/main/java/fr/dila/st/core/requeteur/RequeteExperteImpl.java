package fr.dila.st.core.requeteur;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.schema.SmartFolderSchemaUtils;

public class RequeteExperteImpl implements RequeteExperte {
	protected DocumentModel	document;

	public RequeteExperteImpl(DocumentModel doc) {
		this.document = doc;
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
	public String getWhereClause() {
		return SmartFolderSchemaUtils.getQueryPart(document);
	}

	@Override
	public void setWhereClause(String whereClause) {
		SmartFolderSchemaUtils.setQueryPart(document, whereClause);
	}

	@Override
	public String getLastContributor() {
		return DublincoreSchemaUtils.getLastContributor(document);
	}

	@Override
	public void setLastContributor(String lastContributor) {
		DublincoreSchemaUtils.setLastContributor(document, lastContributor);
	}
}
