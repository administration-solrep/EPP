package fr.dila.solonepp.webengine.wsevenement;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.BeforeClass;

import com.google.common.collect.Lists;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.core.util.ZipUtil;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.ChercherPieceJointeRequest;
import fr.sword.xsd.solon.epp.ChercherPieceJointeResponse;
import fr.sword.xsd.solon.epp.CompressionFichier;
import fr.sword.xsd.solon.epp.ContenuFichier;
import fr.sword.xsd.solon.epp.CreationType;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.PieceJointe;
import fr.sword.xsd.solon.epp.PieceJointeType;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;
import junit.framework.Assert;

/**
 * Tests fonctionnels des pièces jointes.
 * 
 * @author jtremeaux
 */
public class TestPieceJointe extends AbstractEppWSTest {

	private static WSEvenement	wsEvenementGvt;

	private static WSEpp		wsEppAn;

	@BeforeClass
	public static void setup() throws Exception {

		wsEppAn = WSServiceHelper.getWSEppAn();

		wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
	}

	/**
	 * Ce test vérifie la création de version comportant des pièces jointes. - Emetteur crée une version comportant 1
	 * pièce jointe et 2 fichiers ; - Emetteur modifie les pièces jointes : modifie une PJ, ajoute une PJ et supprime
	 * une PJ ; - Emetteur modifie les fichier de pièces jointes : modifie un fichier et supprime un fichier ; -
	 * Emetteur modifie les fichier de pièces jointes : teste l'envoi d'un fichier compressé.
	 * 
	 * @throws Exception
	 */
	@WebTest(description = "Ce test vérifie la création de version comportant des pièces jointes", useDriver = false)
	public void testCreerVersionAvecPieceJointe() throws Exception {
		ContenuFichier texte = null;
		ContenuFichier expose = null;
		ContenuFichier decret = null;
		ContenuFichier lettrePm = null;
		ContenuFichier autre1 = null;
		ContenuFichier autre2 = null;
		
		// GVT crée un dossier + événement créateur + version brouillon 0.1
		String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0010 Creer dossier EVT01 EFIM1100003V avec piece jointe EVT01.xml";
		CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		Assert.assertNotNull(creerVersionRequest);
		
		texte = creerVersionRequest.getEvenement().getEvt01().getTexte().getFichier().get(0);
		expose = creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().getFichier().get(0);
		decret = creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().getFichier().get(0);
		lettrePm = creerVersionRequest.getEvenement().getEvt01().getLettrePm().getFichier().get(0);
		autre1 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).getFichier().get(0);
		autre2 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(1).getFichier().get(0);
		
		CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		TraitementStatut traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
		List<PieceJointe> pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(2, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url1.fr", pieceJointeList.get(0).getUrl());
		List<ContenuFichier> contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE2", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url2.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.doc", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("application/msword", contenuFichierList.get(0).getMimeType());
		Assert.assertEquals("e8c1a7eb263efae95407fbf356bc9da7bc02ce36016ee325de9c9d3286f9191f12659b25114931f26a27efd9a764b14ae336f73a458f00feec4ff3ce4d3f0e59", contenuFichierList.get(0).getSha512());
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Récupère les identifiants des documents créés pour les tests suivants
		final String idEvenement = creerVersionResponse.getEvenement().getEvt01().getIdEvenement();
		final String idInterneEpp = pieceJointeList.get(0).getIdInterneEpp();

		// Modification de la PJ AUTRE1, suppression de la PJ AUTRE2, et ajoute d'une nouvelle PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0020 Ajout suppression modif PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp());

		int i = 0;
		for (PieceJointe pieceJointe : creerVersionRequest.getEvenement().getEvt01().getAutres()) {
			pieceJointe.setIdInterneEpp(
					creerVersionResponse.getEvenement().getEvt01().getAutres().get(i).getIdInterneEpp());
			i++;
		}

		EppEvt01 eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);

