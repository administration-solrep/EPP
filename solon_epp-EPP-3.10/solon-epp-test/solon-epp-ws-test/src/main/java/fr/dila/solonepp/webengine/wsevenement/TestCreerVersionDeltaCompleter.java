package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.CreerVersionDeltaRequest;
import fr.sword.xsd.solon.epp.CreerVersionDeltaResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvtDelta;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.PieceJointe;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;

/**
 * Tests fonctionnels des créations de version complétées en mode delta.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionDeltaCompleter extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    private static WSEvenement wsEvenementSenat;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();
    }

    /**
     * Test de complétion en mode delta à partir d'une version publiée :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une version delta pour complétion (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de complétion en mode delta à partir d'une version publiée", useDriver = false)
    public void testCreerVersionCompleterDeltaPublie() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/0010 Creer dossier EVT01 CCOZ1100001D publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
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

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // GVT publie la version 2.0 complétée en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/0020 Completer delta publier.xml";
        CreerVersionDeltaRequest creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        EppEvtDelta eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        CreerVersionDeltaResponse creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage1_0.equals(horodatage2_0));
        List<Mandat> coauteur = evt01Reponse.getCoAuteur();
        Assert.assertEquals(2, coauteur.size());
        Assert.assertEquals("MandatTest0", coauteur.get(0).getId());
        Assert.assertEquals("MandatTest1", coauteur.get(1).getId());
        Assert.assertTrue(StringUtils.isEmpty(evt01Reponse.getCommentaire()));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage2_0);

        // GVT publie la version 2.0 complétée en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/0030 Rectifier delta publier delete.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage1_0.equals(horodatage2_0));
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertEquals(1, coauteur.size());
        Assert.assertEquals("MandatTest0", coauteur.get(0).getId());
        Assert.assertTrue(StringUtils.isEmpty(evt01Reponse.getCommentaire()));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage2_0);

    }

    /**
     * Test de complétion en mode delta à partir d'une version brouillon :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une brouillon pour complétion (1.1) ;
     * - Emetteur publie une version delta pour complétion (2.0 publiée, 2.1 brouillon).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de complétion en mode delta à partir d'une version brouillon", useDriver = false)
    public void testCreerVersionCompleterDeltaBrouillon() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/2000 Creer dossier EVT01 CCOZ1100002D publie.xml";
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

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // GVT crée la version 1.1 brouillon pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/2010 Creer version brouillon pour completion.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        final EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Commentaire complété", evt01Reponse.getCommentaire());

        // GVT publie la version 2.0 complétée en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/2020 Completer delta publier.xml";
        final CreerVersionDeltaRequest creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        final EppEvtDelta eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        final CreerVersionDeltaResponse creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage1_0.equals(horodatage2_0));
        Assert.assertEquals("Commentaire complété delta", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 2.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, null);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage2_0);
    }

    /**
     * Test de complétion en mode delta d'une version publiée, vérifications sur les pièces jointes :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une version delta pour complétion (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de complétion en mode delta d'une version publiée, vérifications sur les pièces jointes", useDriver = false)
    public void testCreerVersionCompleterDeltaPieceJointe() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4000 Creer dossier EVT01 CCOZ1100004D publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
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

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // GVT publie la version 2.0 complétée en mode delta : réinitialise et retransmet toutes les données
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4010 Completer delta publier PJ identique.xml";
        CreerVersionDeltaRequest creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        EppEvtDelta eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        eppEvtDelta.getPieceJointe().get(0).getValeur().get(0).setIdInterneEpp(pieceJointeTexteId);
        eppEvtDelta.getPieceJointe().get(1).getValeur().get(0).setIdInterneEpp(pieceJointeExposeDesMotifsId);
        eppEvtDelta.getPieceJointe().get(2).getValeur().get(0).setIdInterneEpp(pieceJointeDecretPresentationId);
        eppEvtDelta.getPieceJointe().get(3).getValeur().get(0).setIdInterneEpp(pieceJointeLettrePmId);
        CreerVersionDeltaResponse creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(pieceJointeTexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertEquals(pieceJointeExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertEquals(pieceJointeDecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertEquals(pieceJointeLettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());

        // GVT publie la version 3.0 complétée en mode delta : réinitialise sans retransmettre les fichiers = comme le mode non delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4020 Completer delta publier reinitialiser sans fichiers.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        eppEvtDelta.getPieceJointe().get(0).getValeur().get(0).setIdInterneEpp(pieceJointeTexteId);
        eppEvtDelta.getPieceJointe().get(1).getValeur().get(0).setIdInterneEpp(pieceJointeExposeDesMotifsId);
        eppEvtDelta.getPieceJointe().get(2).getValeur().get(0).setIdInterneEpp(pieceJointeDecretPresentationId);
        eppEvtDelta.getPieceJointe().get(3).getValeur().get(0).setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(pieceJointeTexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertEquals(pieceJointeExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertEquals(pieceJointeDecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertEquals(pieceJointeLettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());

        // Récupère les identifiants générés pour les tests suivants
        List<PieceJointe> pieceJointeList = evt01Reponse.getAutres();
        Assert.assertEquals(1, pieceJointeList.size());
        final String pieceJointeAutre0Id = pieceJointeList.get(0).getIdInterneEpp();

        // GVT publie la version 4.0 complétée en mode delta : ajoute une PJ à une liste multivaluée en mode réinitialiser
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4030 Completer delta publier reinitialiser ajout PJ.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        eppEvtDelta.getPieceJointe().get(0).getValeur().get(0).setIdInterneEpp(pieceJointeAutre0Id);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(4, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(pieceJointeTexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertEquals(pieceJointeExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertEquals(pieceJointeDecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertEquals(pieceJointeLettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());
        pieceJointeList = evt01Reponse.getAutres();
        Assert.assertEquals(2, pieceJointeList.size());

        // Récupère les identifiants générés pour les tests suivants
        //        String pieceJointeAutre1Id = pieceJointeList.get(1).getIdInterneEpp();

        // GVT publie la version 5.0 complétée en mode delta : ajout de pièces jointes en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4040 Completer delta publier ajout PJ.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(5, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(pieceJointeTexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertEquals(pieceJointeExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertEquals(pieceJointeDecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertEquals(pieceJointeLettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());
        pieceJointeList = evt01Reponse.getAutres();
        Assert.assertEquals(3, pieceJointeList.size());

        // Récupère les identifiants générés pour les tests suivants
        final String pieceJointeAutre2Id = pieceJointeList.get(2).getIdInterneEpp();
        Assert.assertNotNull(pieceJointeAutre2Id);
        final String pieceJointeEtudeImpact = evt01Reponse.getEtudeImpact().getIdInterneEpp();
        Assert.assertNotNull(pieceJointeEtudeImpact);

        // GVT publie la version 6.0 complétée en mode delta : ajout de fichiers de pièces jointes en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4050 Completer delta publier ajout fichier PJ.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        eppEvtDelta.getPieceJointe().get(0).getValeur().get(0).setIdInterneEpp(pieceJointeEtudeImpact);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionDeltaResponse.getEvenement());
        evt01Reponse = creerVersionDeltaResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(6, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(pieceJointeTexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertEquals(pieceJointeExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertEquals(pieceJointeDecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertEquals(pieceJointeLettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());
        Assert.assertEquals(pieceJointeEtudeImpact, evt01Reponse.getEtudeImpact().getIdInterneEpp());
        Assert.assertEquals("Etude impact modifiée", evt01Reponse.getEtudeImpact().getLibelle());
        Assert.assertEquals("http://www.etude_impact_modif.fr", evt01Reponse.getEtudeImpact().getUrl());
        Assert.assertEquals(1, evt01Reponse.getEtudeImpact().getFichier().size());
        pieceJointeList = evt01Reponse.getAutres();
        Assert.assertEquals(3, pieceJointeList.size());

        // GVT publie une version complétée en mode delta : suppression de PJ non obligatoire : autorisé
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4060 Completer delta supprimer PJ.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT publie une version complétée en mode delta : suppression de PJ non obligatoire d'une liste : autorisé
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4070 Completer delta supprimer PJ multivalue.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT publie une version complétée en mode delta : ajout d'un type de pièce jointe qui ne fait pas partie du type d'événement : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/4080 Completer delta ajouter PJ type interdit.xml";
        creerVersionDeltaRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionDeltaRequest.class);
        eppEvtDelta = creerVersionDeltaRequest.getEvenement();
        eppEvtDelta.setIdEvenement(idEvenement);
        creerVersionDeltaResponse = wsEvenementGvt.creerVersionDelta(creerVersionDeltaRequest);
        Assert.assertNotNull(creerVersionDeltaResponse);
        traitementStatut = creerVersionDeltaResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionDeltaResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionDeltaResponse.getMessageErreur(),
                creerVersionDeltaResponse.getMessageErreur().contains("La pièce jointe AVIS n'existe pas"));

    }

    /**
     * Vérifie la présence d'un message dans la corbeille de l'institution.
     * 
     * @param wsEvenement WS evenement (AN / gouvernement / sénat)
     * @param idEvenement Identifiant technique de l'événement
     * @param horodatage Horodatage (date publication de la version)
     * @throws Exception
     */
    protected void checkMessage(final WSEvenement wsEvenement, final String idEvenement, final XMLGregorianCalendar horodatage) throws Exception {
        final String filenameRecherche = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaCompleter/0015 Rechercher message.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filenameRecherche,
                RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenement.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        final TraitementStatut traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertEquals(1, messageList.size());
        final Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        if (horodatage != null) {
            Assert.assertNotNull(message.getDateEvenement());
            Assert.assertEquals(horodatage, message.getDateEvenement());
        }
    }
}
