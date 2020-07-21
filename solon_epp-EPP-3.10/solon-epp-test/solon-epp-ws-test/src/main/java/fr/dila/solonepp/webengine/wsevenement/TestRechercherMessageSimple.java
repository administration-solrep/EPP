package fr.dila.solonepp.webengine.wsevenement;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEpp;
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
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvt02;
import fr.sword.xsd.solon.epp.EppEvt28;
import fr.sword.xsd.solon.epp.EtatDossier;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NiveauLecture;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.Order;
import fr.sword.xsd.solon.epp.OrderBy;
import fr.sword.xsd.solon.epp.OrderField;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;

/**
 * Tests fonctionnels de recherche simple des messages.
 * 
 * @author jtremeaux
 */
public class TestRechercherMessageSimple extends AbstractEppWSTest {

    private static WSEpp wsEppAn;

    private static WSEpp wsEppSenat;

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    private static WSEvenement wsEvenementSenat;

    @BeforeClass
    public static void setup() throws Exception {

        wsEppAn = WSServiceHelper.getWSEppAn();
        wsEppSenat = WSServiceHelper.getWSEppSenat();

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();
    }

    /**
     * Ce test vérifie la recherche de messages par critères.
     * - Emetteur crée un dossier, un événement et une version à l'état publiée ;
     * - Destinataire recherche suivant tous les critèrse possibles (filtres positifs et négatifs).
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche de messages par critères", useDriver = false)
    public void testRechercherMessageSimple() throws Exception {
        // GVT crée un dossier, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0010 Creer dossier EVT01 EFIM1100001R.xml";
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

        // Récupère les données de l'événement créé pour les tests suivants
        final String idDossier = evt01Reponse.getIdDossier();
        final String idEvenement = evt01Reponse.getIdEvenement();
        final XMLGregorianCalendar dateEvenement = evt01Reponse.getHorodatage();
        final String objet = evt01Reponse.getObjet();

        // GVT recherche le message par identifiant technique d'événement
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
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
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertEquals(1, messageList.size());
        Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertNotNull(message.getDateEvenement());

        // AN recherche le message par identifiant technique d'événement + tous les critères : 1 message
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setEtatMessage(EtatMessage.NON_TRAITE);
        critereRechercheEvenement.setEtatDossier(EtatDossier.EN_INSTANCE);
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdDossier(idDossier);
        critereRechercheEvenement.setTypeEvenement(EvenementType.EVT_01);
        critereRechercheEvenement.setEmetteur(Institution.GOUVERNEMENT);
        critereRechercheEvenement.setDestinataire(Institution.ASSEMBLEE_NATIONALE);
        critereRechercheEvenement.setCopie(Institution.SENAT);
        critereRechercheEvenement.setIdCorbeille("CORBEILLE_AN_SEANCE_RECEPTION");
        critereRechercheEvenement.setEtatEvenement(EtatEvenement.PUBLIE);
        critereRechercheEvenement.setObjet("evenement pour la rech");
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        GregorianCalendar dateMin = new GregorianCalendar();
        dateMin.setTime(format.parse("20110101"));
        GregorianCalendar dateMax = new GregorianCalendar();
        dateMax.setTime(format.parse("20990101"));
        NiveauLecture niveauLecture = new NiveauLecture();
        critereRechercheEvenement.setNiveauLecture(niveauLecture);
        niveauLecture.setNiveau(1);
        niveauLecture.setCode(NiveauLectureCode.AN);
        critereRechercheEvenement.setDateEvenementMin(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateMin));
        critereRechercheEvenement.setDateEvenementMax(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateMax));
        critereRechercheEvenement.setPresencePieceJointe(true);
        //TODO Critère de recherche n° dépot
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(idDossier, message.getIdDossier());
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, message.getEtatEvenement());
        Assert.assertEquals(EvenementType.EVT_01, message.getTypeEvenement());
        Assert.assertEquals(dateEvenement, message.getDateEvenement());
        Assert.assertEquals(Institution.GOUVERNEMENT, message.getEmetteurEvenement());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, message.getDestinataireEvenement());
        final List<Institution> copieEvenement = message.getCopieEvenement();
        Assert.assertNotNull(copieEvenement);
        Assert.assertTrue(message.getCopieEvenement().contains(Institution.SENAT));
        Assert.assertEquals(objet, message.getObjet());

        // AN recherche le message par identifiant technique d'événement + corbeille : 0 message (filtré par la corbeille)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdCorbeille("CORBEILLE_AN_SEANCE_ENVOI");
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + état événement : 0 message (filtré par l'état événement)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setEtatEvenement(EtatEvenement.BROUILLON);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + type événement : 0 message (filtré par type événement)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setTypeEvenement(EvenementType.EVT_02);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + émetteur : 0 message (filtré par émetteur)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setEmetteur(Institution.SENAT);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + destinataire : 0 message (filtré par destinataire)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setDestinataire(Institution.SENAT);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + copie : 0 message (filtré par copie)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setCopie(Institution.GOUVERNEMENT);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + id dossier : 0 message (filtré par l'Id dossier)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdDossier("DOSSIER_NON_EXISTANT");
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + dossier alerte : 0 message (filtré par l'état du dossier)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setEtatDossier(EtatDossier.ALERTE);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + objet : 0 message (filtré par l'objet)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setObjet("OBJET INEXISTANT");
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + date min horodatage : 0 message (filtré par la date min)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        dateMin = new GregorianCalendar();
        dateMin.setTime(format.parse("20990101"));
        critereRechercheEvenement.setDateEvenementMin(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateMin));
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + date max horodatage : 0 message (filtré par la date max)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        dateMax = new GregorianCalendar();
        dateMax.setTime(format.parse("20100101"));
        critereRechercheEvenement.setDateEvenementMax(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateMax));
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + niveau de lecture (code) : 0 message (filtré par le niveau de lecture)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        niveauLecture = new NiveauLecture();
        critereRechercheEvenement.setNiveauLecture(niveauLecture);
        niveauLecture.setCode(NiveauLectureCode.SENAT);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + niveau de lecture (numéro) : 0 message (filtré par le niveau de lecture)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        niveauLecture = new NiveauLecture();
        critereRechercheEvenement.setNiveauLecture(niveauLecture);
        niveauLecture.setNiveau(2);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // AN recherche le message par identifiant technique d'événement + numéro de dépôt : 0 message (filtré par numéro de dépôt)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        final Depot depot = new Depot();
        depot.setNumero("NUMERO_DEPOT_INEXISTANT");
        critereRechercheEvenement.setNumeroDepot(depot);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

    }

    /**
     * Ce test vérifie la recherche simple de messages avec un ID sénat, et la remontée de l'ID sénat.
     * - Sénat crée un dossier et renseigne l'ID sénat ;
     * - Sénat recherche le message par ID sénat ;
     * - Gouvernement recherche le message par ID sénat : critère sans effet pour cette institution.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de messages avec un ID sénat, et la remontée de l'ID sénat", useDriver = false)
    public void testRechercherMessageIdSenat() throws Exception {

        // Sénat crée un dossier, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/1000 Creer dossier EVT02 EFIM1100002R publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementSenat.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt02 evt02Reponse = creerVersionResponse.getEvenement().getEvt02();
        Assert.assertNotNull(evt02Reponse);
        Assert.assertNotNull(evt02Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt02Reponse.getEtat());
        Assert.assertNotNull(evt02Reponse.getVersionCourante());
        Assert.assertNotNull(evt02Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt02Reponse.getIdEvenement();
        final String idSenat = evt02Reponse.getIdSenat();

        // Sénat recherche le message par identifiant technique d'événement + ID sénat : 1 message
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdSenat(idSenat);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementSenat.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertEquals(1, messageList.size());
        Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertNotNull(message.getDateEvenement());
        Assert.assertEquals(idSenat, message.getIdSenat());

        // Sénat recherche le message par identifiant technique d'événement + ID sénat : 0 message (filtré par l'ID sénat)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdSenat("ID sénat inexistant");
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementSenat.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertTrue(messageList.isEmpty());

        // Gouvernement recherche le message par identifiant technique d'événement + ID sénat : 1 message (critère non pris en compte)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setIdSenat("ID sénat inexistant");
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertNotNull(message.getDateEvenement());
        Assert.assertTrue(StringUtils.isBlank(message.getIdSenat()));

    }

    /**
     * Ce test vérifie la recherche de messages par critères.
     * - Emetteur crée 3 dossiers ; 
     * - Destinataire effectue une recherche paginée avec 2 document par page.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche de messages par critères", useDriver = false)
    public void testRechercherMessagePagination() throws Exception {

        // GVT crée un dossier EFIM1100010R, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/2000 Creer dossier EVT01 EFIM1100010R publie.xml";
        CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionRequest.getEvenement().getEvt01().setIdDossier("EFIM1100010R");
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement0 = evt01Reponse.getIdEvenement();

        // GVT crée un dossier EFIM1100011R, un événement et une version publiée 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/2000 Creer dossier EVT01 EFIM1100010R publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionRequest.getEvenement().getEvt01().setIdDossier("EFIM1100011R");
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement1 = evt01Reponse.getIdEvenement();

        // GVT crée un dossier EFIM1100011R, un événement et une version publiée 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/2000 Creer dossier EVT01 EFIM1100010R publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionRequest.getEvenement().getEvt01().setIdDossier("EFIM1100012R");
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement2 = evt01Reponse.getIdEvenement();

        // GVT recherche les messages (1ère page)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/2010 Rechercher message paginee par objet.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        OrderBy order = new OrderBy();
        order.setField(OrderField.HORODATAGE);
        order.setOrder(Order.DESC);
        rechercherEvenementRequest.getPagination().setNumeroPage(0);
        
        rechercherEvenementRequest.getParCritere().getOrderBy().add(order);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertEquals(2, messageList.size());
        Message message = messageList.get(0);
        //TODO trier, tester l'ordre en retour -> manque le tri dans le XSD
        final Set<String> evenementSet = new HashSet<String>();
        evenementSet.add(idEvenement0);
        evenementSet.add(idEvenement1);
        evenementSet.add(idEvenement2);
        Assert.assertTrue(evenementSet.contains(message.getIdEvenement()));
        message = messageList.get(1);
        Assert.assertTrue(evenementSet.contains(message.getIdEvenement()));

        // GVT recherche les messages (2ème page)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/2010 Rechercher message paginee par objet.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        rechercherEvenementRequest.getPagination().setNumeroPage(1);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        //On cherche à vérifier qu'on a au moins un résultat mais qu'avec la pagination on en a au maximum 2
        Assert.assertTrue(1 == messageList.size() || 2 == messageList.size());
        message = messageList.get(0);
        Assert.assertTrue(evenementSet.contains(message.getIdEvenement()));

    }

    /**
     * Ce test vérifie la recherche simple de message par état de message.
     * - Emetteur crée un dossier / EVT01 / version à l'état publié ;
     * - Emetteur crée un événement successif EVT28 (ne nécessitant pas d'AR) / version à l'état brouillon ;
     * - Recherche de tous les types d'état possibles pour les messages.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par état de message", useDriver = false)
    public void testRechercherMessageEtatMessageSansAr() throws Exception {

        // GVT crée un dossier, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/4000 Creer dossier EVT01 EFIM1100040R publie.xml";
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement01 = evt01Reponse.getIdEvenement();

        // GVT crée un événement successif ne nécessitant pas d'AR et une version brouillon 0.1
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/4010 Creer dossier EVT28 EFIM1100040R brouillon.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionRequest.getEvenement().getEvt28().setIdEvenementPrecedent(idEvenement01);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        EppEvt28 evt28Reponse = creerVersionResponse.getEvenement().getEvt28();
        Assert.assertNotNull(evt28Reponse);
        Assert.assertNotNull(evt28Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt28Reponse.getEtat());
        Assert.assertNotNull(evt28Reponse.getVersionCourante());
        Assert.assertNotNull(evt28Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement28 = evt28Reponse.getIdEvenement();

        // Gouvernement recherche le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement28, EtatMessage.EN_COURS_REDACTION, null, null);

        // GVT publie la version 0.1 -> 1.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/4020 Publier version.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.getEvenement().getEvt28().setIdEvenement(idEvenement28);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt28Reponse = creerVersionResponse.getEvenement().getEvt28();
        Assert.assertNotNull(evt28Reponse);
        Assert.assertNotNull(evt28Reponse.getIdEvenement());
        Assert.assertNotNull(evt28Reponse.getVersionCourante());
        Assert.assertEquals(1, evt28Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt28Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt28Reponse.getHorodatage());

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement28, EtatMessage.EMIS, EtatMessage.NON_TRAITE, null);

        // AN accuse réception de la version active
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3020 Accuser reception destinataire.xml";
        final AccuserReceptionRequest accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement28);
        final AccuserReceptionResponse accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
        Assert.assertNotNull(accuserReceptionResponse);
        traitementStatut = accuserReceptionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(accuserReceptionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(accuserReceptionResponse.getEvenement());
        evt28Reponse = accuserReceptionResponse.getEvenement().getEvt28();
        Assert.assertNotNull(evt28Reponse);
        Assert.assertNotNull(evt28Reponse.getIdEvenement());
        Assert.assertNotNull(evt28Reponse.getVersionCourante());
        Assert.assertEquals(1, evt28Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt28Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar dateAr2_0 = evt28Reponse.getDateAr();
        Assert.assertNotNull(dateAr2_0);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement28, EtatMessage.EMIS, EtatMessage.NON_TRAITE, null);

        // AN notifie la transition "non traité" -> "en cours de traitement"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3030 Notifier en cours traitement.xml";
        NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement28);
        Assert.assertNotNull(notifierTransitionRequest);
        NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement28, EtatMessage.EMIS, EtatMessage.EN_COURS_TRAITEMENT, null);

        // AN notifie la transition "en cours de traitement" -> "traité"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3040 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement28);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement28, EtatMessage.EMIS, EtatMessage.TRAITE, null);

    }

    /**
     * Ce test vérifie la recherche simple de message par état de message.
     * - Emetteur crée un dossier / EVT01 (nécessitant un AR) / version à l'état brouillon ;
     * - Recherche de tous les types d'état possibles pour les messages.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par état de message", useDriver = false)
    public void testRechercherMessageEtatMessageAr() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état brouillon 0.1
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3000 Creer dossier EVT01 EFIM1100030R brouillon.xml";
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
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();

        // Gouvernement recherche le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.EN_COURS_REDACTION, null, null);

        String idTexte = creerVersionResponse.getEvenement().getEvt01().getTexte().getIdInterneEpp();
        String idExpose = creerVersionResponse.getEvenement().getEvt01().getExposeDesMotifs().getIdInterneEpp();
        String idDecret = creerVersionResponse.getEvenement().getEvt01().getDecretPresentation().getIdInterneEpp();
        String idLettre = creerVersionResponse.getEvenement().getEvt01().getLettrePm().getIdInterneEpp();

        // GVT publie la version 0.1 -> 1.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3010 Publier version.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.getEvenement().getEvt01().setIdEvenement(idEvenement);
        creerVersionRequest.getEvenement().getEvt01().getTexte().setIdInterneEpp(idTexte);
        creerVersionRequest.getEvenement().getEvt01().getExposeDesMotifs().setIdInterneEpp(idExpose);
        creerVersionRequest.getEvenement().getEvt01().getDecretPresentation().setIdInterneEpp(idDecret);
        creerVersionRequest.getEvenement().getEvt01().getLettrePm().setIdInterneEpp(idLettre);

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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.EN_ATTENTE_AR, EtatMessage.NON_TRAITE, EtatMessage.NON_TRAITE);

        // AN accuse réception de la version active
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3020 Accuser reception destinataire.xml";
        final AccuserReceptionRequest accuserReceptionRequest = JaxBHelper.buildRequestFromFile(filename, AccuserReceptionRequest.class);
        accuserReceptionRequest.getIdEvenement().setId(idEvenement);
        final AccuserReceptionResponse accuserReceptionResponse = wsEvenementAn.accuserReception(accuserReceptionRequest);
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
        final XMLGregorianCalendar dateAr2_0 = evt01Reponse.getDateAr();
        Assert.assertNotNull(dateAr2_0);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.AR_RECU, EtatMessage.NON_TRAITE, EtatMessage.NON_TRAITE);

        // AN notifie la transition "non traité" -> "en cours de traitement"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3030 Notifier en cours traitement.xml";
        NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.AR_RECU, EtatMessage.EN_COURS_TRAITEMENT, EtatMessage.NON_TRAITE);

        // Sénat notifie la transition "non traité" -> "en cours de traitement"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3030 Notifier en cours traitement.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppSenat.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.AR_RECU, EtatMessage.EN_COURS_TRAITEMENT, EtatMessage.EN_COURS_TRAITEMENT);

        // AN notifie la transition "en cours de traitement" -> "traité"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3040 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.AR_RECU, EtatMessage.TRAITE, EtatMessage.EN_COURS_TRAITEMENT);

        // Sénat notifie la transition "non traité" -> "en cours de traitement"
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/3040 Notifier traite.xml";
        notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        notifierTransitionResponse = wsEppSenat.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // Les 3 institutions recherchent le message par identifiant technique d'événement + état message
        rechercherMessageEtat(idEvenement, EtatMessage.AR_RECU, EtatMessage.TRAITE, EtatMessage.TRAITE);

    }

    /**
     * Ce test vérifie la recherche simple de message par état de message.
     * - Emetteur crée un dossier / EVT01 / version à l'état publié comportant une pièce jointe ;
     * - Destinataire recherche le message avec le critère de présence de pièce jointe.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par état de message", useDriver = false)
    public void testRechercherMessagePresencePieceJointe() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état publié 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/5000 Creer dossier EVT01 EFIM1100050R publie.xml";
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

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();

        // AN recherche le message par identifiant technique d'événement + présence de pièce jointe
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setPresencePieceJointe(true);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertFalse(messageList.isEmpty());
        final Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertTrue(message.isPresencePieceJointe());

    }

    /**
     * Ce test vérifie la recherche simple de message par état de message.
     * - Emetteur crée un dossier / EVT01 / version à l'état publié comportant une pièce jointe ;
     * - Destinataire recherche le message avec le critère de présence de pièce jointe.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par état de message", useDriver = false)
    public void testRechercherMessagePresencePieceJointeFiltre() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état publié 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/5000 Creer dossier EVT01 EFIM1100050R publie.xml";
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

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();

        // AN recherche le message par identifiant technique d'événement + présence de pièce jointe : 0 message (filtré par la présence de pièce jointe)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setPresencePieceJointe(true);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenementAn.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertFalse(messageList.isEmpty());

    }

    /**
     * Ce test vérifie la recherche simple de message par numéro de dépôt.
     * - Emetteur crée un dossier / EVT02 / version à l'état publié comportant un numéro de dépôt ;
     * - Destinataire recherche le message avec le critère de numéro de dépôt.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par numéro de dépôt", useDriver = false)
    public void testRechercherMessageNumeroDepot() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état publié 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/6000 Creer dossier EVT02 EFIM1100060R publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt02 evt02Reponse = creerVersionResponse.getEvenement().getEvt02();
        Assert.assertNotNull(evt02Reponse);
        Assert.assertNotNull(evt02Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt02Reponse.getEtat());
        Assert.assertNotNull(evt02Reponse.getVersionCourante());
        Assert.assertNotNull(evt02Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt02Reponse.getIdEvenement();

        // AN recherche le message par identifiant technique d'événement + numéro de dépôt
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        final Depot depot = new Depot();
        depot.setNumero("DEPOT_1");
        critereRechercheEvenement.setNumeroDepot(depot);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertFalse(messageList.isEmpty());
        final Message message = messageList.get(0);
        Assert.assertEquals("DEPOT_1", message.getNumeroDepot().getNumero());

    }

    /**
     * Ce test vérifie la recherche simple de message par numéro de dépôt.
     * - Emetteur crée un dossier / EVT02 / version à l'état publié comportant un numéro de dépôt ;
     * - Destinataire recherche le message avec le critère de numéro de dépôt.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche simple de message par numéro de dépôt", useDriver = false)
    public void testRechercherMessageParCorbeille() throws Exception {

        // AN recherche le message par identifiant technique d'événement + numéro de dépôt
        final String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/7000 Rechercher message par corbeille.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);

        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        final TraitementStatut traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        Assert.assertFalse(messageList.isEmpty());
        Assert.assertTrue(messageList.size() > 0);

    }

    /**
     * Vérifie l'état des messages pour les 3 institutions.
     * Effectue une recherche pour tous les états de messages possibles.
     * 
     * @param idEvenement Identifiant techinque de l'événement
     * @param Etat du message pour l'institution GOUVERNEMENT
     * @param Etat du message pour l'institution ASSEMBLEE_NATIONALE
     * @param Etat du message pour l'institution SENAT
     * @throws Exception
     */
    private void rechercherMessageEtat(final String idEvenement, final EtatMessage etatMessageGvt, final EtatMessage etatMessageAn,
            final EtatMessage etatMessageSenat) throws Exception {
        final WSEvenement[] wsEvenement = { wsEvenementGvt, wsEvenementAn, wsEvenementSenat };
        final EtatMessage[] etatMessage = { etatMessageGvt, etatMessageAn, etatMessageSenat };
        for (int i = 0; i < etatMessage.length; i++) {
            if (etatMessage[i] == null) {
                continue;
            }
            for (final EtatMessage etatMessageToTest : EtatMessage.values()) {
                final boolean etatActif = etatMessage[i] == etatMessageToTest;
                rechercherMessageEtat(wsEvenement[i], idEvenement, etatMessageToTest, etatActif);
            }
        }
    }

    /**
     * Recherche un événement suivant l'état du message.
     * 
     * @param wsEvenement WS événement
     * @param idEvenement Identifiant technique de l'événement
     * @param etatMessage Etat du message
     * @param resultat Vrai si la recherche doit retourner un (unique) résultat
     * @throws Exception
     */
    protected void rechercherMessageEtat(final WSEvenement wsEvenement, final String idEvenement, final EtatMessage etatMessage,
            final boolean resultat) throws Exception {
        // Gouvernement recherche le message par identifiant technique d'événement + état BROUILLON : 1 message
        final String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageSimple/0020 Rechercher message par id_evenement.xml";
        final RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        final CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        critereRechercheEvenement.setEtatMessage(etatMessage);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        final RechercherEvenementResponse rechercherEvenementResponse = wsEvenement.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        final TraitementStatut traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final List<Message> messageList = rechercherEvenementResponse.getMessage();
        Assert.assertNotNull(messageList);
        if (resultat) {
            Assert.assertEquals(1, messageList.size());
            final Message message = messageList.get(0);
            Assert.assertEquals(idEvenement, message.getIdEvenement());
        } else {
            Assert.assertEquals(0, messageList.size());
        }
    }
}
