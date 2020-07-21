package fr.dila.solonepp.webengine.wsevenement;

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
import fr.sword.xsd.solon.epp.AnnulerEvenementRequest;
import fr.sword.xsd.solon.epp.AnnulerEvenementResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;

/**
 * Tests fonctionnels des annulations d'événement.
 * 
 * @author jtremeaux
 */
public class TestAnnulerEvenement extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    private static WSEvenement wsEvenementSenat;

    private static WSEpp wsEppAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();

        wsEppAn = WSServiceHelper.getWSEppAn();
    }

    /**
     * Test d'annulation d'un événement :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Emetteur annule l'événement -> 1.0 OBSOLETE, 2.0 ANNULE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'annulation d'un événement", useDriver = false)
    public void testAnnulerEvenement() throws Exception {
        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/0010 Creer dossier EVT01 EFIM1100008V.xml";
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

        // Récupère les identifiants des documents créés pour les tests suivants
        final String idEvenement = creerVersionResponse.getEvenement().getEvt01().getIdEvenement();

        // GVT annule l'événement -> 2.0 ANNULE, 1.0 OBSOLETE
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/0020 Annuler evenement.xml";
        final AnnulerEvenementRequest annulerEvenementRequest = JaxBHelper.buildRequestFromFile(filename, AnnulerEvenementRequest.class);
        annulerEvenementRequest.setIdEvenement(idEvenement);
        final AnnulerEvenementResponse annulerEvenementResponse = wsEvenementGvt.annulerEvenement(annulerEvenementRequest);
        Assert.assertNotNull(annulerEvenementResponse);
        traitementStatut = annulerEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(annulerEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(annulerEvenementResponse.getEvenement());
        evt01Reponse = annulerEvenementResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.ANNULE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertTrue(!horodatage2_0.equals(horodatage1_0));

    }

    /**
     * Test d'annulation d'un événement :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe à "EN_INSTANCE" ;
     * - Emetteur demande l'annulation de l'événement -> version 1.1 EN_ATTENTE_VALIDATION ;
     * - Destinataire refuse la demande d'annulation -> 1.1 REJETE ;
     * - Emetteur demande l'annulation de l'événement -> version 1.2 EN_ATTENTE_VALIDATION ;
     * - Emetteur annule la demande d'annulation -> 1.2 ABANDONNE ;
     * - Emetteur demande l'annulation de l'événement -> version 1.3 EN_ATTENTE_VALIDATION ;
     * - Destinataire accepte la demande d'annulation -> 2.0 ANNULE, 1.0 OBSOLETE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'annulation d'un événement", useDriver = false)
    public void testAnnulerEvenementValidation() throws Exception {
        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1000 Creer dossier EVT01 EFIM1100009V.xml";
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
        Assert.assertEquals(Institution.GOUVERNEMENT, evt01Reponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt01Reponse.getDestinataire());
        final List<Institution> copieList = evt01Reponse.getCopie();
        Assert.assertNotNull(copieList);
        Assert.assertEquals(1, copieList.size());
        Assert.assertEquals(Institution.SENAT, copieList.get(0));

        // Récupère l'identifiant de l'événement créé pour les tests suivants
        final String idEvenement = creerVersionResponse.getEvenement().getEvt01().getIdEvenement();

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.PUBLIE, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.PUBLIE, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.PUBLIE, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accuse réception de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1010 Notifier transition traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT demande l'annulation de l'événement -> 1.1 en attente de validation, pour annulation
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/0020 Annuler evenement.xml";
        AnnulerEvenementRequest annulerEvenementRequest = JaxBHelper.buildRequestFromFile(filename, AnnulerEvenementRequest.class);
        annulerEvenementRequest.setIdEvenement(idEvenement);
        AnnulerEvenementResponse annulerEvenementResponse = wsEvenementGvt.annulerEvenement(annulerEvenementRequest);
        Assert.assertNotNull(annulerEvenementResponse);
        traitementStatut = annulerEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(annulerEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(annulerEvenementResponse.getEvenement());
        evt01Reponse = annulerEvenementResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.1 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.1 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT rejette le rectificatif : interdit, seul le destinataire peut rejetter le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1020 Valider version rejeter.xml";
        ValiderVersionRequest validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        ValiderVersionResponse validerVersionResponse = wsEvenementGvt.validerVersion(validerVersionRequest);
        Assert.assertNotNull(validerVersionResponse);
        traitementStatut = validerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(validerVersionResponse.getMessageErreur(),
                validerVersionResponse.getMessageErreur().contains("Seule l'institution destinataire peut"));

        // AN rejette le rectificatif : 1.1 rejetée
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1020 Valider version rejeter.xml";
        validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        validerVersionResponse = wsEvenementAn.validerVersion(validerVersionRequest);
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
        Assert.assertEquals(horodatage1_0, evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT demande l'annulation de l'événement -> 1.2 en attente de validation, pour annulation
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/0020 Annuler evenement.xml";
        annulerEvenementRequest = JaxBHelper.buildRequestFromFile(filename, AnnulerEvenementRequest.class);
        annulerEvenementRequest.setIdEvenement(idEvenement);
        annulerEvenementResponse = wsEvenementGvt.annulerEvenement(annulerEvenementRequest);
        Assert.assertNotNull(annulerEvenementResponse);
        traitementStatut = annulerEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(annulerEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(annulerEvenementResponse.getEvenement());
        evt01Reponse = annulerEvenementResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.2 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.2 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN abandonne le rectificatif : interdit, seul l'émetteur peut abandonner le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1030 Valider version abandonner.xml";
        validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        validerVersionResponse = wsEvenementAn.validerVersion(validerVersionRequest);
        Assert.assertNotNull(validerVersionResponse);
        traitementStatut = validerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(validerVersionResponse.getMessageErreur(),
                validerVersionResponse.getMessageErreur().contains("Seule l'institution émettrice"));

        // GVT abandonne le rectificatif : 1.2 abandonnée
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1030 Valider version abandonner.xml";
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
        Assert.assertEquals(horodatage1_0, evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.EN_INSTANCE, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT demande l'annulation de l'événement -> 1.3 en attente de validation, pour annulation
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/0020 Annuler evenement.xml";
        annulerEvenementRequest = JaxBHelper.buildRequestFromFile(filename, AnnulerEvenementRequest.class);
        annulerEvenementRequest.setIdEvenement(idEvenement);
        annulerEvenementResponse = wsEvenementGvt.annulerEvenement(annulerEvenementRequest);
        Assert.assertNotNull(annulerEvenementResponse);
        traitementStatut = annulerEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(annulerEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(annulerEvenementResponse.getEvenement());
        evt01Reponse = annulerEvenementResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.3 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.3 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.EN_ATTENTE_DE_VALIDATION, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accepte le rectificatif : 1.0 obsolète, 2.0 annulée
        filename = "fr/dila/solonepp/webengine/wsevenement/annulerEvenement/1040 Valider version accepter.xml";
        validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        validerVersionResponse = wsEvenementAn.validerVersion(validerVersionRequest);
        Assert.assertNotNull(validerVersionResponse);
        traitementStatut = validerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(validerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = validerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.ANNULE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage2_0.equals(horodatage1_0));

        // Vérifie le message de l'émetteur : version 2.0 annulée
        checkMessage(wsEvenementGvt, idEvenement, EtatEvenement.ANNULE, EtatMessage.AR_RECU, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 annulée
        checkMessage(wsEvenementAn, idEvenement, EtatEvenement.ANNULE, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatEvenement.ANNULE, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Vérifie la présence d'un message dans la corbeille de l'institution.
     * 
     * @param wsEvenement WS evenement (AN / gouvernement / sénat)
     * @param idEvenement Identifiant technique de l'événement
     * @param etatEvenement Etat de l'événement
     * @param etatMessage Etat du message
     * @param horodatage Horodatage (date publication de la version)
     * @throws Exception
     */
    protected void checkMessage(final WSEvenement wsEvenement, final String idEvenement, final EtatEvenement etatEvenement,
            final EtatMessage etatMessage, final XMLGregorianCalendar horodatage) throws Exception {
        final String filenameRecherche = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0015 Rechercher message.xml";
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
        Assert.assertEquals(etatEvenement, message.getEtatEvenement());
        Assert.assertEquals(etatMessage, message.getEtatMessage());
        if (horodatage != null) {
            Assert.assertNotNull(message.getDateEvenement());
            Assert.assertEquals(horodatage, message.getDateEvenement());
        }
    }
}
