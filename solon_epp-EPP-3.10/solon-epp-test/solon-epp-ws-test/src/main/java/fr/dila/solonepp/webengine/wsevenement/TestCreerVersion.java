package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.BeforeClass;

import com.google.common.collect.Sets;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.Action;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvt02;
import fr.sword.xsd.solon.epp.EppEvt21;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatVersion;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels des créations de version initiales.
 * 
 * @author jtremeaux
 */
public class TestCreerVersion extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
    }

    /**
     * Test de création de version :
     * - Emetteur crée une version à l'état brouillon -> 0.1 BROUILLON ;
     * - Emetteur modifie la version brouillon ;
     * - Emetteur publie la version brouillon -> 1.0 PUBLIEE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionBrouillon() throws Exception {
        // GVT crée un dossier + événement créateur + version brouillon 0.1
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0010 Creer dossier EVT01 CCOZ1100001V.xml";
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
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Version versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(0, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertEquals(EtatVersion.BROUILLON, versionCourante.getEtat());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evt01Reponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt01Reponse.getDestinataire());
        Assert.assertTrue("Trop de destinaires en copie: 1 attendu. ", evt01Reponse.getCopie().size() == 1);
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(Institution.SENAT, evt01Reponse.getCopie().get(0));
        final Set<Action> expectedActionSuivante = Sets.newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT crée un événement créateur avec le même identifiant de dossier
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0015 Creer dossier EVT01 erreur destinataire.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(TraitementStatut.KO, traitementStatut);

        // GVT modifie la version brouillon 0.1
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0020 Modifier version brouillon.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(Institution.SENAT, evt01Reponse.getDestinataire());
        final List<Institution> copieList = evt01Reponse.getCopie();
        Assert.assertNotNull(copieList);
        Assert.assertEquals("Nombre de destinataires en copie", 1, copieList.size());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, copieList.get(0));
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet modifié", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // AN modifie la version brouillon 0.1 de l'événement AN : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0020 Modifier version brouillon.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(TraitementStatut.KO, traitementStatut);

        // AN publie la version brouillon 0.1 de l'événement AN : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0030 Publier version.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(TraitementStatut.KO, traitementStatut);

        // GVT publie la version 0.1 -> 1.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/0030 Publier version.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(versionCourante.getHorodatage(), evt01Reponse.getHorodatage());
        Assert.assertEquals("Publié", creerVersionResponse.getEvenement().getEvt01().getObjet());

    }

    /**
     * Test de création de version :
     * - Emetteur crée une version à l'état publié -> 1.0 PUBLIEE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionPublie() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        final String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/1000 Creer dossier EVT01 CCOZ1100002V publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        final TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evt01Reponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt01Reponse.getDestinataire());

    }

    /**
     * Test de création de version :
     * - Vérifie que l'émetteur fait partie des émetteurs autorisés du type d'événement.
     *
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionEmetteur() throws Exception {

        // AN crée un événement EVT01 : émetteur interdit par ce type d'événement
        final String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/2000 Creer dossier EVT01 CCOZ1100003V publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        final TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Seule l'institution émettrice peut modifier cette communication"));

    }

    /**
     * Test de création de version :
     * - Vérifie que le destinataire fait partie des émetteurs autorisés du type d'événement.
     *
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionDestinataire() throws Exception {

        // AN crée un événement EVT02 avec emetteur = destinataire : interdit
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/3000 Creer dossier EVT02 CCOZ1100004V publie.xml";
        CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        EppEvt02 evt02Requete = creerVersionRequest.getEvenement().getEvt02();
        evt02Requete.setDestinataire(Institution.ASSEMBLEE_NATIONALE);
        CreerVersionResponse creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("doit être différent de l'émetteur"));

        // AN crée un événement EVT02 : destinataire interdit par ce type d'événement
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/3000 Creer dossier EVT02 CCOZ1100004V publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt02Requete = creerVersionRequest.getEvenement().getEvt02();
        evt02Requete.setDestinataire(Institution.SENAT);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("n'est pas autorisé pour le type de communication"));

        // AN crée un événement EVT02 avec copie = emetteur : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/3000 Creer dossier EVT02 CCOZ1100004V publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt02Requete = creerVersionRequest.getEvenement().getEvt02();
        List<Institution> copie = evt02Requete.getCopie();
        copie.set(0, Institution.ASSEMBLEE_NATIONALE);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("doit être différent de l'émetteur"));

        // AN crée un événement EVT02 avec copie = destinataire : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/3000 Creer dossier EVT02 CCOZ1100004V publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt02Requete = creerVersionRequest.getEvenement().getEvt02();
        copie = evt02Requete.getCopie();
        copie.set(0, Institution.GOUVERNEMENT);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("doit être différent du destinataire"));

        // AN crée un événement EVT02 avec copie interdite par ce type d'événement TODO copie = DILA
        //        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/3000 Creer dossier EVT02 CCOZ1100004V publie.xml";
        //        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        //        Assert.assertNotNull(creerVersionRequest);
        //        evt02Requete = creerVersionRequest.getEvenement().getEvt02();
        //        copie = evt02Requete.getCopie();
        //        copie.set(0, Institution.GOUVERNEMENT);
        //        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        //        Assert.assertNotNull(creerVersionResponse);
        //        traitementStatut = creerVersionResponse.getStatut();
        //        Assert.assertNotNull(traitementStatut);
        //        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        //        Assert.assertTrue(creerVersionResponse.getMessageErreur(), creerVersionResponse.getMessageErreur().contains("doit être différent du destinataire"));

    }

    /**
     * Test de création de version :
     * - Emetteur crée un événement EVT01 et une version directement à l'état publié -> 1.0 PUBLIE ;
     * - Emetteur crée un événement EVT21 successif et une version directement à l'état publié -> 1.0 PUBLIE ;
     *   Vérification du caractère facultatif de la copie.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionCopieObligatoire() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/2000 Creer dossier EVT01 CCOZ1100003V publie.xml";
        CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evt01Reponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt01Reponse.getDestinataire());
        List<Institution> copieList = evt01Reponse.getCopie();
        Assert.assertNotNull(copieList);
        Assert.assertEquals("Nombre de destinataires en copie", 1, copieList.size());
        Assert.assertEquals(Institution.SENAT, copieList.get(0));

        // Récupère l'identifiant de l'événement créé pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();

        // GVT crée un événement successif EVT21 sans copie
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersion/2010 Creer evenement EVT21 publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.getEvenement().getEvt21().setIdEvenementPrecedent(idEvenement);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt21 evt21Reponse = creerVersionResponse.getEvenement().getEvt21();
        Assert.assertNotNull(evt21Reponse);
        Assert.assertNotNull(evt21Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt21Reponse.getEtat());
        Assert.assertNotNull(evt21Reponse.getVersionCourante());
        Assert.assertEquals(1, evt21Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt21Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt21Reponse.getHorodatage());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt21Reponse.getEmetteur());
        Assert.assertEquals(Institution.GOUVERNEMENT, evt21Reponse.getDestinataire());
        copieList = evt21Reponse.getCopie();
        Assert.assertNotNull(copieList);
        Assert.assertEquals("Nombre de destinataires en copie", 0, copieList.size());

    }
}
