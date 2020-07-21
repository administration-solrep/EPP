package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.jeton.JetonDoc;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.FlexibleQueryMaker;
import fr.dila.st.core.query.QueryUtils;

/**
 * Service de gestions de jetons. Un jeton maitre est un "panier" qui contient des jetonsDoc Un jetonDoc est un
 * conteneur de Document (un dossier, une question... ). Les utilisateurs webservices peuvent lire les documents en
 * requêtant sur le numéro de jeton. La validation de certaines étapes entraine la création d'un jeton doc.
 * 
 * @author sly
 */
public class JetonServiceImpl implements JetonService {

	protected static final long		JETON_RESULT_SIZE_DEFAULT		= 100L;

	/**
	 * constante long de creation du jetonDoc pour indiquer sa présence dans le panier
	 */
	protected static final long		NUMERO_PANIER					= -999;

	/**
	 * constante de retour d'erreur de récupération de jeton
	 */
	protected static final long		LONG_ERROR_JETON				= -100;

	/**
	 * Document racine des jetons / documents.
	 */
	protected static String			jetonDocRootDocPath;

	/**
	 * Message d'erreur si un paramètre pour créé un jeton est absent
	 */
	protected static final String	ERROR_PARAM_NULL				= "Le type, le propriétaire et l'identifiant du document ne doivent pas être null pour le jeton";

	/**
	 * select id from jetonDoc where id_jeton = ? and type_ws = ? and id_owner = ? order by created
	 */
	protected static final String	QUERY_JETON_BY_NUMBER			= "SELECT jt.ecm:uuid AS id FROM "
																			+ STConstant.JETON_DOC_TYPE
																			+ " as jt WHERE jt."
																			+ STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX
																			+ ":"
																			+ STSchemaConstant.JETON_DOCUMENT_ID_JETON_DOC
																			+ " = ?  AND jt."
																			+ STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX
																			+ ":"
																			+ STSchemaConstant.JETON_DOCUMENT_WEBSERVICE
																			+ " = ? AND jt."
																			+ STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX
																			+ ":"
																			+ STSchemaConstant.JETON_DOCUMENT_ID_OWNER
																			+ " = ? ORDER BY jt."
																			+ STSchemaConstant.JETON_DOCUMENT_SCHEMA_PREFIX
																			+ ":"
																			+ STSchemaConstant.JETON_DOC_CREATED_PROPERTY;

	// doc oracle : COALESCE returns the first non-null expr in the expression list.
	protected static final String	QUERY_JETON_MAX					= "SELECT coalesce(MAX(ID_JETON),"
																			+ LONG_ERROR_JETON
																			+ ") AS id FROM JETON_DOC WHERE ID_OWNER = ? AND TYPE_WEBSERVICE = ?";

	protected static final String	ERROR_DOC_DOESNT_EXIST_ANYMORE	= "Le jeton contenait une ou plusieurs communications de type brouillon supprimées depuis";

	/**
	 * Serial UID.
	 */
	private static final long		serialVersionUID				= -6532526050884124001L;

	/**
	 * Logger.
	 */
	private static final STLogger	LOGGER							= STLogFactory.getLog(JetonServiceImpl.class);

	/**
	 * Default constructor
	 */
	public JetonServiceImpl() {
		// do nothing
	}

