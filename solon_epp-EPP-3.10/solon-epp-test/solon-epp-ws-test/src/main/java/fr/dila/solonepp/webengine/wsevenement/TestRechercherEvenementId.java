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
import fr.sword.xsd.solon.epp.ChercherEvenementRequest;
import fr.sword.xsd.solon.epp.ChercherEvenementResponse;
import fr.sword.xsd.solon.epp.CreationType;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatVersion;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels de recherche simple des événements par identifiant d'événement.
 * 
 * @author jtremeaux
 */
public class TestRechercherEvenementId extends AbstractEppWSTest {

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
     * Ce test vérifie la recherche d'événements par identifiant d'événement.
     * - Emetteur crée des dossier, événements et versions ;
     * - Les institutions recherchent les événements par identifiants d'événement et numéros de version.
     * 
     * @throws Exception
     */
    @WebTest(description = "Ce test vérifie la recherche d'événements par identifiant d'événement", useDriver = false)
    public void testRechercherEvenement() throws Exception {

        // GVT crée un dossier, un événement et une version brouillon 0.1
        String filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0010 Creer dossier EVT01 EFIM1100001C.xml";
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
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement1 = evt01Reponse.getIdEvenement();
        final String pieceJointe1TexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointe1ExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointe1DecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointe1LettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT recherche l'événement par identifiant technique d'événement sans contenu PJ 
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        ChercherEvenementRequest chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        List<EvtId> evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        ChercherEvenementResponse chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        List<EppEvtContainer> evenementList = chercherEvenementResponse.getEvenement();
        Assert.assertNotNull(evenementList);
        Assert.assertEquals(1, evenementList.size());
        EppEvtContainer eppEvtContainer = evenementList.get(0);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement1, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getTexte());
        Assert.assertEquals(pieceJointe1TexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getTexte().getFichier());
        Assert.assertEquals(evt01Reponse.getTexte().getFichier().size(), 1, evt01Reponse.getTexte().getFichier().size());
        Assert.assertNull(evt01Reponse.getTexte().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getExposeDesMotifs());
        Assert.assertEquals(pieceJointe1ExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getExposeDesMotifs().getFichier());
        Assert.assertEquals(evt01Reponse.getExposeDesMotifs().getFichier().size(), 1, evt01Reponse.getExposeDesMotifs().getFichier().size());
        Assert.assertNull(evt01Reponse.getExposeDesMotifs().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getDecretPresentation());
        Assert.assertEquals(pieceJointe1DecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getDecretPresentation().getFichier());
        Assert.assertEquals(evt01Reponse.getDecretPresentation().getFichier().size(), 1, evt01Reponse.getDecretPresentation().getFichier().size());
        Assert.assertNull(evt01Reponse.getDecretPresentation().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getLettrePm());
        Assert.assertEquals(pieceJointe1LettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getLettrePm().getFichier());
        Assert.assertEquals(evt01Reponse.getLettrePm().getFichier().size(), 1, evt01Reponse.getLettrePm().getFichier().size());
        Assert.assertNull(evt01Reponse.getLettrePm().getFichier().get(0).getContenu());

