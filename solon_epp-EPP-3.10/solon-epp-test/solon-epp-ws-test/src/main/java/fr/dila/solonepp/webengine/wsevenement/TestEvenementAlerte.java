package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

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
import fr.sword.xsd.solon.epp.CreationType;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvt03;
import fr.sword.xsd.solon.epp.EppEvtAlerte;
import fr.sword.xsd.solon.epp.EppEvtGenerique;
import fr.sword.xsd.solon.epp.EtatDossier;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;

/**
 * Tests fonctionnels des événements de type alerte.
 * 
 * @author jtremeaux
 */
public class TestEvenementAlerte extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
    }

    /**
     * Ce test vérifie la pose des alertes.
     * - Emetteur crée un événement de type EVT01 à l'état publié ;
     * - Emetteur pose une 1ère alerte -> le dossier est en alerte ;
     * - Emetteur pose une 2ème alerte -> le dossier est en alerte (2 alertes simultanées) ;
     *   Evt01
     *    |         \
     *   Alerte1(+) Alerte2(+)
     * - Emetteur lever la 1ère alerte -> le dossier est en alerte (1 alerte) ;
     * - Emetteur lever la 2ème alerte -> le dossier n'est plus en alerte (0 alerte).
     *   Evt01
     *    |         \
     *   Alerte1(+) Alerte2(+)
     *    |          |
     *   Generique  Alerte2(-)
     *    |          |
     *   Alerte1(-) Alerte2(-)
     *    |
     *   Generique
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification", useDriver = false)
    public void testCreerAlerteSimultane() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0010 Creer dossier EVT01 CCOZ1100001A publie.xml";
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
        Set<Action> expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER,
                Action.CREER_EVENEMENT, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement créé pour les tests suivants
        final String idEvenement01 = evt01Reponse.getIdEvenement();
        final XMLGregorianCalendar evt01Horodatage = evt01Reponse.getHorodatage();

        // Vérifie l'état du dossier en instance
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.EN_INSTANCE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // GVT crée un événement alerte sans événement parent : erreur
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0020 Creer evenement ALERTE erreur brouillon.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        EppEvtAlerte eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent("NON_EXISTANT");
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(), creerVersionResponse.getMessageErreur()
                .contains("Communication parent non trouvé"));

        // GVT crée un événement alerte avec une version brouillon : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0020 Creer evenement ALERTE erreur brouillon.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenement01);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(), creerVersionResponse.getMessageErreur().contains("une version brouillon"));

        // GVT crée un événement alerte (levée) -> interdit car pas d'alerte au dessus de ce noeud
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenement01);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Une communication alerte (levée) doit succéder à une communication alerte (pose)"));

        // GVT crée un événement alerte (pose) : dossier en alerte (1 alerte)
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenement01);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        EppEvtAlerte evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.LEVER_ALERTE, Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement pose de l'alerte 1 pour les tests suivants
        final String idEvenementAlerte1Pose = evtAlerteReponse.getIdEvenement();

        // Vérifie l'état du dossier en alerte
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.ALERTE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // AN crée et publie un événement générique en dessous de l'alerte (pose) : ok (permet d'effectuer des commentaires)
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0040 Creer evenement EVT_GENERIQUE successif.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        EppEvtGenerique eppEvtGenerique = creerVersionRequest.getEvenement().getEvtGenerique01();
        eppEvtGenerique.setIdEvenementPrecedent(idEvenementAlerte1Pose);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        EppEvtGenerique evtGeneriqueReponse = creerVersionResponse.getEvenement().getEvtGenerique01();
        Assert.assertNotNull(evtGeneriqueReponse);
        Assert.assertNotNull(evtGeneriqueReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtGeneriqueReponse.getEtat());
        Assert.assertNotNull(evtGeneriqueReponse.getVersionCourante());
        Assert.assertEquals(1, evtGeneriqueReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtGeneriqueReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtGeneriqueReponse.getHorodatage());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtGeneriqueReponse.getEmetteur());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtGeneriqueReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.LEVER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER, Action.CREER_EVENEMENT,
                Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtGeneriqueReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtGeneriqueReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement pose de l'alerte 1 pour les tests suivants
        final String idEvenementGenerique1 = evtGeneriqueReponse.getIdEvenement();

        // GVT crée un événement alerte (pose) : dossier en alerte (2 alertes simultanées)
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenement01);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.LEVER_ALERTE, Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement pose de l'alerte 2 pour les tests suivants
        final String idEvenementAlerte2Pose = evtAlerteReponse.getIdEvenement();

        // Vérifie l'état du dossier en alerte
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.ALERTE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // GVT crée un événement alerte (levée) : dossier en alerte (1 alerte)
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementGenerique1);
        eppEvtAlerte.setPositionAlerte(false);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement levée de l'alerte 1 pour les tests suivants
        final String idEvenementAlerte1Levee = evtAlerteReponse.getIdEvenement();

        // Vérifie l'état du dossier en alerte
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.ALERTE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // GVT crée une nouvelle version brouillon pour complétion de l'événement Alerte : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0035 Completer publier alerte erreur.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        creerVersionRequest.setModeCreation(CreationType.COMPLETER_BROUILLON);
        eppEvtAlerte.setIdEvenement(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Vous n'êtes pas autorisé à compléter"));

        // GVT complète et publie l'événement Alerte : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0035 Completer publier alerte erreur.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        creerVersionRequest.setModeCreation(CreationType.COMPLETER_BROUILLON);
        eppEvtAlerte.setIdEvenement(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Vous n'êtes pas autorisé à compléter"));

        // GVT crée une nouvelle version brouillon pour rectification de l'événement Alerte : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0035 Completer publier alerte erreur.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        creerVersionRequest.setModeCreation(CreationType.RECTIFIER_BROUILLON);
        eppEvtAlerte.setIdEvenement(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Vous n'êtes pas autorisé à rectifier"));

        // GVT complète et rectifie l'événement Alerte : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0035 Completer publier alerte erreur.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        creerVersionRequest.setModeCreation(CreationType.RECTIFIER_PUBLIER);
        eppEvtAlerte.setIdEvenement(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("Vous n'êtes pas autorisé à rectifier"));

        // GVT crée un événement alerte (pose) en dessous de l'événement alerte (levée) : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(
                creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains(
                        "Une communication d'alerte (pose) ne peut pas être créé après une autre communication alerte"));

        // GVT crée un événement alerte (pose) en dessous de l'événement alerte (pose) : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte2Pose);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(
                creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains(
                        "Une communication d'alerte (pose) ne peut pas être créé après une autre communication alerte"));

        // GVT crée un événement alerte (levée) en dessous de l'événement alerte (levée) : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte1Levee);
        eppEvtAlerte.setPositionAlerte(false);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur()
                        .contains("Une communication alerte (levée) ne peut pas être créé après une alerte déjà levée"));

        // GVT crée un événement alerte (levée) : le dossier repasse en instance
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0030 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte2Pose);
        eppEvtAlerte.setPositionAlerte(false);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Vérifie l'état du dossier en instance
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.EN_INSTANCE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // AN crée et publie un événement générique en dessous de l'alerte : ok (permet d'effectuer des commentaires)
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0040 Creer evenement EVT_GENERIQUE successif.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtGenerique = creerVersionRequest.getEvenement().getEvtGenerique01();
        eppEvtGenerique.setIdEvenementPrecedent(idEvenementAlerte1Levee);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evtGeneriqueReponse = creerVersionResponse.getEvenement().getEvtGenerique01();
        Assert.assertNotNull(evtGeneriqueReponse);
        Assert.assertNotNull(evtGeneriqueReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtGeneriqueReponse.getEtat());
        Assert.assertNotNull(evtGeneriqueReponse.getVersionCourante());
        Assert.assertEquals(1, evtGeneriqueReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtGeneriqueReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtGeneriqueReponse.getHorodatage());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtGeneriqueReponse.getEmetteur());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtGeneriqueReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.COMPLETER, Action.RECTIFIER, Action.ANNULER, Action.CREER_EVENEMENT, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtGeneriqueReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtGeneriqueReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement générique pour les tests suivants
        final String idEvenementGenerique2 = evtAlerteReponse.getIdEvenement();

        // AN crée un événement successif en dessous de l'alerte : interdit
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0050 Creer evenement EVT03 successif.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final EppEvt03 eppEvt03 = creerVersionRequest.getEvenement().getEvt03();
        eppEvt03.setIdEvenementPrecedent(idEvenementGenerique2);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("ne peut pas succéder à une communication de type Alerte"));

    }

    /**
     * Ce test vérifie la pose des alertes.
     * - Emetteur crée un événement de type EVT01 à l'état publié ;
     * - Emetteur pose une alerte -> le dossier est en alerte ;
     * - Emetteur crée un événement générique successif à l'alerte ;
     *   Evt01
     *    |
     *   Alerte1(+) 
     *    |         \
     *   Generique  Alerte1(-) 
     * - Emetteur crée un événement levée alerte successif à Alerte1(+) : l'Alerte1(+) est annulée ;
     * - Emetteur crée un événement levée alerte successif à Générique : interdit.
     * 
     * @throws Exception
     */
    @WebTest(description = "vérifie la pose des alertes", useDriver = false)
    public void testCreerAlerteBranche() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/1000 Creer dossier EVT01 CCOZ1100002A publie.xml";
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
        Set<Action> expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER,
                Action.CREER_EVENEMENT, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement créé pour les tests suivants
        final String idEvenement01 = evt01Reponse.getIdEvenement();
        final XMLGregorianCalendar evt01Horodatage = evt01Reponse.getHorodatage();

        // Vérifie l'état du dossier en instance
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.EN_INSTANCE, EtatMessage.EN_ATTENTE_AR, evt01Horodatage);

        // AN crée et publie un événement générique successif à EVT01
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/1020 Creer evenement EVT_GENERIQUE successif.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final EppEvtGenerique eppEvtGenerique = creerVersionRequest.getEvenement().getEvtGenerique01();
        eppEvtGenerique.setIdEvenementPrecedent(idEvenement01);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvtGenerique evtGeneriqueReponse = creerVersionResponse.getEvenement().getEvtGenerique01();
        Assert.assertNotNull(evtGeneriqueReponse);
        Assert.assertNotNull(evtGeneriqueReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtGeneriqueReponse.getEtat());
        Assert.assertNotNull(evtGeneriqueReponse.getVersionCourante());
        Assert.assertEquals(1, evtGeneriqueReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtGeneriqueReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtGeneriqueReponse.getHorodatage());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtGeneriqueReponse.getEmetteur());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtGeneriqueReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER, Action.CREER_EVENEMENT,
                Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtGeneriqueReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtGeneriqueReponse.getActionSuivante()));

        // GVT crée un événement alerte (pose) successif à EVT01 : dossier en alerte
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/1010 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        EppEvtAlerte eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenement01);
        eppEvtAlerte.setPositionAlerte(true);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        EppEvtAlerte evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.LEVER_ALERTE, Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Récupère l'identifiant de l'événement pose de l'alerte 1 pour les tests suivants
        final String idEvenementAlerte1Pose = evtAlerteReponse.getIdEvenement();

        // Vérifie l'état du dossier en alerte
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.ALERTE, EtatMessage.AR_RECU, evt01Horodatage);

        // GVT crée un événement alerte (levée) : le dossier n'est plus en alerte
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/1010 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte1Pose);
        eppEvtAlerte.setPositionAlerte(false);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evtAlerteReponse = creerVersionResponse.getEvenement().getEvtAlerte01();
        Assert.assertNotNull(evtAlerteReponse);
        Assert.assertNotNull(evtAlerteReponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evtAlerteReponse.getEtat());
        Assert.assertNotNull(evtAlerteReponse.getVersionCourante());
        Assert.assertEquals(1, evtAlerteReponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evtAlerteReponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evtAlerteReponse.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evtAlerteReponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evtAlerteReponse.getDestinataire());
        expectedActionSuivante = Sets.newHashSet(Action.CREER_EVENEMENT);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evtAlerteReponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evtAlerteReponse.getActionSuivante()));

        // Vérifie l'état du dossier en instance
        checkMessage(wsEvenementGvt, idEvenement01, EtatDossier.EN_INSTANCE, EtatMessage.AR_RECU, evt01Horodatage);

        // GVT crée un document levée de l'alerte successif à GENERIQUE : interdit
        // GVT crée un événement alerte (levée) : le dossier n'est plus en alerte
        filename = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/1010 Creer evenement ALERTE publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        eppEvtAlerte = creerVersionRequest.getEvenement().getEvtAlerte01();
        eppEvtAlerte.setIdEvenementPrecedent(idEvenementAlerte1Pose);
        eppEvtAlerte.setPositionAlerte(false);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur()
                        .contains("Une communication alerte (levée) ne peut pas être créé après une alerte déjà levée"));

    }

    /**
     * Vérifie la présence d'un message dans la corbeille de l'institution.
     * 
     * @param wsEvenement WS evenement (AN / gouvernement / sénat)
     * @param idEvenement Identifiant technique de l'événement
     * @param etatDossier Etat du dossier à vérifier
     * @param etatMessage Etat du message à vérifier
     * @param horodatage Horodatage (date publication de la version)
     * @throws Exception
     */
    protected void checkMessage(final WSEvenement wsEvenement, final String idEvenement, final EtatDossier etatDossier,
            final EtatMessage etatMessage, final XMLGregorianCalendar horodatage) throws Exception {
        final String filenameRecherche = "fr/dila/solonepp/webengine/wsevenement/evenementAlerte/0015 Rechercher message.xml";
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
        Assert.assertEquals(etatDossier, message.getEtatDossier());
        Assert.assertEquals(etatMessage, message.getEtatMessage());
        if (horodatage != null) {
            Assert.assertNotNull(message.getDateEvenement());
            Assert.assertEquals(horodatage, message.getDateEvenement());
        } else {
            Assert.assertNull(message.getDateEvenement());
        }
    }
}
