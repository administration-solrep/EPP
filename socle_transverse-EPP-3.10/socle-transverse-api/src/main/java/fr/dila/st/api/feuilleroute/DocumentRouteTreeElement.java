package fr.dila.st.api.feuilleroute;

import java.util.LinkedList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Représentation de la feuille de route sous forme arborescente, afin de faire les copier / coller d'arbres.
 * 
 * @author jtremeaux
 */
public class DocumentRouteTreeElement {
	/**
	 * Document.
	 */
	private DocumentModel					document;

	/**
	 * Vrai si ce document est à coller.
	 */
	private boolean							toPaste;

	/**
	 * Parent de cet élément.
	 */
	private DocumentRouteTreeElement		parent;

	/**
	 * Enfants de cet élément.
	 */
	private List<DocumentRouteTreeElement>	childrenList;

	/**
	 * Constructeur de DocumentRouteTreeElement.
	 * 
	 * @param parent
	 *            Element parent (nul pour la racine)
	 * @param document
	 *            Document
	 */
	public DocumentRouteTreeElement(DocumentModel document, DocumentRouteTreeElement parent) {
		this.document = document;
		this.parent = parent;
		childrenList = new LinkedList<DocumentRouteTreeElement>();
	}

	/**
	 * Getter de document.
	 * 
	 * @return document
	 */
	public DocumentModel getDocument() {
		return document;
	}

	/**
	 * Setter de document.
	 * 
	 * @param document
	 *            document
	 */
	public void setDocument(DocumentModel document) {
		this.document = document;
	}

	/**
	 * Getter de toPaste.
	 * 
	 * @return toPaste
	 */
	public boolean isToPaste() {
		return toPaste;
	}

	/**
	 * Setter de toPaste.
	 * 
	 * @param toPaste
	 *            toPaste
	 */
	public void setToPaste(boolean toPaste) {
		this.toPaste = toPaste;
	}

	/**
	 * Getter de parent.
	 * 
	 * @return parent
	 */
	public DocumentRouteTreeElement getParent() {
		return parent;
	}

	/**
	 * Setter de parent.
	 * 
	 * @param parent
	 *            parent
	 */
	public void setParent(DocumentRouteTreeElement parent) {
		this.parent = parent;
	}

	/**
	 * Getter de childrenList.
	 * 
	 * @return childrenList
	 */
	public List<DocumentRouteTreeElement> getChildrenList() {
		return childrenList;
	}

	/**
	 * Setter de childrenList.
	 * 
	 * @param childrenList
	 *            childrenList
	 */
	public void setChildrenList(List<DocumentRouteTreeElement> childrenList) {
		this.childrenList = childrenList;
	}
}