        // GVT recherche l'événement par identifiant technique d'événement avec contenu pj
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0080 Rechercher evenement par id_evenement avec contenu PJ.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        evenementList = chercherEvenementResponse.getEvenement();
        Assert.assertNotNull(evenementList);
        Assert.assertEquals(1, evenementList.size());
        eppEvtContainer = evenementList.get(0);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement1, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getTexte());
        Assert.assertEquals(pieceJointe1TexteId, evt01Reponse.getTexte().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getTexte().getFichier());
        Assert.assertEquals(evt01Reponse.getTexte().getFichier().size(), 0, evt01Reponse.getTexte().getFichier().size());
        Assert.assertNotNull(evt01Reponse.getTexte().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getExposeDesMotifs());
        Assert.assertEquals(pieceJointe1ExposeDesMotifsId, evt01Reponse.getExposeDesMotifs().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getExposeDesMotifs().getFichier());
        Assert.assertEquals(evt01Reponse.getExposeDesMotifs().getFichier().size(), 0, evt01Reponse.getExposeDesMotifs().getFichier().size());
        Assert.assertNotNull(evt01Reponse.getExposeDesMotifs().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getDecretPresentation());
        Assert.assertEquals(pieceJointe1DecretPresentationId, evt01Reponse.getDecretPresentation().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getDecretPresentation().getFichier());
        Assert.assertEquals(evt01Reponse.getDecretPresentation().getFichier().size(), 1, evt01Reponse.getDecretPresentation().getFichier().size());
        Assert.assertNotNull(evt01Reponse.getDecretPresentation().getFichier().get(0).getContenu());
        Assert.assertNotNull(evt01Reponse.getLettrePm());
        Assert.assertEquals(pieceJointe1LettrePmId, evt01Reponse.getLettrePm().getIdInterneEpp());
        Assert.assertNotNull(evt01Reponse.getLettrePm().getFichier());
        Assert.assertEquals(evt01Reponse.getLettrePm().getFichier().size(), 1, evt01Reponse.getLettrePm().getFichier().size());
        Assert.assertNotNull(evt01Reponse.getLettrePm().getFichier().get(0).getContenu());

        // AN recherche l'événement par identifiant technique d'événement : aucun résultat, événement pas encore publié
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        chercherEvenementResponse = wsEvenementAn.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(chercherEvenementResponse.getMessageErreur(), chercherEvenementResponse.getMessageErreur().contains("Evenement non trouvé"));

        // Sénat recherche l'événement par identifiant technique d'événement : aucun résultat, événement pas encore publié
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        chercherEvenementResponse = wsEvenementSenat.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(chercherEvenementResponse.getMessageErreur(), chercherEvenementResponse.getMessageErreur().contains("Evenement non trouvé"));

        // GVT publie la version 0.1 -> 1.0 publiée
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0030 Publier version.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement1);
        evt01Request.getTexte().setIdInterneEpp(pieceJointe1TexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointe1ExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointe1DecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointe1LettrePmId);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement1, evt01Reponse.getIdEvenement());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de la version créée pour les tests suivants
        final XMLGregorianCalendar horodatage1 = evt01Reponse.getHorodatage();

        // GVT crée un dossier, un événement et une version publiée 1.0
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0040 Creer dossier EVT01 EFIM1100002C.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement2 = evt01Reponse.getIdEvenement();
        final XMLGregorianCalendar horodatage2_1 = evt01Reponse.getHorodatage();
        final String pieceJointe2TexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointe2ExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointe2DecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointe2LettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT crée la version 1.1 brouillon pour complétion
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0050 Creer version brouillon pour completion.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement2);
        evt01Request.getTexte().setIdInterneEpp(pieceJointe2TexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointe2ExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointe2DecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointe2LettrePmId);
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

        // GVT recherche les événements 1 et 2 par identifiants événement + version
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        // Recherche événement 1 + version 1.0
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        Version version = new Version();
        version.setMineur(0);
        version.setMajeur(1);
        chercherEvenementRequest.getIdEvenement().get(0).setVersion(version);
        // Rechercher événement 2 + version 1.0
        EvtId evtId = new EvtId();
        evtId.setId(idEvenement2);
        version = new Version();
        version.setMineur(0);
        version.setMajeur(1);
        evtId.setVersion(version);
        chercherEvenementRequest.getIdEvenement().add(evtId);
        // Rechercher événement 2 + dernière version
        evtId = new EvtId();
        evtId.setId(idEvenement2);
        chercherEvenementRequest.getIdEvenement().add(evtId);
        chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        evenementList = chercherEvenementResponse.getEvenement();
        Assert.assertNotNull(evenementList);
        Assert.assertEquals(3, evenementList.size());

        eppEvtContainer = evenementList.get(0);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement1, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertEquals(horodatage1, evt01Reponse.getHorodatage());

        eppEvtContainer = evenementList.get(1);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement2, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertEquals(horodatage2_1, evt01Reponse.getHorodatage());

        eppEvtContainer = evenementList.get(2);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement2, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Version versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertEquals(EtatVersion.BROUILLON, versionCourante.getEtat());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        List<Version> versionDisponibleList = evt01Reponse.getVersionDisponible();
        Assert.assertEquals(2, versionDisponibleList.size());
        Version versionDisponible = versionDisponibleList.get(0);
        Assert.assertEquals(1, versionDisponible.getMajeur());
        Assert.assertEquals(1, versionDisponible.getMineur());
        Assert.assertEquals(EtatVersion.BROUILLON, versionDisponible.getEtat());
        Assert.assertNotNull(versionDisponible.getHorodatage());
        versionDisponible = versionDisponibleList.get(1);
        Assert.assertEquals(1, versionDisponible.getMajeur());
        Assert.assertEquals(0, versionDisponible.getMineur());
        Assert.assertEquals(EtatVersion.PUBLIE, versionDisponible.getEtat());
        Assert.assertEquals(horodatage2_1, versionDisponible.getHorodatage());

        // GVT recherche l'événement par identifiant technique d'événement + version : aucun résultat, la version n'existe pas
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement1);
        version = new Version();
        version.setMineur(1);
        version.setMajeur(0);
        chercherEvenementRequest.getIdEvenement().get(0).setVersion(version);
        chercherEvenementResponse = wsEvenementSenat.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(chercherEvenementResponse.getMessageErreur(),
                chercherEvenementResponse.getMessageErreur().contains("Version spécifique non trouvée pour la communication"));

        // GVT publie la version 1.1 brouillon -> 2.0 publiée, 1.0 obsolète
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0050 Creer version brouillon pour completion.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.setModeCreation(CreationType.COMPLETER_PUBLIER);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement2);
        evt01Request.getTexte().setIdInterneEpp(pieceJointe2TexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointe2ExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointe2DecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointe2LettrePmId);
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
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final XMLGregorianCalendar horodatage2_2 = evt01Reponse.getHorodatage();

        // GVT recherche de l'événement 2 par id de version
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0020 Rechercher evenement par id_evenement.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);
        evtIdList = chercherEvenementRequest.getIdEvenement();
        Assert.assertNotNull(evtIdList);
        Assert.assertEquals(evtIdList.size(), 1);
        // Recherche événement 2 + version 1.0
        chercherEvenementRequest.getIdEvenement().get(0).setId(idEvenement2);
        version = new Version();
        version.setMineur(0);
        version.setMajeur(1);
        chercherEvenementRequest.getIdEvenement().get(0).setVersion(version);
        chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        evenementList = chercherEvenementResponse.getEvenement();
        Assert.assertNotNull(evenementList);
        Assert.assertEquals(1, evenementList.size());

        eppEvtContainer = evenementList.get(0);
        Assert.assertEquals(EvenementType.EVT_01, eppEvtContainer.getType());
        evt01Reponse = eppEvtContainer.getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertEquals(idEvenement2, evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertEquals(EtatVersion.OBSOLETE, versionCourante.getEtat());
        Assert.assertEquals(horodatage2_1, versionDisponible.getHorodatage());

        versionDisponibleList = evt01Reponse.getVersionDisponible();
        Assert.assertEquals(2, versionDisponibleList.size());
        versionDisponible = versionDisponibleList.get(0);
        Assert.assertEquals(2, versionDisponible.getMajeur());
        Assert.assertEquals(0, versionDisponible.getMineur());
        Assert.assertEquals(EtatVersion.PUBLIE, versionDisponible.getEtat());
        Assert.assertEquals(horodatage2_2, versionDisponible.getHorodatage());
        versionDisponible = versionDisponibleList.get(1);
        Assert.assertEquals(1, versionDisponible.getMajeur());
        Assert.assertEquals(0, versionDisponible.getMineur());
        Assert.assertEquals(EtatVersion.OBSOLETE, versionDisponible.getEtat());
        Assert.assertEquals(horodatage2_1, versionDisponible.getHorodatage());

        // GVT recherche par jeton
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherEvenement/0060 Chercher evenement par jeton.xml";
        chercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, ChercherEvenementRequest.class);
        Assert.assertNotNull(chercherEvenementRequest);

        chercherEvenementResponse = wsEvenementGvt.chercherEvenement(chercherEvenementRequest);
        Assert.assertNotNull(chercherEvenementResponse);
        traitementStatut = chercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        evenementList = chercherEvenementResponse.getEvenement();
        Assert.assertNotNull(evenementList);
        Assert.assertEquals(false, evenementList.isEmpty());

    }
}
