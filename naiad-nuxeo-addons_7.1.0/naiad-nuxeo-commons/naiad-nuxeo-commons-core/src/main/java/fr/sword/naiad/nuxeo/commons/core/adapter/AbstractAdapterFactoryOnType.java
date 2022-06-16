package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.util.DocumentTypeUtil;

/**
 * Adapte un document à condition qu'il possède le bon type.
 * 
 * @author fmh
 */
public abstract class AbstractAdapterFactoryOnType extends AbstractAdapterFactory {
	
	private final String type;

	/**
	 * Constructeur.
	 * 
	 * @param type
	 *            Type du document.
	 */
	protected AbstractAdapterFactoryOnType(String type) {
		super();
		this.type = type;
	}

	@Override
	protected final boolean checkDocument(DocumentModel document) {
		return DocumentTypeUtil.hasType(document, type);
	}
}
