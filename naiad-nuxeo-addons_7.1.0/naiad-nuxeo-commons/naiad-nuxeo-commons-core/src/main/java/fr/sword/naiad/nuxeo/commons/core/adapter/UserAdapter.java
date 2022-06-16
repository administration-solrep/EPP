package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.adapter.helper.ModelAdapter;
import fr.sword.naiad.nuxeo.commons.core.model.UserModel;

/**
 * Adapter pour manipuler les docment user
 * @author SPL
 *
 */
public interface UserAdapter extends ModelAdapter<UserModel> {
	String getUserName() throws NuxeoException;

	String getFirstName() throws NuxeoException;

	String getLastName() throws NuxeoException;

	String getEmail() throws NuxeoException;

	String getPhone() throws NuxeoException;

	DocumentModel getDocument();
}
