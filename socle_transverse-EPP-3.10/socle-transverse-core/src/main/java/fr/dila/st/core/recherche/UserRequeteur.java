package fr.dila.st.core.recherche;

import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TreeMap;

import javax.naming.LimitExceededException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.ecm.core.utils.SIDGenerator;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.DirectoryFieldMapper;
import org.nuxeo.ecm.directory.EntryAdaptor;
import org.nuxeo.ecm.directory.Reference;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPReference;
import org.nuxeo.ecm.directory.ldap.LDAPSession;
import org.nuxeo.ecm.directory.ldap.LDAPSubstringMatchType;
import org.nuxeo.ecm.directory.ldap.LDAPTreeReference;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.UserManagerImpl;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.CollectionUtil;
import fr.dila.st.core.util.DateUtil;

/**
 * Le requêteur pour memoriser les requêtes sur les utilisateurs
 * 
 * @author asatre
 * 
 */

public class UserRequeteur {

	protected static final String		MISSING_ID_LOWER_CASE	= "lower";
	protected static final String		MISSING_ID_UPPER_CASE	= "upper";

	public static final String			QUERY_AND				= "ET";
	public static final String			QUERY_EQUAL				= "=";
	public static final String			QUERY_SUP				= ">=";
	public static final String			QUERY_INF				= "<=";

	public static final String			ID_USER_POSTE			= "user.poste";
	public static final String			ID_USER_DIRECTION		= "user.direction";
	public static final String			ID_USER_MINISTERE		= "user.ministere";
	public static final String			ID_LAST_NAME			= "lastName";
	public static final String			ID_FIRST_NAME			= "firstName";
	public static final String			ID_USERNAME				= "username";
	public static final String			ID_DATE_DEBUT			= "dateDebut";
	public static final String			ID_DATE_FIN				= "dateFin";
	public static final String			ID_DATE_DEBUT_MIN		= "dateDebut_min";
	public static final String			ID_DATE_DEBUT_MAX		= "dateDebut_max";
	public static final String			ID_DATE_FIN_MIN			= "dateFin_min";
	public static final String			ID_DATE_FIN_MAX			= "dateFin_max";
	public static final String			ID_USER_FUNCTION		= "employeeType";
	public static final String			ID_USER_PROFILS			= "user.profils";
	public static final String			ID_EMAIL				= "mail";
	public static final String			ID_POSTAL_ADDRESS		= "postalAddress";
	public static final String			ID_POSTAL_CODE			= "postalCode";
	public static final String			ID_TELEPHONE			= "telephoneNumber";
	public static final String			ID_LOCALITY				= "l";

	protected static final String		VALUE_USER_POSTE		= "Poste";
	protected static final String		VALUE_USER_DIRECTION	= "Direction de rattachement";
	protected static final String		VALUE_USER_MINISTERE	= "Ministère de rattachement";
	protected static final String		VALUE_LAST_NAME			= "Nom";
	protected static final String		VALUE_FIRST_NAME		= "Prénom";
	protected static final String		VALUE_USERNAME			= "Identifiant";
	protected static final String		VALUE_DATE_DEBUT		= "Date début";
	protected static final String		VALUE_DATE_FIN			= "Date fin";
	protected static final String		VALUE_DATE_DEBUT_MIN	= "Date début";
	protected static final String		VALUE_DATE_DEBUT_MAX	= "Date début";
	protected static final String		VALUE_DATE_FIN_MIN		= "Date fin";
	protected static final String		VALUE_DATE_FIN_MAX		= "Date fin";
	protected static final String		VALUE_USER_FUNCTION		= "Fonction";
	protected static final String		VALUE_USER_PROFILS		= "Profil";
	protected static final String		VALUE_EMAIL				= "mèl";
	protected static final String		VALUE_POSTAL_ADDRESS	= "Adresse Postal";
	protected static final String		VALUE_POSTAL_CODE		= "Code Postal";
	protected static final String		VALUE_TELEPHONE			= "Numéro de téléphone";
	protected static final String		VALUE_LOCALITY			= "Ville";

	private Map<String, String>			DictionaryQuery			= null;
	private Map<String, LDAPElement>	dataForQuery			= null;
	private DocumentModelListImpl		userProfil				= null;
	private Map<String, LDAPElement>	filterGeneral			= null;
	private List<DocumentModelList>		filters					= null;
	private DocumentModelListImpl		userMinistere			= null;
	private DocumentModelListImpl		userDirection			= null;
	private DocumentModelListImpl		userPoste				= null;
	private UserManager					userManager				= null;

