package fr.dila.solonepp.webengine.wsepp;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.ChercherEvenementRequest;
import fr.sword.xsd.solon.epp.ChercherEvenementResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels des notifications de transitions des messages.
 * 
 * @author jtremeaux
 */
public class TestNotifierTransition extends AbstractEppWSTest {

    private static WSEpp wsEppGvt;

    private static WSEpp wsEppAn;

    private static WSEpp wsEppSenat;

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    private static WSEvenement wsEvenementSenat;

    @BeforeClass
    public static void setup() throws Exception {

        wsEppGvt = WSServiceHelper.getWSEppGvt();
        wsEppAn = WSServiceHelper.getWSEppAn();
        wsEppSenat = WSServiceHelper.getWSEppSenat();

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();
    }

    /**
     * Test d'accusé de réception :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire notifie la transition de son message à l'état EN_COURS_TRAITEMENT ;
     * - Destinataire notifie la transition de son message à l'état TRAITE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'accusé de réception", useDriver = false)
    public void testNotifierTransitionDeuxEtapes() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0010 Creer dossier EVT01 AGRT1100001Y.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());

        // Récupère les identifiants des documents créés pour les tests suivants
        final String idEvenement = creerVersionResponse.getEvenement().getEvt01().getIdEvenement();

        // AN notifie la transition "non traité" : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0020 Notifier non traite interdit.xml";
        NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur().contains("Interdiction de transitionner dans l'état"));

        // AN notifie la transition "en cours rédaction" : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0030 Notifier en cours redaction interdit.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur().contains("Interdiction de transitionner dans l'état"));

        // AN notifie la transition "en attente AR" : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0040 Notifier en attente AR interdit.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur().contains("Interdiction de transitionner dans l'état"));

        // AN notifie la transition "emis" : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0050 Notifier emis interdit.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur().contains("Interdiction de transitionner dans l'état"));

        // AN notifie la transition "AR recu" : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0060 Notifier AR recu interdit.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur().contains("Interdiction de transitionner dans l'état"));

        // GVT notifie la transition du message de l'émetteur : interdit
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0070 Notifier transition emetteur interdit.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppGvt.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(notifierTransitionResponse.getMessageErreur(),
                notifierTransitionResponse.getMessageErreur().contains("Il est interdit de notifier une transition sur le message de l'émetteur"));

        // AN notifie la transition "non traité" -> "en cours de traitement"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0100 Notifier en cours traitement.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie que le message du destinataire est à l'état "en cours de traitement" et l'événement à l'état "en instance"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        Message message = messageList.get(0);
        Assert.assertEquals(EtatMessage.EN_COURS_TRAITEMENT, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // AN notifie la transition "en cours de traitement" -> "traité"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0120 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie que le message du destinataire est à l'état "traité" et l'événement à l'état "en instance"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(EtatMessage.TRAITE, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

    }

    /**
     * Test d'accusé de réception :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire notifie la transition de son message à l'état TRAITE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'accusé de réception", useDriver = false)
    public void testNotifierTransitionUneEtape() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/1000 Creer dossier EVT01 AGRT1100002Y.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());

        // Récupère les identifiants des documents créés pour les tests suivants
        final String idEvenement = creerVersionResponse.getEvenement().getEvt01().getIdEvenement();

        // Vérifie que le message de l'expéditeur est à l'état "en attente AR" et l'événement à l'état "publié"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        Message message = messageList.get(0);
        Assert.assertEquals(EtatMessage.EN_ATTENTE_AR, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.PUBLIE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // AN notifie la transition "à traiter" -> "traité" (sans passer par "en cours de traitement")
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0120 Notifier traite.xml";
        NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie que le message de l'expéditeur est à l'état "AR reçu" et l'événement à l'état "en instance"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(EtatMessage.AR_RECU, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // Vérifie que le message du destinataire est à l'état "traité" et l'événement à l'état "en instance"
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(EtatMessage.TRAITE, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // AN notifie la transition "traité" -> "en cours de traitement" : sans effet (RG_MES_NOT_02)
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0100 Notifier en cours traitement.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // AN notifie la transition "traité" -> "traité" : sans effet (non spécifié)
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0120 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

    }

    /**
     * Test d'accusé de réception :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Emetteur crée une version complétée à l'état publié -> 2.0 PUBLIEE, 1.0 OBSOLETE ;
     * - Destinataire notifie la transition de son message à l'état TRAITE
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'accusé de réception", useDriver = false)
    public void testNotifierTransitionARManquant() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/2000 Creer dossier EVT01 AGRT1100003Y.xml";
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

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT publie une version complétée 2.0
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/2010 Publier version completee.xml";
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        
        // Vérifie que le message du destinataire est à l'état "non traité" (RG_DON_MES_03)
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0110 Rechercher message.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        final Message message = messageList.get(0);
        Assert.assertEquals(EtatMessage.NON_TRAITE, message.getEtatMessage());
        Assert.assertEquals(EtatEvenement.PUBLIE, message.getEtatEvenement());
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // AN notifie la transition "à traiter" -> "traité"
        // Cette notification provoque l'accusé de réception sur la version en cours ainsi que les précedentes
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0120 Notifier traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

     // GVT publie une version complétée 3.0 qui ne doit pas être AR
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/2010 Publier version completee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
        final Version version3_0 = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(version3_0);
        Assert.assertEquals(3, version3_0.getMajeur());
        Assert.assertEquals(0, version3_0.getMineur());
        Assert.assertNull(version3_0.getDateAr());
        final XMLGregorianCalendar horodatage3_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage3_0);
        final XMLGregorianCalendar dateArVersion1_0 = evt01Reponse.getVersionDisponible().get(2).getDateAr();
        final XMLGregorianCalendar dateArVersion2_0 = evt01Reponse.getVersionDisponible().get(1).getDateAr();
        Assert.assertNotNull(dateArVersion1_0);
        Assert.assertNotNull(dateArVersion2_0);
        Assert.assertEquals("dates AR version 1 et 2 doivent être identiques car notification transition sur version 2.0 => AR sur 1.0 aussi",
                dateArVersion1_0, 
                dateArVersion2_0);
        
        // On va chercher l'évènement pour vérifier que toutes les versions avant transition ont été AR
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/0130 Chercher message.xml";
        final ChercherEvenementRequest chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        final EvtId evenementId = new EvtId();
        evenementId.setId(idEvenement);
        chercherEvenementRequest.getIdEvenement().clear();
        chercherEvenementRequest.getIdEvenement().add(evenementId);
        final ChercherEvenementResponse chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
                
        final EppEvt01 evtResponse = chercherEvenementResponse.getEvenement().get(0).getEvt01();
        Assert.assertEquals("3 versions sont normalement disponibles", 3, evtResponse.getVersionDisponible().size());
        for (final Version v : evtResponse.getVersionDisponible()) {
            if (v.getMajeur() <= 2) {
                Assert.assertNotNull(v.getDateAr());
                if (v.getMajeur() == 1) {
                    Assert.assertEquals(dateArVersion1_0, v.getDateAr());
                } else {
                    Assert.assertEquals(dateArVersion2_0, v.getDateAr());
                }
            } else {
                if (v.getMajeur() == 3 && v.getMineur() == 0) {
                    // La version 3.0 n'a pas été récupérée par l'AN, donc AR inexistant
                    Assert.assertNull(v.getDateAr());
                } else {
                    // Pas d'autres versions ne devraient exister
                    Assert.fail();
                }
            }
        }
    }

    /**
     * Test d'accusé de réception :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire notifie la transition de son message à l'état TRAITE ;
     * - Copie notifie la transition de son message à l'état TRAITE ;
     * - Emetteur crée une version complétée à l'état publié -> 2.0 PUBLIEE, 1.0 OBSOLETE,
     *   les messages repassent à l'état NON_TRAITE ;
     * - Destinataire notifie la transition de son message à l'état TRAITE.
     * - Copie notifie la transition de son message à l'état TRAITE ;
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'accusé de réception", useDriver = false)
    public void testNotifierTransitionRetourNonTraite() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3000 Creer dossier EVT01 AGRT1100004Y.xml";
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

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN notifie la transition du message "en cours de traitement" -> "traité", accuse réception par effet de bord de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3020 Notifier traite.xml";
        NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // SENAT notifie la transition du message "en cours de traitement" -> "traité", accuse réception par effet de bord de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3020 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppSenat.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.TRAITE, horodatage1_0);

        // GVT publie une version complétée 2.0
        getFlog().action("GVT publie une version complétée 2.0");
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3030 Publier version completee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);

        // Vérifie le message de l'émetteur : version 2.0 publiée
        getFlog().check("Vérifie le message de l'émetteur : version 2.0 publiée");
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        getFlog().check("Vérifie le message du destinataire : version 2.0 publiée");
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        getFlog().check("Vérifie le message de la copie : version 2.0 publiée");
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // SENAT notifie la transition du message "en cours de traitement" -> "traité", accuse réception par effet de bord de la version 2.0
        getFlog()
                .action("SENAT notifie la transition du message \"en cours de traitement\" -> \"traité\", accuse réception par effet de bord de la version 2.0");
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3020 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppSenat.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie le message de l'émetteur : version 2.0 publiée
        getFlog().check("Vérifie le message de l'émetteur : version 2.0 publiée");
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        getFlog().check("Vérifie le message du destinataire : version 2.0 publiée");
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        getFlog().check("Vérifie le message de la copie : version 2.0 publiée");
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.TRAITE, horodatage2_0);

        // AN notifie la transition du message "en cours de traitement" -> "traité", accuse réception par effet de bord de la version 2.0
        getFlog().action(
                "AN notifie la transition du message \"en cours de traitement\" -> \"traité\", accuse réception par effet de bord de la version 2.0");
        filename = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3020 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Vérifie le message de l'émetteur : version 2.0 publiée
        getFlog().check("Vérifie le message de l'émetteur : version 2.0 publiée");
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        getFlog().check("Vérifie le message du destinataire : version 2.0 publiée");
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        getFlog().check("Vérifie le message de la copie : version 2.0 publiée");
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.TRAITE, horodatage2_0);

    }

    /**
     * Vérifie la présence d'un message dans la corbeille de l'institution.
     * 
     * @param wsEvenement WS evenement (AN / gouvernement / sénat)
     * @param idEvenement Identifiant technique de l'événement
     * @param etatMessage Etat du message
     * @param horodatage Horodatage (date publication de la version)
     * @throws Exception
     */
    protected void checkMessage(final WSEvenement wsEvenement, final String idEvenement, final EtatMessage etatMessage,
            final XMLGregorianCalendar horodatage) throws Exception {
        final String filenameRecherche = "fr/dila/solonepp/webengine/wsepp/notifierTransition/3020 Rechercher message.xml";
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
        Assert.assertEquals(etatMessage, message.getEtatMessage());
        if (horodatage != null) {
            Assert.assertNotNull(message.getDateEvenement());
            Assert.assertEquals(horodatage, message.getDateEvenement());
        } else {
            Assert.assertNull(message.getDateEvenement());
        }
    }
}
