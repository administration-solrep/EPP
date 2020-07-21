package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.BeforeClass;

import com.google.common.collect.Sets;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.Action;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;

/**
 * Tests fonctionnels des créations de version pour rectification.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionRectifier extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    private static WSEvenement wsEvenementSenat;

    private static WSEpp wsEppAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEppAn = WSServiceHelper.getWSEppAn();

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();
    }

    /**
     * Test d'une rectification de version :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une version brouillon pour rectification (1.1) ;
     * - Emetteur publie la version rectifiée (2.0), la version 1.0 passe à obsolète.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification de version", useDriver = false)
    public void testCreerVersionRectifierBrouillon() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0010 Creer dossier EVT01 CCOZ1100020V publie.xml";
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
        Set<Action> expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER,
                Action.CREER_EVENEMENT, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

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

        // AN crée la version 1.1 brouillon pour rectification : interdit, ce n'est pas l'émetteur
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0020 Creer version brouillon pour rectification.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Seule l'institution émettrice peut"));

        // GVT crée la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0020 Creer version brouillon pour rectification.xml";
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // GVT modifie la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0025 Modifier version brouillon pour rectification.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié modifié", evt01Reponse.getObjet());
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_COURS_REDACTION, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN publie la version 2.0 rectifiée : interdit, seul l'émetteur peut publier une version pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Seule l'institution émettrice peut"));

        // GVT publie la version 2.0 rectifiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertTrue(!horodatage1_0.equals(horodatage2_0));
        Assert.assertEquals("Objet rectifié 2", evt01Reponse.getObjet());

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test d'une rectification :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur rectifie la version et publie directement (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification", useDriver = false)
    public void testCreerVersionRectifierPublier() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/1000 Creer dossier EVT01 CCOZ1100021V publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 2.0 rectifiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage1_0.equals(horodatage2_0));
        Assert.assertEquals("Objet rectifié 2", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test d'une rectification :
     * - Emetteur crée une version directement à l'état publié -> 1.0 PUBLIEE ;
     * - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe à "EN_INSTANCE" ;
     * - Emetteur crée une version brouillon pour rectification -> 1.1 BROUILLON ;
     * - Emetteur rectifie la version et publie -> version 1.1 EN_ATTENTE_VALIDATION ;
     * - Destinataire refuse la rectification -> 1.1 REJETE ;
     * - Emetteur crée une version brouillon pour rectification -> 1.2 BROUILLON ;
     * - Emetteur rectifie la version et publie -> version 1.2 EN_ATTENTE_VALIDATION ;
     * - Emetteur annule la rectification -> 1.2 ABANDONNE ;
     * - Emetteur crée une version brouillon pour rectification -> 1.3 BROUILLON ;
     * - Emetteur rectifie la version et publie directement -> version 1.3 EN_ATTENTE_VALIDATION ;
     * - Destinataire accepte la rectification -> 2.0 PUBLIE, 1.0 OBSOLETE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification", useDriver = false)
    public void testCreerVersionRectifierBrouillonValidation() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2000 Creer dossier EVT01 CCOZ1100022V publie.xml";
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
        Set<Action> expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER,
                Action.CREER_EVENEMENT, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

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

        // AN notifie la transition en cours de traitement et accuse réception de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2010 Notifier transition traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT crée la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2015 Creer version brouillon pour rectification.xml";
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
        Assert.assertEquals(EtatEvenement.EN_INSTANCE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", creerVersionResponse.getEvenement().getEvt01().getObjet());
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // GVT modifie la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2017 Modifier version brouillon pour rectification.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié modifié", creerVersionResponse.getEvenement().getEvt01().getObjet());
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_COURS_REDACTION, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.1 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié 2", creerVersionResponse.getEvenement().getEvt01().getObjet());
        expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.ABANDONNER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 1.1 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.1 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT rejette le rectificatif : interdit, seul le destinataire peut rejetter le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2020 Valider version rejeter.xml";
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
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2020 Valider version rejeter.xml";
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
        expectedActionSuivante = Sets.newHashSet(Action.CREER_EVENEMENT, Action.CREER_ALERTE, Action.PASSAGE_MESSAGE_EN_COURS_DE_TRAITEMENT,
                Action.MARQUAGE_MESSAGE_TRAITE, Action.MARQUAGE_MESSAGE_TRAITE, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 1.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT crée la version 1.2 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2015 Creer version brouillon pour rectification.xml";
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
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // GVT modifie la version 1.2 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2017 Modifier version brouillon pour rectification.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié modifié", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // Vérifie le message de l'émetteur : version 1.2 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_COURS_REDACTION, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.2 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.2 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.2 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN abandonne le rectificatif : interdit, seul l'émetteur peut abandonner le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2030 Valider version abandonner.xml";
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
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2030 Valider version abandonner.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT crée la version 1.3 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2015 Creer version brouillon pour rectification.xml";
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
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // GVT modifie la version 1.3 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2017 Modifier version brouillon pour rectification.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié modifié", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // Vérifie le message de l'émetteur : version 1.3 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_COURS_REDACTION, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.3 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.3 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.3 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accepte le rectificatif : 1.0 obsolète, 2.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/2040 Valider version accepter.xml";
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
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage2_0.equals(horodatage1_0));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test d'une rectification :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe à "EN_INSTANCE" ;
     * - Emetteur rectifie la version et publie directement -> version 1.1 EN_ATTENTE_VALIDATION ;
     * - Destinataire refuse la rectification -> 1.1 REJETE ;
     * - Emetteur rectifie la version et publie directement -> version 1.2 EN_ATTENTE_VALIDATION ;
     * - Emetteur annule la rectification -> 1.2 ABANDONNE ;
     * - Emetteur rectifie la version et publie directement -> version 1.3 EN_ATTENTE_VALIDATION ;
     * - Destinataire accepte la rectification -> 2.0 PUBLIE, 1.0 OBSOLETE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification", useDriver = false)
    public void testCreerVersionRectifierPublierValidation() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3000 Creer dossier EVT01 CCOZ1100023V publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accuse réception de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3010 Notifier transition traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT publie la version 1.1 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié 2", creerVersionResponse.getEvenement().getEvt01().getObjet());

        // Vérifie le message de l'émetteur : version 1.1 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.1 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT rejette le rectificatif : interdit, seul le destinataire peut rejetter le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3020 Valider version rejeter.xml";
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
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3020 Valider version rejeter.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.2 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.2 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.2 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN abandonne le rectificatif : interdit, seul l'émetteur peut abandonner le rectificatif
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3030 Valider version abandonner.xml";
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
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3030 Valider version abandonner.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.3 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/0030 Publication version rectifiee.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.3 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.3 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accepte le rectificatif : 1.0 obsolète, 2.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionRectifier/3040 Valider version accepter.xml";
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
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        final XMLGregorianCalendar horodatage2_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage2_0);
        Assert.assertFalse(horodatage2_0.equals(horodatage1_0));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

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
        Assert.assertEquals(etatMessage, message.getEtatMessage());
        if (horodatage != null) {
            Assert.assertNotNull(message.getDateEvenement());
            Assert.assertEquals(horodatage, message.getDateEvenement());
        }
    }
}