	/**
	 * Permet d'initialiser les variables d'objet
	 */
	public UserRequeteur() {

		initializeDictionnary();
		dataForQuery = new LinkedHashMap<String, LDAPElement>();
		filterGeneral = new LinkedHashMap<String, LDAPElement>();
		filters = new ArrayList<DocumentModelList>();

		userManager = STServiceLocator.getUserManager();

	}

	/**
	 * recompose la requete LDAP a partir de la requete stocké en base
	 * 
	 * @param dataForQuery
	 * @param resourcesAccessor
	 * @throws ClientException
	 */
	public UserRequeteur(String query) throws ClientException {
		this();

		for (String element : query.split(QUERY_AND)) {// sépare la requete au niveau des ET
			// Le but est de séparer chaque bout de requete avec les différents test, celui qui est bon donne donc 2
			// éléments, les autres ne faisant aucune séparration
			if (!element.isEmpty()) {
				String[] elem = element.split(QUERY_SUP); // >=
				if (elem.length == 2) {
					if (ID_DATE_DEBUT.equals(elem[0])) {// on traite la date qui est cencé être le seul cas avec des >=
						filterGeneral.put(elem[0], new LDAPElement(ID_DATE_DEBUT_MIN, QUERY_SUP, elem[1]));
					} else if (ID_DATE_FIN.equals(elem[0])) {
						filterGeneral.put(elem[0], new LDAPElement(ID_DATE_FIN_MIN, QUERY_SUP, elem[1]));
					}
				} else {
					elem = element.split(QUERY_INF); // <=
					if (elem.length == 2) {
						if (ID_DATE_DEBUT.equals(elem[0])) {// idem, seul un des champs date est cencé passé ici, on
															// ignore donc les autres
							filterGeneral.put(elem[0], new LDAPElement(ID_DATE_DEBUT_MAX, QUERY_INF, elem[1]));
						} else if (ID_DATE_FIN.equals(elem[0])) {
							filterGeneral.put(elem[0], new LDAPElement(ID_DATE_FIN_MAX, QUERY_SUP, elem[1]));
						}
					} else {
						elem = element.split(QUERY_EQUAL); // =
						if (elem.length == 2) {
							if (ID_USERNAME.equals(elem[0]) || ID_FIRST_NAME.equals(elem[0])
									|| ID_LAST_NAME.equals(elem[0])) {
								filterGeneral.put(elem[0], new LDAPElement(elem[0], QUERY_EQUAL, elem[1]));
							}
							if (ID_USER_MINISTERE.equals(elem[0])) {
								setUserMinistere(elem[1]);
							} else if (ID_USER_DIRECTION.equals(elem[0])) {
								setUserDirection(elem[1]);
							} else if (ID_USER_POSTE.equals(elem[0])) {
								setUserPoste(elem[1]);
							} else if (ID_USER_FUNCTION.equals(elem[0])) {
								setUserFonction(elem[1]);
							} else {
								dataForQuery.put(elem[0], new LDAPElement(elem[0], QUERY_EQUAL, elem[1]));
							}
						}
					}
				}
			}
		}
	}

	public Boolean isEmpty() {
		return dataForQuery == null || dataForQuery.isEmpty();
	}

	private void initializeDictionnary() {
		DictionaryQuery = new LinkedHashMap<String, String>();
		DictionaryQuery.put(ID_DATE_DEBUT, VALUE_DATE_DEBUT);
		DictionaryQuery.put(ID_DATE_DEBUT_MAX, VALUE_DATE_DEBUT_MAX);
		DictionaryQuery.put(ID_DATE_DEBUT_MIN, VALUE_DATE_DEBUT_MIN);
		DictionaryQuery.put(ID_DATE_FIN, VALUE_DATE_FIN);
		DictionaryQuery.put(ID_DATE_FIN_MAX, VALUE_DATE_FIN_MAX);
		DictionaryQuery.put(ID_FIRST_NAME, VALUE_FIRST_NAME);
		DictionaryQuery.put(ID_LAST_NAME, VALUE_LAST_NAME);
		DictionaryQuery.put(ID_USER_DIRECTION, VALUE_USER_DIRECTION);
		DictionaryQuery.put(ID_USER_FUNCTION, VALUE_USER_FUNCTION);
		DictionaryQuery.put(ID_USER_MINISTERE, VALUE_USER_MINISTERE);
		DictionaryQuery.put(ID_USER_POSTE, VALUE_USER_POSTE);
		DictionaryQuery.put(ID_USER_PROFILS, VALUE_USER_PROFILS);
		DictionaryQuery.put(ID_USERNAME, VALUE_USERNAME);
	}

