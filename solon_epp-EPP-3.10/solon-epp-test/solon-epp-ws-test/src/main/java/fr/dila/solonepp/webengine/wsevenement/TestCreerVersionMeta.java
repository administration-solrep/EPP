package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.Commission;
import fr.sword.xsd.solon.epp.CreationType;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EppEvt02;
import fr.sword.xsd.solon.epp.EppEvt04Bis;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.NatureLoi;
import fr.sword.xsd.solon.epp.NatureRapport;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.TypeLoi;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels des créations de version, avec vérification des métadonnées.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionMeta extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
    }

    /**
     * Test de création de version. Vérifie les données qui peuvent être modifiées en fonction des actions (initialiser, compléter, rectifier) : - Emetteur crée une version à l'état brouillon -> 0.1 BROUILLON ; - Emetteur modifie la version brouillon : possibilité de modifier tous les champs (mêmes non modifiables) ; - Emetteur publie la version 1.0 : possibilité de modifier tous les champs (mêmes non modifiables). - Emetteur crée la version complétée brouillon 1.1 : modification des champs non obligatoires autorisée, autres interdits ; - Emetteur publie la version complétée 2.0 : modification des champs non obligatoires autorisée, autres interdits. - Emetteur crée la version rectifiée brouillon 2.1 : modification de tous les champs autorisée ; - Emetteur publie la version rectifiée 3.0 :
     * modification de tous les champs autorisée.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionMetaAction() throws Exception {

        // GVT crée un dossier + événement créateur + version : enregistrement Mandat (table de référence) inexistant
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0010 Creer dossier EVT01 CCOZ1100001M.xml";
        CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        EppEvt01 evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.getAuteur().setId("inexistant");
        CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(), creerVersionResponse.getMessageErreur().contains("inexistant"));

        // GVT crée un dossier + événement créateur + version brouillon 0.1
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0010 Creer dossier EVT01 CCOZ1100001M.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Version versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(0, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        Mandat auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        List<Mandat> coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(1, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();
        final String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        final String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        final String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        final String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

        // GVT modifie la version brouillon 0.1 : suppression d'un attribut obligatoire autorisé
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0020 Modifier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        evt01Request.setNor(null);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNull(evt01Reponse.getNor());

        // GVT modifie la version brouillon 0.1 : modification autorisée de tous les attributs (même si non modifiables ou obligatoires)
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0020 Modifier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(0, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet modifié", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROPOSITION, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest1", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(1, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest1", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // GVT publie la version 1.0 : modification autorisée de tous les attributs (même si obligatoires scalaires et multivalués)
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0030 Publier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.setModeCreation(CreationType.PUBLIER);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet modifié 2", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(2, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        auteur = coauteur.get(1);
        Assert.assertEquals("MandatTest0", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire", evt01Reponse.getCommentaire());

        // GVT crée une version 1.1 brouillon pour complétion : interdiction de modifier un attribut scalaire obligatoire
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0040 Completer brouillon EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        evt01Request.setObjet("Objet modifié 3");
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("ne peut pas être modifiée en mode complétion"));

        // GVT crée une version 1.1 brouillon pour complétion : interdiction de supprimer une valeur d'un attribut multivalué obligatoire
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0040 Completer brouillon EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        evt01Request.getCoAuteur().remove(1);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("interdit de retirer un élément de la propriété multivaluée"));

        // GVT crée une version 1.1 brouillon pour complétion : ok
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0040 Completer brouillon EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        auteur = new Mandat();
        auteur.setId("MandatTest1");
        evt01Request.getCoAuteur().add(auteur);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet modifié 2", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(3, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        auteur = coauteur.get(1);
        Assert.assertEquals("MandatTest0", auteur.getId());
        auteur = coauteur.get(2);
        Assert.assertEquals("MandatTest1", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire complété", evt01Reponse.getCommentaire());

        // GVT publie une version 2.0 complétée : interdiction de modifier un attribut scalaire obligatoire
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0050 Completer publier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        evt01Request.setObjet("Objet modifié 3");
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("ne peut pas être modifiée en mode complétion"));

        // GVT publie une version 2.0 complétée : interdiction de supprimer une valeur d'un attribut multivalué obligatoire
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0050 Completer publier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        evt01Request.getCoAuteur().remove(1);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(),
                creerVersionResponse.getMessageErreur().contains("interdit de retirer un élément de la propriété multivaluée"));

        // GVT publie une version 2.0 complétée : ok
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0050 Completer publier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        creerVersionRequest.setModeCreation(CreationType.COMPLETER_PUBLIER);
        Assert.assertNotNull(creerVersionRequest);
        evt01Request = creerVersionRequest.getEvenement().getEvt01();
        evt01Request.setIdEvenement(idEvenement);
        evt01Request.getTexte().setIdInterneEpp(pieceJointeTexteId);
        evt01Request.getExposeDesMotifs().setIdInterneEpp(pieceJointeExposeDesMotifsId);
        evt01Request.getDecretPresentation().setIdInterneEpp(pieceJointeDecretPresentationId);
        evt01Request.getLettrePm().setIdInterneEpp(pieceJointeLettrePmId);
        auteur = new Mandat();
        auteur.setId("MandatTest1");
        evt01Request.getCoAuteur().add(auteur);
        creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(2, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet modifié 2", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(4, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        auteur = coauteur.get(1);
        Assert.assertEquals("MandatTest0", auteur.getId());
        auteur = coauteur.get(2);
        Assert.assertEquals("MandatTest1", auteur.getId());
        auteur = coauteur.get(3);
        Assert.assertEquals("MandatTest1", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire complété 2", evt01Reponse.getCommentaire());

        // GVT crée la version 2.1 brouillon pour rectification : modification autorisée de tous les attributs (même si obligatoires scalaires et multivalués)
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0060 Rectifier brouillon EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(2, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest1", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(1, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest1", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire rectifié", evt01Reponse.getCommentaire());

        // GVT crée la version 2.1 brouillon pour rectification : modification autorisée de tous les attributs (même si obligatoires scalaires et multivalués)
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/0070 Rectifier publier EVT01.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(3, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals("Objet rectifié 2", evt01Reponse.getObjet());
        Assert.assertEquals("CCOZ1100001M", evt01Reponse.getNor());
        Assert.assertEquals(Integer.valueOf(1), evt01Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt01Reponse.getNiveauLecture().getCode());
        Assert.assertEquals(TypeLoi.LOI, evt01Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt01Reponse.getNatureLoi());
        auteur = evt01Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        coauteur = evt01Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(1, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt01Reponse.getIntitule());
        Assert.assertEquals("Commentaire rectifié 2", evt01Reponse.getCommentaire());

    }

    /**
     * Test de création de version. Vérifie les obligatoires en fonction des événements (tables de références, vocabulaires) :
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionMetaEvenement() throws Exception {

        // Emetteur crée un dossier + événement créateur + version : objet de référence organisme inexistant
        String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/1000 Creer dossier EVT02 CCOZ1100002M publie.xml";
        CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final EppEvt02 evt02Request = creerVersionRequest.getEvenement().getEvt02();
        evt02Request.getCommission().getSaisieAuFond().setId("inexistant");
        CreerVersionResponse creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);
        Assert.assertTrue(creerVersionResponse.getMessageErreur(), creerVersionResponse.getMessageErreur().contains("inexistant"));

        // Emetteur publie la version 1.0 de l'événement Evt02
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/1000 Creer dossier EVT02 CCOZ1100002M publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt02 evt02Reponse = creerVersionResponse.getEvenement().getEvt02();
        Assert.assertEquals(EtatEvenement.PUBLIE, evt02Reponse.getEtat());
        Assert.assertNotNull(evt02Reponse);
        Assert.assertNotNull(evt02Reponse.getIdEvenement());
        Version versionCourante = evt02Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt02Reponse.getHorodatage());
        Assert.assertEquals("Objet Evt02", evt02Reponse.getObjet());
        Assert.assertEquals(Integer.valueOf(1), evt02Reponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt02Reponse.getNiveauLecture().getCode());
        Assert.assertEquals("http://www.gouvernement.fr", evt02Reponse.getUrlDossierAn());
        Assert.assertEquals(TypeLoi.LOI, evt02Reponse.getTypeLoi());
        Assert.assertEquals(NatureLoi.PROJET, evt02Reponse.getNatureLoi());
        Mandat auteur = evt02Reponse.getAuteur();
        Assert.assertNotNull(auteur);
        Assert.assertEquals("MandatTest0", auteur.getId());
        final List<Mandat> coauteur = evt02Reponse.getCoAuteur();
        Assert.assertNotNull(coauteur);
        Assert.assertEquals(1, coauteur.size());
        auteur = coauteur.get(0);
        Assert.assertEquals("MandatTest0", auteur.getId());
        Assert.assertNotNull(evt02Reponse.getCoSignataireCollectif());
        Assert.assertEquals("Cosignataire 1", evt02Reponse.getCoSignataireCollectif());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt02Reponse.getIntitule());
        Assert.assertEquals("Commentaire Evt02", evt02Reponse.getCommentaire());
        Depot depot = evt02Reponse.getDepot();
        Assert.assertNotNull(depot);
        Assert.assertNotNull(depot.getDate());
        Assert.assertEquals("DEPOT_1", depot.getNumero());
        final Commission commission = evt02Reponse.getCommission();
        Assert.assertNotNull(commission);
        Organisme organisme = commission.getSaisieAuFond();
        Assert.assertNotNull(organisme);
        Assert.assertEquals("OrganismeTest0", organisme.getId());
        final List<Organisme> organismeList = commission.getSaisiePourAvis();
        Assert.assertEquals(1, organismeList.size());
        organisme = organismeList.get(0);
        Assert.assertEquals("OrganismeTest0", organisme.getId());

        // Récupère les identifiants générés pour les tests suivants
        final String idEvenement = evt02Reponse.getIdEvenement();

        // Emetteur publie la version 1.0 de l'événement Evt04Bis
        filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionMeta/1010 Creer dossier EVT04BIS CCOZ1100004M publie.xml";
        creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        final EppEvt04Bis evt04BisRequest = creerVersionRequest.getEvenement().getEvt04Bis();
        evt04BisRequest.setIdEvenementPrecedent(idEvenement);
        Assert.assertNotNull(creerVersionRequest);
        creerVersionResponse = wsEvenementAn.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt04Bis evt04BisReponse = creerVersionResponse.getEvenement().getEvt04Bis();
        Assert.assertEquals(EtatEvenement.PUBLIE, evt04BisReponse.getEtat());
        Assert.assertNotNull(evt04BisReponse);
        Assert.assertNotNull(evt04BisReponse.getIdEvenement());
        versionCourante = evt04BisReponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(1, versionCourante.getMajeur());
        Assert.assertEquals(0, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertNotNull(evt04BisReponse.getHorodatage());
        Assert.assertEquals(Integer.valueOf(1), evt04BisReponse.getNiveauLecture().getNiveau());
        Assert.assertEquals(NiveauLectureCode.AN, evt04BisReponse.getNiveauLecture().getCode());
        Assert.assertEquals("Objet Evt04", evt04BisReponse.getObjet());
        Assert.assertEquals("Titre Evt04", evt04BisReponse.getTitre());
        Assert.assertEquals("Projet de loi relatif à l'action extérieure de l'État", evt04BisReponse.getIntitule());
        Assert.assertEquals("Commentaire Evt04", evt04BisReponse.getCommentaire());
        Assert.assertNotNull(evt04BisReponse.getDateDistribution());
        Assert.assertEquals(NatureRapport.RAPPORT, evt04BisReponse.getNatureRapport());
        final List<Mandat> mandatList = evt04BisReponse.getRapporteur();
        Assert.assertNotNull(mandatList);
        Assert.assertEquals(1, mandatList.size());
        Assert.assertEquals("MandatTest0", mandatList.get(0).getId());
        depot = evt04BisReponse.getDepotRapport();
        Assert.assertNotNull(depot);
        Assert.assertNotNull(depot.getDate());
        Assert.assertEquals("DEPOT_1", depot.getNumero());
        depot = evt04BisReponse.getDepotTexte();
        Assert.assertNotNull(depot);
        Assert.assertNotNull(depot.getDate());
        Assert.assertEquals("DEPOT_2", depot.getNumero());
        organisme = evt04BisReponse.getCommission();
        Assert.assertEquals("OrganismeTest0", organisme.getId());

    }
}
