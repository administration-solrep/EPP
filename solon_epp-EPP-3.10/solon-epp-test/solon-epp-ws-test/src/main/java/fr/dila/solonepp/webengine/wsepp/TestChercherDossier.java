package fr.dila.solonepp.webengine.wsepp;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.Dossier;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.FicheDossier;

/**
 * Tests fonctionnels de chercher dossier
 * 
 * @author feo
 */
public class TestChercherDossier extends AbstractEppWSTest {

	private static WSEpp		wsEppGvt;

	private static WSEvenement	wsEvenementGvt;

	@BeforeClass
	public static void setup() throws Exception {
		// Utilise l'endpoint spécifié dans la variable d'environnement si elle est renseignée

		wsEppGvt = WSServiceHelper.getWSEppGvt();

		wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
	}

	/**
	 * Ce test vérifie la recherche de dossiers
	 * 
	 * @throws Exception
	 */
	@WebTest(description = "Chercher Dossier", useDriver = false)
	public void testChercherDossier() throws Exception {
		// GVT crée un dossier, un événement et une version publiée 1.0
		String filename = "fr/dila/solonepp/webengine/wsepp/chercherDossier/0010 Creer dossier EVT01 EFIM1100005F.xml";
		final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename,
				CreerVersionRequest.class);
		Assert.assertNotNull(creerVersionRequest);
		final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
		Assert.assertNotNull(creerVersionResponse);
		TraitementStatut traitementStatut = creerVersionResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		Assert.assertNotNull(creerVersionResponse.getEvenement());
		final EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
		Assert.assertNotNull(evt01Reponse);
		Assert.assertNotNull(evt01Reponse.getIdEvenement());
		Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
		Assert.assertNotNull(evt01Reponse.getVersionCourante());
		Assert.assertNotNull(evt01Reponse.getHorodatage());

		// Récupère les données de l'événement créé pour les tests suivants
		final String idEvenement = evt01Reponse.getIdEvenement();

		// AN recherche le message par identifiant d'événement : 1 message
		filename = "fr/dila/solonepp/webengine/wsepp/chercherDossier/0020 Chercher dossier.xml";
		final ChercherDossierRequest chercherDossierRequest = JaxBHelper.buildRequestFromFile(filename,
				ChercherDossierRequest.class);
		Assert.assertNotNull(chercherDossierRequest);
		chercherDossierRequest.getIdDossier().add("EFIM1100005F");
		final ChercherDossierResponse chercherDossierResponse = wsEppGvt.chercherDossier(chercherDossierRequest);
		Assert.assertNotNull(chercherDossierResponse);
		traitementStatut = chercherDossierResponse.getStatut();
		Assert.assertNotNull(traitementStatut);
		Assert.assertEquals(chercherDossierResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
		final Dossier dossier = chercherDossierResponse.getDossier().get(0);
		Assert.assertNotNull(dossier);
		final FicheDossier ficheDossier = dossier.getFicheDossier();
		Assert.assertNotNull(ficheDossier);
		Assert.assertEquals("EFIM1100005F", ficheDossier.getIdDossier());

		final List<EppEvtContainer> listEvt = dossier.getEvenement();
		Assert.assertNotNull(listEvt);

		final EppEvtContainer evtContainer = listEvt.get(0);
		Assert.assertNotNull(evtContainer);

		Assert.assertEquals(idEvenement, evtContainer.getEvt01().getIdEvenement());
		Assert.assertEquals(EtatEvenement.PUBLIE, evtContainer.getEvt01().getEtat());

	}
}
