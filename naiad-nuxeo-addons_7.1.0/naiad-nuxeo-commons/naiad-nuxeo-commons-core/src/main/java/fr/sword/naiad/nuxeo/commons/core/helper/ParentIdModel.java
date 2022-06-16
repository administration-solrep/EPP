package fr.sword.naiad.nuxeo.commons.core.helper;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Modèle de données permettant de stocker un document, sont identifiant et l'identifiant de son parent.
 * 
 * @author fmh
 */
public class ParentIdModel {
	private String id;

	private String parentId;

	private DocumentModel document;

	public ParentIdModel(String anId, String parentId, DocumentModel document) {
		super();
		this.id = anId;
		this.parentId = parentId;
		this.document = document;
	}

	public String getId() {
		return id;
	}

	public void setId(String anId) {
		this.id = anId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public DocumentModel getDocument() {
		return document;
	}

	public void setDocument(DocumentModel document) {
		this.document = document;
	}
}
