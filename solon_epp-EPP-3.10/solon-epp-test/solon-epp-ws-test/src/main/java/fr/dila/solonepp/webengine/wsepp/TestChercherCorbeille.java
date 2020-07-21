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
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.Corbeille;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.Section;

/**
 * Tests fonctionnels des recherches de corbeilles.
 * 
 * @author jtremeaux
 */
public class TestChercherCorbeille extends AbstractEppWSTest {

    private static WSEpp wsEppGvt;

    private static WSEpp wsEppAn;

    private static WSEpp wsEppSenat;

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {
        // Utilise l'endpoint spécifié dans la variable d'environnement si elle est renseignée        
        wsEppGvt = WSServiceHelper.getWSEppGvt();
        wsEppAn = WSServiceHelper.getWSEppAn();
        wsEppSenat = WSServiceHelper.getWSEppSenat();

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    /**
     * Test recherche de corbeilles :
     * - Retourne l'arborescence des corbeilles du gouvernement.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test recherche de corbeilles du gouvernement", useDriver = false)
    public void testChercherCorbeilleGouvernement() throws Exception {

        // GVT recherche la liste de ses corbeilles
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherCorbeille/0010 Chercher corbeille.xml";
        final ChercherCorbeilleRequest chercherCorbeilleRequest = JaxBHelper.buildRequestFromFile(filename, ChercherCorbeilleRequest.class);
        Assert.assertNotNull(chercherCorbeilleRequest);
        final ChercherCorbeilleResponse chercherCorbeilleResponse = wsEppGvt.chercherCorbeille(chercherCorbeilleRequest);
        Assert.assertNotNull(chercherCorbeilleResponse);
        final TraitementStatut traitementStatut = chercherCorbeilleResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherCorbeilleResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Section> sectionList = chercherCorbeilleResponse.getSection();
        Assert.assertFalse(sectionList.isEmpty());
        final List<Corbeille> corbeilleList = chercherCorbeilleResponse.getCorbeille();
        Assert.assertNotNull(corbeilleList);
        final Corbeille corbeille = corbeilleList.get(0);
        Assert.assertEquals("CORBEILLE_GOUVERNEMENT_CENSURE", corbeille.getIdCorbeille());
        Assert.assertEquals("Motion de censure", corbeille.getNom());
        Assert.assertTrue(corbeille.getDescription().contains("l'article 49 de la constitution"));
        Assert.assertFalse(sectionList.isEmpty());
        final Section section = sectionList.get(0);
        Assert.assertEquals("SECTION_GOUVERNEMENT_PROCEDURE_LEGISLATIVE", section.getIdSection());
        Assert.assertEquals("Procédure législative", section.getNom());
        Assert.assertTrue(section.getDescription().contains("concernant les procédures législatives"));
        final List<Corbeille> subCorbeilleList = section.getCorbeille();
        Assert.assertFalse(subCorbeilleList.isEmpty());
        final Corbeille subCorbeille = subCorbeilleList.get(0);
        Assert.assertEquals("CORBEILLE_GOUVERNEMET_PROC_LEG_RECEPTION", subCorbeille.getIdCorbeille());
        Assert.assertEquals("Reçu", subCorbeille.getNom());
        Assert.assertTrue(subCorbeille.getDescription().contains("procédure législative dont le gouvernement est destinataire"));

    }

    /**
     * Test recherche de corbeilles :
     * - Retourne l'arborescence des corbeilles de l'AN.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test recherche de corbeilles de l'AN", useDriver = false)
    public void testChercherCorbeilleAssembleeNationale() throws Exception {

        // AN recherche la liste de ses corbeilles
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherCorbeille/0010 Chercher corbeille.xml";
        final ChercherCorbeilleRequest chercherCorbeilleRequest = JaxBHelper.buildRequestFromFile(filename, ChercherCorbeilleRequest.class);
        Assert.assertNotNull(chercherCorbeilleRequest);
        final ChercherCorbeilleResponse chercherCorbeilleResponse = wsEppAn.chercherCorbeille(chercherCorbeilleRequest);
        Assert.assertNotNull(chercherCorbeilleResponse);
        final TraitementStatut traitementStatut = chercherCorbeilleResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherCorbeilleResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Section> sectionList = chercherCorbeilleResponse.getSection();
        Assert.assertFalse(sectionList.isEmpty());
        final List<Corbeille> corbeilleList = chercherCorbeilleResponse.getCorbeille();
        Assert.assertNotNull(corbeilleList);
        final Corbeille corbeille = corbeilleList.get(0);
        Assert.assertEquals("CORBEILLE_AN_RAPPORT_PARLEMENT", corbeille.getIdCorbeille());
        Assert.assertEquals("Rapports au Parlement", corbeille.getNom());
        Assert.assertTrue(corbeille.getDescription().contains("procédure de dépôt des rapports"));
        Assert.assertFalse(sectionList.isEmpty());
        final Section section = sectionList.get(0);
        Assert.assertEquals("SECTION_AN_SEANCE_PROCEDURE_LEGISLATIVE", section.getIdSection());
        Assert.assertEquals("Séance procédure législative", section.getNom());
        Assert.assertTrue(section.getDescription().contains("concernant les procédures législatives"));
        final List<Corbeille> subCorbeilleList = section.getCorbeille();
        Assert.assertFalse(subCorbeilleList.isEmpty());
        final Corbeille subCorbeille = subCorbeilleList.get(0);
        Assert.assertEquals("CORBEILLE_AN_SEANCE_RECEPTION", subCorbeille.getIdCorbeille());
        Assert.assertEquals("Séance réception", subCorbeille.getNom());
        Assert.assertTrue(subCorbeille.getDescription().contains("de la procédure législative"));

    }

    /**
     * Test recherche de corbeilles :
     * - Retourne l'arborescence des corbeilles du sénat.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test recherche de corbeilles du sénat", useDriver = false)
    public void testChercherCorbeilleSenat() throws Exception {

        // Sénat recherche la liste de ses corbeilles
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherCorbeille/0010 Chercher corbeille.xml";
        final ChercherCorbeilleRequest chercherCorbeilleRequest = JaxBHelper.buildRequestFromFile(filename, ChercherCorbeilleRequest.class);
        Assert.assertNotNull(chercherCorbeilleRequest);
        final ChercherCorbeilleResponse chercherCorbeilleResponse = wsEppSenat.chercherCorbeille(chercherCorbeilleRequest);
        Assert.assertNotNull(chercherCorbeilleResponse);
        final TraitementStatut traitementStatut = chercherCorbeilleResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherCorbeilleResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Section> sectionList = chercherCorbeilleResponse.getSection();
        Assert.assertFalse(sectionList.isEmpty());
        final List<Corbeille> corbeilleList = chercherCorbeilleResponse.getCorbeille();
        Assert.assertNotNull(corbeilleList);
        Assert.assertFalse(sectionList.isEmpty());

    }

    /**
     * Test recherche de corbeilles :
     * - Crée un dossier EVT01 publié ;
     * - Vérifie la présence des messages correspondant dans les différentes corbeilles.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test recherche de corbeilles EVT01", useDriver = false)
    public void testChercherCorbeilleCount() throws Exception {
        // GVT crée un dossier, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/chercherCorbeille/0020 Creer dossier EVT01 EFIM1100004C.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
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

        // GVT recherche la liste de ses corbeilles
        filename = "fr/dila/solonepp/webengine/wsepp/chercherCorbeille/0010 Chercher corbeille.xml";
        final ChercherCorbeilleRequest chercherCorbeilleRequest = JaxBHelper.buildRequestFromFile(filename, ChercherCorbeilleRequest.class);
        Assert.assertNotNull(chercherCorbeilleRequest);
        final ChercherCorbeilleResponse chercherCorbeilleResponse = wsEppGvt.chercherCorbeille(chercherCorbeilleRequest);
        Assert.assertNotNull(chercherCorbeilleResponse);
        traitementStatut = chercherCorbeilleResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherCorbeilleResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Corbeille> corbeilleList = chercherCorbeilleResponse.getCorbeille();
        Assert.assertNotNull(corbeilleList);
        //final Set<String> corbeilleNonVideSet = Sets.newHashSet("CORBEILLE_GOUVERNEMENT_EMETTEUR");
        //        for (Corbeille corbeille : corbeilleList) {
        //            if (corbeilleNonVideSet.contains(corbeille.getIdCorbeille())) {
        //                Assert.assertTrue("La corbeille " + corbeille.getIdCorbeille() + " doit être non vide", corbeille.getNombreEvenementCourant() > 0);
        //            }
        //        }
    }
}