	public List<DocumentModel> searchUsers() throws ClientException {
		DocumentModelList users = new DocumentModelListImpl();
		if (!dataForQuery.isEmpty()) {

			LDAPElement element = filterGeneral.get(ID_DATE_FIN_MIN);

			Map<String, LDAPElement> mayfilter = new HashMap<String, LDAPElement>();
			// requete avec un nom=valeur | nom is null, pour les attributs non obligatoires (pour le moment date fin
			// min)
			if (element != null) {
				filterGeneral.remove(ID_DATE_FIN_MIN);
				mayfilter.put(ID_DATE_FIN_MIN, element);
			}
			// que les utilisateurs non supprimé
			LDAPElement elementD = new LDAPElement("deleted", QUERY_EQUAL, "FALSE");
			filterGeneral.put("deleted", elementD);
			if (filterGeneral.isEmpty()) {
				users = null;
			} else {
				users = searchUsersInLDAP(filterGeneral, null, null, mayfilter);
			}
			// on ajoute les ministères à la liste des filtres
			filters.add(userMinistere);
			// on ajoute les directions à la liste des filtres
			filters.add(userDirection);
			// on ajoute les postes à la liste des filtres
			filters.add(userPoste);
			// on ajoute les groupes à la liste des filtres
			filters.add(userProfil);

			// on effectue le trie avec les filtres
			if (!CollectionUtil.isEmpty(users) || filters.isEmpty()) {
				users = getFilteredElements(users, filters);
			}
		}
		Map<String, DocumentModel> docm = new TreeMap<String, DocumentModel>(Collator.getInstance());
		for (DocumentModel mod : users) {
			STUser user = mod.getAdapter(STUser.class);
			// the name variable allow to sort the map based on the first name, then the alst name and finaly on the
			// UID.
			// the rightpad allow to complete up to 25 char allowing to sort avoiding the "empty name" case
			String name = StringUtils.rightPad(user.getLastName(), 25, ' ')
					+ StringUtils.rightPad(user.getFirstName(), 25, ' ')
					+ StringUtils.rightPad(user.getUsername(), 25, ' ');

			docm.put(name, mod);
		}
		return new ArrayList<DocumentModel>(docm.values());
	}

	public DocumentModelList searchUsers(String query, List<String> filterArgs, Map<String, String> orderBy)
			throws ClientException {
		DirectoryService dirService = Framework.getLocalService(DirectoryService.class);

		LDAPSession userDir = null;
		DocumentModelList entries = null;
		try {
			userDir = (LDAPSession) dirService.open(userManager.getUserDirectoryName());
			LDAPDirectory directory = (LDAPDirectory) userDir.getDirectory();
			try {
				SearchControls scts = directory.getSearchControls();
				NamingEnumeration<SearchResult> results;
				results = userDir.getContext().search(directory.getConfig().getSearchBaseDn(), query,
						filterArgs.toArray(), scts);
				entries = ldapResultsToDocumentModels(userDir, results, false);
				if (orderBy != null && !orderBy.isEmpty()) {
					// sort: cannot sort before virtual users are added
					Collections.sort(entries, new DocumentModelComparator(userManager.getUserSchemaName(), orderBy));
				}
			} catch (NamingException e) {
				throw new DirectoryException("executeQuery failed", e);
			}

			return entries;
		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}

	/**
	 * retourne la dataForQuery traduite en language compréhensible
	 * 
	 * @return
	 */
	public String getTranslatedQuery() {
		StringBuilder builder = new StringBuilder();
		if (dataForQuery != null && !dataForQuery.isEmpty()) {
			String and = " " + QUERY_AND + " ";

			int index = 0;
			Set<String> keys = dataForQuery.keySet();
			for (String key : keys) {
				LDAPElement ldapElement = dataForQuery.get(key);
				builder.append(DictionaryQuery.get(key));
				builder.append(" " + ldapElement.getOperator() + " ");
				builder.append(ldapElement.getRealValue());
				if (++index < keys.size()) {
					builder.append(and);
				}
			}
			return builder.toString();
		}
		return builder.toString();
	}

	/**
	 * Retourne la requete dont le séparateur est précisé.
	 * 
	 * @param querySeparator
	 * @return
	 */
	public String getQuery(String querySeparator) {
		StringBuilder builder = new StringBuilder();
		if (dataForQuery != null && !dataForQuery.isEmpty()) {
			String and = " " + querySeparator + " ";

			for (String key : dataForQuery.keySet()) {
				builder.append(key);
				LDAPElement ldapElement = dataForQuery.get(key);
				builder.append(ldapElement.getOperator());
				builder.append(ldapElement.getValue());
				builder.append(and);
			}
			return builder.substring(0, builder.length() - and.length());
		}
		return builder.toString();
	}

	/**
	 * Set a filter parameter with the given LDAPElement with the given name
	 * 
	 * @param element
	 */
	public void setFilterValue(String name, LDAPElement element) {
		if (element != null && !StringUtils.isEmpty(name)) {
			filterGeneral.put(name, element);
			dataForQuery.put(name, element);
		}
	}

	/**
	 * Set a filter parameter with the given LDAPElement
	 * 
	 * @param element
	 */
	public void setFilterValue(LDAPElement element) {
		if (element != null) {
			setFilterValue(element.getName(), element);
		}
	}

	/**
	 * Set a filter parameter with the given data.
	 * 
	 * @param name
	 * @param operator
	 * @param value
	 */
	public void setFilterValue(String name, String operator, String value) {
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(operator) && !StringUtils.isEmpty(value)) {
			setFilterValue(new LDAPElement(name, operator, value));
		}
	}

