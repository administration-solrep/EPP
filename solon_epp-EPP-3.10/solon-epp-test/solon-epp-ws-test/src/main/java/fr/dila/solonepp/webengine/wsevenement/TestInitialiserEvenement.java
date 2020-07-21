package fr.dila.solonepp.webengine.wsevenement;

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
import fr.sword.xsd.solon.epp.EppEvt04Bis;
import fr.sword.xsd.solon.epp.InitialiserEvenementRequest;
import fr.sword.xsd.solon.epp.InitialiserEvenementResponse;

/**
 * Tests fonctionnels d'initialisation d'un evenement successif
 * 
 * @author asatre
 */
public class TestInitialiserEvenement extends AbstractEppWSTest {

    private static WSEvenement wsEvenementAn;
    private static WSEvenement wsEvenementSenat;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();
        wsEvenementSenat = WSServiceHelper.getWSEvenementSenat();
    }

    /**
     * Test d'initialisation de l'evenement successif EVT04BIS a l'evenement EFIM1100200R_00000
     * 
     * @throws Exception 
     */
    @WebTest(description = "Test d'initialisation de l'evenement successif EVT04BIS a l'evenement EFIM1100200R_00000", useDriver = false)
    public void testInitialiserEvenement() throws Exception {

        // Creation d'une communication
        String filename = "fr/dila/solonepp/webengine/wsevenement/initialiserEvenement/0001 Creer dossier EVT02 EFIM1100200R publie.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementSenat.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        final String idEvt = creerVersionResponse.getEvenement().getEvt02().getIdEvenement();
        filename = "fr/dila/solonepp/webengine/wsevenement/initialiserEvenement/0002 Initialiser EFIM1100200R_00000 EVT04BIS.xml";
        final InitialiserEvenementRequest initialiserEvenementRequest = JaxBHelper.buildRequestFromFile(filename, InitialiserEvenementRequest.class);
        Assert.assertNotNull(initialiserEvenementRequest);
        initialiserEvenementRequest.setIdEvenementPrecedent(idEvt);
        final InitialiserEvenementResponse initialiserEvenementResponse = wsEvenementAn.initialiserEvenement(initialiserEvenementRequest);
        Assert.assertNotNull(initialiserEvenementResponse);
        traitementStatut = initialiserEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(initialiserEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(initialiserEvenementResponse.getEvenement());
        final EppEvt04Bis evt04Reponse = initialiserEvenementResponse.getEvenement().getEvt04Bis();
        Assert.assertNotNull(evt04Reponse);

    }

}
