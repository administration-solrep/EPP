package fr.sword.naiad.nuxeo.commons.core.adapter.factory;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.adapter.AbstractAdapterFactoryOnSchema;
import fr.sword.naiad.nuxeo.commons.core.adapter.UserAdapterImpl;
import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;

/**
 * Factory pour l'adapter sur les userModel
 * @author SPL
 *
 */
public class UserAdapterFactory extends AbstractAdapterFactoryOnSchema {
	
	public UserAdapterFactory() {
		super(CommonSchemaConstant.SCHEMA_USER);
	}

	@Override
	protected Object adapt(DocumentModel document, Class<?> clazz) {
		return new UserAdapterImpl(document);
	}
}