	/**
	 * Set a filter parameter with the given data.<br/>
	 * Without precision, the default operator used is "="
	 * 
	 * @param name
	 * @param operator
	 * @param value
	 */
	public void setFilterValue(String name, String value) {
		if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(name)) {
			setFilterValue(new LDAPElement(name, QUERY_EQUAL, value));
		}
	}

	/**
	 * Set a filter parameter based on the given date.
	 * 
	 * @param name
	 * @param operator
	 * @param date
	 */
	public void setFilterValue(String name, String operator, Date date) {
		if (date != null && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(operator)) {
			SimpleDateFormat dateFormat = DateUtil.simpleDateFormat("yyyyMMddHHmmss'Z'");
			dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
			String dateValue = dateFormat.format(date);
			setFilterValue(name, new LDAPElement(name, operator, dateValue));
		}
	}

	/**
	 * Try to get the filter element corresponding to the given name
	 * 
	 * @param element
	 * @return the LDAP filter if found, null otherwise.
	 */
	public List<LDAPElement> getFilterValue(String element) {
		if (element != null && !element.isEmpty()) {
			List<LDAPElement> returnList = new ArrayList<LDAPElement>();
			for (String key : dataForQuery.keySet()) {
				if (key.equals(element)) {
					returnList.add(dataForQuery.get(key));
				}
			}
		}
		return null;
	}

	public DocumentModelListImpl getUserProfil() {
		return userProfil;
	}

	public void setUserFonction(String fonction) {

		if (!StringUtils.isEmpty(fonction)) {
			LDAPElement element = new LDAPElement(ID_USER_FUNCTION, "=", fonction);
			setFilterValue(element);
		}
	}

	/**
	 * Load all the users contained in the specified profil, then adding it to the data of the query for later use AND
	 * to the filters for the effective query
	 * 
	 * @param profil
	 * @throws ClientException
	 */
	public void setUserProfil(String currentProfil) throws ClientException {

		if (userProfil == null) {
			userProfil = new DocumentModelListImpl();
		}
		if (!StringUtils.isEmpty(currentProfil)) {
			Set<String> baseFunctions = new HashSet<String>();
			DocumentModelListImpl tempList = new DocumentModelListImpl();
			UserManager userManager = STServiceLocator.getUserManager();
			baseFunctions.addAll(userManager.getUsersInGroup(currentProfil));
			for (String userName : baseFunctions) {
				DocumentModel userDoc = userManager.getUserModel(userName);
				if (userDoc != null) {
					tempList.add(userDoc);
				}
			}
			if (userProfil.isEmpty()) {
				userProfil = tempList;
			} else {
				userProfil.addAll(tempList);
			}
			dataForQuery.put(ID_USER_PROFILS, new LDAPElement(ID_USER_PROFILS, "=", currentProfil));
		}
	}

	/**
	 * Load all the users in all the specified profils.
	 * 
	 * @param profil
	 * @throws ClientException
	 */
	public void setUserProfil(List<String> profils) throws ClientException {
		for (String profil : profils) {
			setUserProfil(profil);
		}
	}

	/**
	 * Load all the users contained in the specified minister, then adding it to the data of the query for later use AND
	 * to the filters for the effectiv query
	 * 
	 * @param ministere
	 * @throws ClientException
	 */
	public void setUserMinistere(String ministere) throws ClientException {
		if (userMinistere == null) {
			userMinistere = new DocumentModelListImpl();
		}
		if (!StringUtils.isEmpty(ministere)) {
			STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
			List<STUser> stUserList = ministeresService.getUserFromMinistere(ministere);

			for (STUser user : stUserList) {
				DocumentModel doc = user.getDocument();
				if (!userMinistere.contains(doc)) {
					userMinistere.add(doc);
				}
			}
			LDAPElement element = new LDAPElement(ID_USER_MINISTERE, QUERY_EQUAL, ministere, ministeresService
					.getEntiteNode(ministere).getLabel());
			dataForQuery.put(ID_USER_MINISTERE, element);
		}
	}

	/**
	 * Load all the users in all the specified ministers.
	 * 
	 * @param ministeres
	 * @throws ClientException
	 */
	public void setUserMinistere(List<String> ministeres) throws ClientException {
		for (String ministere : ministeres) {
			setUserMinistere(ministere);
		}
	}

	/**
	 * Load all the users contained in the specified Directions, then adding it to the data of the query for later use
	 * AND to the filters for the effectiv query
	 * 
	 * @param direction
	 * @throws ClientException
	 */
	public void setUserDirection(String direction) throws ClientException {
		if (userDirection == null) {
			userDirection = new DocumentModelListImpl();
		}
		if (!StringUtils.isEmpty(direction)) {
			STUsAndDirectionService usService = STServiceLocator.getSTUsAndDirectionService();
			List<STUser> stUserList = usService.getUserFromUniteStructurelle(direction);

			for (STUser user : stUserList) {
				userDirection.add(user.getDocument());
			}
			LDAPElement element = new LDAPElement(ID_USER_DIRECTION, QUERY_EQUAL, direction, usService
					.getUniteStructurelleNode(direction).getLabel());
			dataForQuery.put(ID_USER_DIRECTION, element);
		}
	}

	/**
	 * Load all the users in all the specified ministers.
	 * 
	 * @param ministeres
	 * @throws ClientException
	 */
	public void setUserDirection(List<String> directions) throws ClientException {
		for (String direction : directions) {
			setUserDirection(direction);
		}
	}

	/**
	 * Load all the users contained in the specified Postes, then adding it to the data of the query for later use AND
	 * to the filters for the effectiv query
	 * 
	 * @param poste
	 * @throws ClientException
	 */
	public void setUserPoste(String poste) throws ClientException {

		if (userPoste == null) {
			userPoste = new DocumentModelListImpl();
		}
		if (!StringUtils.isEmpty(poste)) {
			PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(poste);
			for (STUser user : (posteNode).getUserList()) {
				userPoste.add(user.getDocument());
			}

			LDAPElement element = new LDAPElement(ID_USER_POSTE, QUERY_EQUAL, poste, posteNode.getLabel());
			dataForQuery.put(ID_USER_POSTE, element);
		}
	}

	/**
	 * Load all the users in all the specified ministers.
	 * 
	 * @param ministeres
	 * @throws ClientException
	 */
	public void setUserPoste(List<String> postes) throws ClientException {
		for (String poste : postes) {
			setUserPoste(poste);
		}
	}

	public String get(String key) {
		LDAPElement element = dataForQuery.get(key);
		if (element != null) {
			return element.getValue();
		}
		return null;
	}

	/**
	 * Cette fonction à pour role de filtrer les éléments des listes.<br/>
	 * Si une list est null, c'est que le filter n'est pas défini<br/>
	 * Si une liste est vide, c'est que le filter est défini mais n'a pas de user<br/>
	 * Si une liste est pleine, c'est que le filter est défini et a des users.
	 * 
	 * @author FLT
	 * @param elementsList
	 *            La liste des éléments à filtrer
	 * @param list2
	 *            La liste des éléments filtrans
	 * @return une liste d'élément
	 */
	private DocumentModelList getFilteredElements(DocumentModelList elementsList, DocumentModelList filter) {
		if (elementsList == null || filter == null) {// une (ou deux) des listes est null on retourne la liste non vide
														// (si applicable) car ça veux dire que le filter n'est pas
														// défini
			if (elementsList != null) {
				return elementsList;// si elementList est définie alors on la retourne
			} else {
				return filter;// si on arrive ici c'est que filter n'est pas null
			}

		} else {// les deux listes ne sont pas null
			if (elementsList.isEmpty() || filter.isEmpty()) {// si les deux listes de données sont vides , alors on
				return new DocumentModelListImpl(); // retourne une liste vide, car si le filtre n'est pas
													// null c'est quon l'a demandé mais qu'il n'y a pas de
													// resultat
			} else {// si on arrive ici c'est que les deux listes sont remplies, on retourne donc l'intersection des 2
				elementsList.retainAll(filter);
				return elementsList;
			}
		}
	}

	/**
	 * @author FLT Cette fonction à pour but de filtrer les léments de la première liste en leurs appliquant plusieurs
	 *         fitlres.
	 * @param elementsList
	 * @param filters
	 * @return
	 */
	private DocumentModelList getFilteredElements(DocumentModelList elementsList, List<DocumentModelList> filters) {
		DocumentModelList returnValue = elementsList;
		for (DocumentModelList filter : filters) {// on loop pour appliquer tous les filtres demandés
			returnValue = getFilteredElements(returnValue, filter);
		}

		return returnValue;
	}

	protected DocumentModelList searchUsersInLDAP(Map<String, LDAPElement> filter, Set<String> fulltext,
			Map<String, String> orderBy, Map<String, LDAPElement> mayFilter) throws ClientException {
		DirectoryService dirService = Framework.getLocalService(DirectoryService.class);

		Session userDir = null;
		try {
			userDir = dirService.open(userManager.getUserDirectoryName());

			filter = removeVirtualFilters(filter);
			// we get the entries corresponding to the basics filters setted previously
			DocumentModelList entries = executeQuery((LDAPSession) userDir, filter, fulltext, false, null, mayFilter);

			if (orderBy != null && !orderBy.isEmpty()) {
				// sort: cannot sort before virtual users are added
				Collections.sort(entries, new DocumentModelComparator(userManager.getUserSchemaName(), orderBy));
			}
			return entries;
		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}

	protected Map<String, LDAPElement> removeVirtualFilters(Map<String, LDAPElement> filter) {
		if (filter == null) {
			return null;
		}
		List<String> keys = new ArrayList<String>(filter.keySet());
		for (String key : keys) {
			if (key.startsWith(UserManagerImpl.VIRTUAL_FIELD_FILTER_PREFIX)) {
				filter.remove(key);
			}
		}

		return filter;
	}

	/**
	 * Build then execute the query based on the specified filter.
	 * 
	 * @param userDir
	 * @param filter
	 * @param fulltext
	 * @param fetchReferences
	 * @param orderBy
	 * @param mayFilter
	 * @return
	 * @throws ClientException
	 */
	private DocumentModelList executeQuery(LDAPSession userDir, Map<String, LDAPElement> filter, Set<String> fulltext,
			boolean fetchReferences, Map<String, String> orderBy, Map<String, LDAPElement> mayFilter)
			throws ClientException {
		try {
			// building the dataForQuery using filterExpr / filterArgs to
			// escape special characters and to fulltext search only on
			// the explicitly specified fields
			List<String> filters = new ArrayList<String>();
			List<String> filterArgs = new ArrayList<String>();

			LDAPDirectory directory = (LDAPDirectory) userDir.getDirectory();

			if (fulltext == null) {
				fulltext = Collections.emptySet();
			}

			int index = 0;
			for (Entry<String, LDAPElement> entry : filter.entrySet()) {

				LDAPElement lDAPElement = entry.getValue();

				String fieldName = lDAPElement.getName();

				if (directory.isReference(fieldName)) {
					continue;
				}

				String backendFieldName = directory.getFieldMapper().getBackendField(fieldName);
				Boolean alreadyAddedToFilter = false;

				StringBuilder currentFilter = new StringBuilder();
				currentFilter.append("(");
				if (lDAPElement.getValue() == null) {
					currentFilter.append("!(" + backendFieldName + lDAPElement.getOperator() + "*)");
				} else if ("".equals(lDAPElement.getValue())) {
					if (fulltext.contains(fieldName)) {
						currentFilter.append(backendFieldName + lDAPElement.getOperator() + "*");
					} else {
						currentFilter.append("!(" + backendFieldName + lDAPElement.getOperator() + "*)");
					}
				} else {
					currentFilter.append(backendFieldName + lDAPElement.getOperator());
					if (fulltext.contains(fieldName)) {
						if ("subinitial".equals(LDAPSubstringMatchType.SUBFINAL)) {
							currentFilter.append("*{" + index + "}");
						} else if ("subinitial".equals(LDAPSubstringMatchType.SUBANY)) {
							currentFilter.append("*{" + index + "}*");
						} else {
							// default behavior: subinitial
							currentFilter.append("{" + index + "}*");
						}
					} else {
						boolean endStar = false;

						String fieldV = lDAPElement.getValue().toString();
						fieldV = fieldV.trim();

						if (fieldV.startsWith("*")) {
							fieldV = fieldV.substring(1);
							currentFilter.append("*");
						}

						if (fieldV.endsWith("*")) {
							fieldV = fieldV.substring(0, fieldV.length() - 1);
							endStar = true;
						}

						String[] splitField = fieldV.split("\\*");

						int lastValue = splitField.length;
						for (String part : splitField) {
							currentFilter.append("{" + index + "}");
							filterArgs.add(part);
							alreadyAddedToFilter = true;
							lastValue--;
							if (lastValue > 0) {
								currentFilter.append("*");
								index++;
							}
						}

						if (endStar) {
							currentFilter.append("*");
						}
					}
				}

				currentFilter.append(")");
				filters.add(currentFilter.toString());
				if (lDAPElement.getValue() != null && !"".equals(lDAPElement.getValue()) && !alreadyAddedToFilter) {
					filterArgs.add(lDAPElement.getValue().toString());
				}
				index++;
			}

			if (mayFilter != null) {
				for (Entry<String, LDAPElement> entry : mayFilter.entrySet()) {
					LDAPElement lDAPElement = entry.getValue();
					StringBuilder currentFilter = new StringBuilder();
					currentFilter.append("(|");
					currentFilter.append("(");

					String fieldName = lDAPElement.getName();
					String backendFieldName = directory.getFieldMapper().getBackendField(fieldName);

					currentFilter.append(backendFieldName + lDAPElement.getOperator());
					currentFilter.append("{" + index + "}");

					currentFilter.append(")");
					currentFilter.append("(!(");
					currentFilter.append(backendFieldName + "=*)))");

					filters.add(currentFilter.toString());
					if (lDAPElement.getValue() != null && !"".equals(lDAPElement.getValue())) {
						// XXX: what kind of Objects can we get here? Is toString()
						// enough?
						filterArgs.add(lDAPElement.getValue().toString());
					}
					index++;
				}
			}
			String filterExpr = "(&" + directory.getBaseFilter() + StringUtils.join(filters.toArray()) + ')';
			SearchControls scts = directory.getSearchControls();

			try {
				NamingEnumeration<SearchResult> results = userDir.getContext().search(
						directory.getConfig().getSearchBaseDn(), filterExpr, filterArgs.toArray(), scts);
				DocumentModelList entries = ldapResultsToDocumentModels(userDir, results, fetchReferences);

				if (orderBy != null && !orderBy.isEmpty()) {
					directory.orderEntries(entries, orderBy);
				}
				return entries;
			} catch (NameNotFoundException nnfe) {
				// sometimes ActiveDirectory have some dataForQuery fail with: LDAP:
				// error code 32 - 0000208D: NameErr: DSID-031522C9, problem
				// 2001 (NO_OBJECT).
				// To keep the application usable return no results instead of
				// crashing but log the error so that the AD admin
				// can fix the issue.
				return new DocumentModelListImpl();
			}
		} catch (LimitExceededException e) {
			throw new org.nuxeo.ecm.directory.SizeLimitExceededException(e);
		} catch (NamingException e) {
			throw new DirectoryException("executeQuery failed", e);
		}
	}

	/**
	 * Transform the raw LDAP data into more useable DocumentModel.
	 * 
	 * @param session
	 * @param results
	 * @param fetchReferences
	 * @return
	 * @throws NamingException
	 * @throws ClientException
	 */
	protected DocumentModelList ldapResultsToDocumentModels(LDAPSession session,
			NamingEnumeration<SearchResult> results, boolean fetchReferences) throws NamingException, ClientException {
		DocumentModelList list = new DocumentModelListImpl();
		while (results.hasMore()) {
			SearchResult result = results.next();
			DocumentModel entry = ldapResultToDocumentModel(session, result, null, fetchReferences);
			if (entry != null) {
				list.add(entry);
			}
		}
		return list;
	}

	/**
	 * Transform one raw LDAP data into a Nuxeo DocumentModel
	 * 
	 * @param session
	 * @param result
	 * @param entryId
	 * @param fetchReferences
	 * @return
	 * @throws NamingException
	 * @throws ClientException
	 */
	protected DocumentModel ldapResultToDocumentModel(LDAPSession session, SearchResult result, String entryId,
			boolean fetchReferences) throws NamingException, ClientException {
		Attributes attributes = result.getAttributes();
		String passwordFieldId = session.getPasswordField();
		Map<String, Object> fieldMap = new HashMap<String, Object>();

		LDAPDirectory directory = (LDAPDirectory) session.getDirectory();
		DirectoryFieldMapper fieldMapper = directory.getFieldMapper();
		String idAttribute = fieldMapper.getBackendField(((LDAPDirectory) session.getDirectory()).getConfig()
				.getIdField());

		Attribute attribute = attributes.get(idAttribute);
		// NXP-2461: check that id field is filled + NXP-2730: make sure that
		// entry id is the one returned from LDAP
		if (attribute != null) {
			Object entry = attribute.get();
			if (entry != null) {
				entryId = entry.toString();
			}
		}

		if (entryId == null) {
			// don't bother
			return null;
		}
		for (String fieldName : directory.getSchemaFieldMap().keySet()) {
			Reference reference = directory.getReference(fieldName);
			if (reference == null) {
				// manage directly stored fields
				String attributeId = directory.getFieldMapper().getBackendField(fieldName);
				if (attributeId.equals(LDAPDirectory.DN_SPECIAL_ATTRIBUTE_KEY)) {
					// this is the special DN readonly attribute
					try {
						fieldMap.put(fieldName, result.getNameInNamespace());
					} catch (UnsupportedOperationException e) {
						// ignore ApacheDS partial implementation when running
						// in embedded mode
					}
				} else {
					// this is a regular attribute
					attribute = attributes.get(attributeId);
					if (fieldName.equals(passwordFieldId)) {
						// do not try to fetch the password attribute
						continue;
					} else {
						fieldMap.put(fieldName,
								getFieldValue(directory, attribute, fieldName, entryId, fetchReferences));
					}
				}
			} else {
				if (fetchReferences) {
					// reference resolution
					List<String> referencedIds;
					if (reference instanceof LDAPReference) {
						// optim: use the current LDAPSession directly to
						// provide the LDAP reference with the needed backend
						// entries
						LDAPReference ldapReference = (LDAPReference) reference;
						referencedIds = ldapReference.getLdapTargetIds(attributes);
					} else if (reference instanceof LDAPTreeReference) {
						// TODO: optimize using the current LDAPSession directly
						// to provide the LDAP reference with the needed backend
						// entries (needs to implement getLdapTargetIds)
						LDAPTreeReference ldapReference = (LDAPTreeReference) reference;
						referencedIds = ldapReference.getTargetIdsForSource(entryId);
					} else {
						try {
							referencedIds = reference.getTargetIdsForSource(entryId);
						} catch (ClientException e) {
							throw new DirectoryException(e);
						}
					}
					fieldMap.put(fieldName, referencedIds);
				}

			}
		}
		// check if the idAttribute was returned from the search. If not
		// set it anyway.
		String fieldId = directory.getFieldMapper().getDirectoryField(idAttribute);
		Object obj = fieldMap.get(fieldId);
		if (obj == null) {
			fieldMap.put(fieldId, changeEntryIdCase(directory, entryId));
		}
		return fieldMapToDocumentModel(directory, fieldMap);
	}

	protected String changeEntryIdCase(LDAPDirectory directory, String ident) {
		String idFieldCase = directory.getConfig().missingIdFieldCase;
		if (MISSING_ID_LOWER_CASE.equals(idFieldCase)) {
			return ident.toLowerCase();
		} else if (MISSING_ID_UPPER_CASE.equals(idFieldCase)) {
			return ident.toUpperCase();
		}
		// returns the unchanged id
		return ident;
	}

	@SuppressWarnings("unchecked")
	protected Object getFieldValue(LDAPDirectory directory, Attribute attribute, String fieldName, String entryId,
			boolean fetchReferences) throws DirectoryException {

		Field field = directory.getSchemaFieldMap().get(fieldName);
		Type type = field.getType();
		Object defaultValue = field.getDefaultValue();
		String typeName = type.getName();
		if (attribute == null) {
			return defaultValue;
		}
		Object value;
		try {
			value = attribute.get();
		} catch (NamingException e) {
			throw new DirectoryException("Could not fetch value for " + attribute, e);
		}
		if (value == null) {
			return defaultValue;
		}
		String trimmedValue = value.toString().trim();
		if ("string".equals(typeName)) {
			return trimmedValue;
		} else if ("integer".equals(typeName) || "long".equals(typeName)) {
			if ("".equals(trimmedValue)) {
				return defaultValue;
			}
			try {
				return Long.valueOf(trimmedValue);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		} else if (type.isListType()) {
			List<String> parsedItems = new LinkedList<String>();
			NamingEnumeration<Object> values = null;
			try {
				values = (NamingEnumeration<Object>) attribute.getAll();
				while (values.hasMore()) {
					parsedItems.add(values.next().toString().trim());
				}
				return parsedItems;
			} catch (NamingException e) {
				return defaultValue;
			}
		} else if ("date".equals(typeName)) {
			if ("".equals(trimmedValue)) {
				return defaultValue;
			}
			try {
				SimpleDateFormat dateFormat = DateUtil.simpleDateFormat("yyyyMMddHHmmss'Z'");
				dateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
				Date date = dateFormat.parse(trimmedValue);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				return cal;
			} catch (ParseException e) {
				return defaultValue;
			}
		} else {
			throw new DirectoryException("Field type not supported in directories: " + typeName);
		}
	}

	protected DocumentModel fieldMapToDocumentModel(LDAPDirectory directory, Map<String, Object> fieldMap)
			throws ClientException {
		String ident = String.valueOf(fieldMap.get(directory.getConfig().getIdField()));
		String sid = String.valueOf(SIDGenerator.next());
		try {
			DocumentModel docModel = BaseSession.createEntryModel(sid, userManager.getUserSchemaName(), ident,
					fieldMap, directory.getConfig().getReadOnly());
			EntryAdaptor adaptor = directory.getConfig().getEntryAdaptor();
			if (adaptor != null) {
				docModel = adaptor.adapt(directory, docModel);
			}
			return docModel;
		} catch (PropertyException e) {
			return null;
		}
	}

}