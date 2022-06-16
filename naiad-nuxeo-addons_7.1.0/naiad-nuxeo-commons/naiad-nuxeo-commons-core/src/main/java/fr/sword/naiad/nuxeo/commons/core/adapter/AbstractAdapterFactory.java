package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * Classe abstraite validant un DocumentModel avant de l'adapter.
 * 
 * @author fmh
 */
public abstract class AbstractAdapterFactory implements DocumentAdapterFactory {
	/**
	 * Valide un document.
	 * 
	 * @param document
	 *            Document à adapter.
	 * @return true si le document peut être adapté, false sinon.
	 */
	protected abstract boolean checkDocument(DocumentModel document);

	/**
	 * Adapte le document.
	 * 
	 * @param document
	 *            Document à adapter.
	 * @param clazz
	 *            Classe de l'adapter.
	 * @return Document adapté, null s'il est invalide.
	 */
	protected abstract Object adapt(DocumentModel document, Class<?> clazz);

	@Override
	public final Object getAdapter(DocumentModel document, Class<?> clazz) {
		if(checkDocument(document)){
			return adapt(document, clazz);
		} else {
			return null;
		}
	}
}
