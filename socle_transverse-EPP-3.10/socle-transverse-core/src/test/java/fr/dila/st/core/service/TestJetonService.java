package fr.dila.st.core.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.jeton.JetonServiceDto;
import fr.dila.st.api.service.JetonService;
import fr.dila.st.core.STRepositoryTestCase;
import fr.dila.st.core.descriptor.ConfigParameterDescriptor;

public class TestJetonService extends STRepositoryTestCase {

	String			typeWebservice	= "typeTest";
	String			owner			= "test";

	JetonService	jetonService;

	public void setUp() throws Exception {
		super.setUp();
		openSession();
		jetonService = new JetonServiceImpl();
		DocumentModel doc = session.createDocumentModel("JetonDocFolder");
		doc = session.createDocument(doc);
		session.saveDocument(doc);

		// On renseigne la configuration du nombre de documents par récupération, ici à 2
		ConfigParameterDescriptor desc = new ConfigParameterDescriptor();
		desc.setName(STConfigConstants.WEBSERVICE_JETON_RESULT_SIZE);
		desc.setValue("2");
		((ConfigServiceImpl) STServiceLocator.getConfigService()).registerContribution(desc, null, null);

		session.saveDocument(doc);
		session.save();
		closeSession();
	}

	public void testAddDocumentInBasket() throws ClientException {
		openSession();

		DocumentModelList dml = null;

		dml = session.query("SELECT * from JetonDoc");
		assertEquals(0, dml.size());

		DocumentModel doc = session.createDocumentModel("Note");
		doc = session.createDocument(doc);
		session.saveDocument(doc);

		jetonService.addDocumentInBasket(session, typeWebservice, owner, doc, "numero dossier test 1", null, null);

		dml = session.query("SELECT * from JetonDoc");
		assertEquals(1, dml.size());

		closeSession();
	}

	public void testGetNumeroJetonMaxForWs() throws ClientException {
		openSession();

		DocumentModelList dml = null;

		dml = session.query("SELECT * from JetonDoc");
		assertEquals(0, dml.size());

		Long numeroJetonMax = jetonService.getNumeroJetonMaxForWS(session, owner, typeWebservice);
		// aucun jeton n'existe, renvoi -100 car on n'a pas pu récupérer de valeur
		assertEquals(-100, numeroJetonMax.longValue());

		DocumentModel doc = session.createDocumentModel("File");
		doc = session.createDocument(doc);
		jetonService.addDocumentInBasket(session, typeWebservice, owner, doc, "numero dossier test 1", null, null);

		dml = session.query("SELECT * from JetonDoc");
		assertEquals(1, dml.size());

		numeroJetonMax = jetonService.getNumeroJetonMaxForWS(session, owner, typeWebservice);
		// Des documents existent dans le panier, et aucun jeton n'a encore été créé, renvoi le numéro de panier
		assertEquals(-999, numeroJetonMax.longValue());

		closeSession();
	}

