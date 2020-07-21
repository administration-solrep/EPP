package fr.dila.solonepp.webengine.wsevenement;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.AccuserReceptionRequest;
import fr.sword.xsd.solon.epp.AccuserReceptionResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels des accusés de réception des messages.
 * 
 * @author jtremeaux
 */
public class TestAccuserReception extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
    }

    /**
     * Test d'accusé de réception :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire accuse réception de la version 1.0 ;
     * - Emetteur crée une version brouillon pour complétion -> 1.1 BROUILLON ;
     * - Emetteur publie la version 1.1 -> 2.0 PUBLIEE ;
     * - Destinataire accuse réception de sa version en cours (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'accusé de réception", useDriver = false)
    public void testAccuserReception() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0010 Creer dossier EVT01.xml";
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
        final XMLGregorianCalendar horodatage1_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage1_0);

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT accuse réception de la version 1.0 : interdit, seul le destinataire peut accuser réception
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0020 Accuser reception emetteur interdit.xml";
        AccuserReceptionRequest accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        AccuserReceptionResponse accuserReceptionResponse = wsEvenementGvt.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(accuserReceptionResponse.getMessageErreur(),
                accuserReceptionResponse.getMessageErreur().contains("Seul le destinataire peut accuser réception de la version"));

        // AN accuse réception de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0030 Accuser reception destinataire.xml";
        accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(accuserReceptionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(accuserReceptionResponse.getEvenement());
        evt01Reponse = accuserReceptionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(horodatage1_0, evt01Reponse.getHorodatage());
        final XMLGregorianCalendar dateAr1_0 = evt01Reponse.getDateAr();
        final XMLGregorianCalendar dateArVersion1_0 = evt01Reponse.getVersionCourante().getDateAr();
        Assert.assertNotNull(dateAr1_0);
        Assert.assertNotNull(dateArVersion1_0);

        // AN accuse réception de la version 1.0 : sans effet (AR déjà émis)
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0030 Accuser reception destinataire.xml";
        accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(accuserReceptionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(accuserReceptionResponse.getEvenement());
        evt01Reponse = accuserReceptionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(horodatage1_0, evt01Reponse.getHorodatage());
        Assert.assertEquals(dateAr1_0, evt01Reponse.getDateAr());

        // GVT crée une nouvelle version brouillon 1.1 pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0040 Creer version brouillon 1.1.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());

        // AN accuse réception de la version 1.1 : interdit, version brouillon
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0050 Accuser reception version brouillon interdit.xml";
        accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(accuserReceptionResponse.getMessageErreur(),
                accuserReceptionResponse.getMessageErreur().contains("Vous n'avez pas le droit d'accuser réception de cette version"));

        // GVT publie la version complétée 1.1 -> 2.0
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0060 Publier version completee.xml";
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
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertFalse(horodatage2_0.equals(horodatage1_0));

        // AN accuse réception de la version active (trouve automatiquement la 2.0)
        filename = "fr/dila/solonepp/webengine/wsevenement/accuserReception/0070 Accuser reception version active.xml";
        accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(accuserReceptionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(accuserReceptionResponse.getEvenement());
        evt01Reponse = accuserReceptionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar dateAr2_0 = evt01Reponse.getDateAr();
        final XMLGregorianCalendar dateArVersion2_0 = evt01Reponse.getVersionCourante().getDateAr();
        Assert.assertNotNull(dateArVersion2_0);
        Assert.assertNotNull(dateAr2_0);
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(horodatage2_0, evt01Reponse.getHorodatage());
        
        Assert.assertEquals(2, evt01Reponse.getVersionDisponible().size());
        //vérifier la date AR des versions     
        for (final Version v : evt01Reponse.getVersionDisponible()) {
            if (v.getMajeur() == 1 && v.getMineur() == 0) {
                Assert.assertEquals(dateArVersion1_0, v.getDateAr());
            } else if (v.getMajeur() == 2 && v.getMineur() == 0) {
                Assert.assertEquals(dateArVersion2_0, v.getDateAr());
            } else {
                // On a une version dont le numéro de version n'est pas 1.0 ou 2.0
                // ça ne devrait pas être le cas
                Assert.fail("On a une version dont le numéro de version n'est pas 1.0 ou 2.0 - ça ne devrait pas être le cas");
            }
        }

    }
}
