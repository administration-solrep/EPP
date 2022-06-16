package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classe abstraite utilisable pour les factory d'adapter nécessitant le test d'un schema pour savoir si la conversion
 * est applicable
 * 
 * @author SPL
 */
public abstract class AbstractAdapterFactoryOnSchema extends AbstractAdapterFactory {
	/**
	 * schema nécessaire
	 */
	private final String schema;

	/**
	 * Constructeur
	 * 
	 * @param schema
	 *            schema necessaire pour appliquer l'adapter
	 */
	protected AbstractAdapterFactoryOnSchema(String schema) {
		super();
		this.schema = schema;
	}

	/**
	 * Teste que le document a le schema nécessaire
	 */
	@Override
	protected final boolean checkDocument(DocumentModel document) {
		return document.hasSchema(schema);
	}

	/**
	 * Accès au schéma à tester
	 * 
	 * @return le nom du schema à tester
	 */
	protected final String getSchema() {
		return schema;
	}
}
