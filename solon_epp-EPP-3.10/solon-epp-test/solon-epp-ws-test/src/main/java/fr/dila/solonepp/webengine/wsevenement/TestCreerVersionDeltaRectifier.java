package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;

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
import fr.sword.xsd.solon.epp.CreerVersionDeltaRequest;
import fr.sword.xsd.solon.epp.CreerVersionDeltaResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvtDelta;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.TypeLoi;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;

/**
 * Tests fonctionnels des créations de version en mode delta.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionDeltaRectifier extends AbstractEppWSTest {

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
     * Test de rectification en mode delta à partir d'une version publiée :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une version delta pour rectification (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de rectification en mode delta à partir d'une version publiée", useDriver = false)
    public void testCreerVersionRectifierDeltaPublie() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/0010 Creer dossier EVT01 CCOZ1100010D publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 2.0 rectifiée en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/0020 Rectifier delta publier.xml";
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
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        final List<Mandat> coauteur = evt01Reponse.getCoAuteur();
        Assert.assertEquals(2, coauteur.size());
        Assert.assertEquals("MandatTest0", coauteur.get(0).getId());
        Assert.assertEquals("MandatTest1", coauteur.get(1).getId());
        Assert.assertTrue(StringUtils.isEmpty(evt01Reponse.getCommentaire()));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test de rectification en mode delta à partir d'une version publiée :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe à "EN_INSTANCE" ;
     * - Emetteur crée une version delta pour rectification (version 1.1 EN_ATTENTE_VALIDATION) ;
     * - Destinataire accepte la rectification (2.0 PUBLIE, 1.0 OBSOLETE).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de rectification en mode delta à partir d'une version publiée", useDriver = false)
    public void testCreerVersionRectifierDeltaPublieValidation() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1000 Creer dossier EVT01 CCOZ1100011D publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accuse réception de la version 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1010 Notifier transition traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT crée la version 1.1 rectifiée en mode delta en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1020 Rectifier delta publier.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        final List<Mandat> coauteur = evt01Reponse.getCoAuteur();
        Assert.assertEquals(2, coauteur.size());
        Assert.assertEquals("MandatTest0", coauteur.get(0).getId());
        Assert.assertEquals("MandatTest1", coauteur.get(1).getId());
        Assert.assertTrue(StringUtils.isEmpty(evt01Reponse.getCommentaire()));

        // Vérifie le message de l'émetteur : version 1.1 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.1 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accepte le rectificatif : 1.0 obsolète, 2.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1050 Valider version accepter.xml";
        final ValiderVersionRequest validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        final ValiderVersionResponse validerVersionResponse = wsEvenementAn.validerVersion(validerVersionRequest);
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
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, horodatage2_0);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test de rectification en mode delta à partir d'une version brouillon :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une brouillon pour rectification (1.1) ;
     * - Emetteur publie une version delta pour rectification (2.0 publiée, 2.1 brouillon).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de rectification en mode delta à partir d'une version brouillon", useDriver = false)
    public void testCreerVersionRectifierDeltaBrouillon() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/2000 Creer dossier EVT01 CCOZ1100012D publie.xml";
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

        // GVT crée la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/2010 Creer version brouillon pour rectification.xml";
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
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals("Commentaire rectifié", evt01Reponse.getCommentaire());

        // GVT publie la version 2.0 rectifiée en mode delta
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/2020 Rectifier delta publier.xml";
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
        Assert.assertEquals("Objet rectifié delta", evt01Reponse.getObjet());
        Assert.assertEquals("Commentaire rectifié delta", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 2.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage2_0);

    }

    /**
     * Test de rectification en mode delta à partir d'une version brouillon :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Destinataire notifie la transition EN_COURS sur son message -> l'événement passe à "EN_INSTANCE" ;
     * - Emetteur crée une brouillon pour rectification (1.1) ;
     * - Emetteur crée une version delta pour rectification (version 1.2 = 1.0 + delta EN_ATTENTE_VALIDATION, 1.3 = 1.1 + delta BLOQUEE) ;
     * - Destinataire refuse la rectification -> 1.2 REJETE, 1.3 supprimée ;
     * - Emetteur crée une version delta pour rectification (version 1.3 = 1.0 + delta EN_ATTENTE_VALIDATION, 1.4 = 1.1 + delta BLOQUEE) ;
     * - Emetteur abandonne la rectification -> 1.3 ABANDONNEE, 1.4 supprimée ;
     * - Emetteur crée une version delta pour rectification (version 1.4 = 1.0 + delta EN_ATTENTE_VALIDATION, 1.5 = 1.1 + delta BLOQUEE) ;
     * - Emetteur abandonne la rectification -> 2.0 PUBLIE (version 1.4 renommée), 2.1 BROUILLON (version 1.5 renommée), 1.1 supprimée.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de rectification en mode delta à partir d'une version brouillon", useDriver = false)
    public void testCreerVersionRectifierDeltaBrouillonValidation() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/3000 Creer dossier EVT01 CCOZ1100013D publie.xml";
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
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1010 Notifier transition traite.xml";
        final NotifierTransitionRequest notifierTransitionRequest = JaxBHelper.buildRequestFromFile(filename, NotifierTransitionRequest.class);
        notifierTransitionRequest.setIdEvenement(idEvenement);
        Assert.assertNotNull(notifierTransitionRequest);
        final NotifierTransitionResponse notifierTransitionResponse = wsEppAn.notifierTransition(notifierTransitionRequest);
        traitementStatut = notifierTransitionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierTransitionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT crée la version 1.1 brouillon pour rectification
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/3010 Creer version brouillon pour rectification.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals("Commentaire rectifié", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_COURS_REDACTION, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT crée la version 1.2 rectifiée en mode delta en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/3020 Rectifier delta publier.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(2, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié delta", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 1.2 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.2 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN rejette le rectificatif : 1.2 rejetée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1030 Valider version rejeter.xml";
        ValiderVersionRequest validerVersionRequest = JaxBHelper.buildRequestFromFile(filename, ValiderVersionRequest.class);
        validerVersionRequest.setIdEvenement(idEvenement);
        ValiderVersionResponse validerVersionResponse = wsEvenementAn.validerVersion(validerVersionRequest);
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

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.3 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/3020 Rectifier delta publier.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(3, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié delta", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 1.3 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.3 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT abandonne le rectificatif : 1.3 abandonnée, émetteur revient à la version brouillon
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1040 Valider version abandonner.xml";
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
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // GVT publie la version 1.4 en attente de validation
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/3020 Rectifier delta publier.xml";
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
        Assert.assertEquals(EtatEvenement.EN_ATTENTE_DE_VALIDATION, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(4, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié delta", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 1.4 en attente de validation
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.EN_ATTENTE_AR, null);

        // Vérifie le message du destinataire : version 1.4 en attente de validation
        checkMessage(wsEvenementAn, idEvenement, EtatMessage.NON_TRAITE, null);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, EtatMessage.NON_TRAITE, horodatage1_0);

        // AN accepte le rectificatif : 1.0 obsolète, 2.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionDeltaRectifier/1050 Valider version accepter.xml";
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
        Assert.assertEquals("Objet rectifié delta", evt01Reponse.getObjet());
        Assert.assertEquals(TypeLoi.LOI_CADRE, evt01Reponse.getTypeLoi());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // Vérifie le message de l'émetteur : version 2.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, EtatMessage.AR_RECU, null);

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