		// autre1 a été modifié, mais pas son contenu qu'il faut réinjecter car non transmis lors de la requête
		byte[] contenu = autre1.getContenu();
		autre1 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).getFichier().get(0);
		autre1.setContenu(contenu);
		autre2 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(1).getFichier().get(0);

		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(2, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1 modif", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(0).getUrl());
		Assert.assertEquals(idInterneEpp, pieceJointeList.get(0).getIdInterneEpp());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("image/gif", contenuFichierList.get(0).getMimeType());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE4", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url4.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test3.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("6681372e4f0819927596e7e77c68ce39ed84fe10e5e27d9bd724ffb2c2ca4e61c3f7fcc7cf5e0bcd803e4fcea3d2ca9db88e669731ee09b97537fb519ea76706", contenuFichierList.get(0).getSha512());
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Suppression d'un fichier de PJ, modification d'un fichier de PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0040 Suppression modif fichier de PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getAutres().get(1).getIdInterneEpp());
		
		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);

		// autre1 a été modifié et complètement retransmis, autre2 est supprimé
		autre1 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).getFichier().get(0);
		autre2 = null;

		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(1, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1 modif", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(0).getUrl());
		Assert.assertEquals(idInterneEpp, pieceJointeList.get(0).getIdInterneEpp());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test2.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("6681372e4f0819927596e7e77c68ce39ed84fe10e5e27d9bd724ffb2c2ca4e61c3f7fcc7cf5e0bcd803e4fcea3d2ca9db88e669731ee09b97537fb519ea76706", contenuFichierList.get(0).getSha512());
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Modification d'un fichier de pièce jointe, envoi d'un checksum erroné
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0050 Modifier fichier PJ erreur checksum.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp());

		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		CreerVersionResponse creerVersionResponseError = wsEvenementGvt.creerVersion(creerVersionRequest);

		Assert.assertNotNull(creerVersionResponseError);
		traitementStatut = creerVersionResponseError.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponseError.getMessageErreur(), TraitementStatut.KO, traitementStatut);
		Assert.assertTrue(creerVersionResponseError.getMessageErreur().contains("Le digest SHA512 du fichier"));
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Modification d'un fichier de pièce jointe, envoi d'un fichier zippé
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0060 Fichier compresse.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp());

		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);

		// autre1 a été modifié et complètement retransmis
		autre1 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).getFichier().get(0);

		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(1, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1 modif", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(0).getUrl());
		Assert.assertEquals(idInterneEpp, pieceJointeList.get(0).getIdInterneEpp());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test4.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Modification d'un fichier de pièce jointe, erreur dans le contenu du ZIP
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0070 Fichier compresse erreur donnees.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponseError = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponseError);
		traitementStatut = creerVersionResponseError.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponseError.getMessageErreur(), TraitementStatut.KO, traitementStatut);
		Assert.assertTrue(creerVersionResponseError.getMessageErreur().contains("Erreur de lecture de l'archive ZIP"));
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Modification d'un fichier de pièce jointe, l'archive contient 2 fichiers
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0080 Fichier compresse plusieurs fichiers.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponseError = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponseError);
		traitementStatut = creerVersionResponseError.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponseError.getMessageErreur(), TraitementStatut.KO, traitementStatut);
		Assert.assertTrue(creerVersionResponseError.getMessageErreur().contains(
				"L'archive ZIP doit contenir un seul fichier"));
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);

		// Modification d'un fichier de pièce jointe, envoi d'un fichier zippé dans des sous-répertoires
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/0090 Fichier compresse sous-repertoire.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp());
		creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(
				creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp());

		eppEvt01 = creerVersionRequest.getEvenement().getEvt01();
		eppEvt01.setIdEvenement(idEvenement);
		eppEvt01.getAutres().get(0).setIdInterneEpp(idInterneEpp);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);

		// autre1 a été modifié et complètement retransmis, autre2 est supprimé
		autre1 = creerVersionRequest.getEvenement().getEvt01().getAutres().get(0).getFichier().get(0);

		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(1, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1 modif", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(0).getUrl());
		Assert.assertEquals(idInterneEpp, pieceJointeList.get(0).getIdInterneEpp());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test4.zip", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("a49b1c5647aa44575f02d0afa3c4281192f66822bf0694ec3b24cad1cc5d2102caf6b51e77e7bf6513d263dade2360236850c04cbc97ac70303f365df764ffdd", contenuFichierList.get(0).getSha512());
		assertPiecesJointes(creerVersionResponse.getEvenement().getEvt01().getIdEvenement(), texte, expose, decret, lettrePm, autre1, autre2);
	}

	/**
	 * Ce test vérifie la complétion de version comportant des pièces jointes. - Emetteur crée une version comportant 1
	 * pièce jointe et 2 fichiers ; - Emetteur complète la version en modifiant / supprimant des pièces jointes : action
	 * interdite ; - Emetteur complète la version en modifiant / supprimant des fichiers pièces jointes : action
	 * interdite ; - Emetteur complète la version en ajoutant / modifiant des pièces jointes : action autorisée ; -
	 * Emetteur complète la version en ajoutant des fichiers de pièces jointes : action autorisée.
	 * 
	 * @throws Exception
	 */
	@WebTest(description = "Ce test vérifie la complétion de version comportant des pièces jointes", useDriver = false)
	public void testCreerVersionCompleterAvecPieceJointe() throws Exception {

		// GVT crée un dossier + événement créateur + version publiée 1.0
		String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1000 Creer dossier EVT01 EFIM1100010V avec piece jointe EVT01.xml";
		CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		Assert.assertNotNull(creerVersionRequest);
		CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		TraitementStatut traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		List<PieceJointe> pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(2, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url1.fr", pieceJointeList.get(0).getUrl());
		List<ContenuFichier> contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE2", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url2.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(2, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertEquals("test2.gif", contenuFichierList.get(1).getNomFichier());
		Assert.assertEquals("0c9b603e2c98689345fbaaa30421a388583fda80a834c2a148ed83f34842a334ff76afb20396f99922a3f7f97d7a7317cb7d82ad4c8a812a09b43719769f1c4d", contenuFichierList.get(1).getSha512());

		// Récupère les identifiants générés pour les tests suivants
		final String idEvenement = evt01Reponse.getIdEvenement();
		final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
		final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
		final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
		final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();
		final String pieceJointeAutre0Id = evt01Reponse.getAutres().get(0).getIdInterneEpp();
		final String pieceJointeAutre1Id = evt01Reponse.getAutres().get(1).getIdInterneEpp();
		EppEvt01 evt01Request;
		
		// GVT publie la version 2.0 brouillon complétée : ajout de fichier de PJ, ajout / modification PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1040 Completer ajouter modifier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(3, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url1.fr", pieceJointeList.get(0).getUrl());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE2 modif", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url2_modif.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(3, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertEquals("test2.gif", contenuFichierList.get(1).getNomFichier());
		Assert.assertEquals("0c9b603e2c98689345fbaaa30421a388583fda80a834c2a148ed83f34842a334ff76afb20396f99922a3f7f97d7a7317cb7d82ad4c8a812a09b43719769f1c4d", contenuFichierList.get(1).getSha512());
		Assert.assertEquals("test3.gif", contenuFichierList.get(2).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(2).getSha512());
		Assert.assertNotNull(pieceJointeList.get(2).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE3", pieceJointeList.get(2).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(2).getUrl());
		contenuFichierList = pieceJointeList.get(2).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.doc", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("e8c1a7eb263efae95407fbf356bc9da7bc02ce36016ee325de9c9d3286f9191f12659b25114931f26a27efd9a764b14ae336f73a458f00feec4ff3ce4d3f0e59", contenuFichierList.get(0).getSha512());
		
		// GVT publie la version 2.0 brouillon complétée : supprimer un fichier de PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1020 Completer supprimer fichier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT publie la version 2.0 brouillon complétée : modifier un fichier de PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1030 Completer modifier fichier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT publie la version 2.0 brouillon complétée : supprimer une PJ
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1010 Completer supprimer PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		

	}

	/**
	 * Ce test vérifie la rectification de version comportant des pièces jointes. - Emetteur crée une version comportant
	 * 1 pièce jointe et 2 fichiers ; - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe
	 * à "EN_INSTANCE" ; - Emetteur rectifie la version en modifiant / supprimant des pièces jointes : action autorisée
	 * ; - Emetteur rectifie la version en modifiant / supprimant des fichiers pièces jointes : action autorisée ; -
	 * Emetteur rectifie la version en ajoutant / modifiant des pièces jointes : action autorisée ; - Emetteur rectifie
	 * la version en ajoutant des fichiers de pièces jointes : action autorisée.
	 * 
	 * @throws Exception
	 */
	@WebTest(description = "Ce test vérifie la rectification de version comportant des pièces jointes", useDriver = false)
	public void testCreerVersionRectifierAvecPieceJointe() throws Exception {

		// GVT crée un dossier + événement créateur + version publiée 1.0
		String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/2000 Creer dossier EVT01 EFIM1100020V avec piece jointe EVT01.xml";
		CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		Assert.assertNotNull(creerVersionRequest);
		CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		TraitementStatut traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		List<PieceJointe> pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(2, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url1.fr", pieceJointeList.get(0).getUrl());
		List<ContenuFichier> contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE2", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url2.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(2, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertEquals("test2.gif", contenuFichierList.get(1).getNomFichier());
		Assert.assertEquals("0c9b603e2c98689345fbaaa30421a388583fda80a834c2a148ed83f34842a334ff76afb20396f99922a3f7f97d7a7317cb7d82ad4c8a812a09b43719769f1c4d", contenuFichierList.get(1).getSha512());

		// Récupère les identifiants générés pour les tests suivants
		final String idEvenement = evt01Reponse.getIdEvenement();
		final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
		final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
		final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
		final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();
		final String pieceJointeAutre0Id = evt01Reponse.getAutres().get(0).getIdInterneEpp();
		final String pieceJointeAutre1Id = evt01Reponse.getAutres().get(1).getIdInterneEpp();

		// AN accuse réception de la version 1.0
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/3010 Notifier transition traite.xml";
		final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename,
				NotifierTransitionRequest.class);
		notifierTransitionRequest.setIdEvenement(idEvenement);
		Assert.assertNotNull(notifierTransitionRequest);
		final NotifierTransitionResponse notifierTransitionResponse = wsEppAn
				.notifierTransition(notifierTransitionRequest);
		traitementStatut = notifierTransitionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT publie la version rectifiée : 1.1 en attente de validation
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1010 Completer supprimer PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.setModeCreation(CreationType.RECTIFIER_PUBLIER);
		EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT abandonne le rectificatif : 1.1 abandonnée
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/3020 Valider version abandonner.xml";
		ValiderVersionRequest validerVersionRequest = JaxBHelper.buildRequestFromFile(filename,
				ValiderVersionRequest.class);
		validerVersionRequest.setIdEvenement(idEvenement);
		ValiderVersionResponse validerVersionResponse = wsEvenementGvt.validerVersion(validerVersionRequest);
		Assert.assertNotNull(validerVersionResponse);
		traitementStatut = validerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = validerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		Assert.assertNotNull(evt01Reponse.getHorodatage());

		// GVT publie la version rectifiée : 1.2 en attente de validation
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1020 Completer supprimer fichier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.setModeCreation(CreationType.RECTIFIER_PUBLIER);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT abandonne le rectificatif : 1.2 abandonnée
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/3020 Valider version abandonner.xml";
		validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
		validerVersionRequest.setIdEvenement(idEvenement);
		validerVersionResponse = wsEvenementGvt.validerVersion(validerVersionRequest);
		Assert.assertNotNull(validerVersionResponse);
		traitementStatut = validerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = validerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		Assert.assertNotNull(evt01Reponse.getHorodatage());

		// GVT publie la version rectifiée : 1.3 en attente de validation
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1030 Completer modifier fichier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.setModeCreation(CreationType.RECTIFIER_PUBLIER);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

		// GVT abandonne le rectificatif : 1.3 abandonnée
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/3020 Valider version abandonner.xml";
		validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
		validerVersionRequest.setIdEvenement(idEvenement);
		validerVersionResponse = wsEvenementGvt.validerVersion(validerVersionRequest);
		Assert.assertNotNull(validerVersionResponse);
		traitementStatut = validerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = validerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
		Assert.assertNotNull(evt01Reponse.getHorodatage());

		// GVT publie la version rectifiée : 1.4 en attente de validation
		filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionAvecPieceJointe/1040 Completer ajouter modifier PJ.xml";
		creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
		creerVersionRequest.setModeCreation(CreationType.RECTIFIER_PUBLIER);
		evt01Request = creerVersionRequest.getEvenement().getEvt01();
		evt01Request.setIdEvenement(idEvenement);
		evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
		evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
		evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
		evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
		evt01Request.getAutres().get(0).setIdInterneEpp(pieceJointeAutre0Id);
		evt01Request.getAutres().get(1).setIdInterneEpp(pieceJointeAutre1Id);
		creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
		Assert.assertEquals(4, evt01Reponse.getVersionCourante().getMineur());
		pieceJointeList = evt01Reponse.getAutres();
		Assert.assertEquals(3, pieceJointeList.size());
		Assert.assertNotNull(pieceJointeList.get(0).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE1", pieceJointeList.get(0).getLibelle());
		Assert.assertEquals("http://www.url1.fr", pieceJointeList.get(0).getUrl());
		contenuFichierList = pieceJointeList.get(0).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertNotNull(pieceJointeList.get(1).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE2 modif", pieceJointeList.get(1).getLibelle());
		Assert.assertEquals("http://www.url2_modif.fr", pieceJointeList.get(1).getUrl());
		contenuFichierList = pieceJointeList.get(1).getFichier();
		Assert.assertEquals(3, contenuFichierList.size());
		Assert.assertEquals("test1.gif", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(0).getSha512());
		Assert.assertEquals("test2.gif", contenuFichierList.get(1).getNomFichier());
		Assert.assertEquals("0c9b603e2c98689345fbaaa30421a388583fda80a834c2a148ed83f34842a334ff76afb20396f99922a3f7f97d7a7317cb7d82ad4c8a812a09b43719769f1c4d", contenuFichierList.get(1).getSha512());
		Assert.assertEquals("test3.gif", contenuFichierList.get(2).getNomFichier());
		Assert.assertEquals("1f844f2bf5dc2e9806c090e61de2e0f8e784d1524dce623a51d095d13c232936f9fc97dc0241c83ef880640f2e5389f8958974c2a38705927033326cabedef11", contenuFichierList.get(2).getSha512());
		Assert.assertNotNull(pieceJointeList.get(2).getIdInterneEpp());
		Assert.assertEquals("Libellé AUTRE3", pieceJointeList.get(2).getLibelle());
		Assert.assertEquals("http://www.url3.fr", pieceJointeList.get(2).getUrl());
		contenuFichierList = pieceJointeList.get(2).getFichier();
		Assert.assertEquals(1, contenuFichierList.size());
		Assert.assertEquals("test1.doc", contenuFichierList.get(0).getNomFichier());
		Assert.assertEquals("e8c1a7eb263efae95407fbf356bc9da7bc02ce36016ee325de9c9d3286f9191f12659b25114931f26a27efd9a764b14ae336f73a458f00feec4ff3ce4d3f0e59", contenuFichierList.get(0).getSha512());

	}

	/**
	 * Vérifie le contenu des pièces jointe après une modification. Sert à vérifier qu'on ne perd pas de données
	 * lors de la création d'une nouvelle version, même si les fichiers ne sont pas retransmis.
	 */
	private void assertPiecesJointes(String evtId, ContenuFichier texte, ContenuFichier expose, ContenuFichier decret,
			ContenuFichier lettrePm, ContenuFichier autre1, ContenuFichier autre2) throws Exception {
		assertPieceJointe(evtId, PieceJointeType.TEXTE, Collections.singletonList(texte));
		assertPieceJointe(evtId, PieceJointeType.EXPOSE_DES_MOTIFS, Collections.singletonList(expose));
		assertPieceJointe(evtId, PieceJointeType.DECRET_PRESENTATION, Collections.singletonList(decret));
		assertPieceJointe(evtId, PieceJointeType.LETTRE_PM, Collections.singletonList(lettrePm));
		List<ContenuFichier> fichiersAutre = Lists.newArrayList();
		if (autre1 != null) {
			fichiersAutre.add(autre1);
		}
		if (autre2 != null) {
			fichiersAutre.add(autre2);
		}
		assertPieceJointe(evtId, PieceJointeType.AUTRE, fichiersAutre);
	}

	/**
	 * Vérifie le contenu des pièces jointe après une modification. Sert à vérifier qu'on ne perd pas de données
	 * lors de la création d'une nouvelle version, même si les fichiers ne sont pas retransmis.
	 */
	private void assertPieceJointe(String evtId, PieceJointeType typePieceJointe, List<ContenuFichier> fichiers) throws Exception {
		// vérification intégrité pièce jointe
		for (ContenuFichier fichier : fichiers) {
			ChercherPieceJointeRequest req = new ChercherPieceJointeRequest();
			EvtId evtIdObj = new EvtId();
			evtIdObj.setId(evtId);
			req.setIdEvt(evtIdObj);
			req.setNomFichier(fichier.getNomFichier());
			req.setTypePieceJointe(typePieceJointe);
			ChercherPieceJointeResponse response = wsEvenementGvt.chercherPieceJointe(req);
			if (CompressionFichier.ZIP.equals(fichier.getCompression())) {
				// Le fichier de la réponse est systématiquement décompressé
				ByteArrayOutputStream os = ZipUtil.unzipSingleFile(new ByteArrayInputStream(fichier.getContenu()));
				byte[] contenu = os.toByteArray();
				Assert.assertTrue(ArrayUtils.isEquals(contenu, response.getPieceJointe().getFichier().get(0).getContenu()));
			} else {
				Assert.assertTrue(ArrayUtils.isEquals(fichier.getContenu(), response.getPieceJointe().getFichier().get(0).getContenu()));
			}
			Assert.assertNotNull(response.getPieceJointe().getFichier().get(0).getSha512());
		}
	}
}
