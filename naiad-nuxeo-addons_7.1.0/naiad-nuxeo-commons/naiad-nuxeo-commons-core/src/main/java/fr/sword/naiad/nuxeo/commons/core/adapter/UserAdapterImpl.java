package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.model.UserModel;
import fr.sword.naiad.nuxeo.commons.core.schema.UserPropertyUtil;

/**
 * Implémentation de UserAdapter
 * 
 * Extraction des données depuis un document Nuxeo
 *
 */
public class UserAdapterImpl implements UserAdapter {
	
	private final DocumentModel document;

	public UserAdapterImpl(DocumentModel document) {
		this.document = document;
	}

	@Override
	public String getUserName() throws NuxeoException {
		return UserPropertyUtil.getUserName(document);
	}

	@Override
	public String getFirstName() throws NuxeoException {
		return UserPropertyUtil.getFirstName(document);
	}

	@Override
	public String getLastName() throws NuxeoException {
		return UserPropertyUtil.getLastName(document);
	}

	@Override
	public String getEmail() throws NuxeoException {
		return UserPropertyUtil.getEmail(document);
	}

	@Override
	public String getPhone() throws NuxeoException {
		return UserPropertyUtil.getPhone(document);
	}

	@Override
	public DocumentModel getDocument() {
		return document;
	}

	@Override
	public UserModel convertToModel(Class<UserModel> modelClass, Object... arguments) throws NuxeoException {
		return new UserModel(getUserName(), getFirstName(), getLastName(), getEmail(), getPhone());
	}
}
