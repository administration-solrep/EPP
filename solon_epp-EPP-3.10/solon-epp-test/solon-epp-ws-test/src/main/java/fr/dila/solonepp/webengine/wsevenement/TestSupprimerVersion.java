package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.CritereRechercheEvenement;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.Message;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.SupprimerVersionRequest;
import fr.sword.xsd.solon.epp.SupprimerVersionResponse;

/**
 * Tests fonctionnels de suppression de version.
 * 
 * @author jtremeaux
 */
public class TestSupprimerVersion extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    @WebTest(description = "SupprimerVersionCascade", useDriver = false)
    public void testSupprimerVersionCascade() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état brouillon 0.1
        String filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0010 Creer dossier EVT01 ARTL1100001Y.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        final EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());

        // Récupère l'identifiant technique de l'événement
        final String idEvenement = evt01Reponse.getIdEvenement();

        // GVT recherche le message de la version brouillon 0.1
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0015 Rechercher message par id_evenement.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertEquals(1, messageList.size());
        final Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());

        // GVT supprime la version, puis l'événement, et le dossier
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0020 Supprimer version EVT01.xml";
        final SupprimerVersionRequest supprimerVersionRequest = JaxBHelper.buildRequestFromFile(filename, SupprimerVersionRequest.class);
        Assert.assertNotNull(supprimerVersionRequest);
        final EvtId evtId = supprimerVersionRequest.getIdEvenement();
        evtId.setId(idEvenement);
        supprimerVersionRequest.setIdEvenement(evtId);
        final SupprimerVersionResponse supprimerVersionResponse = wsEvenementGvt.supprimerVersion(supprimerVersionRequest);
        Assert.assertNotNull(supprimerVersionResponse);
        traitementStatut = supprimerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(supprimerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT recherche le message de la version brouillon 0.1 : aucun résultat car le message a été supprimé
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0015 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        messageList = rechercherEvenementResponse.getMessage();
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertEquals(0, messageList.size());

    }

    @WebTest(description = "testSupprimerVersionRecalcul", useDriver = false)
    public void testSupprimerVersionRecalcul() throws Exception {

        // GVT crée un dossier, un événement et une version à l'état publié 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/1000 Creer dossier EVT01 ARTL1100002Y.xml";
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
        final XMLGregorianCalendar horodatage1_0 = evt01Reponse.getHorodatage();
        Assert.assertNotNull(horodatage1_0);

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT recherche le message de la version publiée 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0015 Rechercher message par id_evenement.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        CritereRechercheEvenement critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        List<Message> messageList = rechercherEvenementResponse.getMessage();
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertEquals(1, messageList.size());
        Message message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertNotNull(message.getDateEvenement());
        Assert.assertEquals(horodatage1_0, message.getDateEvenement());

        // GVT crée la version 1.1 brouillon pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/1010 Creer version brouillon pour completion.xml";
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
        Assert.assertTrue(creerVersionResponse.getEvenement().getEvt01().getCommentaire().contains("Commentaire complété"));

        // GVT recherche le message de la version brouillon 1.1
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0015 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(idEvenement, message.getIdEvenement());
        Assert.assertNotNull(message.getDateEvenement());
        Assert.assertEquals(EtatMessage.EN_COURS_REDACTION, message.getEtatMessage());

        // GVT supprime la version brouillon 1.1 : entraine le recalcul du message émetteur
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/1020 Supprimer version brouillon EVT01.xml";
        final SupprimerVersionRequest supprimerVersionRequest = JaxBHelper.buildRequestFromFile(filename, SupprimerVersionRequest.class);
        Assert.assertNotNull(supprimerVersionRequest);
        final EvtId evtId = supprimerVersionRequest.getIdEvenement();
        evtId.setId(idEvenement);
        supprimerVersionRequest.setIdEvenement(evtId);
        final SupprimerVersionResponse supprimerVersionResponse = wsEvenementGvt.supprimerVersion(supprimerVersionRequest);
        Assert.assertNotNull(supprimerVersionResponse);
        traitementStatut = supprimerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(supprimerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // GVT recherche le message de l'événement : retour à l'événement 1.0 publié
        filename = "fr/dila/solonepp/webengine/wsevenement/supprimerVersion/0015 Rechercher message par id_evenement.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        critereRechercheEvenement = new CritereRechercheEvenement();
        critereRechercheEvenement.setIdEvenement(idEvenement);
        rechercherEvenementRequest.setParCritere(critereRechercheEvenement);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        Assert.assertNotNull(rechercherEvenementResponse.getMessage());
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        messageList = rechercherEvenementResponse.getMessage();
        Assert.assertEquals(1, messageList.size());
        message = messageList.get(0);
        Assert.assertEquals(horodatage1_0, message.getDateEvenement());
        Assert.assertEquals(EtatMessage.EN_ATTENTE_AR, message.getEtatMessage());

    }
}
