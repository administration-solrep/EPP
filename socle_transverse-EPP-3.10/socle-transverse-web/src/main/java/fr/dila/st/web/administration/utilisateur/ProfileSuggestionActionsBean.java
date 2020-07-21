package fr.dila.st.web.administration.utilisateur;

import static org.jboss.seam.ScopeType.PAGE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.faces.convert.Converter;

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
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryFactory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryProxy;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.ecm.webapp.security.UserDisplayConverter;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Bean JSF pour le composant de suggestion des fonctions unitaires.
 * 
 * @author jtremeaux
 */
@Name("profileSuggestionActions")
@SerializedConcurrentAccess
@Scope(PAGE)
public class ProfileSuggestionActionsBean implements Serializable {

	private static final long				serialVersionUID		= 1L;

	private static final Log				LOG						= LogFactory
																			.getLog(ProfileSuggestionActionsBean.class);

	public static final String				GROUP_TYPE				= "GROUP_TYPE";

	public static final String				TYPE_KEY_NAME			= "type";

	public static final String				PREFIXED_ID_KEY_NAME	= "prefixed_id";

	public static final String				ID_KEY_NAME				= "id";

	public static final String				ENTRY_KEY_NAME			= "entry";

	@In(create = true)
	protected transient UserManager			userManager;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	@RequestParameter
	protected String						userSuggestionSearchType;

	protected String						cachedUserSuggestionSearchType;

	@RequestParameter
	protected Integer						userSuggestionMaxSearchResults;

	@RequestParameter
	protected Boolean						hideVirtualGroups;

	protected Integer						cachedUserSuggestionMaxSearchResults;

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
		if (userSuggestionMessageId != null) {
			facesMessages.addToControl(userSuggestionMessageId, StatusMessage.Severity.ERROR, resourcesAccessor
					.getMessages().get("label.security.searchOverFlow"));
		} else {
			LOG.error("Search overflow");
		}
	}

	private static LDAPDirectory getLDAPDirectory(String name) throws DirectoryException {
		LDAPDirectoryFactory factory = STServiceLocator.getLDAPDirectoryFactory();
		return ((LDAPDirectoryProxy) factory.getDirectory(name)).getDirectory();
	}

	/**
	 * Recherche des fonctions.
	 * 
	 * @param input
	 *            Chaine de recherche
	 * @return Liste d'entrées du LDAP correspondant à des fonctions
	 * @throws ClientException
	 *             ClientException
	 */
	public List<DocumentModel> getGroupsSuggestions(Object input) throws ClientException {
		Session session = getLDAPDirectory(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR).getSession();
		try {
			String pattern = (String) input;

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			// filter.put("cn", "Administrateur SGG");
			if (pattern != null && !"".equals(pattern)) {
				filter.put("description", pattern);
			}
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("description", DocumentModelComparator.ORDER_ASC);

			return session.query(filter, new HashSet<String>(filter.keySet()), orderBy, false);
		} catch (SizeLimitExceededException e) {
			addSearchOverflowMessage();
			return Collections.emptyList();
		} catch (Exception e) {
			throw new ClientException("error searching for functions", e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
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
		if (areEquals(cachedUserSuggestionSearchType, userSuggestionSearchType)
				&& areEquals(cachedUserSuggestionMaxSearchResults, userSuggestionMaxSearchResults)
				&& areEquals(cachedInput, input)) {
			return cachedSuggestions;
		}

		List<DocumentModel> groups = Collections.emptyList();
		if (GROUP_TYPE.equals(userSuggestionSearchType) || StringUtils.isEmpty(userSuggestionSearchType)) {
			groups = getGroupsSuggestions(input);
		}

		int groupSize = groups.size();
		int totalSize = groupSize;

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(totalSize);

		for (DocumentModel group : groups) {
			Map<String, Object> entry = new HashMap<String, Object>();
			entry.put(TYPE_KEY_NAME, GROUP_TYPE);
			// entry.put(ENTRY_KEY_NAME, group);
			final String description = (String) group.getProperty(STSchemaConstant.BASE_FUNCTION_SCHEMA,
					STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY);
			entry.put(ENTRY_KEY_NAME, description);
			String groupId = group.getId();
			entry.put(ID_KEY_NAME, groupId);
			entry.put(PREFIXED_ID_KEY_NAME, NuxeoGroup.PREFIX + groupId);
			result.add(entry);
		}

		cachedInput = input;
		cachedUserSuggestionSearchType = userSuggestionSearchType;
		cachedUserSuggestionMaxSearchResults = userSuggestionMaxSearchResults;
		cachedSuggestions = result;

		return result;
	}

	// XXX: needs optimisation
	public Map<String, Object> getPrefixedUserInfo(String id) throws ClientException {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(PREFIXED_ID_KEY_NAME, id);
		if (id != null) {
			if (id.startsWith(NuxeoPrincipal.PREFIX)) {
				String username = id.substring(NuxeoPrincipal.PREFIX.length());
				res.put(ID_KEY_NAME, username);
				res.put(ENTRY_KEY_NAME, userManager.getUserModel(username));
			} else if (id.startsWith(NuxeoGroup.PREFIX)) {
				res.put(TYPE_KEY_NAME, GROUP_TYPE);
				String groupname = id.substring(NuxeoGroup.PREFIX.length());
				res.put(ID_KEY_NAME, groupname);
				res.put(ENTRY_KEY_NAME, userManager.getGroupModel(groupname));
			} else {
				res.put(ID_KEY_NAME, id);
			}
		}
		return res;
	}

	/**
	 * Retourne un tableau associatif contenant les propriétés d'un profil.
	 * 
	 * @param id
	 *            Nom du profil (ex. "UserCreator"
	 * @return Propriétés
	 * @throws ClientException
	 *             ClientException
	 */
	public Map<String, Object> getUserInfo(String id) throws ClientException {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put(ID_KEY_NAME, id);

		res.put(PREFIXED_ID_KEY_NAME, NuxeoGroup.PREFIX + id);
		res.put(TYPE_KEY_NAME, GROUP_TYPE);
		DocumentModel functionDocument = getProfileModel(id);
		if (functionDocument != null) {
			final String description = (String) functionDocument.getProperty(STSchemaConstant.BASE_FUNCTION_SCHEMA,
					STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY);
			res.put(ENTRY_KEY_NAME, description);
		} else {
			res.put(ENTRY_KEY_NAME, id);
		}

		return res;
	}

	protected DocumentModel getProfileModel(String profileName) throws ClientException {
		if (profileName == null) {
			return null;
		}
		Session session = null;
		try {
			session = getLDAPDirectory(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR).getSession();
			final DocumentModel functionDocument = session.getEntry(profileName);
			return functionDocument;
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public Converter getUserConverter() {
		return new UserDisplayConverter();
	}
}
