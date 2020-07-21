package fr.dila.solonepp.webengine.wsepp;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.Acteur;
import fr.sword.xsd.solon.epp.ActionObjetReference;
import fr.sword.xsd.solon.epp.AttributionCommissionReference;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.Circonscription;
import fr.sword.xsd.solon.epp.Civilite;
import fr.sword.xsd.solon.epp.Gouvernement;
import fr.sword.xsd.solon.epp.Identite;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.MembreGroupe;
import fr.sword.xsd.solon.epp.Ministere;
import fr.sword.xsd.solon.epp.MotifIrrecevabiliteReference;
import fr.sword.xsd.solon.epp.NatureLoiReference;
import fr.sword.xsd.solon.epp.NatureRapportReference;
import fr.sword.xsd.solon.epp.ObjetType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.Periode;
import fr.sword.xsd.solon.epp.SensAvisReference;
import fr.sword.xsd.solon.epp.SortAdoptionReference;
import fr.sword.xsd.solon.epp.TypeLoiReference;
import fr.sword.xsd.solon.epp.TypePeriode;

/**
 * Tests des tables de référence en : création, modification, consultation.
 * 
 * @author sly
 */
public class TestTableReference extends AbstractEppWSTest {

    
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(TestTableReference.class);      
    
    private static WSEpp wsEppGvt;

    private static WSEpp wsEppAn;
    
    private static WSEpp wsEppSenat;

    @BeforeClass
    public static void setup() throws Exception {
        wsEppGvt = WSServiceHelper.getWSEppGvt();
        wsEppAn = WSServiceHelper.getWSEppAn();
        wsEppSenat = WSServiceHelper.getWSEppSenat();
    }

    private void logInfo(String message) {
      LOGGER.info(null, STLogEnumImpl.GET_TDR_TEC, message);
    }
    
    @WebTest(description = "testActeurs", useDriver = false)
    public void testActeur() throws Exception {
        this.logInfo("-- testActeurs");

        // CREATION
        this.logInfo("Création d'un acteur");
        getFlog().action("Création d'un acteur");
        final String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0010 Creer acteur.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        final MajTableResponse response = wsEppAn.majTable(majRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getMessageErreur(), TraitementStatut.OK, response.getStatut());
        Assert.assertNotNull(response.getObjetContainer());
        Assert.assertTrue(ObjetType.ACTEUR.equals(response.getObjetContainer().getType()));
        Assert.assertNotNull(response.getObjetContainer().getActeur().size() == 1);

        final Acteur acteurCree = response.getObjetContainer().getActeur().get(0);
        Assert.assertNotNull(acteurCree.getId());
        //        Assert.assertTrue(acteurCree.isActif());

        //RECHERCHE
        this.logInfo("Recherche de l'acteur créé");
        getFlog().action("Recherche de l'acteur créé");
        final String chercherActeursFileRequest = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0010 Chercher Acteurs.xml";
        final ChercherTableDeReferenceRequest chercherTableDeReferenceRequest = JaxBHelper.buildRequestFromFile(chercherActeursFileRequest,
                ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(chercherTableDeReferenceRequest);
        final ChercherTableDeReferenceResponse chercherTableDeReferenceResponse = wsEppAn.chercherTableDeReference(chercherTableDeReferenceRequest);

        Assert.assertNotNull(chercherTableDeReferenceResponse);
        Assert.assertEquals(chercherTableDeReferenceResponse.getMessageErreur(), TraitementStatut.OK, chercherTableDeReferenceResponse.getStatut());
        Assert.assertNotNull(chercherTableDeReferenceResponse.getObjetContainer());
        Assert.assertTrue(ObjetType.ACTEUR.equals(chercherTableDeReferenceResponse.getObjetContainer().getType()));
        Assert.assertTrue(chercherTableDeReferenceResponse.getObjetContainer().getActeur().size() > 0);

        final List<Acteur> acteurs = chercherTableDeReferenceResponse.getObjetContainer().getActeur();
        boolean acteurTrouve = false;
        for (final Acteur acteur : acteurs) {
            if (acteur.getId().equals(acteurCree.getId())) {
                acteurTrouve = true;
            }
        }
        Assert.assertTrue(acteurTrouve);

        // Modification de l'acteur
        this.logInfo("Modification de l'acteur créé");
        getFlog().action("Modification de l'acteur créé");
        majRequest.getObjetContainer().getActeur().get(0).setId(acteurCree.getId());
        //        majRequest.getObjetContainer().getActeur().get(0).setActif(false);
        majRequest.setAction(ActionObjetReference.MODIFIER);
        Assert.assertNotNull(majRequest);

        final MajTableResponse modifierTableDeReferenceResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + modifierTableDeReferenceResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(modifierTableDeReferenceResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + modifierTableDeReferenceResponse.getMessageErreur());
        }
        Assert.assertNotNull(modifierTableDeReferenceResponse);
        Assert.assertEquals(modifierTableDeReferenceResponse.getMessageErreur(), TraitementStatut.OK, modifierTableDeReferenceResponse.getStatut());
        Assert.assertNotNull(modifierTableDeReferenceResponse.getObjetContainer());
        Assert.assertTrue(ObjetType.ACTEUR.equals(modifierTableDeReferenceResponse.getObjetContainer().getType()));

        // Recherche de l'acteur modifié
        //        this.logInfo("Récupération de l'acteur modifié");
        //        chercherTableDeReferenceResponse = wsEppAn.chercherTableDeReference(chercherTableDeReferenceRequest);
        //        Assert.assertNotNull(chercherTableDeReferenceResponse);
        //        Assert.assertTrue(chercherTableDeReferenceResponse.getMessageErreur(), TraitementStatut.OK.equals(chercherTableDeReferenceResponse.getStatut()));
        //        Assert.assertNotNull(chercherTableDeReferenceResponse.getObjetContainer());
        //        Assert.assertTrue(ObjetType.ACTEUR.equals(chercherTableDeReferenceResponse.getObjetContainer().getType()));
        //        
        //        acteurs = chercherTableDeReferenceResponse.getObjetContainer().getActeur();
        //        acteurTrouve = false;
        //        for (Acteur acteur : acteurs) {
        //            if (acteur.getId().equals(acteurCree.getId())) {
        //                acteurTrouve = true;
        //            }
        //        }
        //        Assert.assertFalse(acteurTrouve);
        // TODO plus de suppressions apparement à confirmer LBE et supprimer ce test

    }

