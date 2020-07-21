package fr.dila.st.web.administration.utilisateur;

import static org.jboss.seam.ScopeType.PAGE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryFactory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryProxy;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Bean JSF pour le composant de suggestion d'envoi de mails.
 * 
 * @author jtremeaux
 */
@Name("mailSuggestionActions")
@SerializedConcurrentAccess
@Scope(PAGE)
public class MailSuggestionActionsBean implements Serializable {

	private static final long				serialVersionUID		= 1L;

	private static final Log				LOGGER					= LogFactory
																			.getLog(MailSuggestionActionsBean.class);

	public static final String				TYPE_KEY_NAME			= "type";

	public static final String				PREFIXED_ID_KEY_NAME	= "prefixed_id";

	public static final String				ID_KEY_NAME				= "id";

	public static final String				ENTRY_KEY_NAME			= "entry";

	private static final String				LAST_NAME_PROPERTY		= "lastName";
	private static final String				FIRST_NAME_PROPERTY		= "firstName";
	private static final String				EMAIL_PROPERTY			= "email";

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	@RequestParameter
	protected Integer						mailSuggestionMaxSearchResults;

	@RequestParameter
	protected Boolean						hideVirtualGroups;

	protected Integer						cachedMailSuggestionMaxSearchResults;

	protected Object						cachedInput;

	protected Object						cachedSuggestions;

	@RequestParameter
	protected String						userSuggestionMessageId;

	/**
	 * Id of the editable list component where selection ids are put.
	 * <p>
	 * Component must be an instance of {@link UIEditableList}
	 */
	@RequestParameter
	protected String						suggestionSelectionListId;

	protected void addSearchOverflowMessage() {
		if (userSuggestionMessageId == null) {
			LOGGER.error("Search overflow");
		} else {
			facesMessages.addToControl(userSuggestionMessageId, StatusMessage.Severity.ERROR, resourcesAccessor
					.getMessages().get("label.security.searchOverFlow"));
		}
	}

	private static LDAPDirectory getLDAPDirectory(String name) throws DirectoryException {
		LDAPDirectoryFactory factory = STServiceLocator.getLDAPDirectoryFactory();
		return ((LDAPDirectoryProxy) factory.getDirectory(name)).getDirectory();
	}

	/**
	 * Recherche des mails.
	 * 
	 * @param input
	 *            Chaine de recherche
	 * @return Liste d'entrées du LDAP correspondant à des fonctions
	 * @throws ClientException
	 *             ClientException
	 */
	public Set<DocumentModel> getMailSuggestions(Object input) throws ClientException {
		Set<DocumentModel> docs = new HashSet<DocumentModel>();

		Session session = null;
		try {
			session = getLDAPDirectory(STConstant.ORGANIGRAMME_USER_DIR).getSession();

			String pattern = (String) input;

			if (StringUtils.isNotBlank(pattern)) {
				Map<String, Serializable> filter = new HashMap<String, Serializable>();

				filter.put("mail", pattern);

				Map<String, String> orderBy = new HashMap<String, String>();

				Set<String> fulltext = new HashSet<String>();

				fulltext.add("mail");

				docs.addAll(session.query(filter, fulltext, orderBy, false));
			}

			if (StringUtils.isNotBlank(pattern)) {
				Map<String, Serializable> filter = new HashMap<String, Serializable>();

				filter.put("sn", pattern);

				Map<String, String> orderBy = new HashMap<String, String>();

				Set<String> fulltext = new HashSet<String>();

				fulltext.add("sn");

				docs.addAll(session.query(filter, fulltext, orderBy, false));
			}

		} catch (SizeLimitExceededException e) {
			addSearchOverflowMessage();
		} catch (Exception e) {
			LOGGER.error("error searching for functions", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

		return docs;
	}

	/**
	 * Retourne un tableau associatif contenant les propriétés d'un profil.
	 * 
	 * @param userId
	 *            id de l'utilisateur
	 * @return Propriétés
	 * @throws ClientException
	 *             ClientException
	 */
	public Map<String, Object> getMailInfo(String userId) throws ClientException {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(ID_KEY_NAME, userId);

		DocumentModel mailDocument = getMailModel(userId);
		if (mailDocument == null) {
			res.put(ENTRY_KEY_NAME, userId);
		} else {
			final String description = (String) mailDocument.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
					EMAIL_PROPERTY);
			res.put(ENTRY_KEY_NAME, description);
			String lastName = (String) mailDocument.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
					LAST_NAME_PROPERTY);
			res.put(LAST_NAME_PROPERTY, lastName);
			String firstName = (String) mailDocument.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
					FIRST_NAME_PROPERTY);
			res.put(FIRST_NAME_PROPERTY, firstName);
		}

		return res;
	}

	protected boolean areEquals(Object item1, Object item2) {
		if (item1 == null && item2 == null) {
			return true;
		} else if (item1 == null) {
			return false;
		} else {
			return item1.equals(item2);
		}
	}

	public Object getSuggestions(Object input) throws ClientException {
		if (areEquals(cachedMailSuggestionMaxSearchResults, mailSuggestionMaxSearchResults)
				&& areEquals(cachedInput, input)) {
			return cachedSuggestions;
		}

		Set<DocumentModel> groups = getMailSuggestions(input);

		int groupSize = groups.size();
		int totalSize = groupSize;

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(totalSize);
		Map<String, Object> entry = new HashMap<String, Object>();
		for (DocumentModel group : groups) {
			entry.clear();
			final String description = (String) group.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
					EMAIL_PROPERTY);
			entry.put(ENTRY_KEY_NAME, description);
			String username = (String) group.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA, "username");
			entry.put(ID_KEY_NAME, username);
			String firstName = (String) group.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA,
					FIRST_NAME_PROPERTY);
			entry.put(FIRST_NAME_PROPERTY, firstName);
			String lastName = (String) group.getProperty(STSchemaConstant.ORGANIGRAMME_USER_SCHEMA, LAST_NAME_PROPERTY);
			entry.put(LAST_NAME_PROPERTY, lastName);
			result.add(entry);
		}

		cachedInput = input;
		cachedMailSuggestionMaxSearchResults = mailSuggestionMaxSearchResults;
		cachedSuggestions = result;

		return result;
	}

	protected DocumentModel getMailModel(String profileName) throws ClientException {
		if (profileName == null) {
			return null;
		}
		Session session = null;
		try {
			session = getLDAPDirectory(STConstant.ORGANIGRAMME_USER_DIR).getSession();
			// retourne le mailDocument
			return session.getEntry(profileName);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void addMailToList(ActionEvent event) {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}
		UIComponent base = component;
		UIEditableList list = ComponentUtils.getComponent(base, suggestionSelectionListId, UIEditableList.class);
		UIInput input = ComponentUtils.getComponent(base, userSuggestionMessageId, UIInput.class);

		if (list != null && input != null && input.getValue() != null && !((String) input.getValue()).isEmpty()) {
			// add selected value to the list
			list.addValue(input.getValue());
		}
		if (input != null) {
			input.setValue("");
		}
	}
}