	@Override
	public JetonServiceDto getDocuments(final CoreSession session, final String owner, final Long numJeton,
			final String typeWebservice) throws ClientException {
		Long numeroJeton = numJeton;
		if (numeroJeton == null) {
			numeroJeton = 0L;
		}

		// Le numéro de jeton le plus grand qui ait déjà été utilisé pour ramener des résultats
		final Long numeroJetonMax = getNumeroJetonMaxForWS(session, owner, typeWebservice);

		// *** cas du jeton qui n'existe pas ***
		// Si le numeroJeton = 0 et numeroJetonMax = -100, on est dans un cas de jeton encore jamais requêté, on n'entre
		// pas dans ce cas
		if (numeroJetonMax.equals(Long.valueOf(LONG_ERROR_JETON)) && numeroJeton.longValue() == 0) {
			final StringBuilder message = new StringBuilder();
			message.append("[JETON] - Jeton 0 pour proprietaire ").append(owner).append(" et WS ")
					.append(typeWebservice).append(" n'existe pas. Aucun document n'a été transmis pour ce ws.");
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_JETON_TEC, message.toString());
		} else if (numeroJeton.longValue() != 0 && numeroJetonMax.longValue() + 1 < numeroJeton
				|| numeroJeton.longValue() < 0) {
			// Jeton introuvable
			final StringBuilder message = new StringBuilder();
			message.append("[JETON] - Jeton numéro ").append(numeroJeton.longValue())
					.append(" introuvable pour proprietaire ").append(owner).append(" et WS ").append(typeWebservice)
					.append(" - Dernier numéro de jeton connu : ").append(numeroJetonMax.longValue());
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_JETON_TEC, message.toString());
			return null;
		}

		// *** cas du jeton qui existe ***
		// Récupère les documents correspondants
		// Plusieurs cas : soit c'est une récupération des documents qui ont déjà été récupérés -> récupération par
		// numéro jeton, nb resultat < TAILLE MAX JETON donc pas de vérif
		// Soit il faut aller récupérer les documents dans le panier et leur attribuer le nouveau numero de jeton, nb
		// resultat doit etre <= TAILLE MAX JETON, on doit vérifier
		if (numeroJetonMax.compareTo(numeroJeton) >= 0) {
			return getOldJeton(session, typeWebservice, owner, numeroJeton, numeroJetonMax);
		} else {
			return getNewJeton(session, typeWebservice, owner, numeroJeton);
		}
	}

	/**
	 * Méthode de récupération d'un jeton déjà lu
	 * 
	 * @param session
	 * @param typeWebservice
	 * @param owner
	 * @param panierIsEmpty
	 *            : vrai si le panier est vice
	 * @param numeroJeton
	 *            : le numéro de jeton demandé
	 * @param numeroJetonMax
	 *            : le numéro de jeton maximum existant
	 * @return JetonServiceDto
	 * @throws ClientException
	 */
	protected JetonServiceDto getOldJeton(final CoreSession session, String typeWebservice, String owner,
			Long numeroJeton, Long numeroJetonMax) throws ClientException {

		final JetonServiceDto result = new JetonServiceDto();
		// récupération des jetonDoc associés au numéro
		final List<DocumentModel> jetonsDocList = getJetonsByJetonNumber(session, numeroJeton, typeWebservice, owner,
				null);
		final List<DocumentModel> documentsList = new ArrayList<DocumentModel>();
		// Récupération des documents liés à chaque jeton
		for (final DocumentModel jetonDoc : jetonsDocList) {
			final JetonDoc jeton = jetonDoc.getAdapter(JetonDoc.class);
			DocumentModel document = getDocumentInJeton(session, jeton);
			if (document != null) {
				documentsList.add(document);
			}
		}

		if (!jetonsDocList.isEmpty() && documentsList.isEmpty()) {
			result.setMessageErreur(ERROR_DOC_DOESNT_EXIST_ANYMORE);
		}

		result.setJetonDocDocList(jetonsDocList);
		result.setDocumentList(documentsList);
		// On a récupéré un jeton déjà existant, on renvoi donc les informations suivantes :
		// - S'il y a d'autres jetons qui suivent (dernier envoi = false), sinon si le panier est vide dernier envoi =
		// true
		// - Numéro du jeton suivant (numeroJeton +1)
		if (numeroJetonMax.compareTo(numeroJeton) == 0 && isBasketEmpty(session, typeWebservice, owner)) {
			result.setLastSending(true);
		} else {
			result.setLastSending(false);
		}
		result.setNextJetonNumber(Long.valueOf(numeroJeton.longValue() + 1));
		return result;
	}

	/**
	 * Méthode de récupération d'un nouveau jeton : créé un nouveau jeton avec les documents associés
	 * 
	 * @param session
	 * @param typeWebservice
	 * @param owner
	 * @param numeroJeton
	 * @return
	 * @throws ClientException
	 */
	protected JetonServiceDto getNewJeton(final CoreSession session, String typeWebservice, String owner,
			Long numeroJeton) throws ClientException {

		final JetonServiceDto result = new JetonServiceDto();
		final List<DocumentModel> documentsList = new ArrayList<DocumentModel>();
		final List<DocumentModel> jetonsDocList = new ArrayList<DocumentModel>();
		final boolean lastSending = getDocumentsFromBasketJeton(session, typeWebservice, owner, jetonsDocList,
				documentsList, numeroJeton);

		// Mise à jour du retour pour l'utilisateur
		result.setLastSending(lastSending);
		result.setDocumentList(documentsList);
		result.setJetonDocDocList(jetonsDocList);
		if (documentsList.isEmpty()) {
			// Retour vide, numero de jeton est le même
			result.setNextJetonNumber(numeroJeton);
		} else {
			// Panier non vide : on incrémente le numeroJeton pour le retour
			final Long nextJeton = Long.valueOf(numeroJeton.longValue() + 1);
			result.setNextJetonNumber(nextJeton);
		}

		return result;
	}

	/**
	 * Récupère les documents provenant des jetonsDoc présents dans le panier. Met à jour la valeur du numero de jeton
	 * utilisé pour les jetonsDoc
	 * 
	 * @param session
	 *            CoreSession
	 * @param jetonsDocPanier
	 *            liste de jetonsDoc qui sont présents dans le panier
	 * @param documents
	 *            liste de documentModel contenus par les jetonsDoc. Sera utilisé pour le retour
	 * @param numeroJeton
	 *            le numéro de jeton requêté.
	 * @return boolean vrai si le panier est vide, faux s'il reste des jetonsDoc non lus dans le panier
	 * @throws ClientException
	 */
	protected boolean getDocumentsFromBasketJeton(final CoreSession session, String typeWebservice, String owner,
			final List<DocumentModel> jetonsDocList, final List<DocumentModel> documents, final Long numeroJeton)
			throws ClientException {
		final ConfigService configService = STServiceLocator.getConfigService();
		boolean lastSending = true;
		Long jetonResultSize = null;
		try {
			jetonResultSize = Long.parseLong(configService.getValue(STConfigConstants.WEBSERVICE_JETON_RESULT_SIZE));
		} catch (final Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, "Valeur " + JETON_RESULT_SIZE_DEFAULT
					+ " par défaut utilisée", exc);
			jetonResultSize = JETON_RESULT_SIZE_DEFAULT;
		}
		// On s'assure que la liste des documents est bien vide
		// Et que la liste des jetonDoc également
		documents.clear();
		jetonsDocList.clear();
		// Cas du panier vide
		// Il n'y a pas d'autres jetons qui suivent (dernier envoi = true)
		// documentsDoc retournée vide
		// *** rien à faire ***

		// On récupère au maximum un document de plus que peut en contenir le retour pour éviter de remonter toute la
		// base
		List<DocumentModel> jetonsDocsPanier = getJetonsInBasket(session, typeWebservice, owner, jetonResultSize + 1);

		// Si le panier contient des données
		if (!jetonsDocsPanier.isEmpty()) {
			lastSending = updateJetons(session, typeWebservice, owner, jetonsDocsPanier, jetonsDocList, documents, jetonResultSize, numeroJeton);
		}
		return lastSending;
	}
	
	protected boolean updateJetons(final CoreSession session, String typeWebservice, String owner,
			final List<DocumentModel> jetonsDocsPanier, final List<DocumentModel> jetonsDocList, 
			final List<DocumentModel> documents, final Long jetonResultSize, final Long numeroJeton) throws ClientException {
		boolean lastSending = true;
		// S'il y a assez de place dans un jeton pour contenir les documents
		// Ou cas spécifique du ws chercher modification dossier epg
		if (jetonsDocsPanier.size() <= jetonResultSize.intValue()) {
			// il n'y a pas d'autres jetons qui suivent (dernier envoi = true)
			for (final DocumentModel jetonDocDoc : jetonsDocsPanier) {
				setDocumentsAndJetonNumber(session, jetonDocDoc, jetonsDocList, documents, numeroJeton);
			}
		} else {
			// Les documents sont trop nombreux pour être contenus dans un seul résultat,
			// Il faut en remplir un, et annoncer qu'il y a encore des résultats en attente de lecture (dernier
			// envoi = false)
			proceedHighNumberOfJetonDoc(session, jetonsDocsPanier, jetonResultSize, jetonsDocList, documents,
					numeroJeton);
			lastSending = isBasketEmpty(session, typeWebservice, owner);
		}
		return lastSending;
	}

	/**
	 * Méthode qui gère le cas où le nombre de jetons dans le panier dépasse la valeur de résultat max définie Les
	 * jetons sont retirés de la liste une fois traités
	 * 
	 * @param session
	 * @param jetonsDocPanier
	 *            liste des jetonsDoc présents dans le panier
	 * @param jetonResultSize
	 *            taille Max de retour des résultats
	 * @param documents
	 *            liste des documentModel de dossiers à renvoyer
	 * @param numeroJeton
	 *            le numéro de jeton requêté
	 * @return vrai s'il n'y a plus de jeton à traiter, faux sinon
	 */
	protected boolean proceedHighNumberOfJetonDoc(final CoreSession session, final List<DocumentModel> jetonsDocPanier,
			final Long jetonResultSize, final List<DocumentModel> jetonsDocList, final List<DocumentModel> documents,
			final Long numeroJeton) {
		int compteurDocInJeton = 0;
		while (compteurDocInJeton < jetonResultSize.intValue() && !jetonsDocPanier.isEmpty()) {
			// Comme les jetons sont sortis de la liste après traitement, on récupère toujours le premier element
			final DocumentModel jetonDocDoc = jetonsDocPanier.get(0);
			setDocumentsAndJetonNumber(session, jetonDocDoc, jetonsDocList, documents, numeroJeton);
			jetonsDocPanier.remove(jetonDocDoc);
			++compteurDocInJeton;
		}

		return jetonsDocPanier.isEmpty();
	}

	/**
	 * Ajoute le documentModel contenu dans un jetonDoc dans une liste de DocumentModel et met à jour le numéro de jeton
	 * du jetonDoc
	 * 
	 * @param session
	 *            CoreSession
	 * @param jetonDocDoc
	 *            le documentModel du jetonDoc
	 * @param documentsList
	 *            la liste contenant les documentsModel
	 * @param numeroJeton
	 *            le numéro de jeton à mettre dans le jetonDoc à la place du numéro du panier
	 */
	protected void setDocumentsAndJetonNumber(final CoreSession session, final DocumentModel jetonDocDoc,
			final List<DocumentModel> jetonsDocList, final List<DocumentModel> documentsList, final Long numeroJeton) {
		final JetonDoc jeton = jetonDocDoc.getAdapter(JetonDoc.class);
		try {
			DocumentModel document = getDocumentInJeton(session, jeton);
			if (document == null) {
				LOGGER.warn(session, STLogEnumImpl.DEL_JETON_DOC_TEC, jetonDocDoc);
				session.removeDocument(jetonDocDoc.getRef());
				session.save();
			} else {
				setDocumentsAndJetonNumber(session, jetonDocDoc, document, jetonsDocList, documentsList, numeroJeton);
			}
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_DOCUMENT_TEC, jeton.getIdDoc(), exc);
		}
	}

	/**
	 * Ajoute le documentModel contenu dans un jetonDoc dans une liste de DocumentModel et met à jour le numéro de jeton
	 * du jetonDoc
	 * 
	 * @param session
	 *            CoreSession
	 * @param jetonDocDoc
	 *            le documentModel du jetonDoc
	 * @param documentToAdd
	 *            le documentModel à ajouter
	 * @param documentsList
	 *            la liste contenant les documentsModel
	 * @param numeroJeton
	 *            le numéro de jeton à mettre dans le jetonDoc à la place du numéro du panier
	 */
	protected void setDocumentsAndJetonNumber(final CoreSession session, final DocumentModel jetonDocDoc,
			final DocumentModel documentToAdd, final List<DocumentModel> jetonsDocList,
			final List<DocumentModel> documentsList, final Long numeroJeton) {
		final JetonDoc jeton = jetonDocDoc.getAdapter(JetonDoc.class);
		jetonsDocList.add(jetonDocDoc);
		documentsList.add(documentToAdd);
		jeton.setNumeroJeton(numeroJeton);
		try {
			session.saveDocument(jetonDocDoc);
			session.save();
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SAVE_JETON_TEC, jetonDocDoc, exc);
		}
	}

	@Override
	public Long getNumeroJetonMaxForWS(final CoreSession session, final String owner, final String typeWebservice) {
		IterableQueryResult res = null;
		try {
			res = QueryUtils.doSqlQuery(session, new String[] { FlexibleQueryMaker.COL_ID }, QUERY_JETON_MAX,
					new Object[] { owner, typeWebservice });
			final Iterator<Map<String, Serializable>> iterator = res.iterator();
			if (iterator.hasNext()) {
				final Map<String, Serializable> row = iterator.next();
				return Long.parseLong((String) row.get(FlexibleQueryMaker.COL_ID));
			}
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_EXEC_SQL, QUERY_JETON_MAX, exc);
		} finally {
			if (res != null) {
				res.close();
			}
		}
		return Long.valueOf(LONG_ERROR_JETON);
	}

	/**
	 * Crée un nouveau modèle de document JetonDoc (pas encore persisté).
	 * 
	 * @param session
	 *            Session
	 * @return Modèle de document
	 * @throws ClientException
	 */
	protected DocumentModel createBareJetonDoc(final CoreSession session) throws ClientException {
		final DocumentModel jetonDocModel = session.createDocumentModel(STConstant.JETON_DOC_TYPE);
		final JetonDoc jetonDoc = jetonDocModel.getAdapter(JetonDoc.class);
		jetonDoc.setCreated(new GregorianCalendar());
		return jetonDocModel;
	}

	/**
	 * Récupère le chemin du répertoire des jetonDoc
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	private String getJetonDocFolderPath(final CoreSession session) throws ClientException {
		if (jetonDocRootDocPath != null) {
			return jetonDocRootDocPath;
		}

		synchronized (this) {
			final StringBuilder query = new StringBuilder("SELECT j.ecm:uuid as id FROM ");
			query.append(STConstant.JETON_DOC_FOLDER_DOCUMENT_TYPE);
			query.append(" as j ");

			final List<DocumentModel> list = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session,
					STConstant.JETON_DOC_FOLDER_DOCUMENT_TYPE, query.toString(), null);
			if (list == null || list.size() <= 0) {
				throw new ClientException("Racine des jetons docs non trouvée");
			} else if (list.size() > 1) {
				throw new ClientException("Plusieurs racines de jetons docs trouvées");
			}

			final DocumentModel jetonDocRootDoc = list.get(0);
			jetonDocRootDocPath = jetonDocRootDoc.getPathAsString();
			return jetonDocRootDocPath;
		}
	}

	/**
	 * Créé un jetonDoc pour un type de ws et un propriétaire donné et pour un document et l'ajoute au panier
	 * 
	 * @param session
	 * @param typeWebservice
	 *            le type de webservice auquel appartient le jeton
	 * @param owner
	 *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
	 * @param documentId
	 *            uid du document qui appartient au jeton
	 * @throws ClientException
	 */
	protected void createJetonDoc(final CoreSession session, final String typeWebservice, final String owner,
			final String documentId, final String typeModification, final List<String> idsComplementaires)
			throws ClientException {

		if (typeWebservice == null || owner == null || documentId == null) {
			LOGGER.error(session, STLogEnumImpl.NPE_PARAM_METH_TEC, ERROR_PARAM_NULL);
		} else {
			final DocumentModel jetonDocModel = createBareJetonDoc(session);
			createJetonDocFromBareJeton(session, jetonDocModel, typeWebservice, owner, documentId, typeModification,
					idsComplementaires);

		}
	}
	
	protected void createJetonDocWithoutSave(final CoreSession session, final String typeWebservice, final String owner,
			final String documentId, final String typeModification, final List<String> idsComplementaires)
			throws ClientException {

		if (typeWebservice == null || owner == null || documentId == null) {
			LOGGER.error(session, STLogEnumImpl.NPE_PARAM_METH_TEC, ERROR_PARAM_NULL);
		} else {
			final DocumentModel jetonDocModel = createBareJetonDoc(session);
			createJetonDocFromBareJeton(session, jetonDocModel, typeWebservice, owner, documentId, typeModification,
					idsComplementaires);

		}
	}

	/**
	 * Créé un jetonDoc pour un type de ws et un propriétaire donné, et pour un document et l'ajoute au panier à partir
	 * d'un jeton non persisté
	 * 
	 * @param session
	 * @param typeWebservice
	 *            le type de webservice auquel appartient le jeton
	 * @param owner
	 *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
	 * @param documentId
	 *            uid du document qui appartient au jeton
	 * @throws ClientException
	 */
	protected void createJetonDocFromBareJeton(final CoreSession session, final DocumentModel bareJeton,
			final String typeWebservice, final String owner, final String documentId, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {

		if (typeWebservice == null || owner == null || documentId == null) {
			LOGGER.error(session, STLogEnumImpl.NPE_PARAM_METH_TEC, ERROR_PARAM_NULL);
		} else {
			// Renseigne le chemin et le nom du document
			final String parentPath = getJetonDocFolderPath(session);
			final StringBuilder jetonDocName = new StringBuilder();
			jetonDocName.append("jd-").append(typeWebservice).append("-").append(owner).append("-").append(documentId);

			bareJeton.setPathInfo(parentPath, jetonDocName.toString());
			final DocumentModel jetonDocPersiste = session.createDocument(bareJeton);
			session.save();

			setJetonDocInBasket(session, typeWebservice, owner, documentId, jetonDocPersiste, typeModification,
					idsComplementaires);

			notificationCreateJeton(session, typeWebservice, owner, documentId, typeModification, idsComplementaires);
		}
	}
	
	protected void createJetonDocFromBareJetonWithoutSave(final CoreSession session, final DocumentModel bareJeton,
			final String typeWebservice, final String owner, final String documentId, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {

		if (typeWebservice == null || owner == null || documentId == null) {
			LOGGER.error(session, STLogEnumImpl.NPE_PARAM_METH_TEC, ERROR_PARAM_NULL);
		} else {
			// Renseigne le chemin et le nom du document
			final String parentPath = getJetonDocFolderPath(session);
			final StringBuilder jetonDocName = new StringBuilder();
			jetonDocName.append("jd-").append(typeWebservice).append("-").append(owner).append("-").append(documentId);

			bareJeton.setPathInfo(parentPath, jetonDocName.toString());
	
			setJetonDocInBasketWithoutSave(session, typeWebservice, owner, documentId, bareJeton, typeModification,
					idsComplementaires);

			session.createDocument(bareJeton);
		}
	}

	/**
	 * mets à jour les attributs du jetonDoc avec le typeWS, owner, id du dossier, et le numéro du panier
	 * 
	 * @param session
	 *            Session
	 * @param typeWebservice
	 *            le type de webservice auquel appartient le jeton
	 * @param owner
	 *            Identifiant du propriétaire du jeton (noeud de l'organigramme)
	 * @param documentId
	 *            Id du document auquel est rattaché le jeton
	 * @param jetonDocDoc
	 *            le documentModel du jetonDoc nouvellement créé
	 * @throws ClientException
	 */
	protected void setJetonDocInBasket(final CoreSession session, final String typeWebservice, final String owner,
			final String documentId, final DocumentModel jetonDocDoc, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {

		final JetonDoc jetonDoc = jetonDocDoc.getAdapter(JetonDoc.class);
		jetonDoc.setIdDoc(documentId);
		jetonDoc.setTypeWebservice(typeWebservice);
		jetonDoc.setIdOwner(owner);
		jetonDoc.setNumeroJeton(Long.valueOf(NUMERO_PANIER));
		jetonDoc.setTypeModification(typeModification);
		jetonDoc.setIdsComplementaires(idsComplementaires);

		session.saveDocument(jetonDoc.getDocument());
		session.save();
	}

	protected void setJetonDocInBasketWithoutSave(final CoreSession session, final String typeWebservice, final String owner,
			final String documentId, final DocumentModel jetonDocDoc, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {

		final JetonDoc jetonDoc = jetonDocDoc.getAdapter(JetonDoc.class);
		jetonDoc.setIdDoc(documentId);
		jetonDoc.setTypeWebservice(typeWebservice);
		jetonDoc.setIdOwner(owner);
		jetonDoc.setNumeroJeton(Long.valueOf(NUMERO_PANIER));
		jetonDoc.setTypeModification(typeModification);
		jetonDoc.setIdsComplementaires(idsComplementaires);
	}

	
	/**
	 * Lance l'evenement de création de jeton
	 * 
	 * @param session
	 * @param typeWebservice
	 * @param owner
	 * @param dossierId
	 * @throws ClientException
	 */
	protected void notificationCreateJeton(final CoreSession session, final String typeWebservice, final String owner,
			final String dossierId, final String typeModification, final List<String> idsComplementaires)
			throws ClientException {

		// Notification
		// Lève un événement de création du jeton
		final EventProducer eventProducer = STServiceLocator.getEventProducer();
		final Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
		eventProperties.put(STEventConstant.AFTER_CREATION_JETON_PARAM_WEBSERVICE, typeWebservice);
		eventProperties.put(STEventConstant.AFTER_CREATION_JETON_PARAM_DOC_ID, dossierId);
		eventProperties.put(STEventConstant.AFTER_CREATION_JETON_PARAM_OWNER, owner);
		eventProperties.put(STEventConstant.AFTER_CREATION_JETON_PARAM_TYPE_MODIFICATION, typeModification);
		eventProperties.put(STEventConstant.AFTER_CREATION_JETON_PARAM_IDS_COMPLEMENTAIRES, (Serializable) idsComplementaires);

		final InlineEventContext inlineEventContext = new InlineEventContext(session, session.getPrincipal(),
				eventProperties);
		try {
			eventProducer.fireEvent(inlineEventContext.newEvent(STEventConstant.AFTER_CREATION_JETON));
		} catch (final ClientException exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SEND_EVENT_TEC, STEventConstant.AFTER_CREATION_JETON, exc);
		}
	}

	@Override
	public void addDocumentInBasket(final CoreSession session, final String typeWebservice, final String owner,
			final DocumentModel document, final String numeroDocument, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {
		final String documentId = document.getId();
		try {
			createJetonDoc(session, typeWebservice, owner, documentId, typeModification, idsComplementaires);

		} catch (final ClientException exc) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_CREATE_JETON_TEC, exc);
			try {
				sendMessageForError(session, numeroDocument);
			} catch (final ClientException exc1) {
				LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc1);
			}
			throw new ClientException(exc);
		}
	}
	
	@Override
	public void addDocumentInBasket(final CoreSession session, final String typeWebservice, final String owner,
			final List<DocumentModel> dossierDocs, final List<String> numeroDocuments, final String typeModification,
			final List<String> idsComplementaires) throws ClientException {
			
		LOGGER.info(STLogEnumImpl.CREATE_JETON_TEC, String.format("Creation de %d jetons", dossierDocs.size()));
		
		Set<String> docIds = new HashSet<String>();
		for(int i = 0; i < dossierDocs.size(); ++i){
			final String documentId = dossierDocs.get(i).getId();
			final String numeroDocument = numeroDocuments.get(i);
			try {
				createJetonDocWithoutSave(session, typeWebservice, owner, documentId, typeModification, idsComplementaires);
				docIds.add(documentId);
			} catch (final ClientException exc) {
				LOGGER.warn(session, STLogEnumImpl.FAIL_CREATE_JETON_TEC, exc);
				try {
					sendMessageForError(session, numeroDocument);
				} catch (final ClientException exc1) {
					LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, exc1);
				}
				throw new ClientException(exc);
			}
		}
		session.save();
		for(String documentId : docIds){
			notificationCreateJeton(session, typeWebservice, owner, documentId, typeModification, idsComplementaires);
		}
		session.save();
	}

	/**
	 * Récupère la liste des documents correspondant à un jeton donné
	 * 
	 * @param session
	 *            Session
	 * @param numeroJeton
	 *            le numéro du jeton requêté
	 * @param typeWebservice
	 *            le type de webservice auquel appartient le jeton
	 * @param owner
	 *            identifiant du propriétaire du jeton
	 * @param limitResults
	 *            si non null on limite le nombre de résultats renvoyé
	 * @return
	 * @throws ClientException
	 */
	protected List<DocumentModel> getJetonsByJetonNumber(final CoreSession session, final Long numeroJeton,
			final String typeWebservice, final String owner, final Long limitResults) throws ClientException {

		final List<DocumentModel> docs;
		if (limitResults == null) {
			docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, STConstant.JETON_DOC_TYPE,
					QUERY_JETON_BY_NUMBER, new Object[] { numeroJeton.toString(), typeWebservice, owner });
		} else {
			docs = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, STConstant.JETON_DOC_TYPE,
					QUERY_JETON_BY_NUMBER, new Object[] { numeroJeton.toString(), typeWebservice, owner },
					limitResults, 0);
		}

		if (docs == null || docs.isEmpty()) {
			return new ArrayList<DocumentModel>();
		} else {
			return docs;
		}
	}

	/**
	 * Récupère les jetons présents dans le panier
	 * 
	 * @param session
	 * @param typeWebservice
	 * @param owner
	 * @return
	 * @throws ClientException
	 */
	protected List<DocumentModel> getJetonsInBasket(final CoreSession session, final String typeWebservice,
			final String owner, Long limitResults) throws ClientException {
		return getJetonsByJetonNumber(session, NUMERO_PANIER, typeWebservice, owner, limitResults);
	}

	/**
	 * Envoi un mail en cas d'erreur de création d'un jeton
	 * 
	 * @param session
	 * @param numeroDossier
	 * @throws ClientException
	 */
	protected void sendMessageForError(final CoreSession session, final String numeroDossier) throws ClientException {
		final STMailService stMailService = STServiceLocator.getSTMailService();
		final STParametreService stParamService = STServiceLocator.getSTParametreService();

		final String subject = stParamService.getParametreValue(session,
				STParametreConstant.OBJET_MAIL_ERREUR_CREATION_JETON);
		final String message = stParamService.getParametreValue(session,
				STParametreConstant.TEXTE_MAIL_ERREUR_CREATION_JETON);

		final String addressTechnicalAdministrator = stParamService.getParametreValue(session,
				STParametreConstant.MAIL_ADMIN_TECHNIQUE);

		final Map<String, Object> variablesMap = new HashMap<String, Object>();

		variablesMap.put("numero_dossier", numeroDossier);
		stMailService.sendTemplateMail(addressTechnicalAdministrator, subject, message, variablesMap);
	}

	/**
	 * Récupère le document d'un jeton. Teste son existence avant. Retourne null s'il n'existe pas.
	 * 
	 * @param session
	 * @param jeton
	 * @return
	 * @throws ClientException
	 */
	protected DocumentModel getDocumentInJeton(final CoreSession session, final JetonDoc jeton) throws ClientException {
		DocumentRef docRef = new IdRef(jeton.getIdDoc());
		if (session.exists(docRef)) {
			return session.getDocument(docRef);
		} else {
			return null;
		}
	}

	/**
	 * Vérifie la présence de document dans le panier
	 * 
	 * @param session
	 * @param typeWebservice
	 * @param owner
	 * @return
	 * @throws ClientException
	 */
	protected boolean isBasketEmpty(final CoreSession session, final String typeWebservice, final String owner)
			throws ClientException {
		Long nbDocsInBasket = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(QUERY_JETON_BY_NUMBER),
				new Object[] { NUMERO_PANIER, typeWebservice, owner });
		if (nbDocsInBasket != null) {
			return nbDocsInBasket.compareTo(0L) == 0;
		}
		return true;
	}
}