	/**
	 * Les actions suivantes sont effectuées : * Récupération jeton positif inexistant * Récupération jeton négatif
	 * inexistant * Récupération jeton avec numero = null * Récupération jeton max quand aucun jeton n'existe * Création
	 * document 1 + ajout dans panier => 1 document dans panier * Récupération jeton 0 (premier appel) => 0 document
	 * dans panier, 1 document dans jeton0 * Récupération jeton 0 (deuxième appel) * Récupération jeton 1 (vide) *
	 * Création document 2 + ajout dans panier => 1 document dans panier, 1 document dans jeton0 * Récupération jeton 0
	 * * Création documents 3 et 4 + ajout panier dans l'ordre document 4 puis document 3 => 3 documents dans panier, 1
	 * document dans jeton0 * Récupération jeton 1 (premier appel) contient document 2 et 4l => 1 document dans panier,
	 * 1 document dans jeton0, 2 documents dans jeton1 * Suppression document 1 * Récupération jeton 0 => 1 document
	 * dans panier, 0 document dans jeton0, 2 documents dans jeton1 * Suppression document 3 => 0 document dans panier,
	 * 0 document dans jeton0, 2 documents dans jeton1 * Récupération jeton 2 (vide) * Récupération jeton 1 *
	 * Suppression document 2 * Récupération jeton 1 => 0 document dans panier, 0 document dans jeton0, 1 document dans
	 * jeton1
	 * 
	 * 
	 * @throws ClientException
	 */
	public void testGetDocuments() throws ClientException {
		openSession();

		// ############################################* * Récupération jeton positif inexistant

		// On essaie de récuperer un jeton qui n'existe pas, le retour est null
		JetonServiceDto jetonDto = jetonService.getDocuments(session, owner, (long) 55, typeWebservice);
		assertNull(jetonDto);

		// ############################################* * Récupération jeton négatif inexistant

		// On essaie de récuperer un jeton qui n'existe pas, le retour est null
		jetonDto = jetonService.getDocuments(session, owner, (long) -55, typeWebservice);
		assertNull(jetonDto);

		// ############################################* * Récupération jeton avec numero = null

		// On passe null pour numéro de jeton, le service doit le convertir en O
		jetonDto = jetonService.getDocuments(session, owner, null, typeWebservice);
		// Le retour doit être un jetonDto avec une liste de document vide, un dernier envoi à true
		// et un numero de prochain jeton à 0
		checkJetonDto(jetonDto, 0, 0, true);

		DocumentModelList dml = null;

		dml = session.query("SELECT * from JetonDoc");
		assertEquals(0, dml.size());

		// ############################################* * Récupération jeton max quand aucun jeton n'existe

		Long numeroJetonMax = jetonService.getNumeroJetonMaxForWS(session, owner, typeWebservice);
		// aucun jeton n'existe, renvoi -100 car on n'a pas pu récupérer de valeur
		assertEquals(-100, numeroJetonMax.longValue());

		// ############################################* * Création document 1 + ajout dans panier => 1 document dans
		// panier

		// Création d'un premier document et ajout dans un jeton
		DocumentModel docJeton0 = createDocument("/", "fichierTest", "File");
		jetonService
				.addDocumentInBasket(session, typeWebservice, owner, docJeton0, "numero dossier test 0", null, null);

		// ############################################ * * Récupération jeton 0 (premier appel) => 0 document dans
		// panier, 1 document dans jeton0

		jetonDto = jetonService.getDocuments(session, owner, (long) 0, typeWebservice);
		// On vient de récupérer le jeton numéro 0,
		// on a donc un document dedans, un prochain numéro de jeton à 1, et un dernier envoi à true
		checkJetonDto(jetonDto, 1, 1, true);
		assertEquals(docJeton0.getId(), jetonDto.getDocumentList().get(0).getId());

		// ############################################ * * Récupération jeton 0 (deuxième appel)

		// On le récupère à nouveau pour passer sur la récupération d'un jeton déjà lu
		jetonDto = jetonService.getDocuments(session, owner, (long) 0, typeWebservice);

		// On vient de récupérer le jeton numéro 0,
		// on a donc un document dedans, un prochain numéro de jeton à 1, et un dernier envoi à true
		checkJetonDto(jetonDto, 1, 1, true);
		assertEquals(docJeton0.getId(), jetonDto.getDocumentList().get(0).getId());

		// ############################################* * Récupération jeton 1 (vide)

		// On tente la récupération du jeton 1
		jetonDto = jetonService.getDocuments(session, owner, (long) 1, typeWebservice);

		// On vient de récupérer le jeton numéro 1,
		// on a donc une liste vide, un dernier envoi à true, et un prochain numéro jeton à 1
		checkJetonDto(jetonDto, 0, 1, true);

		// ############################################ * * Création document 2 + ajout dans panier => 1 document dans
		// panier, 1 document dans jeton0

		// On ajoute un nouveau document dans le panier pour changer la valeur dernier envoi à false
		DocumentModel docJeton1_1 = createDocument("/", "fichierTest2", "File");

		jetonService.addDocumentInBasket(session, typeWebservice, owner, docJeton1_1, "numero dossier test 1", null,
				null);

		// ############################################* * Récupération jeton 0

		// On le récupère à nouveau pour passer sur la récupération d'un jeton déjà lu
		jetonDto = jetonService.getDocuments(session, owner, (long) 0, typeWebservice);

		// On vient de récupérer le jeton numéro 0,
		// on a donc un document dedans, un prochain numéro de jeton à 1, et un dernier envoi à false
		checkJetonDto(jetonDto, 1, 1, false);
		assertEquals(docJeton0.getId(), jetonDto.getDocumentList().get(0).getId());

		// ############################################ * * Création documents 3 et 4 + ajout panier dans l'ordre
		// document 4 puis document 3 => 3 documents dans panier, 1 document dans jeton0

		// On ajoute deux nouveaux documents dans le panier pour dépasser la limite du jeton qui est à 2
		DocumentModel docJeton1_2 = createDocument("/", "fichierTest3", "File");
		DocumentModel docJeton1_3 = createDocument("/", "fichierTest4", "File");

		// On ajoute le troisième document avant le deuxième pour vérifier que la création du document n'influe pas
		// sur l'ordre de récupération des jetons
		jetonService.addDocumentInBasket(session, typeWebservice, owner, docJeton1_3, "numero dossier test 3", null,
				null);
		jetonService.addDocumentInBasket(session, typeWebservice, owner, docJeton1_2, "numero dossier test 2", null,
				null);

		// ############################################ * * Récupération jeton 1 (premier appel) contient document 2 et
		// 4l => 1 document dans panier, 1 document dans jeton0, 2 documents dans jeton1

		// On récupère le jeton 1, pas encore existant qui doit contenir uniquement 2 résultats
		jetonDto = jetonService.getDocuments(session, owner, (long) 1, typeWebservice);

		// On vient de récupérer le jeton numéro 1,
		// on a donc deux documents dedans, un prochain numéro de jeton à 2, et un dernier envoi à false
		checkJetonDto(jetonDto, 2, 2, false);
		// On doit récupérer les documents dans l'ordre dans lequel ils ont été ajoutés au panier
		// donc on doit avoir docJeton1_1 et docJeton1_3 dans la liste
		assertEquals(docJeton1_1.getId(), jetonDto.getDocumentList().get(0).getId());
		assertEquals(docJeton1_3.getId(), jetonDto.getDocumentList().get(1).getId());

		// ############################################* * Suppression document 1

		// On supprime le document présent dans le jeton 0 pour vérifier le message d'erreur
		session.removeDocument(docJeton0.getRef());
		session.save();

		// ############################################* * Récupération jeton 0 => 1 document dans panier, 0 document
		// dans jeton0, 2 documents dans jeton1

		jetonDto = jetonService.getDocuments(session, owner, (long) 0, typeWebservice);
		assertEquals("Le jeton contenait une ou plusieurs communications de type brouillon supprimées depuis",
				jetonDto.getMessageErreur());
		// Il reste des données à lire puisqu'il y a le jeton 1 qui existe et un document dans le panier
		// Le jeton suivant est le 1 puisqu'on vient d'appeler le 0
		checkJetonDto(jetonDto, 0, 1, false);

		// ############################################ * * Suppression document 3 => 0 document dans panier, 0 document
		// dans jeton0, 2 documents dans jeton1

		// On supprime le document présent dans le panier
		session.removeDocument(docJeton1_2.getRef());
		session.save();

		// On vérifie que le jeton existe toujours
		// On a créé 4 documents dans les jetons depuis le début
		dml = session.query("SELECT * from JetonDoc");
		assertEquals(4, dml.size());

		// ############################################* * Récupération jeton 2 (vide)

		// On tente sa récupération
		jetonDto = jetonService.getDocuments(session, owner, (long) 2, typeWebservice);
		// Il ne reste pas de données à lire
		// Le jeton suivant reste le 2
		checkJetonDto(jetonDto, 0, 2, true);

		// Le jeton qui ne contenait plus de données existantes a été supprimé
		dml = session.query("SELECT * from JetonDoc");
		assertEquals(3, dml.size());

		// ############################################* * Récupération jeton 1

		// On retente l'appel du jeton 1 pour vérifier que les données de retour ont été modifiées
		jetonDto = jetonService.getDocuments(session, owner, (long) 1, typeWebservice);
		// on a donc toujours deux documents dedans, un prochain numéro de jeton à 2, et un dernier envoi à true
		checkJetonDto(jetonDto, 2, 2, true);
		// On doit récupérer les documents dans l'ordre dans lequel ils ont été ajoutés au panier
		// donc on doit avoir docJeton1_1 et docJeton1_3 dans la liste
		assertEquals(docJeton1_1.getId(), jetonDto.getDocumentList().get(0).getId());
		assertEquals(docJeton1_3.getId(), jetonDto.getDocumentList().get(1).getId());

		// ############################################ * * Suppression document 2

		// On supprime le premier document du jeton 1 pour vérifier qu'on retourne bien toujours les mêmes informations
		// exceptées le document supprimés
		session.removeDocument(docJeton1_1.getRef());
		session.save();

		// Le jeton existe toujours
		dml = session.query("SELECT * from JetonDoc");
		assertEquals(3, dml.size());

		// ############################################* * Récupération jeton 1

		jetonDto = jetonService.getDocuments(session, owner, (long) 1, typeWebservice);
		// on a donc toujours deux documents dedans, un prochain numéro de jeton à 2, et un dernier envoi à true
		checkJetonDto(jetonDto, 1, 2, true);
		// On doit récupérer les documents dans l'ordre dans lequel ils ont été ajoutés au panier
		// donc on doit avoir docJeton1_3 dans la liste
		assertEquals(docJeton1_3.getId(), jetonDto.getDocumentList().get(0).getId());

		// Le jeton qui contenait des données existe toujours puisqu'il a été récupéré par le passé
		dml = session.query("SELECT * from JetonDoc");
		assertEquals(3, dml.size());

		closeSession();
	}

	private void checkJetonDto(JetonServiceDto jetonDto, int nbDocumentsExpected, long nextJetonNumberExpected,
			boolean isLastSendingExpected) {
		assertEquals(nbDocumentsExpected, jetonDto.getDocumentList().size());
		assertEquals(nextJetonNumberExpected, (long) jetonDto.getNextJetonNumber());
		assertEquals(isLastSendingExpected, (boolean) jetonDto.isLastSending());
	}

	private DocumentModel createDocument(String pathParent, String nameDoc, String typeDoc) throws ClientException {
		DocumentModel document = session.createDocumentModel(pathParent, nameDoc, typeDoc);
		document = session.createDocument(document);
		session.saveDocument(document);
		session.save();
		return document;
	}

}
