package fr.dila.solonepp.webengine.wsevenement;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;

/**
 * Test de recherche xsd des messages.
 * 
 * @author asatre
 */
public class TestRechercherMessageXSD extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    @WebTest(description = "RechercherMessageSimple", useDriver = false)
    public void testRechercherMessageSimple() throws Exception {

        String filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageXSD/0010 ChercherMessageXSD.xml";
        RechercherEvenementRequest rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        RechercherEvenementResponse rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        TraitementStatut traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // manque un paramètre
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageXSD/0020 ChercherMessageXSD.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.KO, traitementStatut);

        // X AND ( Y OR Z)
        filename = "fr/dila/solonepp/webengine/wsevenement/rechercherMessageXSD/0030 ChercherMessageXSD.xml";
        rechercherEvenementRequest = JaxBHelper.buildRequestFromFile(filename, RechercherEvenementRequest.class);
        Assert.assertNotNull(rechercherEvenementRequest);
        rechercherEvenementResponse = wsEvenementGvt.rechercherEvenement(rechercherEvenementRequest);
        Assert.assertNotNull(rechercherEvenementResponse);
        traitementStatut = rechercherEvenementResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(rechercherEvenementResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

    }
}
