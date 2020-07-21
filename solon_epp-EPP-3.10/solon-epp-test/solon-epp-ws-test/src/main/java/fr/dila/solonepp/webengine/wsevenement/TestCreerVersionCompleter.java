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
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;

/**
 * Tests fonctionnels des créations de version pour complétion.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionCompleter extends AbstractEppWSTest {

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
     * Test d'une complétion de version :
     * - Emetteur crée une version directement à l'état publié (1.0) ;
     * - Emetteur crée une version brouillon pour complétion (1.1) ;
     * - Emetteur publie la version complétée (2.0), la version 1.0 passe à obsolète.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionCompleterBrouillon() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0010 Creer dossier EVT01 publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, horodatage1_0);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // AN crée la version 1.1 brouillon pour complétion : interdit, ce n'est pas l'émetteur
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0020 Creer version brouillon pour completion.xml";
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

        // GVT crée la version 1.1 brouillon pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0020 Creer version brouillon pour completion.xml";
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
        Assert.assertTrue(evt01Reponse.getCommentaire().contains("Commentaire complété"));
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // GVT modifie la version 1.1 brouillon pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0025 Modifier version brouillon pour completion.xml";
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
        Assert.assertTrue(creerVersionResponse.getEvenement().getEvt01().getCommentaire().contains("Commentaire complété modifié"));
        expectedActionSuivante = Sets
                .newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 1.1 brouillon
        checkMessage(wsEvenementGvt, idEvenement, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // AN publie la version 2.0 complétée : interdit, seul l'émetteur peut publier une version pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0030 Publication version completee.xml";
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

        // GVT publie la version 2.0 complétée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0030 Publication version completee.xml";
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
        Assert.assertTrue(evt01Reponse.getCommentaire().contains("Commentaire complété 2"));
        expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER, Action.CREER_EVENEMENT,
                Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, null);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage2_0);

    }

    /**
     * Test d'une rectification :
     * - Emetteur créer une version directement à l'état publié (1.0) ;
     * - Emetteur complète la version et publie directement (2.0).
     * 
     * @throws Exception
     */
    @WebTest(description = "Test d'une rectification", useDriver = false)
    public void testCreerVersionCompleterPublier() throws Exception {

        // GVT crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/1000 Creer dossier EVT01 publie.xml";
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
        checkMessage(wsEvenementGvt, idEvenement, null);

        // Vérifie le message du destinataire : version 1.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage1_0);

        // Vérifie le message de la copie : version 1.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage1_0);

        // GVT publie la version 2.0 complétée
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/1010 Publication version completee.xml";
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
        Assert.assertTrue(evt01Reponse.getCommentaire().contains("Commentaire complété 2"));
        expectedActionSuivante = Sets.newHashSet(Action.CREER_ALERTE, Action.COMPLETER, Action.RECTIFIER, Action.ANNULER, Action.CREER_EVENEMENT,
                Action.TRANSMETTRE_MEL, Action.VISUALISER_VERSION);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Vérifie le message de l'émetteur : version 2.0 publiée
        checkMessage(wsEvenementGvt, idEvenement, null);

        // Vérifie le message du destinataire : version 2.0 publiée
        checkMessage(wsEvenementAn, idEvenement, horodatage2_0);

        // Vérifie le message de la copie : version 2.0 publiée
        checkMessage(wsEvenementSenat, idEvenement, horodatage2_0);

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
        final String filenameRecherche = "fr/dila/solonepp/webengine/wsevenement/creerVersionCompleter/0015 Rechercher message.xml";
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