    @WebTest(description = "Circonscription", useDriver = false)
    public void testCirconscription() throws Exception {
        this.logInfo("-- testCirconscription");
        //CREATION 
        this.logInfo("Création d'une Circonscription");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0030 Creer circonscription.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getCirconscription().size();
        Assert.assertTrue(listSize > 0);
        final Circonscription receivedCirconscription = majResponse.getObjetContainer().getCirconscription().get(listSize - 1);
        this.logInfo("Identifiant Circonscription : " + receivedCirconscription.getId());

        //RECHERCHE      
        this.logInfo("Recherche des circonscriptions");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0020 Chercher Circonscriptions.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        Assert.assertNotNull(rechercheResponse);
        Assert.assertTrue(rechercheResponse.getMessageErreur(), TraitementStatut.OK.equals(rechercheResponse.getStatut()));
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(rechercheResponse.getObjetContainer().getType()));

        List<Circonscription> objetList = rechercheResponse.getObjetContainer().getCirconscription();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Circonscription objet : objetList) {
            if (objet.getId().equals(receivedCirconscription.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Circonscription persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getCirconscription().get(0).setId(receivedCirconscription.getId());
        majRequest.getObjetContainer().getCirconscription().get(0).setNom("NOM MODIFIE");

        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getCirconscription().size();
        Assert.assertTrue(listSize > 0);
        final Circonscription modifiedCirconscription = majResponse.getObjetContainer().getCirconscription().get(listSize - 1);
        this.logInfo("Identifiant Circonscription modifié: " + modifiedCirconscription.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Circonscriptions");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getCirconscription();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Circonscription objetModifie = null;
        for (final Circonscription objet : objetList) {
            if (objet.getId().equals(receivedCirconscription.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertEquals("NOM MODIFIE", objetModifie.getNom());
        this.logInfo("Modification réussie.");

        //Test de la propriété de l'objet
        majResponse = wsEppGvt.majTable(majRequest);
        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertTrue(TraitementStatut.KO.equals(majResponse.getStatut()));

    }

    @WebTest(description = "testGouvernement", useDriver = false)
    public void testGouvernement() throws Exception {

        this.logInfo("-- testGouvernement");

        //CREATION 
        this.logInfo("Création d'une Gouvernement");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0050 Creer gouvernement.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getGouvernement().size();
        Assert.assertTrue(listSize > 0);
        final Gouvernement receivedGouvernement = majResponse.getObjetContainer().getGouvernement().get(listSize - 1);
        this.logInfo("Identifiant Gouvernement : " + receivedGouvernement.getId());

        //RECHERCHE      
        this.logInfo("Recherche des gouvernements");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0030 Chercher Gouvernements.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        Assert.assertNotNull(rechercheResponse);
        Assert.assertTrue(rechercheResponse.getMessageErreur(), TraitementStatut.OK.equals(rechercheResponse.getStatut()));
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(rechercheResponse.getObjetContainer().getType()));

        List<Gouvernement> objetList = rechercheResponse.getObjetContainer().getGouvernement();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Gouvernement objet : objetList) {
            if (objet.getId().equals(receivedGouvernement.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Gouvernement persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getGouvernement().get(0).setId(receivedGouvernement.getId());
        majRequest.getObjetContainer().getGouvernement().get(0).setAppellation("NOUVELLE APPELLATION");

        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getGouvernement().size();
        Assert.assertTrue(listSize > 0);
        final Gouvernement modifiedGouvernement = majResponse.getObjetContainer().getGouvernement().get(listSize - 1);
        this.logInfo("Identifiant Gouvernement modifié: " + modifiedGouvernement.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Gouvernements");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getGouvernement();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Gouvernement objetModifie = null;
        for (final Gouvernement objet : objetList) {
            if (objet.getId().equals(receivedGouvernement.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(objetModifie.getAppellation().equals("NOUVELLE APPELLATION"));
        this.logInfo("Modification réussie.");

    }

    @WebTest(description = "testIdentite", useDriver = false)
    public void testIdentite() throws Exception {

        this.logInfo("-- testIdentite");

        //CREATION 
        this.logInfo("Création d'une Identite");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0070 Creer identite.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getIdentite().size();
        Assert.assertTrue(listSize > 0);
        final Identite receivedIdentite = majResponse.getObjetContainer().getIdentite().get(listSize - 1);
        this.logInfo("Identifiant Identite : " + receivedIdentite.getId());

        //RECHERCHE      
        this.logInfo("Recherche des identites");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0040 Chercher Identites.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);
        Assert.assertNotNull(rechercheResponse);
        Assert.assertTrue(rechercheResponse.getMessageErreur(), TraitementStatut.OK.equals(rechercheResponse.getStatut()));
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(rechercheResponse.getObjetContainer().getType()));

        List<Identite> objetList = rechercheResponse.getObjetContainer().getIdentite();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Identite objet : objetList) {
            if (objet.getId().equals(receivedIdentite.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Identite persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getIdentite().get(0).setId(receivedIdentite.getId());
        majRequest.getObjetContainer().getIdentite().get(0).setCivilite(Civilite.MME);

        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getIdentite().size();
        Assert.assertTrue(listSize > 0);
        final Identite modifiedIdentite = majResponse.getObjetContainer().getIdentite().get(listSize - 1);
        this.logInfo("Identifiant Identite modifié: " + modifiedIdentite.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Identites");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getIdentite();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Identite objetModifie = null;
        for (final Identite objet : objetList) {
            if (objet.getId().equals(receivedIdentite.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(objetModifie.getCivilite().equals(Civilite.MME));
        this.logInfo("Modification réussie.");

    }

    @WebTest(description = "testMandat", useDriver = false)
    public void testMandat() throws Exception {
        this.logInfo("-- testMandat");

        //CREATION DES OBJETS NECESSAIRES

        //ACTEUR
        this.logInfo("Création d'un acteur");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0010 Creer acteur.xml";
        MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        final MajTableResponse response = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + response.getStatut().toString());
        if (TraitementStatut.KO.equals(response.getStatut())) {
            this.logInfo("Message d'erreur : " + response.getMessageErreur());
        }
        Assert.assertNotNull(response);
        Assert.assertTrue(TraitementStatut.OK.equals(response.getStatut()));
        Assert.assertNotNull(response.getObjetContainer());
        Assert.assertTrue(ObjetType.ACTEUR.equals(response.getObjetContainer().getType()));
        Assert.assertNotNull(response.getObjetContainer().getActeur().size() == 1);

        final Acteur acteurCree = response.getObjetContainer().getActeur().get(0);
        Assert.assertNotNull(acteurCree.getId());
        //        Assert.assertTrue(acteurCree.isActif());

        //CIRCONSCRIPTION 
        this.logInfo("Création d'une Circonscription");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0030 Creer circonscription.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getCirconscription().size();
        Assert.assertTrue(listSize > 0);
        final Circonscription receivedCirconscription = majResponse.getObjetContainer().getCirconscription().get(listSize - 1);
        this.logInfo("Identifiant Circonscription : " + receivedCirconscription.getId());

        //GOUVERNEMENT
        this.logInfo("Création d'une Gouvernement");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0050 Creer gouvernement.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getGouvernement().size();
        Assert.assertTrue(listSize > 0);
        final Gouvernement receivedGouvernement = majResponse.getObjetContainer().getGouvernement().get(listSize - 1);
        this.logInfo("Identifiant Gouvernement : " + receivedGouvernement.getId());

        //MINISTERE 
        this.logInfo("Création d'une Ministere");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0130 Creer ministere.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majRequest.getObjetContainer().getMinistere().get(0).setIdGouvernement(receivedGouvernement.getId());
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMinistere().size();
        Assert.assertTrue(listSize > 0);
        final Ministere receivedMinistere = majResponse.getObjetContainer().getMinistere().get(listSize - 1);
        this.logInfo("Identifiant Ministere : " + receivedMinistere.getId());

        //IDENTITE
        this.logInfo("Création d'une Identite");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0070 Creer identite.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getIdentite().size();
        Assert.assertTrue(listSize > 0);
        final Identite receivedIdentite = majResponse.getObjetContainer().getIdentite().get(listSize - 1);
        this.logInfo("Identifiant Identite : " + receivedIdentite.getId());

        //CREATION DU MANDAT
        this.logInfo("Création d'un Mandat");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0090 Creer mandat.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        //majRequest.getObjetContainer().getMandat().get(0).setIdActeur(acteurCree.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdCirconscription(receivedCirconscription.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdMinistere(receivedMinistere.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdIdentite(receivedIdentite.getId());
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMandat());
        Assert.assertTrue(ObjetType.MANDAT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMandat().size();
        Assert.assertTrue(listSize > 0);
        final Mandat receivedMandat = majResponse.getObjetContainer().getMandat().get(listSize - 1);
        this.logInfo("Identifiant Mandat : " + receivedMandat.getId());

        //RECHERCHE      
        this.logInfo("Recherche des mandats");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0050 Chercher Mandats.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);
        Assert.assertNotNull(rechercheResponse);
        Assert.assertTrue(rechercheResponse.getMessageErreur(), TraitementStatut.OK.equals(rechercheResponse.getStatut()));
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMandat());
        Assert.assertTrue(ObjetType.MANDAT.equals(rechercheResponse.getObjetContainer().getType()));

        List<Mandat> objetList = rechercheResponse.getObjetContainer().getMandat();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Mandat objet : objetList) {
            if (objet.getId().equals(receivedMandat.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Mandat persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getMandat().get(0).setId(receivedMandat.getId());
        majRequest.getObjetContainer().getMandat().get(0).setTitre("Nouveau titre");

        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMandat());
        Assert.assertTrue(ObjetType.MANDAT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMandat().size();
        Assert.assertTrue(listSize > 0);
        final Mandat modifiedMandat = majResponse.getObjetContainer().getMandat().get(listSize - 1);
        this.logInfo("Identifiant Mandat modifié: " + modifiedMandat.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Mandats");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMandat());
        Assert.assertTrue(ObjetType.MANDAT.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getMandat();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Mandat objetModifie = null;
        for (final Mandat objet : objetList) {
            if (objet.getId().equals(receivedMandat.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(objetModifie.getTitre().equals("Nouveau titre"));
        this.logInfo("Modification réussie.");

        // Test de la propriété de l'objet
        majResponse = wsEppGvt.majTable(majRequest);
        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertTrue(TraitementStatut.KO.equals(majResponse.getStatut()));

    }

    @WebTest(description = "testMembreGroupe", useDriver = false)
    public void testMembreGroupe() throws Exception {
        this.logInfo("-- testMembreGroupe");

        //ACTEUR
        this.logInfo("Création d'un acteur");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0010 Creer acteur.xml";
        MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        final MajTableResponse response = wsEppAn.majTable(majRequest);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getMessageErreur(), TraitementStatut.OK, response.getStatut());
        Assert.assertNotNull(response.getObjetContainer());
        Assert.assertTrue(ObjetType.ACTEUR.equals(response.getObjetContainer().getType()));
        Assert.assertNotNull(response.getObjetContainer().getActeur().size() == 1);

        final Acteur acteurCree = response.getObjetContainer().getActeur().get(0);
        Assert.assertNotNull(acteurCree.getId());

        //CIRCONSCRIPTION 
        this.logInfo("Création d'une Circonscription");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0030 Creer circonscription.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getCirconscription());
        Assert.assertTrue(ObjetType.CIRCONSCRIPTION.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getCirconscription().size();
        Assert.assertTrue(listSize > 0);
        final Circonscription receivedCirconscription = majResponse.getObjetContainer().getCirconscription().get(listSize - 1);
        this.logInfo("Identifiant Circonscription : " + receivedCirconscription.getId());

        //GOUVERNEMENT
        this.logInfo("Création d'une Gouvernement");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0050 Creer gouvernement.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getGouvernement().size();
        Assert.assertTrue(listSize > 0);
        final Gouvernement receivedGouvernement = majResponse.getObjetContainer().getGouvernement().get(listSize - 1);
        this.logInfo("Identifiant Gouvernement : " + receivedGouvernement.getId());

        //MINISTERE 
        this.logInfo("Création d'une Ministere");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0130 Creer ministere.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majRequest.getObjetContainer().getMinistere().get(0).setIdGouvernement(receivedGouvernement.getId());
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMinistere().size();
        Assert.assertTrue(listSize > 0);
        final Ministere receivedMinistere = majResponse.getObjetContainer().getMinistere().get(listSize - 1);
        this.logInfo("Identifiant Ministere : " + receivedMinistere.getId());

        //IDENTITE
        this.logInfo("Création d'une Identite");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0070 Creer identite.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getIdentite());
        Assert.assertTrue(ObjetType.IDENTITE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getIdentite().size();
        Assert.assertTrue(listSize > 0);
        final Identite receivedIdentite = majResponse.getObjetContainer().getIdentite().get(listSize - 1);
        this.logInfo("Identifiant Identite : " + receivedIdentite.getId());

        //MANDAT
        this.logInfo("Création d'un Mandat");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0090 Creer mandat.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        //majRequest.getObjetContainer().getMandat().get(0).setIdActeur(acteurCree.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdCirconscription(receivedCirconscription.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdMinistere(receivedMinistere.getId());
        majRequest.getObjetContainer().getMandat().get(0).setIdIdentite(receivedIdentite.getId());
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMandat());
        Assert.assertTrue(ObjetType.MANDAT.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMandat().size();
        Assert.assertTrue(listSize > 0);
        final Mandat receivedMandat = majResponse.getObjetContainer().getMandat().get(listSize - 1);
        this.logInfo("Identifiant Mandat : " + receivedMandat.getId());

        //ORGANISME
        this.logInfo("Création d'un Organisme");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0150 Creer organisme.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme receivedOrganisme = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme : " + receivedOrganisme.getId());

        //CREATION 
        this.logInfo("Création d'une MembreGroupe");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0110 Creer membreGroupe.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        majRequest.getObjetContainer().getMembreGroupe().get(0).setIdMandat(receivedMandat.getId());
        majRequest.getObjetContainer().getMembreGroupe().get(0).setIdOrganisme(receivedOrganisme.getId());
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMembreGroupe());
        Assert.assertTrue(ObjetType.MEMBRE_GROUPE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMembreGroupe().size();
        Assert.assertTrue(listSize > 0);
        final MembreGroupe receivedMembreGroupe = majResponse.getObjetContainer().getMembreGroupe().get(listSize - 1);
        this.logInfo("Identifiant MembreGroupe : " + receivedMembreGroupe.getId());

        //RECHERCHE      
        this.logInfo("Recherche des membreGroupes");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0060 Chercher MembresGroupe.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMembreGroupe());
        Assert.assertTrue(ObjetType.MEMBRE_GROUPE.equals(rechercheResponse.getObjetContainer().getType()));

        List<MembreGroupe> objetList = rechercheResponse.getObjetContainer().getMembreGroupe();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final MembreGroupe objet : objetList) {
            if (objet.getId().equals(receivedMembreGroupe.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("MembreGroupe persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getMembreGroupe().get(0).setId(receivedMembreGroupe.getId());
        final XMLGregorianCalendar dateFin = DateUtil.calendarToXMLGregorianCalendar(new GregorianCalendar());
        majRequest.getObjetContainer().getMembreGroupe().get(0).setDateFin(dateFin);

        majResponse = wsEppAn.majTable(majRequest);
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMembreGroupe());
        Assert.assertTrue(ObjetType.MEMBRE_GROUPE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMembreGroupe().size();
        Assert.assertTrue(listSize > 0);
        final MembreGroupe modifiedMembreGroupe = majResponse.getObjetContainer().getMembreGroupe().get(listSize - 1);
        this.logInfo("Identifiant MembreGroupe modifié: " + modifiedMembreGroupe.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des MembreGroupes");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMembreGroupe());
        Assert.assertTrue(ObjetType.MEMBRE_GROUPE.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getMembreGroupe();
        listSize = objetList.size();
        Assert.assertTrue(listSize >= 0);
        if (listSize > 0) {
            objetTrouve = false;
            for (final MembreGroupe objet : objetList) {
                if (objet.getId().equals(receivedMembreGroupe.getId())) {
                    objetTrouve = true;
                    objet.getDateFin().equals(receivedMembreGroupe.getDateFin());
                }
            }
            Assert.assertTrue(objetTrouve);
        }

        this.logInfo("Modification réussie.");

        //Test désactivation / remplacement par 1 seul membre groupe
        majRequest.setAction(ActionObjetReference.RENOUVELER);
        majRequest.getObjetContainer().getMembreGroupe().get(0).setDateFin(null);
        majResponse = wsEppAn.majTable(majRequest);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());

    }

    @WebTest(description = "testMinistere", useDriver = false)
    public void testMinistere() throws Exception {
        this.logInfo("-- test Ministere");
        //GOUVERNEMENT
        this.logInfo("Création d'un Gouvernement");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0050 Creer gouvernement.xml";
        MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getGouvernement());
        Assert.assertTrue(ObjetType.GOUVERNEMENT.equals(majResponse.getObjetContainer().getType()));

        Integer listSize = majResponse.getObjetContainer().getGouvernement().size();
        Assert.assertTrue(listSize > 0);
        final Gouvernement receivedGouvernement = majResponse.getObjetContainer().getGouvernement().get(listSize - 1);
        this.logInfo("Identifiant Gouvernement : " + receivedGouvernement.getId());

        //CREATION
        this.logInfo("Création d'un Ministere");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0130 Creer ministere.xml";
        majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        majRequest.getObjetContainer().getMinistere().get(0).setIdGouvernement(receivedGouvernement.getId());

        Assert.assertNotNull(majRequest);
        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMinistere().size();
        Assert.assertTrue(listSize > 0);
        final Ministere receivedMinistere = majResponse.getObjetContainer().getMinistere().get(listSize - 1);
        this.logInfo("Identifiant Ministere : " + receivedMinistere.getId());

        //RECHERCHE      
        this.logInfo("Recherche des ministeres");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0070 Chercher Ministeres.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(rechercheResponse.getObjetContainer().getType()));

        List<Ministere> objetList = rechercheResponse.getObjetContainer().getMinistere();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Ministere objet : objetList) {
            if (objet.getId().equals(receivedMinistere.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Ministere persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getMinistere().get(0).setId(receivedMinistere.getId());
        majRequest.getObjetContainer().getMinistere().get(0).setNom("Nom du Ministere MODIFIE");

        majResponse = wsEppAn.majTable(majRequest);
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getMinistere().size();
        Assert.assertTrue(listSize > 0);
        final Ministere modifiedMinistere = majResponse.getObjetContainer().getMinistere().get(listSize - 1);
        this.logInfo("Identifiant Ministere modifié: " + modifiedMinistere.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Ministeres");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMinistere());
        Assert.assertTrue(ObjetType.MINISTERE.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getMinistere();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Ministere objetModifie = null;
        for (final Ministere objet : objetList) {
            if (objet.getId().equals(receivedMinistere.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(objetModifie.getNom().equals("Nom du Ministere MODIFIE"));
        this.logInfo("Modification réussie.");

    }

    @WebTest(description = "testOrganisme", useDriver = false)
    public void testOrganisme() throws Exception {
        this.logInfo("-- test Organisme");
        //CREATION
        this.logInfo("Création d'un Organisme");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0150 Creer organisme.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        int listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme receivedOrganisme = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme : " + receivedOrganisme.getId());
        
        //CREATION ORGANISME SENAT
        this.logInfo("Création d'un Organisme sénat");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0155 Creer organisme Senat.xml";
        final MajTableRequest majRequestSenat = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequestSenat);
        majResponse = wsEppSenat.majTable(majRequestSenat);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme receivedOrganismeSenat = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme : " + receivedOrganismeSenat.getId());

        //RECHERCHE      
        this.logInfo("Recherche des organismes");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0080 Chercher Organismes.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(rechercheResponse.getObjetContainer().getType()));

        List<Organisme> objetList = rechercheResponse.getObjetContainer().getOrganisme();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Organisme objet : objetList) {
            if (objet.getId().equals(receivedOrganisme.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Organisme persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getOrganisme().get(0).setId(receivedOrganisme.getId());
        majRequest.getObjetContainer().getOrganisme().get(0).setNom("NOM MODIFIE");

        majResponse = wsEppAn.majTable(majRequest);
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme modifiedOrganisme = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme modifié: " + modifiedOrganisme.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Organismes");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getOrganisme();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Organisme objetModifie = null;
        for (final Organisme objet : objetList) {
            if (objet.getId().equals(receivedOrganisme.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(objetModifie.getNom().equals("NOM MODIFIE"));
        this.logInfo("Modification réussie.");

        // Test de la propriété de l'objet
        majResponse = wsEppGvt.majTable(majRequest);
        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertTrue(TraitementStatut.KO.equals(majResponse.getStatut()));

    }

    @WebTest(description = "testOrganisme (groupe)", useDriver = false)
    public void testGroupe() throws Exception {
        this.logInfo("-- test Organisme (groupe)");
        //CREATION
        this.logInfo("Création d'un Organisme (groupe)");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0180 Creer groupeAN.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        int listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme receivedOrganisme = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme : " + receivedOrganisme.getId());
        
        //CREATION ORGANISME SENAT
        this.logInfo("Création d'un Organisme groupe sénat");
        filename = "fr/dila/solonepp/webengine/wsepp/majTable/0185 Creer groupeSENAT.xml";
        final MajTableRequest majRequestSenat = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequestSenat);
        majResponse = wsEppSenat.majTable(majRequestSenat);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getOrganisme());
        Assert.assertTrue(ObjetType.ORGANISME.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getOrganisme().size();
        Assert.assertTrue(listSize > 0);
        final Organisme receivedOrganismeSenat = majResponse.getObjetContainer().getOrganisme().get(listSize - 1);
        this.logInfo("Identifiant Organisme : " + receivedOrganismeSenat.getId());
    }
    
    @WebTest(description = "test Periode", useDriver = false)
    public void testPeriode() throws Exception {

        this.logInfo("-- test Periode");
        //CREATION
        this.logInfo("Création d'un Periode");
        String filename = "fr/dila/solonepp/webengine/wsepp/majTable/0170 Creer periode.xml";
        final MajTableRequest majRequest = JaxBHelper.buildRequestFromFile(filename, MajTableRequest.class);
        Assert.assertNotNull(majRequest);
        MajTableResponse majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getPeriode());
        Assert.assertTrue(ObjetType.PERIODE.equals(majResponse.getObjetContainer().getType()));

        int listSize = majResponse.getObjetContainer().getPeriode().size();
        Assert.assertTrue(listSize > 0);
        final Periode receivedPeriode = majResponse.getObjetContainer().getPeriode().get(listSize - 1);
        this.logInfo("Identifiant Periode : " + receivedPeriode.getId());

        //RECHERCHE      
        this.logInfo("Recherche des periodes");
        filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0090 Chercher Periodes.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getPeriode());
        Assert.assertTrue(ObjetType.PERIODE.equals(rechercheResponse.getObjetContainer().getType()));

        List<Periode> objetList = rechercheResponse.getObjetContainer().getPeriode();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        boolean objetTrouve = false;
        for (final Periode objet : objetList) {
            if (objet.getId().equals(receivedPeriode.getId())) {
                objetTrouve = true;
            }
        }
        Assert.assertTrue(objetTrouve);
        this.logInfo("Periode persisté");

        //Modification
        majRequest.setAction(ActionObjetReference.MODIFIER);
        majRequest.getObjetContainer().getPeriode().get(0).setId(receivedPeriode.getId());
        majRequest.getObjetContainer().getPeriode().get(0).setType(TypePeriode.PERIODE);

        majResponse = wsEppAn.majTable(majRequest);

        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());
        Assert.assertNotNull(majResponse.getObjetContainer());
        Assert.assertNotNull(majResponse.getObjetContainer().getPeriode());
        Assert.assertTrue(ObjetType.PERIODE.equals(majResponse.getObjetContainer().getType()));

        listSize = majResponse.getObjetContainer().getPeriode().size();
        Assert.assertTrue(listSize > 0);
        final Periode modifiedPeriode = majResponse.getObjetContainer().getPeriode().get(listSize - 1);
        this.logInfo("Identifiant Periode modifié: " + modifiedPeriode.getId());

        //Récupération de l'élément modifié:
        this.logInfo("Recherche des Periodes");
        rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);
        Assert.assertNotNull(rechercheResponse);
        Assert.assertTrue(rechercheResponse.getMessageErreur(), TraitementStatut.OK.equals(rechercheResponse.getStatut()));
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getPeriode());
        Assert.assertTrue(ObjetType.PERIODE.equals(rechercheResponse.getObjetContainer().getType()));

        objetList = rechercheResponse.getObjetContainer().getPeriode();
        listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

        objetTrouve = false;
        Periode objetModifie = null;
        for (final Periode objet : objetList) {
            if (objet.getId().equals(receivedPeriode.getId())) {
                objetTrouve = true;
                objetModifie = objet;
            }
        }
        Assert.assertTrue(objetTrouve);
        Assert.assertTrue(TypePeriode.PERIODE.equals(objetModifie.getType()));
        this.logInfo("Modification réussie.");

        // Test de la propriété de l'objet
        majResponse = wsEppGvt.majTable(majRequest);
        this.logInfo("Statut : " + majResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(majResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + majResponse.getMessageErreur());
        }
        Assert.assertNotNull(majResponse);
        Assert.assertTrue(TraitementStatut.KO.equals(majResponse.getStatut()));

        //Test désactivation / remplacement par 1 seule Periode
        majRequest.setAction(ActionObjetReference.RENOUVELER);
        //        XMLGregorianCalendar dateFin = DateUtil.calendarToXMLGregorianCalendar(new GregorianCalendar());
        majRequest.getObjetContainer().getPeriode().get(0).setDateFin(null);
        majResponse = wsEppAn.majTable(majRequest);
        Assert.assertEquals(majResponse.getMessageErreur(), TraitementStatut.OK, majResponse.getStatut());

    }

    @WebTest(description = "test Attribution Commission", useDriver = false)
    public void testAttributionCommission() throws Exception {
        this.logInfo("-- test Attribution Commission");
        //RECHERCHE      
        this.logInfo("Recherche des  Attributions Commissions");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0100 Chercher Attribution commission.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getAttributionCommission());
        Assert.assertTrue(ObjetType.ATTRIBUTION_COMMISSION.equals(rechercheResponse.getObjetContainer().getType()));

        final List<AttributionCommissionReference> objetList = rechercheResponse.getObjetContainer().getAttributionCommission();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Nature Loi", useDriver = false)
    public void testNatureLoi() throws Exception {
        this.logInfo("-- test Nature Loi");

        //RECHERCHE      
        this.logInfo("Recherche des  Natures Loi");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0110 Chercher Nature Loi.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getNatureLoi());
        Assert.assertTrue(ObjetType.NATURE_LOI.equals(rechercheResponse.getObjetContainer().getType()));

        final List<NatureLoiReference> objetList = rechercheResponse.getObjetContainer().getNatureLoi();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Motif Irrecevabilite", useDriver = false)
    public void testMotifIrrecevabilite() throws Exception {
        this.logInfo("-- test Motif Irrecevabilite");

        //RECHERCHE      
        this.logInfo("Recherche des Motifs Irrecevabilite");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0120 Chercher Motif Irrecevabilite.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getMotifIrrecevabilite());
        Assert.assertTrue(ObjetType.MOTIF_IRRECEVABILITE.equals(rechercheResponse.getObjetContainer().getType()));

        final List<MotifIrrecevabiliteReference> objetList = rechercheResponse.getObjetContainer().getMotifIrrecevabilite();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Nature Rapport", useDriver = false)
    public void testNatureRapport() throws Exception {
        this.logInfo("-- test Nature Rapport");

        //RECHERCHE      
        this.logInfo("Recherche des Natures Rapport");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0130 Chercher Nature Rapport.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getNatureRapport());
        Assert.assertTrue(ObjetType.NATURE_RAPPORT.equals(rechercheResponse.getObjetContainer().getType()));

        final List<NatureRapportReference> objetList = rechercheResponse.getObjetContainer().getNatureRapport();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Sens Avis", useDriver = false)
    public void testSensAvis() throws Exception {
        this.logInfo("-- test Sens Avis");
        //RECHERCHE      
        this.logInfo("Recherche des Sens Avis");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0140 Chercher Sens Avis.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getSensAvis());
        Assert.assertTrue(ObjetType.SENS_AVIS.equals(rechercheResponse.getObjetContainer().getType()));

        final List<SensAvisReference> objetList = rechercheResponse.getObjetContainer().getSensAvis();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Type Loi", useDriver = false)
    public void testTypeLoi() throws Exception {
        this.logInfo("-- test Type Loi");

        //RECHERCHE      
        this.logInfo("Recherche des Type Loi");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0150 Chercher Type Loi.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getTypeLoi());
        Assert.assertTrue(ObjetType.TYPE_LOI.equals(rechercheResponse.getObjetContainer().getType()));

        final List<TypeLoiReference> objetList = rechercheResponse.getObjetContainer().getTypeLoi();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }

    @WebTest(description = "test Sort Adoption", useDriver = false)
    public void testSortAdoption() throws Exception {
        this.logInfo("-- test Sort Adoption");
        //RECHERCHE      
        this.logInfo("Recherche des Sort Adoption");
        final String filename = "fr/dila/solonepp/webengine/wsepp/chercherTableDeReference/0160 Chercher Sort Adoption.xml";
        final ChercherTableDeReferenceRequest rechercheRequest = JaxBHelper.buildRequestFromFile(filename, ChercherTableDeReferenceRequest.class);
        Assert.assertNotNull(rechercheRequest);
        final ChercherTableDeReferenceResponse rechercheResponse = wsEppAn.chercherTableDeReference(rechercheRequest);

        this.logInfo("Statut : " + rechercheResponse.getStatut().toString());
        if (TraitementStatut.KO.equals(rechercheResponse.getStatut())) {
            this.logInfo("Message d'erreur : " + rechercheResponse.getMessageErreur());
        }
        Assert.assertNotNull(rechercheResponse);
        Assert.assertEquals(rechercheResponse.getMessageErreur(), TraitementStatut.OK, rechercheResponse.getStatut());
        Assert.assertNotNull(rechercheResponse.getObjetContainer());
        Assert.assertNotNull(rechercheResponse.getObjetContainer().getSortAdoption());
        Assert.assertTrue(ObjetType.SORT_ADOPTION.equals(rechercheResponse.getObjetContainer().getType()));

        final List<SortAdoptionReference> objetList = rechercheResponse.getObjetContainer().getSortAdoption();
        final int listSize = objetList.size();
        Assert.assertTrue(listSize > 0);

    }
}
